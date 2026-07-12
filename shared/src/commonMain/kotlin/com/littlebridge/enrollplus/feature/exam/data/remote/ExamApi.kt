package com.littlebridge.enrollplus.feature.exam.data.remote

import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
import com.littlebridge.enrollplus.feature.exam.domain.model.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ExamApi(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    private fun getUrl(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    // ── Timetable ────────────────────────────────────────────────────────────

    suspend fun importOcr(
        token: String,
        image: String,
        mimeType: String,
        className: String,
        section: String,
    ): NetworkResult<ExamOcrEnvelope> = safeApiCall {
        client.post(getUrl("api/v1/exam/timetable/import-ocr")) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(ExamTimetableOcrRequest(image = image, mimeType = mimeType, className = className, section = section))
        }
    }

    suspend fun importText(
        token: String,
        text: String,
        className: String,
        section: String,
    ): NetworkResult<ExamOcrEnvelope> = safeApiCall {
        client.post(getUrl("api/v1/exam/timetable/import-text")) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(ExamTimetableTextRequest(text = text, className = className, section = section))
        }
    }

    suspend fun createTimetable(
        token: String,
        request: ExamTimetableCreateRequest,
    ): NetworkResult<ExamTimetableEnvelope> = safeApiCall {
        client.post(getUrl("api/v1/exam/timetable")) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun publishTimetable(
        token: String,
        timetableId: String,
    ): NetworkResult<ApiResponseUnit> = safeApiCall {
        client.post(getUrl("api/v1/exam/timetable/$timetableId/publish")) {
            header("Authorization", "Bearer $token")
        }
    }

    suspend fun listTimetables(
        token: String,
        className: String? = null,
        status: String? = null,
    ): NetworkResult<ExamTimetableListEnvelope> = safeApiCall {
        client.get(getUrl("api/v1/exam/timetable")) {
            header("Authorization", "Bearer $token")
            className?.let { parameter("class_name", it) }
            status?.let { parameter("status", it) }
        }
    }

    suspend fun getTimetable(
        token: String,
        timetableId: String,
    ): NetworkResult<ExamTimetableEnvelope> = safeApiCall {
        client.get(getUrl("api/v1/exam/timetable/$timetableId")) {
            header("Authorization", "Bearer $token")
        }
    }

    // ── Syllabus mapping ─────────────────────────────────────────────────────

    suspend fun getExamSyllabus(
        token: String,
        assessmentId: String,
    ): NetworkResult<ExamSyllabusEnvelope> = safeApiCall {
        client.get(getUrl("api/v1/exam/syllabus/$assessmentId")) {
            header("Authorization", "Bearer $token")
        }
    }

    suspend fun updateExamSyllabus(
        token: String,
        assessmentId: String,
        unitIds: List<String>,
    ): NetworkResult<ApiResponseUnit> = safeApiCall {
        client.put(getUrl("api/v1/exam/syllabus/$assessmentId")) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(ExamSyllabusUpdateRequest(unitIds = unitIds))
        }
    }

    // ── Parent: read exam syllabus ───────────────────────────────────────────

    suspend fun getParentExamSyllabus(
        token: String,
        childId: String,
        assessmentId: String,
    ): NetworkResult<ExamSyllabusEnvelope> = safeApiCall {
        client.get(getUrl("api/v1/exam/parent/$childId/syllabus/$assessmentId")) {
            header("Authorization", "Bearer $token")
        }
    }

    // ── Parent: request syllabus from teacher ────────────────────────────────

    suspend fun requestSyllabus(
        token: String,
        assessmentId: String,
        message: String = "",
    ): NetworkResult<ExamSyllabusRequestEnvelope> = safeApiCall {
        client.post(getUrl("api/v1/exam/request-syllabus")) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(ExamSyllabusRequestDto(assessmentId = assessmentId, message = message))
        }
    }
}

// Lightweight wrapper for endpoints that return only { success, message }
@kotlinx.serialization.Serializable
data class ApiResponseUnit(
    val success: Boolean = true,
    val message: String = "",
)
