/*
 * File: AttendanceApi.kt
 * Module: feature.admin.data.remote
 *
 * Network client for daily attendance endpoint.
 * Server route: GET /api/v1/school/attendance/daily
 */
package com.littlebridge.enrollplus.feature.admin.data.remote

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceSaveRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AttendanceApi(
    private val client: HttpClient,
    private val baseUrl: String
) {

    private fun getUrl(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    /**
     * @param type  "student" or "faculty"
     * @param grade Required when type == "student" (e.g. "Class 10")
     * @param section Optional section filter (e.g. "A")
     * @param date  YYYY-MM-DD; defaults to today on the server
     */
    suspend fun getDailyAttendance(
        token: String,
        type: String = "student",
        grade: String? = null,
        section: String? = null,
        date: String? = null
    ): NetworkResult<ApiResponse<AttendanceResponse>> = safeApiCall {
        client.get(getUrl("api/v1/school/attendance/daily")) {
            parameter("type", type)
            grade?.let { parameter("grade", it) }
            section?.let { parameter("section", it) }
            date?.let { parameter("date", it) }
        }
    }

    suspend fun saveDailyAttendance(
        token: String,
        request: AttendanceSaveRequest
    ): NetworkResult<ApiResponse<Map<String, Int>>> = safeApiCall {
        client.post(getUrl("api/v1/school/attendance/daily")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
