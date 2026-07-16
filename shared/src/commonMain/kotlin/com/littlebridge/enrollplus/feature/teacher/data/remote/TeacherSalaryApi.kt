package com.littlebridge.enrollplus.feature.teacher.data.remote

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherSalaryResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get

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
}
