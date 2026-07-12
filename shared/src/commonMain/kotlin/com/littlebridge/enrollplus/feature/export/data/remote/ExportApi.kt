package com.littlebridge.enrollplus.feature.export.data.remote

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
import com.littlebridge.enrollplus.feature.export.domain.model.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ExportApi(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    private fun url(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    suspend fun getExportTypes(token: String): NetworkResult<ApiResponse<ExportTypesResponse>> =
        safeApiCall {
            client.get(url("api/v1/school/export/types")) {
                header("Authorization", "Bearer $token")
            }
        }

    suspend fun generateExport(token: String, request: ExportRequest): NetworkResult<ApiResponse<ExportResponse>> =
        safeApiCall {
            client.post(url("api/v1/school/export")) {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}
