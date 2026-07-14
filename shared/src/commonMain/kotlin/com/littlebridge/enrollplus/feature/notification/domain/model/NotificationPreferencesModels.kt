package com.littlebridge.enrollplus.feature.notification.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationPreferenceDto(
    val category: String,
    val enabled: Boolean = true,
    val pushEnabled: Boolean? = null,
    val inAppEnabled: Boolean? = null,
    val emailEnabled: Boolean? = null,
    val smsEnabled: Boolean? = null,
    val sound: String? = null,
)

@Serializable
data class NotificationPreferencesResponse(
    val preferences: List<NotificationPreferenceDto>,
)

@Serializable
data class UpdatePreferenceRequest(
    val category: String,
    val enabled: Boolean,
    val pushEnabled: Boolean? = null,
    val inAppEnabled: Boolean? = null,
    val emailEnabled: Boolean? = null,
    val smsEnabled: Boolean? = null,
    val sound: String? = null,
)
