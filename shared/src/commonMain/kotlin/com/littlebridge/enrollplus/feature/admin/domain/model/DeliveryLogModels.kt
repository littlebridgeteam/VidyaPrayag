package com.littlebridge.enrollplus.feature.admin.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryLogItem(
    @SerialName("id") val id: String,
    @SerialName("announcement_id") val announcementId: String,
    @SerialName("announcement_title") val announcementTitle: String,
    @SerialName("channel") val channel: String,
    @SerialName("recipient_identifier") val recipientIdentifier: String,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class DeliveryLogResponse(
    @SerialName("items") val items: List<DeliveryLogItem>,
    @SerialName("next_cursor") val nextCursor: String? = null,
    @SerialName("has_more") val hasMore: Boolean = false,
)
