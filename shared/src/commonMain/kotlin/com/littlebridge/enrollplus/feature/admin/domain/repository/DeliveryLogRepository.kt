package com.littlebridge.enrollplus.feature.admin.domain.repository

import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.domain.model.DeliveryLogResponse

interface DeliveryLogRepository {
    suspend fun getDeliveryLog(
        token: String,
        limit: Int = 20,
        cursor: String? = null,
        channel: String? = null,
        announcementId: String? = null,
    ): NetworkResult<DeliveryLogResponse>
}
