/*
 * File: ParentHomeworkRouting.kt
 * Module: feature.parent
 *
 * Parent-facing homework lifecycle (RA-PHW-1):
 *   - GET  /api/v1/parent/child/{childId}/homework
 *          → active homework for the child, with submission status.
 *   - GET  /api/v1/parent/child/{childId}/homework/{homeworkId}
 *          → detail of one homework + any existing submission.
 *   - POST /api/v1/parent/child/{childId}/homework/{homeworkId}/submit
 *          → parent submits written text + photo attachments on behalf of child.
 *
 * Auth: JWT parent token; the child must belong to the caller.
 */
package com.littlebridge.enrollplus.feature.parent

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.principalUserId
import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.HomeworkExtensionsTable
import com.littlebridge.enrollplus.db.HomeworkSubmissionAttachmentsTable
import com.littlebridge.enrollplus.db.HomeworkSubmissionsTable
import com.littlebridge.enrollplus.db.HomeworkTable
import com.littlebridge.enrollplus.db.StudentsTable
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

// ── DTOs ─────────────────────────────────────────────────────────────────────

@Serializable
data class ParentHomeworkAttachmentDto(
    val id: String,
    val url: String,
    val filename: String = "",
    val mime: String = "",
    @SerialName("size_bytes") val sizeBytes: Long = 0,
)

@Serializable
data class ParentHomeworkItemDto(
    val id: String,
    val title: String,
    val description: String = "",
    val subject: String = "",
    @SerialName("due_date") val dueDate: String,
    @SerialName("due_time") val dueTime: String? = null,
    @SerialName("allow_late") val allowLate: Boolean = false,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_past_due") val isPastDue: Boolean = false,
    val status: String = "not_submitted", // submitted | late | graded | not_submitted
    @SerialName("submitted_at") val submittedAt: String? = null,
    @SerialName("submission_text") val submissionText: String = "",
    val attachments: List<ParentHomeworkAttachmentDto> = emptyList(),
    @SerialName("has_extension") val hasExtension: Boolean = false,
    @SerialName("extended_to") val extendedTo: String? = null,
)

@Serializable
data class ParentHomeworkListData(
    val items: List<ParentHomeworkItemDto> = emptyList(),
)

@Serializable
data class ParentHomeworkDetailData(
    val homework: ParentHomeworkItemDto? = null,
)

@Serializable
data class ParentSubmitHomeworkAttachmentDto(
    val url: String,
    val filename: String = "",
    val mime: String = "",
    @SerialName("size_bytes") val sizeBytes: Long = 0,
)

@Serializable
data class ParentSubmitHomeworkRequest(
    val text: String = "",
    val attachments: List<ParentSubmitHomeworkAttachmentDto> = emptyList(),
)

@Serializable
data class ParentHomeworkMutationData(
    val success: Boolean = true,
    val message: String = "",
)

// ── Helpers ──────────────────────────────────────────────────────────────────

private fun todayIst(): LocalDate = LocalDate.now(ZoneId.of("Asia/Kolkata"))

private fun isPastDue(dueDate: LocalDate, dueTime: LocalTime?, extension: LocalDate?, extTime: LocalTime?): Boolean {
    val effectiveDate = extension ?: dueDate
    val effectiveTime = extTime ?: dueTime
    val today = todayIst()
    return when {
        effectiveDate.isBefore(today) -> true
        effectiveDate.isAfter(today) -> false
        else -> effectiveTime != null && LocalTime.now(ZoneId.of("Asia/Kolkata")).isAfter(effectiveTime)
    }
}

/**
 * Resolve a child that belongs to the caller. Returns the child row or responds and returns null.
 */
private suspend fun ApplicationCall.requireOwnedChild(parentId: UUID, childId: String): org.jetbrains.exposed.sql.ResultRow? {
    val cid = runCatching { UUID.fromString(childId) }.getOrNull() ?: run {
        fail("Invalid child id", HttpStatusCode.BadRequest); return null
    }
    val child = dbQuery {
        ChildrenTable.selectAll()
            .where {
                (ChildrenTable.id eq cid) and
                (ChildrenTable.parentId eq parentId) and
                (ChildrenTable.isActive eq true)
            }
            .firstOrNull()
    } ?: run {
        fail("Child not found", HttpStatusCode.NotFound); return null
    }
    return child
}

/**
 * Find the enrolled student row for this child in the homework's class+section.
 */
private suspend fun studentFor(child: org.jetbrains.exposed.sql.ResultRow, homeworkRow: org.jetbrains.exposed.sql.ResultRow): org.jetbrains.exposed.sql.ResultRow? = dbQuery {
    val studentCode = child[ChildrenTable.studentCode]
    if (!studentCode.isNullOrBlank()) {
        StudentsTable.selectAll()
            .where {
                (StudentsTable.studentCode eq studentCode) and
                (StudentsTable.schoolId eq homeworkRow[HomeworkTable.schoolId]) and
                (StudentsTable.className eq homeworkRow[HomeworkTable.className]) and
                (StudentsTable.section eq homeworkRow[HomeworkTable.section]) and
                (StudentsTable.isActive eq true)
            }
            .firstOrNull()
    } else null
}

fun Route.parentHomeworkRouting() {
    authenticate("jwt") {
        route("/api/v1/parent/child/{childId}/homework") {

            // ── LIST active homework for this child ─────────────────────────────
            get {
                val parentId = call.principalUserId()?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid token", HttpStatusCode.Unauthorized); return@get }
                val childId = call.parameters["childId"] ?: run {
                    call.fail("childId is required", HttpStatusCode.BadRequest); return@get
                }
                val child = call.requireOwnedChild(parentId, childId) ?: return@get
                val studentCode = child[ChildrenTable.studentCode] ?: ""
                val studentUuid = child[ChildrenTable.id].value

                val items = dbQuery {
                    val schoolId = child[ChildrenTable.schoolId]
                    val currentGrade = child[ChildrenTable.currentGrade] ?: ""
                    val homeworks = HomeworkTable.selectAll()
                        .where {
                            (HomeworkTable.className eq currentGrade) and
                            (if (schoolId != null) HomeworkTable.schoolId eq schoolId else Op.TRUE) and
                            (HomeworkTable.isActive eq true)
                        }
                        .orderBy(HomeworkTable.dueDate, SortOrder.ASC)
                        .toList()

                    val hwIds = homeworks.map { it[HomeworkTable.id].value }

                    val submissions = HomeworkSubmissionsTable.selectAll()
                        .where {
                            (HomeworkSubmissionsTable.homeworkId inList hwIds) and
                            (
                                (HomeworkSubmissionsTable.studentUuid eq studentUuid) or
                                (HomeworkSubmissionsTable.studentId eq studentCode)
                            )
                        }
                        .toList()
                        .associateBy { it[HomeworkSubmissionsTable.homeworkId] }

                    val submissionIds = submissions.values.map { it[HomeworkSubmissionsTable.id].value }
                    val attachments = HomeworkSubmissionAttachmentsTable.selectAll()
                        .where { HomeworkSubmissionAttachmentsTable.submissionId inList submissionIds }
                        .map { att ->
                            att[HomeworkSubmissionAttachmentsTable.submissionId] to ParentHomeworkAttachmentDto(
                                id = att[HomeworkSubmissionAttachmentsTable.id].value.toString(),
                                url = att[HomeworkSubmissionAttachmentsTable.url],
                                filename = att[HomeworkSubmissionAttachmentsTable.filename],
                                mime = att[HomeworkSubmissionAttachmentsTable.mime],
                                sizeBytes = att[HomeworkSubmissionAttachmentsTable.sizeBytes],
                            )
                        }
                        .groupBy({ it.first }, { it.second })

                    val extensions = HomeworkExtensionsTable.selectAll()
                        .where { HomeworkExtensionsTable.homeworkId inList hwIds }
                        .toList()

                    homeworks.map { hw ->
                        val hwId = hw[HomeworkTable.id].value
                        val sub = submissions[hwId]
                        val status = sub?.get(HomeworkSubmissionsTable.status)?.takeIf { it.isNotBlank() } ?: "not_submitted"
                        val dueDate = hw[HomeworkTable.dueDate]
                        val dueTime = hw[HomeworkTable.dueTime]

                        val perStudentExt = extensions.firstOrNull {
                            it[HomeworkExtensionsTable.homeworkId] == hwId &&
                            it[HomeworkExtensionsTable.studentId] == studentUuid
                        }?.get(HomeworkExtensionsTable.newDueDate)
                        val classExt = extensions.firstOrNull {
                            it[HomeworkExtensionsTable.homeworkId] == hwId &&
                            it[HomeworkExtensionsTable.studentId] == null
                        }?.get(HomeworkExtensionsTable.newDueDate)
                        val ext = perStudentExt ?: classExt

                        ParentHomeworkItemDto(
                            id = hwId.toString(),
                            title = hw[HomeworkTable.title],
                            description = hw[HomeworkTable.description],
                            subject = hw[HomeworkTable.subject],
                            dueDate = dueDate.toString(),
                            dueTime = dueTime?.toString(),
                            allowLate = hw[HomeworkTable.allowLate],
                            isActive = hw[HomeworkTable.isActive],
                            isPastDue = isPastDue(dueDate, dueTime, ext, null),
                            status = status,
                            submittedAt = sub?.get(HomeworkSubmissionsTable.submittedAt)?.toString(),
                            submissionText = sub?.get(HomeworkSubmissionsTable.submissionText) ?: "",
                            attachments = sub?.let { s -> attachments[s[HomeworkSubmissionsTable.id].value] } ?: emptyList(),
                            hasExtension = ext != null,
                            extendedTo = ext?.toString(),
                        )
                    }
                }

                call.ok(ParentHomeworkListData(items), message = "Homework loaded")
            }

            // ── DETAIL one homework + existing submission ─────────────────────
            get("/{homeworkId}") {
                val parentId = call.principalUserId()?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid token", HttpStatusCode.Unauthorized); return@get }
                val childId = call.parameters["childId"] ?: run {
                    call.fail("childId is required", HttpStatusCode.BadRequest); return@get
                }
                val homeworkId = call.parameters["homeworkId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid homework id", HttpStatusCode.BadRequest); return@get }

                val child = call.requireOwnedChild(parentId, childId) ?: return@get
                val studentCode = child[ChildrenTable.studentCode] ?: ""
                val studentUuid = child[ChildrenTable.id].value

                val dto = dbQuery {
                    val hw = HomeworkTable.selectAll()
                        .where { HomeworkTable.id eq homeworkId }
                        .firstOrNull() ?: return@dbQuery null

                    val sub = HomeworkSubmissionsTable.selectAll()
                        .where {
                            (HomeworkSubmissionsTable.homeworkId eq homeworkId) and
                            (
                                (HomeworkSubmissionsTable.studentUuid eq studentUuid) or
                                (HomeworkSubmissionsTable.studentId eq studentCode)
                            )
                        }
                        .firstOrNull()

                    val attachments = sub?.let { s ->
                        HomeworkSubmissionAttachmentsTable.selectAll()
                            .where { HomeworkSubmissionAttachmentsTable.submissionId eq s[HomeworkSubmissionsTable.id].value }
                            .map { att ->
                                ParentHomeworkAttachmentDto(
                                    id = att[HomeworkSubmissionAttachmentsTable.id].value.toString(),
                                    url = att[HomeworkSubmissionAttachmentsTable.url],
                                    filename = att[HomeworkSubmissionAttachmentsTable.filename],
                                    mime = att[HomeworkSubmissionAttachmentsTable.mime],
                                    sizeBytes = att[HomeworkSubmissionAttachmentsTable.sizeBytes],
                                )
                            }
                    } ?: emptyList()

                    val extensions = HomeworkExtensionsTable.selectAll()
                        .where { HomeworkExtensionsTable.homeworkId eq homeworkId }
                        .toList()
                    val perStudentExt = extensions.firstOrNull { it[HomeworkExtensionsTable.studentId] == studentUuid }?.get(HomeworkExtensionsTable.newDueDate)
                    val classExt = extensions.firstOrNull { it[HomeworkExtensionsTable.studentId] == null }?.get(HomeworkExtensionsTable.newDueDate)
                    val ext = perStudentExt ?: classExt

                    val status = sub?.get(HomeworkSubmissionsTable.status)?.takeIf { it.isNotBlank() } ?: "not_submitted"
                    val dueDate = hw[HomeworkTable.dueDate]
                    val dueTime = hw[HomeworkTable.dueTime]

                    ParentHomeworkItemDto(
                        id = hw[HomeworkTable.id].value.toString(),
                        title = hw[HomeworkTable.title],
                        description = hw[HomeworkTable.description],
                        subject = hw[HomeworkTable.subject],
                        dueDate = dueDate.toString(),
                        dueTime = dueTime?.toString(),
                        allowLate = hw[HomeworkTable.allowLate],
                        isActive = hw[HomeworkTable.isActive],
                        isPastDue = isPastDue(dueDate, dueTime, ext, null),
                        status = status,
                        submittedAt = sub?.get(HomeworkSubmissionsTable.submittedAt)?.toString(),
                        submissionText = sub?.get(HomeworkSubmissionsTable.submissionText) ?: "",
                        attachments = attachments,
                        hasExtension = ext != null,
                        extendedTo = ext?.toString(),
                    )
                }

                if (dto == null) {
                    call.fail("Homework not found", HttpStatusCode.NotFound); return@get
                }
                call.ok(ParentHomeworkDetailData(dto), message = "Homework detail loaded")
            }

            // ── SUBMIT homework (text + photo attachments) ──────────────────────
            post("/{homeworkId}/submit") {
                val parentId = call.principalUserId()?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid token", HttpStatusCode.Unauthorized); return@post }
                val childId = call.parameters["childId"] ?: run {
                    call.fail("childId is required", HttpStatusCode.BadRequest); return@post
                }
                val homeworkId = call.parameters["homeworkId"]?.let {
                    runCatching { UUID.fromString(it) }.getOrNull()
                } ?: run { call.fail("Invalid homework id", HttpStatusCode.BadRequest); return@post }

                val child = call.requireOwnedChild(parentId, childId) ?: return@post
                val req = runCatching { call.receive<ParentSubmitHomeworkRequest>() }.getOrNull() ?: run {
                    call.fail("Invalid request body", HttpStatusCode.BadRequest); return@post
                }

                val homework = dbQuery {
                    HomeworkTable.selectAll()
                        .where { HomeworkTable.id eq homeworkId }
                        .firstOrNull()
                } ?: run { call.fail("Homework not found", HttpStatusCode.NotFound); return@post }

                // Find the student row to backfill student_code if missing.
                val studentRow = studentFor(child, homework)
                val studentCode = child[ChildrenTable.studentCode]
                    ?: studentRow?.get(StudentsTable.studentCode)
                    ?: ""
                val studentUuid = child[ChildrenTable.id].value

                // Enforce due date unless allow_late or extension.
                val dueDate = homework[HomeworkTable.dueDate]
                val dueTime = homework[HomeworkTable.dueTime]
                val allowLate = homework[HomeworkTable.allowLate]
                val extensions = dbQuery {
                    HomeworkExtensionsTable.selectAll()
                        .where { HomeworkExtensionsTable.homeworkId eq homeworkId }
                        .toList()
                }
                val perStudentExt = extensions.firstOrNull { it[HomeworkExtensionsTable.studentId] == studentUuid }?.get(HomeworkExtensionsTable.newDueDate)
                val classExt = extensions.firstOrNull { it[HomeworkExtensionsTable.studentId] == null }?.get(HomeworkExtensionsTable.newDueDate)
                val ext = perStudentExt ?: classExt

                if (!allowLate && ext == null && isPastDue(dueDate, dueTime, null, null)) {
                    call.fail("Submission deadline has passed", HttpStatusCode.Conflict, "PAST_DUE")
                    return@post
                }

                val now = Instant.now()
                val isLate = isPastDue(dueDate, dueTime, ext, null)
                val status = if (isLate) "late" else "submitted"

                dbQuery {
                    val existing = HomeworkSubmissionsTable.selectAll()
                        .where {
                            (HomeworkSubmissionsTable.homeworkId eq homeworkId) and
                            (
                                (HomeworkSubmissionsTable.studentUuid eq studentUuid) or
                                (HomeworkSubmissionsTable.studentId eq studentCode)
                            )
                        }
                        .firstOrNull()

                    val submissionId = if (existing != null) {
                        HomeworkSubmissionsTable.update({ HomeworkSubmissionsTable.id eq existing[HomeworkSubmissionsTable.id] }) {
                            it[HomeworkSubmissionsTable.status] = status
                            it[submittedAt] = now
                            it[submissionText] = req.text.trim()
                            it[HomeworkSubmissionsTable.studentUuid] = studentUuid
                        }
                        existing[HomeworkSubmissionsTable.id].value
                    } else {
                        val newId = UUID.randomUUID()
                        HomeworkSubmissionsTable.insert {
                            it[id] = newId
                            it[HomeworkSubmissionsTable.homeworkId] = homeworkId
                            it[HomeworkSubmissionsTable.studentId] = studentCode
                            it[HomeworkSubmissionsTable.studentUuid] = studentUuid
                            it[HomeworkSubmissionsTable.status] = status
                            it[submittedAt] = now
                            it[submissionText] = req.text.trim()
                        }
                        newId
                    }

                    // Attachments by reference (parent already uploaded to storage).
                    req.attachments.filter { it.url.isNotBlank() }.forEach { att ->
                        HomeworkSubmissionAttachmentsTable.insert {
                            it[id] = UUID.randomUUID()
                            it[HomeworkSubmissionAttachmentsTable.submissionId] = submissionId
                            it[url] = att.url
                            it[filename] = att.filename
                            it[mime] = att.mime
                            it[sizeBytes] = att.sizeBytes
                            it[uploadedBy] = parentId
                            it[createdAt] = now
                        }
                    }
                }

                call.ok(ParentHomeworkMutationData(success = true, message = "Homework submitted"), message = "Homework submitted")
            }
        }
    }
}
