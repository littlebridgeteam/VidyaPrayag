/*
 * File: SkillTestRouting.kt
 * Module: feature.skilltest
 *
 * REST API for the Skill Test System. All endpoints are parent-scoped
 * (JWT-authenticated parent initiates the test for their child).
 *
 * Endpoints:
 *   GET  /api/v1/parent/skill-test/{childId}/eligibility   — can the child take a test?
 *   POST /api/v1/parent/skill-test/{childId}/start          — start a new attempt, returns questions
 *   POST /api/v1/parent/skill-test/{attemptId}/answer       — submit one answer (instant eval)
 *   GET  /api/v1/parent/skill-test/{childId}/best-score     — best score + badge status
 *   GET  /api/v1/parent/skill-test/{childId}/history        — all past attempts
 *   GET  /api/v1/parent/skill-test/{attemptId}/review       — questions + answers for review
 *   POST /api/v1/parent/skill-test/generate/{gradeLevel}    — admin: trigger weekly generation
 */
package com.littlebridge.enrollplus.feature.skilltest

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.okMessage
import com.littlebridge.enrollplus.core.principalUserId
import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

@Serializable
data class StartAttemptResponse(
    @SerialName("attempt_id") val attemptId: String,
    val questions: List<SkillTestService.QuestionDto>,
)

@Serializable
data class SubmitAnswerRequest(
    @SerialName("question_id") val questionId: String,
    @SerialName("selected_answer") val selectedAnswer: String,
)

fun Route.skillTestRouting() {
    authenticate("jwt") {
        route("/api/v1/parent/skill-test") {

            // ── Eligibility check ──────────────────────────────────────
            get("/{childId}/eligibility") {
                val parentId = call.principalUserId()?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized); return@get
                }

                val childId = call.parameters["childId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid childId", HttpStatusCode.BadRequest); return@get
                }

                // Verify the child belongs to this parent
                val ownsChild = dbQuery {
                    ChildrenTable.selectAll()
                        .where {
                            (ChildrenTable.id eq childId) and
                            (ChildrenTable.parentId eq parentId) and
                            (ChildrenTable.isActive eq true)
                        }
                        .any()
                }
                if (!ownsChild) {
                    call.fail("Child not found", HttpStatusCode.NotFound); return@get
                }

                val eligibility = SkillTestService.checkEligibility(childId)
                call.ok(eligibility)
            }

            // ── Start a new attempt ────────────────────────────────────
            post("/{childId}/start") {
                val parentId = call.principalUserId()?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized); return@post
                }

                val childId = call.parameters["childId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid childId", HttpStatusCode.BadRequest); return@post
                }

                // Verify ownership + get schoolId
                val childRow = dbQuery {
                    ChildrenTable.selectAll()
                        .where {
                            (ChildrenTable.id eq childId) and
                            (ChildrenTable.parentId eq parentId) and
                            (ChildrenTable.isActive eq true)
                        }
                        .firstOrNull()
                }
                if (childRow == null) {
                    call.fail("Child not found", HttpStatusCode.NotFound); return@post
                }

                val schoolId = childRow[ChildrenTable.schoolId]
                val result = SkillTestService.startAttempt(childId, parentId, schoolId)

                if (result == null) {
                    call.fail(
                        "Cannot start test. Either no questions available or you're in the 7-day cooldown period.",
                        HttpStatusCode.Conflict,
                        "NOT_ELIGIBLE"
                    ); return@post
                }

                call.ok(StartAttemptResponse(
                    attemptId = result.first,
                    questions = result.second,
                ))
            }

            // ── Submit a single answer (instant evaluation) ────────────
            post("/{attemptId}/answer") {
                val parentId = call.principalUserId()?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized); return@post
                }

                val attemptId = call.parameters["attemptId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid attemptId", HttpStatusCode.BadRequest); return@post
                }

                val req = runCatching { call.receive<SubmitAnswerRequest>() }.getOrNull()
                    ?: run {
                        call.fail("Invalid request body", HttpStatusCode.BadRequest); return@post
                    }

                val questionId = runCatching { UUID.fromString(req.questionId) }.getOrNull()
                    ?: run {
                        call.fail("Invalid questionId", HttpStatusCode.BadRequest); return@post
                    }

                val result = SkillTestService.submitAnswer(attemptId, questionId, req.selectedAnswer)

                if (result == null) {
                    call.fail("Answer submission failed", HttpStatusCode.BadRequest); return@post
                }

                call.ok(result)
            }

            // ── Get best score ─────────────────────────────────────────
            get("/{childId}/best-score") {
                val parentId = call.principalUserId()?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized); return@get
                }

                val childId = call.parameters["childId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid childId", HttpStatusCode.BadRequest); return@get
                }

                // Verify ownership
                val ownsChild = dbQuery {
                    ChildrenTable.selectAll()
                        .where {
                            (ChildrenTable.id eq childId) and
                            (ChildrenTable.parentId eq parentId) and
                            (ChildrenTable.isActive eq true)
                        }
                        .any()
                }
                if (!ownsChild) {
                    call.fail("Child not found", HttpStatusCode.NotFound); return@get
                }

                val bestScore = SkillTestService.getBestScore(childId)
                call.ok(bestScore ?: SkillTestService.BestScoreDto(
                    bestScore = 0,
                    attemptsCount = 0,
                    badgeEarned = false,
                ))
            }

            // ── Get attempt history ────────────────────────────────────
            get("/{childId}/history") {
                val parentId = call.principalUserId()?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized); return@get
                }

                val childId = call.parameters["childId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid childId", HttpStatusCode.BadRequest); return@get
                }

                // Verify ownership
                val ownsChild = dbQuery {
                    ChildrenTable.selectAll()
                        .where {
                            (ChildrenTable.id eq childId) and
                            (ChildrenTable.parentId eq parentId) and
                            (ChildrenTable.isActive eq true)
                        }
                        .any()
                }
                if (!ownsChild) {
                    call.fail("Child not found", HttpStatusCode.NotFound); return@get
                }

                val history = SkillTestService.getAttemptHistory(childId)
                call.ok(history)
            }

            // ── Review a completed attempt ─────────────────────────────
            get("/{attemptId}/review") {
                val attemptId = call.parameters["attemptId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run {
                    call.fail("Invalid attemptId", HttpStatusCode.BadRequest); return@get
                }

                val review = SkillTestService.getAttemptReview(attemptId)
                call.ok(review)
            }

            // ── Admin: trigger weekly question generation ──────────────
            post("/generate/{gradeLevel}") {
                val gradeLevel = call.parameters["gradeLevel"]
                    ?: run { call.fail("gradeLevel required", HttpStatusCode.BadRequest); return@post }

                val count = SkillTestService.generateWeeklyBatch(gradeLevel)
                if (count == 0) {
                    call.fail(
                        "Question generation failed. AI service may be unavailable.",
                        HttpStatusCode.ServiceUnavailable,
                        "AI_UNAVAILABLE"
                    ); return@post
                }

                call.okMessage("Generated $count questions for grade $gradeLevel")
            }
        }
    }
}
