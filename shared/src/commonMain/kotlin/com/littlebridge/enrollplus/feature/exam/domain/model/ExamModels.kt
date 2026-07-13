package com.littlebridge.enrollplus.feature.exam.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExamTimetableEntry(
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
data class ExamTimetable(
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
    val entries: List<ExamTimetableEntry> = emptyList(),
)

@Serializable
data class ExamTimetableListResponse(
    val timetables: List<ExamTimetable> = emptyList(),
)

@Serializable
data class ExamTimetableListEnvelope(
    val success: Boolean,
    val message: String = "",
    val data: ExamTimetableListResponse,
)

@Serializable
data class ExamTimetableCreateRequest(
    @SerialName("class_name") val className: String,
    val section: String = "A",
    val name: String,
    val term: String? = null,
    val entries: List<ExamTimetableEntry>,
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
    val entries: List<ExamTimetableEntry> = emptyList(),
    @SerialName("raw_ai_output") val rawAiOutput: String? = null,
)

@Serializable
data class ExamOcrEnvelope(
    val success: Boolean,
    val message: String = "",
    val data: ExamTimetableOcrResponse,
)

@Serializable
data class ExamTimetableEnvelope(
    val success: Boolean,
    val message: String = "",
    val data: ExamTimetable,
)

@Serializable
data class CurriculumUnitDto(
    val id: String,
    val title: String,
    @SerialName("parent_id") val parentId: String? = null,
    val depth: Int = 0,
    val position: Int = 0,
    @SerialName("is_mapped") val isMapped: Boolean = false,
)

@Serializable
data class ExamSyllabusResponse(
    @SerialName("assessment_id") val assessmentId: String,
    @SerialName("exam_name") val examName: String,
    val subject: String,
    @SerialName("class_name") val className: String,
    val section: String,
    val units: List<CurriculumUnitDto> = emptyList(),
)

@Serializable
data class ExamSyllabusEnvelope(
    val success: Boolean,
    val message: String = "",
    val data: ExamSyllabusResponse,
)

@Serializable
data class ExamSyllabusUpdateRequest(
    @SerialName("unit_ids") val unitIds: List<String>,
)

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

@Serializable
data class ExamSyllabusRequestEnvelope(
    val success: Boolean,
    val message: String = "",
    val data: ExamSyllabusRequestResponse,
)
