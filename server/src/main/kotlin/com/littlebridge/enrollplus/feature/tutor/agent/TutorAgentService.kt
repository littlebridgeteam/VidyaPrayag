// FILE: server/src/main/kotlin/com/littlebridge/enrollplus/feature/tutor/agent/TutorAgentService.kt
package com.littlebridge.enrollplus.feature.tutor.agent

import com.littlebridge.enrollplus.feature.ai.AiLane
import com.littlebridge.enrollplus.feature.ai.AiService
import com.littlebridge.enrollplus.feature.tutor.core.TutorConstants
import com.littlebridge.enrollplus.feature.tutor.core.TutorKillSwitch
import com.littlebridge.enrollplus.feature.tutor.data.TutorSessionRepository
import com.littlebridge.enrollplus.feature.tutor.sense.LearnerBundleBuilder
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * TIER 2 — Tutor Agent Service.
 *
 * Runs AiService.runAgent() with the Tutor toolset, parses the structured
 * TutorTurn, passes it through TutorGroundingGuard, and persists the session.
 *
 * Graceful degradation: if no AI provider, produces a deterministic TutorTurn
 * (Socratic step) from the bundle alone (no tools, no LLM).
 *
 * Kill-switched under module name "tutor_agent".
 *
 * SOLID:
 *   S → Single responsibility: orchestrates the agent loop.
 *   D → Depends on AiService abstraction, not a specific provider.
 *
 * Spec: AI_TUTOR_2.0_AGENTIC_REDESIGN.md §6.4
 */
class TutorAgentService(
    private val sessionRepo: TutorSessionRepository = TutorSessionRepository(),
    private val bundleBuilder: LearnerBundleBuilder = LearnerBundleBuilder(),
) {
    private val log = LoggerFactory.getLogger("TutorAgentService")

    data class TutorResult(
        val sessionId: UUID?,
        val turn: TutorTurn,
        val modelUsed: Boolean,
        val providerUsed: String? = null,
        val toolCallsMade: Int = 0,
        val stepsTaken: Int = 0,
        val grounded: Boolean = true,
        val safetyFlag: String? = null,
    )

    /**
     * Resolve a doubt: run the agent loop, ground the output, persist the session.
     *
     * @param schoolId       tenant scope (from JWT, never from the model)
     * @param childId        the child's UUID
     * @param subjectId      the subject's UUID
     * @param question       the child's doubt text
     * @param intent         triage intent (doubt | practice_request | concept_explain | plan_review | check_in)
     * @param syllabusStatus triage syllabus status (ON_SYLLABUS | AHEAD_OF_SYLLABUS | OFF_CURRICULUM | UNKNOWN)
     * @return a grounded TutorTurn wrapped in a TutorResult
     */
    suspend fun resolveDoubt(
        schoolId: UUID,
        childId: UUID,
        subjectId: UUID?,
        question: String,
        intent: String = "doubt",
        syllabusStatus: String = "UNKNOWN",
    ): TutorResult {
        TutorKillSwitch.require(TutorConstants.MODULE_AGENT)

        // Build the deterministic bundle (Tier 0) — null subjectId is OK,
        // bundle will be null and the agent runs in DIAGNOSTIC mode
        val bundle = if (subjectId != null) bundleBuilder.build(childId, subjectId) else null

        // Try the agentic path (LLM + tools)
        val agentResult = runAgent(schoolId, childId, subjectId, question, bundle, intent, syllabusStatus)

        if (agentResult != null) {
            val content = agentResult.content ?: ""
            val turn = TutorTurnCodec.parse(content)
            if (turn != null) {
                // Ground the turn against the deterministic bundle
                val grounded = if (bundle != null) {
                    TutorGroundingGuard.verify(turn, bundle)
                } else {
                    turn // No bundle → can't ground, but serve with a warning
                }

                val wasGrounded = grounded != null && grounded.studentFacing?.text == turn.studentFacing?.text
                val finalTurn = grounded ?: TutorTurnCodec.deterministic(question)

                // Persist the session
                val sessionId = persistSession(
                    schoolId, childId, subjectId, finalTurn,
                    agentResult.providerUsed, agentResult.toolCallsMade,
                    wasGrounded,
                    tokensUsed = agentResult.totalInputTokens + agentResult.totalOutputTokens,
                )

                return TutorResult(
                    sessionId = sessionId,
                    turn = finalTurn,
                    modelUsed = true,
                    providerUsed = agentResult.providerUsed,
                    toolCallsMade = agentResult.toolCallsMade,
                    stepsTaken = agentResult.stepsTaken,
                    grounded = wasGrounded,
                    safetyFlag = finalTurn.teacherFlag?.reason
                        ?: if (finalTurn.mode == "ESCALATE") "repeated_answer_request" else null,
                )
            } else {
                log.warn("TutorAgent: model output failed TutorTurn parse — falling back to deterministic")
                log.warn("TutorAgent: raw model output (first 800 chars): {}", agentResult.content?.take(800))
            }
        }

        // Fallback: deterministic TutorTurn (no AI)
        val detTurn = TutorTurnCodec.deterministic(question)
        val sessionId = persistSession(
            schoolId, childId, subjectId, detTurn,
            null, 0, true,
        )
        return TutorResult(
            sessionId = sessionId,
            turn = detTurn,
            modelUsed = false,
        )
    }

    // ── Agent prompt + execution ───────────────────────────────────────────

    private val systemPrompt = """
        You are an AI Tutor for school students. Use the SOCRATIC method — guide, don't solve.

        RULES:
        - Use ONLY numbers from the LEARNER DATA in your output. Never invent figures.
        - Never teach ahead of covered topics (use EXPLANATION mode if the child asks about not-yet-covered topics).
        - If you spot a misconception, call log_misconception.
        - Respond with a JSON object (NOT a tool call) with this schema:
        {"mode":"SOCRATIC_STEP|HINT|EXPLANATION|PRACTICE_SET|PLAN_UPDATE|ESCALATE",
         "groundedRefs":[{"topicId":"","source":"MARKS|SYLLABUS|NCERT|RAG","value":""}],
         "studentFacing":{"text":"","mathBlocks":["LaTeX"],"nextPrompt":""},
         "practice":[{"questionId":"","stem":"","options":[],"answerKey":"","topicId":"","difficulty":"easy|medium|hard"}],
         "planDelta":null,"teacherFlag":null,
         "misconception":{"topicId":"","type":"","evidence":""}}

        MODES:
        - SOCRATIC_STEP: guide with a question
        - HINT: targeted hint, not the answer
        - EXPLANATION: explain a concept (only if stuck after hints)
        - PRACTICE_SET: 2-3 questions on weakest covered topic
        - ESCALATE: child keeps asking for the answer → flag teacher
    """.trimIndent()

    private suspend fun runAgent(
        schoolId: UUID,
        childId: UUID,
        subjectId: UUID?,
        question: String,
        bundle: com.littlebridge.enrollplus.feature.tutor.sense.LearnerBundle?,
        intent: String,
        syllabusStatus: String,
    ): AiService.AgentResult? {
        if (!AiService.anyProviderConfigured()) {
            log.debug("TutorAgent: no AI provider — using deterministic for child {}", childId)
            return null
        }

        val userPrompt = buildUserPrompt(childId, subjectId, question, bundle, syllabusStatus)
        val tools = TutorTools.allTools()

        // Route to cheaper lane for simple intents
        val lane = when (intent) {
            "check_in", "practice_request", "plan_review" -> AiLane.FAST_CHAT
            else -> AiLane.REASON
        }

        val result = AiService.runAgent(
            feature = "ai_tutor",
            lane = lane,
            systemPrompt = systemPrompt,
            userPrompt = userPrompt,
            tools = tools,
            schoolId = schoolId,
            containsPii = true,
            maxSteps = 3,
            temperature = 0.3,
            maxTokens = 1024,
        )

        if (!result.ok) {
            log.warn("TutorAgent: agent failed for child {} — {}", childId, result.errorMessage)
            return null
        }

        log.info("TutorAgent: agent completed for child {} ({} steps, {} tool calls, {}+{} tokens, provider={})",
            childId, result.stepsTaken, result.toolCallsMade,
            result.totalInputTokens, result.totalOutputTokens, result.providerUsed)
        return result
    }

    private fun buildUserPrompt(
        childId: UUID,
        subjectId: UUID?,
        question: String,
        bundle: com.littlebridge.enrollplus.feature.tutor.sense.LearnerBundle?,
        syllabusStatus: String,
    ): String = buildString {
        if (bundle != null) {
            appendLine("LEARNER DATA (ground truth — cite ONLY these numbers):")
            appendLine("  Syllabus status: $syllabusStatus")
            appendLine("  Covered topics (${bundle.syllabusPosition.coveredTopicIds.size}): ${bundle.syllabusPosition.coveredTopicTitles.joinToString(", ")}")
            if (bundle.syllabusPosition.notYetCoveredTitles.isNotEmpty()) {
                appendLine("  Not-yet-covered (${bundle.syllabusPosition.notYetCoveredIds.size}): ${bundle.syllabusPosition.notYetCoveredTitles.joinToString(", ")}")
            }
            appendLine("  Current chapter: ${bundle.syllabusPosition.currentChapter ?: "unknown"}")
            if (bundle.weakTopics.isNotEmpty()) {
                appendLine("  Weak topics (${bundle.weakTopics.size}):")
                bundle.weakTopics.take(3).forEach { wt ->
                    appendLine("    - ${wt.topicId}: ${wt.pct}% (${wt.severity})")
                }
            }
            if (bundle.homeworkContext.dueSoon.isNotEmpty()) {
                appendLine("  Homework due soon: ${bundle.homeworkContext.dueSoon.joinToString { "${it.title} (due ${it.dueDate})" }}")
            }
            if (bundle.homeworkContext.missed.isNotEmpty()) {
                appendLine("  Homework missed: ${bundle.homeworkContext.missed.joinToString { it.title }}")
            }
            if (bundle.reviewQueue.isNotEmpty()) {
                appendLine("  Due reviews: ${bundle.reviewQueue.size}")
            }
            appendLine("  Has marks: ${bundle.dataConfidence.hasMarks}")
        } else {
            appendLine("LEARNER DATA: NOT AVAILABLE (child may not be linked to school)")
            appendLine("Run in DIAGNOSTIC mode — ask gentle placement questions.")
        }
        appendLine()
        appendLine("Child's doubt: $question")
        appendLine()
        appendLine("Respond with TutorTurn JSON. Call log_misconception if you spot a pattern.")
    }

    private suspend fun persistSession(
        schoolId: UUID,
        childId: UUID,
        subjectId: UUID?,
        turn: TutorTurn,
        providerUsed: String?,
        toolCallsMade: Int,
        grounded: Boolean,
        tokensUsed: Int = 0,
    ): UUID? = runCatching {
        sessionRepo.insert(
            schoolId = schoolId,
            childId = childId,
            subjectId = subjectId,
            mode = "DOUBT",
            turns = TutorTurnCodec.encode(turn),
            groundedRefs = kotlinx.serialization.json.Json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(GroundedRef.serializer()),
                turn.groundedRefs
            ),
            providerUsed = providerUsed,
            tokensUsed = tokensUsed,
            cacheHit = false,
            safetyFlag = if (turn.mode == "ESCALATE") "repeated_answer_request" else null,
        )
    }.onFailure { log.warn("TutorAgent: failed to persist session — {}", it.message) }.getOrNull()
}
