package com.littlebridge.enrollplus.core.notification

import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentNotificationsResponse

interface NotificationFeedRepository {
    suspend fun getNotifications(token: String): NetworkResult<ParentNotificationsResponse>
    suspend fun markNotificationRead(token: String, id: String): NetworkResult<Unit>
    suspend fun markAllNotificationsRead(token: String): NetworkResult<Unit>
    suspend fun markNotificationByRef(token: String, refType: String, refId: String): NetworkResult<Unit>
    suspend fun clearReadNotifications(token: String): NetworkResult<Unit>
    suspend fun clearAllNotifications(token: String): NetworkResult<Unit>
}
