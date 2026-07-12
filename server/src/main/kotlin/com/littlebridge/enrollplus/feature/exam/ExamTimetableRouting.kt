/*
 * File: ExamTimetableRouting.kt
 * Module: feature.exam
 *
 * Exam Timetable lifecycle: AI OCR import, text import, manual create,
 * publish (creates calendar events + draft assessments), list, detail.
 *
 * Endpoints (JWT, teacher/admin):
 *   POST /api/v1/exam/timetable/import-ocr    — AI vision extraction from image
 *   POST /api/v1/exam/timetable/import-text   — parse pasted text via AI
 *   POST /api/v1/exam/timetable               — create timetable with entries
 *   POST /api/v1/exam/timetable/{id}/publish  — publish: create calendar events + assessments
 *   GET  /api/v1/exam/timetable               — list timetables for school
 *   GET  /api/v1/exam/timetable/{id}          — timetable detail with entries
 */
package com.littlebridge.enrollplus.feature.exam

import com.littlebridge.enrollplus.core.created
import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.okMessage
import com.littlebridge.enrollplus.core.requireTeacherContext
import com.littlebridge.enrollplus.db.AssessmentsTable
import com.littlebridge.enrollplus.db.CalendarEventsTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.ExamTimetableEntriesTable
import com.littlebridge.enrollplus.db.ExamTimetablesTable
import com.littlebridge.enrollplus.feature.ai.AiLane
import com.littlebridge.enrollplus.feature.ai.AiService
import com.littlebridge.enrollplus.feature.ai.LlmMessage
import com.littlebridge.enrollplus.feature.calendar.EventAudience
import com.littlebridge.enrollplus.feature.calendar.EventSource
import com.littlebridge.enrollplus.feature.calendar.EventStatus
import com.littlebridge.enrollplus.feature.calendar.EventType
import com.littlebridge.enrollplus.feature.calendar.activeAcademicYearId
import com.littlebridge.enrollplus.feature.calendar.createCalendarEvent
import com.littlebridge.enrollplus.feature.notifications.Notify
import com.littlebridge.enrollplus.feature.notifications.NotifyRecipients
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

// ── DTOs ─────────────────────────────────────────────────────────────────────

@Serializable
data class ExamTimetableEntryDto(
    val id: String? = null,
    @SerialName("exam_date") val examDate: String,
    @SerialName("start_time") val startTime: String? = null,
    @SerialName("end_time") val endTime: String? = null,
    val subject: String,
    @SerialName("exam_name") val examName: String,
    @SerialName("max_marks") val maxMarks: Int = 100,
    val room: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
    @SerialName("assessment_id") val assessmentId: String? = null,
    @SerialName("calendar_event_id") val calendarEventId: String? = null,
)

@Serializable
data class ExamTimetableDto(
    val id: String,
    @SerialName("school_id") val schoolId: String,
    @SerialName("teacher_id") val teacherId: String,
    @SerialName("class_name") val className: String,
    val section: String,
    val name: String,
    val term: String? = null,
    val status: String,
    @SerialName("ai_used") val aiUsed: Boolean,
    @SerialName("source_image_url") val sourceImageUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val entries: List<ExamTimetableEntryDto> = emptyList(),
)

@Serializable
data class ExamTimetableListResponse(
    val timetables: List<ExamTimetableDto>,
)

@Serializable
data class ExamTimetableCreateRequest(
    @SerialName("class_name") val className: String,
    val section: String = "A",
    val name: String,
    val term: String? = null,
    val entries: List<ExamTimetableEntryDto>,
)

@Serializable
data class ExamTimetableOcrRequest(
    val image: String,
    @SerialName("mime_type") val mimeType: String = "image/jpeg",
    @SerialName("class_name") val className: String,
    val section: String = "A",
)

@Serializable
data class ExamTimetableTextRequest(
    val text: String,
    @SerialName("class_name") val className: String,
    val section: String = "A",
)

@Serializable
data class ExamTimetableOcrResponse(
    val entries: List<ExamTimetableEntryDto>,
    @SerialName("raw_ai_output") val rawAiOutput: String? = null,
)

// ── AI System Prompt for exam timetable OCR ──────────────────────────────────

private const val EXAM_OCR_SYSTEM_PROMPT = """
You are an exam timetable parser. Extract exam schedule entries from the provided image or text.

Return ONLY a JSON array (no markdown fences, no commentary). Each element must have:
  "exam_date":   "YYYY-MM-DD"
  "start_time":  "HH:mm" or null
  "end_time":    "HH:mm" or null
  "subject":     subject name (e.g. "Mathematics")
  "exam_name":   exam title (e.g. "Mid Term Exam", "Unit Test 1")
  "max_marks":   integer (default 100)
  "room":        exam room/hall or null

If the year is not visible, assume the current year. Sort by date then time.
Multiple exams on the same day are allowed — produce one entry per exam.
"""

// ── Helpers ──────────────────────────────────────────────────────────────────

private fun parseExamEntriesFromAi(raw: String): List<ExamTimetableEntryDto> {
    val jsonStr = raw.substringAfter("[").substringBeforeLast("]")
    if (jsonStr.isBlank()) return emptyList()
    val items = jsonStr.split("}").filter { it.contains("{") }
    return items.mapIndexed { idx, item ->
        val body = item.substringAfter("{")
        fun field(name: String): String? =
            Regex("\"$name\"\\s*:\\s*\"?([^\",}]*)\"?").find(body)?.groupValues?.get(1)?.trim()?.takeIf { it.isNotBlank() && it != "null" }
        ExamTimetableEntryDto(
            examDate = field("exam_date") ?: "",
            startTime = field("start_time"),
            endTime = field("end_time"),
            subject = field("subject") ?: "",
            examName = field("exam_name") ?: "",
            maxMarks = field("max_marks")?.toIntOrNull() ?: 100,
            room = field("room"),
            sortOrder = idx,
        )
    }.filter { it.examDate.isNotBlank() && it.subject.isNotBlank() }
}

private fun rowToTimetableDto(
    row: org.jetbrains.exposed.sql.ResultRow,
    entries: List<ExamTimetableEntryDto>,
): ExamTimetableDto = ExamTimetableDto(
    id = row[ExamTimetablesTable.id].value.toString(),
    schoolId = row[ExamTimetablesTable.schoolId].toString(),
    teacherId = row[ExamTimetablesTable.teacherId].toString(),
    className = row[ExamTimetablesTable.className],
    section = row[ExamTimetablesTable.section],
    name = row[ExamTimetablesTable.name],
    term = row[ExamTimetablesTable.term],
    status = row[ExamTimetablesTable.status],
    aiUsed = row[ExamTimetablesTable.aiUsed],
    sourceImageUrl = row[ExamTimetablesTable.sourceImageUrl],
    createdAt = row[ExamTimetablesTable.createdAt].toString(),
    updatedAt = row[ExamTimetablesTable.updatedAt].toString(),
    entries = entries,
)

private fun rowToEntryDto(row: org.jetbrains.exposed.sql.ResultRow): ExamTimetableEntryDto = ExamTimetableEntryDto(
    id = row[ExamTimetableEntriesTable.id].value.toString(),
    examDate = row[ExamTimetableEntriesTable.examDate].toString(),
    startTime = row[ExamTimetableEntriesTable.startTime]?.toString(),
    endTime = row[ExamTimetableEntriesTable.endTime]?.toString(),
    subject = row[ExamTimetableEntriesTable.subject],
    examName = row[ExamTimetableEntriesTable.examName],
    maxMarks = row[ExamTimetableEntriesTable.maxMarks],
    room = row[ExamTimetableEntriesTable.room],
    sortOrder = row[ExamTimetableEntriesTable.sortOrder],
    assessmentId = row[ExamTimetableEntriesTable.assessmentId]?.toString(),
    calendarEventId = row[ExamTimetableEntriesTable.calendarEventId]?.toString(),
)

// ── Routing ──────────────────────────────────────────────────────────────────

fun Route.examTimetableRouting() {
    authenticate("jwt") {
        route("/api/v1/exam/timetable") {

            // ── POST /import-ocr — AI vision extraction from image ──────────
            post("/import-ocr") {
                val ctx = call.requireTeacherContext() ?: return@post
                val req = call.receive<ExamTimetableOcrRequest>()

                if (req.image.length > 5_000_000) {
                    call.fail("Image too large (max 5MB base64)", HttpStatusCode.BadRequest)
                    return@post
                }

                val aiResult = AiService.completeWithVision(
                    feature = "exam_timetable_ocr",
                    systemPrompt = EXAM_OCR_SYSTEM_PROMPT,
                    userText = "Extract the exam timetable from this image.",
                    imageBase64 = req.image,
                    imageMimeType = req.mimeType,
                    schoolId = ctx.schoolId,
                    temperature = 0.2,
                    maxTokens = 2048,
                )

                val rawText = aiResult.content ?: ""
                val entries = parseExamEntriesFromAi(rawText)
                call.ok(
                    ExamTimetableOcrResponse(entries = entries, rawAiOutput = rawText),
                    message = "OCR extraction complete (${entries.size} entries)",
                )
            }

            // ── POST /import-text — parse pasted text via AI ─────────────────
            post("/import-text") {
                val ctx = call.requireTeacherContext() ?: return@post
                val req = call.receive<ExamTimetableTextRequest>()

                if (req.text.length > 20_000) {
                    call.fail("Text too long (max 20K chars)", HttpStatusCode.BadRequest)
                    return@post
                }

                val aiResult = AiService.complete(
                    feature = "exam_timetable_text",
                    lane = AiLane.REASON,
                    messages = listOf(
                        LlmMessage("system", EXAM_OCR_SYSTEM_PROMPT),
                        LlmMessage("user", req.text),
                    ),
                    schoolId = ctx.schoolId,
                    temperature = 0.2,
                    maxTokens = 2048,
                )

                val rawText = aiResult.content ?: ""
                val entries = parseExamEntriesFromAi(rawText)
                call.ok(
                    ExamTimetableOcrResponse(entries = entries, rawAiOutput = rawText),
                    message = "Text parsing complete (${entries.size} entries)",
                )
            }

            // ── POST / — create timetable with entries (draft) ───────────────
            post {
                val ctx = call.requireTeacherContext() ?: return@post
                val req = call.receive<ExamTimetableCreateRequest>()

                if (req.name.isBlank()) {
                    call.fail("Timetable name is required", HttpStatusCode.BadRequest)
                    return@post
                }
                if (req.entries.isEmpty()) {
                    call.fail("At least one exam entry is required", HttpStatusCode.BadRequest)
                    return@post
                }

                val now = Instant.now()
                val ttId = UUID.randomUUID()
                val yearId = dbQuery { activeAcademicYearId(ctx.schoolId) }

                dbQuery {
                    ExamTimetablesTable.insert {
                        it[ExamTimetablesTable.id] = ttId
                        it[schoolId] = ctx.schoolId
                        it[teacherId] = ctx.userId
                        it[className] = req.className
                        it[section] = req.section
                        it[academicYearId] = yearId
                        it[name] = req.name
                        it[term] = req.term
                        it[status] = "draft"
                        it[aiUsed] = false
                        it[createdAt] = now
                        it[updatedAt] = now
                    }

                    req.entries.forEachIndexed { idx, entry ->
                        ExamTimetableEntriesTable.insert {
                            it[timetableId] = ttId
                            it[schoolId] = ctx.schoolId
                            it[examDate] = LocalDate.parse(entry.examDate)
                            it[startTime] = entry.startTime?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
                            it[endTime] = entry.endTime?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
                            it[subject] = entry.subject
                            it[examName] = entry.examName
                            it[maxMarks] = entry.maxMarks
                            it[room] = entry.room
                            it[sortOrder] = entry.sortOrder.takeIf { it > 0 } ?: idx
                            it[createdAt] = now
                        }
                    }
                }

                val dto = dbQuery {
                    val ttRow = ExamTimetablesTable.selectAll()
                        .where { ExamTimetablesTable.id eq ttId }
                        .single()
                    val entryRows = ExamTimetableEntriesTable.selectAll()
                        .where { ExamTimetableEntriesTable.timetableId eq ttId }
                        .orderBy(ExamTimetableEntriesTable.sortOrder to SortOrder.ASC)
                        .map(::rowToEntryDto)
                    rowToTimetableDto(ttRow, entryRows)
                }

                call.created(dto, message = "Exam timetable created (draft)")
            }

            // ── POST /{id}/publish — publish: create calendar events + assessments ─
            post("/{id}/publish") {
                val ctx = call.requireTeacherContext() ?: return@post
                val ttId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: run {
                    call.fail("Invalid timetable id", HttpStatusCode.BadRequest)
                    return@post
                }

                val ttRow = dbQuery {
                    ExamTimetablesTable.selectAll()
                        .where {
                            (ExamTimetablesTable.id eq ttId) and
                                (ExamTimetablesTable.schoolId eq ctx.schoolId)
                        }
                        .singleOrNull()
                } ?: run {
                    call.fail("Timetable not found", HttpStatusCode.NotFound)
                    return@post
                }

                if (ttRow[ExamTimetablesTable.status] == "published") {
                    call.fail("Timetable already published", HttpStatusCode.BadRequest)
                    return@post
                }

                val entries = dbQuery {
                    ExamTimetableEntriesTable.selectAll()
                        .where { ExamTimetableEntriesTable.timetableId eq ttId }
                        .orderBy(ExamTimetableEntriesTable.sortOrder to SortOrder.ASC)
                        .toList()
                }

                val className = ttRow[ExamTimetablesTable.className]
                val section = ttRow[ExamTimetablesTable.section]
                val ttName = ttRow[ExamTimetablesTable.name]
                val now = Instant.now()

                // For each entry: create a calendar EXAM event + a draft assessment
                entries.forEach { entryRow ->
                    val entryId = entryRow[ExamTimetableEntriesTable.id].value
                    val examDate = entryRow[ExamTimetableEntriesTable.examDate]
                    val subject = entryRow[ExamTimetableEntriesTable.subject]
                    val examName = entryRow[ExamTimetableEntriesTable.examName]
                    val maxMarks = entryRow[ExamTimetableEntriesTable.maxMarks]

                    // Create calendar event
                    val eventCode = createCalendarEvent(
                        schoolId = ctx.schoolId,
                        title = "$examName — $subject",
                        description = "Exam: $examName, Subject: $subject, Class: $className-$section, Max Marks: $maxMarks",
                        type = EventType.EXAM,
                        status = EventStatus.PUBLISHED,
                        source = EventSource.MANUAL,
                        startDate = examDate.toString(),
                        endDate = examDate.toString(),
                        allDay = true,
                        audience = EventAudience.ALL_SCHOOL,
                        notifyParents = true,
                        notifyTeachers = true,
                        createdBy = ctx.userId,
                    )

                    // Resolve calendar event UUID
                    val calEventId = dbQuery {
                        CalendarEventsTable.selectAll()
                            .where { CalendarEventsTable.eventCode eq eventCode }
                            .singleOrNull()
                            ?.get(CalendarEventsTable.id)
                            ?.value
                    }

                    // Create draft assessment
                    val assessmentId = UUID.randomUUID()
                    dbQuery {
                        AssessmentsTable.insert {
                            it[AssessmentsTable.id] = assessmentId
                            it[AssessmentsTable.schoolId] = ctx.schoolId
                            it[AssessmentsTable.teacherId] = ctx.userId
                            it[AssessmentsTable.className] = className
                            it[AssessmentsTable.section] = section
                            it[AssessmentsTable.subject] = subject
                            it[AssessmentsTable.name] = examName
                            it[AssessmentsTable.maxMarks] = maxMarks
                            it[AssessmentsTable.examDate] = examDate
                            it[AssessmentsTable.type] = "exam"
                            it[AssessmentsTable.status] = "scheduled"
                            it[AssessmentsTable.isActive] = true
                            it[AssessmentsTable.isPublished] = false
                            it[AssessmentsTable.calendarEventId] = calEventId
                            it[AssessmentsTable.createdAt] = now
                            it[AssessmentsTable.updatedAt] = now
                        }
                    }

                    // Link entry to assessment + calendar event
                    dbQuery {
                        ExamTimetableEntriesTable.update({ ExamTimetableEntriesTable.id eq entryId }) {
                            it[ExamTimetableEntriesTable.assessmentId] = assessmentId
                            it[ExamTimetableEntriesTable.calendarEventId] = calEventId
                        }
                    }
                }

                // Mark timetable as published
                dbQuery {
                    ExamTimetablesTable.update({ ExamTimetablesTable.id eq ttId }) {
                        it[status] = "published"
                        it[updatedAt] = now
                    }
                }

                // Notify parents
                val parentIds = NotifyRecipients.parentsOfClass(ctx.schoolId, className)
                if (parentIds.isNotEmpty()) {
                    Notify.toUsers(
                        userIds = parentIds,
                        category = "exam",
                        title = "Exam Timetable Published: $ttName",
                        body = "$ttName for $className-$section has been published. Tap to view the schedule.",
                        schoolId = ctx.schoolId,
                        deepLink = "/parent/academics?tab=exams",
                        refType = "exam_timetable",
                        refId = ttId.toString(),
                    )
                }

                call.okMessage("Timetable published — ${entries.size} exams scheduled")
            }

            // ── GET / — list timetables for school ───────────────────────────
            get {
                val ctx = call.requireTeacherContext() ?: return@get
                val className = call.request.queryParameters["class_name"]
                val status = call.request.queryParameters["status"]

                val timetables = dbQuery {
                    val query = ExamTimetablesTable.selectAll()
                        .where { ExamTimetablesTable.schoolId eq ctx.schoolId }

                    val filtered = if (className != null) {
                        query.andWhere { ExamTimetablesTable.className eq className }
                    } else {
                        query
                    }

                    val statusFiltered = if (status != null) {
                        filtered.andWhere { ExamTimetablesTable.status eq status }
                    } else {
                        filtered
                    }

                    statusFiltered.orderBy(ExamTimetablesTable.createdAt to SortOrder.DESC)
                        .map { rowToTimetableDto(it, emptyList()) }
                }

                call.ok(ExamTimetableListResponse(timetables = timetables), message = "Timetables loaded")
            }

            // ── GET /{id} — timetable detail with entries ────────────────────
            get("/{id}") {
                val ctx = call.requireTeacherContext() ?: return@get
                val ttId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: run {
                    call.fail("Invalid timetable id", HttpStatusCode.BadRequest)
                    return@get
                }

                val dto = dbQuery {
                    val ttRow = ExamTimetablesTable.selectAll()
                        .where {
                            (ExamTimetablesTable.id eq ttId) and
                                (ExamTimetablesTable.schoolId eq ctx.schoolId)
                        }
                        .singleOrNull() ?: return@dbQuery null

                    val entries = ExamTimetableEntriesTable.selectAll()
                        .where { ExamTimetableEntriesTable.timetableId eq ttId }
                        .orderBy(ExamTimetableEntriesTable.sortOrder to SortOrder.ASC)
                        .map(::rowToEntryDto)

                    rowToTimetableDto(ttRow, entries)
                } ?: run {
                    call.fail("Timetable not found", HttpStatusCode.NotFound)
                    return@get
                }

                call.ok(dto, message = "Timetable loaded")
            }
        }
    }
}
