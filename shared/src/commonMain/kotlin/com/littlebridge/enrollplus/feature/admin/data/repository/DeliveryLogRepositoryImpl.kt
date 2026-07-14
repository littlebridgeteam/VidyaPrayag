package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.DeliveryLogApi
import com.littlebridge.enrollplus.feature.admin.domain.model.DeliveryLogResponse
import com.littlebridge.enrollplus.feature.admin.domain.repository.DeliveryLogRepository

class DeliveryLogRepositoryImpl(
    private val api: DeliveryLogApi,
) : DeliveryLogRepository {

    override suspend fun getDeliveryLog(
        token: String,
        limit: Int,
        cursor: String?,
        channel: String?,
        announcementId: String?,
    ): NetworkResult<DeliveryLogResponse> {
        return when (val result = api.getDeliveryLog(token, limit, cursor, channel, announcementId)) {
            is NetworkResult.Success -> {
                val envelope = result.data
                val data = envelope.data
                when {
                    !envelope.success -> NetworkResult.Error(
                        envelope.message.ifBlank { "Failed to fetch delivery log" }
                    )
                    data == null -> NetworkResult.Error("No data in response")
                    else -> NetworkResult.Success(data)
                }
            }
            is NetworkResult.Error -> NetworkResult.Error(result.message, result.code)
            is NetworkResult.ConnectionError -> NetworkResult.ConnectionError
        }
    }
}
