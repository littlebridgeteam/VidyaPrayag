package com.littlebridge.enrollplus.feature.exam.data.repository

import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.exam.data.remote.ExamApi
import com.littlebridge.enrollplus.feature.exam.domain.model.*

class ExamRepository(
    private val api: ExamApi,
) {
    // ── Timetable ────────────────────────────────────────────────────────────

    suspend fun importOcr(
        token: String,
        image: String,
        mimeType: String,
        className: String,
        section: String,
    ): NetworkResult<ExamOcrEnvelope> =
        api.importOcr(token, image, mimeType, className, section)

    suspend fun importText(
        token: String,
        text: String,
        className: String,
        section: String,
    ): NetworkResult<ExamOcrEnvelope> =
        api.importText(token, text, className, section)

    suspend fun createTimetable(
        token: String,
        request: ExamTimetableCreateRequest,
    ): NetworkResult<ExamTimetableEnvelope> =
        api.createTimetable(token, request)

    suspend fun publishTimetable(
        token: String,
        timetableId: String,
    ): NetworkResult<com.littlebridge.enrollplus.feature.exam.data.remote.ApiResponseUnit> =
        api.publishTimetable(token, timetableId)

    suspend fun listTimetables(
        token: String,
        className: String? = null,
        status: String? = null,
    ): NetworkResult<ExamTimetableListEnvelope> =
        api.listTimetables(token, className, status)

    suspend fun getTimetable(
        token: String,
        timetableId: String,
    ): NetworkResult<ExamTimetableEnvelope> =
        api.getTimetable(token, timetableId)

    // ── Syllabus mapping ─────────────────────────────────────────────────────

    suspend fun getExamSyllabus(
        token: String,
        assessmentId: String,
    ): NetworkResult<ExamSyllabusEnvelope> =
        api.getExamSyllabus(token, assessmentId)

    suspend fun updateExamSyllabus(
        token: String,
        assessmentId: String,
        unitIds: List<String>,
    ): NetworkResult<com.littlebridge.enrollplus.feature.exam.data.remote.ApiResponseUnit> =
        api.updateExamSyllabus(token, assessmentId, unitIds)

    // ── Parent ───────────────────────────────────────────────────────────────

    suspend fun getParentExamSyllabus(
        token: String,
        childId: String,
        assessmentId: String,
    ): NetworkResult<ExamSyllabusEnvelope> =
        api.getParentExamSyllabus(token, childId, assessmentId)

    suspend fun requestSyllabus(
        token: String,
        assessmentId: String,
        message: String = "",
    ): NetworkResult<ExamSyllabusRequestEnvelope> =
        api.requestSyllabus(token, assessmentId, message)
}
