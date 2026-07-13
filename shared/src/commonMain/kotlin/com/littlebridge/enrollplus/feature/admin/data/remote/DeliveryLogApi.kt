package com.littlebridge.enrollplus.feature.admin.data.remote

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
import com.littlebridge.enrollplus.feature.admin.domain.model.DeliveryLogResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.parameters

class DeliveryLogApi(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    private fun getUrl(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    suspend fun getDeliveryLog(
        token: String,
        limit: Int = 20,
        cursor: String? = null,
        channel: String? = null,
        announcementId: String? = null,
    ): NetworkResult<ApiResponse<DeliveryLogResponse>> = safeApiCall {
        client.get(getUrl("api/v1/school/announcements/delivery")) {
            url {
                parameters.append("limit", limit.toString())
                if (!cursor.isNullOrBlank()) parameters.append("cursor", cursor)
                if (!channel.isNullOrBlank()) parameters.append("channel", channel)
                if (!announcementId.isNullOrBlank()) parameters.append("announcement_id", announcementId)
            }
        }
    }
}
