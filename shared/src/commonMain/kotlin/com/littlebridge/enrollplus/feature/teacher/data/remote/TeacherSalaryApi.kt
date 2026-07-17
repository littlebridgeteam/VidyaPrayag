package com.littlebridge.enrollplus.feature.teacher.data.remote

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherSalaryResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.EscalateFeeRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherFeeListResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.request.setBody

class TeacherSalaryApi(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    private fun getUrl(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    suspend fun getMySalary(
        token: String,
    ): NetworkResult<ApiResponse<TeacherSalaryResponse>> = safeApiCall {
        client.get(getUrl("api/v1/teacher/salary")) {
            bearerAuth(token)
        }
    }

    suspend fun getUnpaidFees(
        token: String,
        month: String? = null,
    ): NetworkResult<ApiResponse<TeacherFeeListResponse>> = safeApiCall {
        client.get(getUrl("api/v1/teacher/fees/unpaid")) {
            bearerAuth(token)
            month?.takeIf { it.isNotBlank() }?.let { parameter("month", it) }
        }
    }

    suspend fun escalateFees(
        token: String,
        request: EscalateFeeRequest,
    ): NetworkResult<ApiResponse<Map<String, Int>>> = safeApiCall {
        client.post(getUrl("api/v1/teacher/fees/escalate")) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
