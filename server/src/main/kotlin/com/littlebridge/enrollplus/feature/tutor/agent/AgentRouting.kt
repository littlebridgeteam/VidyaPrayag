// FILE: server/src/main/kotlin/com/littlebridge/enrollplus/feature/tutor/agent/AgentRouting.kt
package com.littlebridge.enrollplus.feature.tutor.agent

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.principalUserUuid
import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.feature.notifications.Notify
import com.littlebridge.enrollplus.feature.notifications.NotifyRecipients
import com.littlebridge.enrollplus.feature.tutor.data.TutorSessionRepository
import com.littlebridge.enrollplus.feature.tutor.triage.TutorTriageService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

/**
 * Routes for Tier 2 — Agent (TutorAgentService).
 *
 * Endpoints:
 *   POST /tutor/doubt — resolve a doubt with the agent loop
 *
 * Authorization: parent must own the child (verified via ChildrenTable.parentId).
 *
 * SOLID: S (routes only — no business logic), D (service injected).
 *
 * Spec: AI_TUTOR_2.0_AGENTIC_REDESIGN.md §6.4
 */
fun Route.agentRouting() {

    post("/tutor/doubt") {
        val uid = call.principalUserUuid() ?: return@post call.fail(
            "Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED"
        )
        val body = call.receive<DoubtRequest>()

        val childId = runCatching { UUID.fromString(body.childId) }.getOrNull()
            ?: return@post call.fail("Invalid childId", HttpStatusCode.BadRequest, "BAD_CHILD_ID")
        val subjectId = body.subjectId.takeIf { it.isNotBlank() }?.let {
            runCatching { UUID.fromString(it) }.getOrNull()
        }

        if (body.question.isBlank()) {
            return@post call.fail("Question is required", HttpStatusCode.BadRequest, "BAD_QUESTION")
        }

        // Ownership check: parent must own this child
        val ownsChild = dbQuery {
            ChildrenTable.selectAll().where {
                (ChildrenTable.id eq childId) and
                (ChildrenTable.parentId eq uid) and
                (ChildrenTable.isActive eq true)
            }.any()
        }
        if (!ownsChild) return@post call.fail(
            "Child not found", HttpStatusCode.NotFound, "CHILD_NOT_FOUND"
        )

        // Resolve schoolId from child
        val schoolId = dbQuery {
            ChildrenTable.selectAll().where { ChildrenTable.id eq childId }
                .singleOrNull()?.get(ChildrenTable.schoolId)
        } ?: return@post call.fail(
            "Child has no school link", HttpStatusCode.BadRequest, "NO_SCHOOL"
        )

        // ── Triage (Tier 1) — classify before engaging the agent ──
        val trace = mutableListOf<ThinkingStep>()
        trace.add(ThinkingStep("Checking syllabus", "done"))

        val triageService = TutorTriageService()
        val triage = triageService.classify(schoolId, childId, subjectId, body.question)

        trace.add(ThinkingStep("Classifying intent", if (triage.modelUsed) "done" else "skipped",
            detail = triage.intent))
        trace.add(ThinkingStep("Syllabus check", "done", detail = triage.syllabusStatus))

        // If triage says skip (known misconception or inappropriate) → return deterministic response
        if (triage.skipAgent) {
            trace.add(ThinkingStep("Agent reasoning", "skipped", detail = triage.skipReason))

            val triageSafetyFlag = triage.safetyFlag

            val detTurn = when (triage.skipReason) {
                "known_misconception" -> TutorTurn(
                    mode = "HINT",
                    studentFacing = StudentFacing(
                        text = "I noticed you might be stuck on this. Let's try a different approach — " +
                            "can you tell me what you already know about this topic?",
                        nextPrompt = "What part feels confusing right now?",
                    ),
                    misconception = MisconceptionLog(
                        type = triage.misconceptionType ?: "unknown",
                        evidence = "Known misconception from triage",
                    ),
                )
                "inappropriate_content" -> TutorTurn(
                    mode = "ESCALATE",
                    studentFacing = StudentFacing(
                        text = "I can't help with that request. I'm here to help you with your " +
                            "school subjects. If you have a question about your homework or need " +
                            "help understanding a topic, I'm happy to assist!",
                        nextPrompt = "Is there a school topic you'd like help with today?",
                    ),
                    teacherFlag = TeacherFlag(
                        reason = "inappropriate_content",
                        severity = "high",
                    ),
                )
                else -> TutorTurnCodec.deterministic(body.question)
            }
            val sessionId = runCatching {
                TutorSessionRepository().insert(
                    schoolId = schoolId,
                    childId = childId,
                    subjectId = subjectId,
                    mode = "DOUBT",
                    turns = TutorTurnCodec.encode(detTurn),
                    groundedRefs = "[]",
                    providerUsed = null,
                    tokensUsed = 0,
                    cacheHit = false,
                    safetyFlag = triageSafetyFlag,
                )
            }.getOrNull()

            call.ok(
                DoubtResponse(
                    sessionId = sessionId?.toString(),
                    turn = detTurn,
                    modelUsed = false,
                    providerUsed = triage.providerUsed,
                    grounded = true,
                    safetyFlag = triageSafetyFlag,
                    thinkingTrace = trace,
                    intent = triage.intent,
                    syllabusStatus = triage.syllabusStatus,
                ),
                "Doubt resolved (triage shortcut: ${triage.skipReason})"
            )

            // Notify parent, teachers, and admins if triage flagged inappropriate content
            if (triageSafetyFlag != null) {
                runCatching {
                    Notify.toUser(
                        userId = uid,
                        category = "tutor_escalation",
                        title = "Tutor Session Update",
                        body = "Your child's tutor session flagged a safety concern: $triageSafetyFlag. " +
                            "A teacher may reach out to discuss next steps.",
                        schoolId = schoolId,
                        deepLink = "/parent/academics/tutor",
                        refType = "tutor_session",
                        refId = sessionId?.toString(),
                    )
                }.onFailure { /* best-effort */ }

                runCatching {
                    val teacherIds = NotifyRecipients.teachersInSchool(schoolId)
                    if (teacherIds.isNotEmpty()) {
                        Notify.toUsers(
                            userIds = teacherIds,
                            category = "tutor_escalation",
                            title = "Student Safety Flag",
                            body = "A student's AI tutor session was flagged: $triageSafetyFlag. " +
                                "Please review and follow up as needed.",
                            schoolId = schoolId,
                            deepLink = "/teacher/academics/tutor",
                            refType = "tutor_session",
                            refId = sessionId?.toString(),
                        )
                    }
                }.onFailure { /* best-effort */ }

                runCatching {
                    val adminIds = NotifyRecipients.adminsInSchool(schoolId)
                    if (adminIds.isNotEmpty()) {
                        Notify.toUsers(
                            userIds = adminIds,
                            category = "tutor_escalation",
                            title = "Student Safety Flag",
                            body = "A student's AI tutor session was flagged: $triageSafetyFlag. " +
                                "Please review and take appropriate action.",
                            schoolId = schoolId,
                            deepLink = "/admin/academics/tutor",
                            refType = "tutor_session",
                            refId = sessionId?.toString(),
                        )
                    }
                }.onFailure { /* best-effort */ }
            }

            return@post
        }

        // ── Agent (Tier 2) — triage says proceed ──
        trace.add(ThinkingStep("Building learner context", "done"))
        trace.add(ThinkingStep("Agent reasoning", "done", detail = triage.intent))

        val service = TutorAgentService()
        val result = service.resolveDoubt(
            schoolId = schoolId,
            childId = childId,
            subjectId = subjectId,
            question = body.question,
            intent = triage.intent,
            syllabusStatus = triage.syllabusStatus,
        )

        trace.add(ThinkingStep("Verifying grounding", if (result.grounded) "done" else "skipped"))

        call.ok(
            DoubtResponse(
                sessionId = result.sessionId?.toString(),
                turn = result.turn,
                modelUsed = result.modelUsed,
                providerUsed = result.providerUsed,
                grounded = result.grounded,
                safetyFlag = result.safetyFlag,
                thinkingTrace = trace,
                intent = triage.intent,
                syllabusStatus = triage.syllabusStatus,
            ),
            if (result.modelUsed) "Doubt resolved" else "Doubt resolved (deterministic fallback)"
        )

        // Notify parent, teachers, and admins if the tutor session was flagged.
        // This happens when the child asks for inappropriate content, repeatedly
        // asks for answers, or shows distress.
        if (result.safetyFlag != null) {
            runCatching {
                Notify.toUser(
                    userId = uid,
                    category = "tutor_escalation",
                    title = "Tutor Session Update",
                    body = "Your child's tutor session flagged a safety concern: ${result.safetyFlag}. " +
                        "A teacher may reach out to discuss next steps.",
                    schoolId = schoolId,
                    deepLink = "/parent/academics/tutor",
                    refType = "tutor_session",
                    refId = result.sessionId?.toString(),
                )
            }.onFailure { /* best-effort */ }

            // Notify all teachers in the school
            runCatching {
                val teacherIds = NotifyRecipients.teachersInSchool(schoolId)
                if (teacherIds.isNotEmpty()) {
                    Notify.toUsers(
                        userIds = teacherIds,
                        category = "tutor_escalation",
                        title = "Student Safety Flag",
                        body = "A student's AI tutor session was flagged: ${result.safetyFlag}. " +
                            "Please review and follow up as needed.",
                        schoolId = schoolId,
                        deepLink = "/teacher/academics/tutor",
                        refType = "tutor_session",
                        refId = result.sessionId?.toString(),
                    )
                }
            }.onFailure { /* best-effort */ }

            // Notify all admins in the school
            runCatching {
                val adminIds = NotifyRecipients.adminsInSchool(schoolId)
                if (adminIds.isNotEmpty()) {
                    Notify.toUsers(
                        userIds = adminIds,
                        category = "tutor_escalation",
                        title = "Student Safety Flag",
                        body = "A student's AI tutor session was flagged: ${result.safetyFlag}. " +
                            "Please review and take appropriate action.",
                        schoolId = schoolId,
                        deepLink = "/admin/academics/tutor",
                        refType = "tutor_session",
                        refId = result.sessionId?.toString(),
                    )
                }
            }.onFailure { /* best-effort */ }
        }
    }
}

@Serializable
data class DoubtRequest(
    val childId: String,
    val subjectId: String,
    val question: String,
)

@Serializable
data class ThinkingStep(
    val label: String,
    val status: String,  // "done" | "skipped"
    val detail: String? = null,
)

@Serializable
data class DoubtResponse(
    val sessionId: String?,
    val turn: TutorTurn,
    val modelUsed: Boolean,
    val providerUsed: String?,
    val grounded: Boolean,
    val safetyFlag: String?,
    val thinkingTrace: List<ThinkingStep> = emptyList(),
    val intent: String = "doubt",
    val syllabusStatus: String = "UNKNOWN",
)
