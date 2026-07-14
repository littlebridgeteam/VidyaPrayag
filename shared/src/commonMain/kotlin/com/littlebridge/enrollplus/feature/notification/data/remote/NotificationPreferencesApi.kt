package com.littlebridge.enrollplus.feature.notification.data.remote

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
import com.littlebridge.enrollplus.feature.notification.domain.model.NotificationPreferenceDto
import com.littlebridge.enrollplus.feature.notification.domain.model.NotificationPreferencesResponse
import com.littlebridge.enrollplus.feature.notification.domain.model.UpdatePreferenceRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class NotificationPreferencesApi(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    private fun getUrl(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    suspend fun getPreferences(
        token: String,
    ): NetworkResult<ApiResponse<NotificationPreferencesResponse>> = safeApiCall {
        client.get(getUrl("api/v1/notifications/preferences"))
    }

    suspend fun updatePreference(
        token: String,
        request: UpdatePreferenceRequest,
    ): NetworkResult<ApiResponse<NotificationPreferenceDto>> = safeApiCall {
        client.put(getUrl("api/v1/notifications/preferences")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun resetPreference(
        token: String,
        category: String,
    ): NetworkResult<ApiResponse<Map<String, String>>> = safeApiCall {
        client.delete(getUrl("api/v1/notifications/preferences/$category"))
    }
}
