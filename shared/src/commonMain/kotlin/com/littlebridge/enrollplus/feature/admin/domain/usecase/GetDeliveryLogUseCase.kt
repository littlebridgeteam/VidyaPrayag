package com.littlebridge.enrollplus.feature.admin.domain.usecase

import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.domain.model.DeliveryLogResponse
import com.littlebridge.enrollplus.feature.admin.domain.repository.DeliveryLogRepository

class GetDeliveryLogUseCase(
    private val repository: DeliveryLogRepository,
) {
    suspend operator fun invoke(
        token: String,
        limit: Int = 20,
        cursor: String? = null,
        channel: String? = null,
        announcementId: String? = null,
    ): NetworkResult<DeliveryLogResponse> = repository.getDeliveryLog(
        token = token,
        limit = limit,
        cursor = cursor,
        channel = channel,
        announcementId = announcementId,
    )
}
