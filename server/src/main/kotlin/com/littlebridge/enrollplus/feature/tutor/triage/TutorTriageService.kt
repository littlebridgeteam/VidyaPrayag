// FILE: server/src/main/kotlin/com/littlebridge/enrollplus/feature/tutor/triage/TutorTriageService.kt
package com.littlebridge.enrollplus.feature.tutor.triage

import com.littlebridge.enrollplus.feature.ai.AiLane
import com.littlebridge.enrollplus.feature.ai.AiService
import com.littlebridge.enrollplus.feature.ai.LlmMessage
import com.littlebridge.enrollplus.feature.tutor.core.TutorConstants
import com.littlebridge.enrollplus.feature.tutor.core.TutorKillSwitch
import com.littlebridge.enrollplus.feature.tutor.data.TutorMisconceptionRepository
import com.littlebridge.enrollplus.feature.tutor.sense.LearnerBundleBuilder
import com.littlebridge.enrollplus.feature.tutor.sense.SyllabusPosition
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.util.UUID

/** Result of the deterministic on-syllabus check. */
enum class SyllabusStatus {
    ON_SYLLABUS,
    AHEAD_OF_SYLLABUS,
    OFF_CURRICULUM,
    UNKNOWN,
}

/**
 * TIER 1 — Triage (cheap CLASSIFY, GROQ_FAST, batched).
 *
 * Every inbound event is classified before any expensive reasoning:
 * - Intent: doubt | practice_request | concept_explain | plan_review | check_in
 * - On-syllabus? (matches coveredTopicIds)
 * - Known misconception? (matches the misconception library)
 * - Cache hit? (ai_response_cache keyed on fact-hash of bundle+question)
 *
 * Only events that survive triage reach Tier 2.
 *
 * Kill-switched under module name "tutor_triage".
 *
 * SOLID:
 *   S → Single responsibility: classify and route inbound events.
 *   D → Depends on AiService abstraction (CLASSIFY lane), not a specific provider.
 *
 * Spec: AI_TUTOR_2.0_AGENTIC_REDESIGN.md §6.1
 */
class TutorTriageService(
    private val bundleBuilder: LearnerBundleBuilder = LearnerBundleBuilder(),
    private val misconceptionRepo: TutorMisconceptionRepository = TutorMisconceptionRepository(),
) {
    private val log = LoggerFactory.getLogger("TutorTriageService")
    private val json = Json { ignoreUnknownKeys = true }

    @Serializable
    data class TriageResult(
        val intent: String,           // doubt | practice_request | concept_explain | plan_review | check_in
        val onSyllabus: Boolean,
        val syllabusStatus: String,   // ON_SYLLABUS | AHEAD_OF_SYLLABUS | OFF_CURRICULUM | UNKNOWN
        val knownMisconception: Boolean,
        val misconceptionType: String? = null,
        val skipAgent: Boolean,       // true → don't invoke Tier 2
        val skipReason: String? = null,
        val cacheHit: Boolean = false,
        val modelUsed: Boolean = false,
        val providerUsed: String? = null,
    )

    /**
     * Classify an inbound event. Returns a [TriageResult] that determines
     * whether the event proceeds to Tier 2 (Agent) or is handled directly.
     *
     * @param schoolId  tenant scope (from JWT)
     * @param childId   the child's UUID
     * @param subjectId the subject's UUID
     * @param question  the child's input text
     */
    suspend fun classify(
        schoolId: UUID,
        childId: UUID,
        subjectId: UUID?,
        question: String,
    ): TriageResult {
        TutorKillSwitch.require(TutorConstants.MODULE_TRIAGE)

        // 1. Build the deterministic bundle (Tier 0) for on-syllabus check
        val bundle = if (subjectId != null) bundleBuilder.build(childId, subjectId) else null

        // 2. Deterministic on-syllabus check (no LLM needed)
        val syllabusPos = bundle?.syllabusPosition
        val coveredTopics = syllabusPos?.coveredTopicIds ?: emptyList()
        val syllabusStatus = checkOnSyllabus(question, syllabusPos)
        val onSyllabus = syllabusStatus == SyllabusStatus.ON_SYLLABUS || syllabusStatus == SyllabusStatus.UNKNOWN

        // 3. Known misconception check (DB lookup, no LLM)
        val knownMisconception = checkKnownMisconception(childId)
        val misconceptionType = if (knownMisconception != null) {
            knownMisconception.misconceptionType
        } else null

        // 4. If known misconception → skip agent, return proven remediation path
        if (knownMisconception != null) {
            log.info("Triage: known misconception '{}' for child {} — skipping agent", misconceptionType, childId)
            return TriageResult(
                intent = "doubt",
                onSyllabus = onSyllabus,
                syllabusStatus = syllabusStatus.name,
                knownMisconception = true,
                misconceptionType = misconceptionType,
                skipAgent = true,
                skipReason = "known_misconception",
            )
        }

        // 5. LLM-based intent classification (cheap CLASSIFY lane)
        val intentResult = classifyIntent(schoolId, question)

        return TriageResult(
            intent = intentResult?.intent ?: "doubt",
            onSyllabus = onSyllabus,
            syllabusStatus = syllabusStatus.name,
            knownMisconception = false,
            skipAgent = false,
            modelUsed = intentResult != null,
            providerUsed = intentResult?.providerUsed,
        )
    }

    // ── Deterministic on-syllabus check ──────────────────────────────────

    /**
     * Keyword-based on-syllabus check. Matches the question against covered
     * and not-yet-covered topic titles from the syllabus.
     *
     * Returns:
     *   - ON_SYLLABUS: question matches a covered topic
     *   - AHEAD_OF_SYLLABUS: question matches a not-yet-covered topic
     *   - OFF_CURRICULUM: question doesn't match any syllabus topic
     *   - UNKNOWN: no syllabus data available (let agent handle)
     */
    private fun checkOnSyllabus(
        question: String,
        syllabusPos: SyllabusPosition?,
    ): SyllabusStatus {
        if (syllabusPos == null) return SyllabusStatus.UNKNOWN

        val coveredTitles = syllabusPos.coveredTopicTitles
        val notCoveredTitles = syllabusPos.notYetCoveredTitles

        if (coveredTitles.isEmpty() && notCoveredTitles.isEmpty()) {
            return SyllabusStatus.UNKNOWN
        }

        val questionLower = question.lowercase()
        val questionWords = questionLower.split(Regex("\\s+"))
            .filter { it.length >= 3 }
            .toSet()

        // Check covered topics first
        for (title in coveredTitles) {
            val titleLower = title.lowercase()
            if (questionLower.contains(titleLower) || titleLower.split(Regex("\\s+"))
                    .filter { it.length >= 3 }
                    .any { questionWords.contains(it) }) {
                return SyllabusStatus.ON_SYLLABUS
            }
        }

        // Check not-yet-covered topics
        for (title in notCoveredTitles) {
            val titleLower = title.lowercase()
            if (questionLower.contains(titleLower) || titleLower.split(Regex("\\s+"))
                    .filter { it.length >= 3 }
                    .any { questionWords.contains(it) }) {
                return SyllabusStatus.AHEAD_OF_SYLLABUS
            }
        }

        return SyllabusStatus.OFF_CURRICULUM
    }

    // ── Known misconception check ────────────────────────────────────────

    private suspend fun checkKnownMisconception(
        childId: UUID,
    ): TutorMisconceptionRepository.MisconceptionRow? {
        val misconceptions = misconceptionRepo.findByChild(childId, includeResolved = false)
        // Return the most recent unresolved misconception
        return misconceptions.firstOrNull()
    }

    // ── LLM intent classification ────────────────────────────────────────

    private val classifySystemPrompt = """
        You are a fast intent classifier for a school tutoring system.
        Classify the student's input into exactly one intent:

        - doubt: The student has a specific question or problem they need help with.
        - practice_request: The student wants to practice problems.
        - concept_explain: The student wants a concept explained.
        - plan_review: The student wants to review their study plan.
        - check_in: A casual check-in or greeting.

        Respond with ONLY a JSON object: {"intent": "one_of_the_above"}
        No other text, no explanation.
    """.trimIndent()

    private suspend fun classifyIntent(
        schoolId: UUID,
        question: String,
    ): ClassifyResult? {
        if (!AiService.anyProviderConfigured()) {
            log.debug("Triage: no AI provider — defaulting to 'doubt'")
            return null
        }

        val result = AiService.complete(
            feature = "ai_tutor_triage",
            lane = AiLane.CLASSIFY,
            messages = listOf(
                LlmMessage("system", classifySystemPrompt),
                LlmMessage("user", question),
            ),
            containsPii = true,
            schoolId = schoolId,
            temperature = 0.1,
            maxTokens = 64,
            cache = true,
        )

        if (!result.ok || result.content == null) {
            log.warn("Triage: classify failed — {}", result.errorMessage)
            return null
        }

        val intent = parseIntent(result.content)
        log.info("Triage: classified intent='{}' (provider={})", intent, result.providerUsed)
        return ClassifyResult(intent = intent, providerUsed = result.providerUsed)
    }

    private data class ClassifyResult(
        val intent: String,
        val providerUsed: String?,
    )

    private fun parseIntent(raw: String): String {
        return try {
            val cleaned = raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val obj = json.parseToJsonElement(cleaned) as kotlinx.serialization.json.JsonObject
            (obj["intent"] as? kotlinx.serialization.json.JsonPrimitive)?.content ?: "doubt"
        } catch (e: Exception) {
            log.warn("Triage: failed to parse intent from '{}' — defaulting to 'doubt'", raw.take(80))
            "doubt"
        }
    }
}
