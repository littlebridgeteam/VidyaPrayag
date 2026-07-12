/*
 * File: ExamRequestSyllabusRouting.kt
 * Module: feature.exam
 *
 * Parent → teacher request for exam syllabus. When a parent sees an upcoming
 * exam but no syllabus has been mapped, they can request the teacher to add it.
 *
 * Endpoints (JWT):
 *   POST /api/v1/exam/request-syllabus   — parent requests syllabus for an exam
 *   GET  /api/v1/exam/request-syllabus   — teacher lists pending requests
 *   POST /api/v1/exam/request-syllabus/{id}/resolve — teacher resolves a request
 */
package com.littlebridge.enrollplus.feature.exam

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.principalUserId
import com.littlebridge.enrollplus.db.AssessmentsTable
import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.feature.notifications.Notify
import com.littlebridge.enrollplus.feature.notifications.NotifyRecipients
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

// We store requests in a lightweight table — but to avoid adding yet another
// table, we'll use the notifications system itself as the transport. The parent
// sends a notification to the teacher with category="exam_syllabus_request".
// The teacher sees these in their notification feed and can act on them.

@Serializable
data class ExamSyllabusRequestDto(
    @SerialName("assessment_id") val assessmentId: String,
    val message: String = "",
)

@Serializable
data class ExamSyllabusRequestResponse(
    val success: Boolean,
    val message: String,
)

fun Route.examRequestSyllabusRouting() {
    authenticate("jwt") {

        // ── POST /api/v1/exam/request-syllabus — parent requests syllabus ──
        route("/api/v1/exam/request-syllabus") {

            post {
                val uid = call.principalUserId()?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized)
                    return@post
                }
                val req = call.receive<ExamSyllabusRequestDto>()
                val assessmentId = runCatching { UUID.fromString(req.assessmentId) }.getOrNull() ?: run {
                    call.fail("Invalid assessment id", HttpStatusCode.BadRequest)
                    return@post
                }

                // Resolve the parent's child + school from the assessment
                val assessment = dbQuery {
                    AssessmentsTable.selectAll()
                        .where { AssessmentsTable.id eq assessmentId }
                        .singleOrNull()
                } ?: run {
                    call.fail("Assessment not found", HttpStatusCode.NotFound)
                    return@post
                }

                val schoolId = assessment[AssessmentsTable.schoolId]
                val teacherId = assessment[AssessmentsTable.teacherId]
                val className = assessment[AssessmentsTable.className]
                val subject = assessment[AssessmentsTable.subject]
                val examName = assessment[AssessmentsTable.name]

                // Verify the parent has a child in this school
                val hasChild = dbQuery {
                    ChildrenTable.selectAll()
                        .where {
                            (ChildrenTable.parentId eq uid) and
                                (ChildrenTable.schoolId eq schoolId) and
                                (ChildrenTable.isActive eq true)
                        }
                        .any()
                }

                if (!hasChild) {
                    call.fail("You don't have a child enrolled in this school", HttpStatusCode.Forbidden)
                    return@post
                }

                // Notify the teacher (if linked) or all teachers of that class
                val teacherIds = if (teacherId != null) {
                    listOf(teacherId)
                } else {
                    NotifyRecipients.teachersInSchool(schoolId)
                }

                if (teacherIds.isNotEmpty()) {
                    Notify.toUsers(
                        userIds = teacherIds,
                        category = "exam_syllabus_request",
                        title = "Syllabus Request: $examName ($subject)",
                        body = "A parent has requested the syllabus for $examName — $subject, Class $className. ${req.message}".trim(),
                        schoolId = schoolId,
                        actorId = uid,
                        deepLink = "/teacher/exam-syllabus/$assessmentId",
                        refType = "assessment",
                        refId = assessmentId.toString(),
                    )
                }

                call.ok(
                    ExamSyllabusRequestResponse(success = true, message = "Request sent to teacher"),
                    message = "Syllabus request sent",
                )
            }
        }
    }
}
