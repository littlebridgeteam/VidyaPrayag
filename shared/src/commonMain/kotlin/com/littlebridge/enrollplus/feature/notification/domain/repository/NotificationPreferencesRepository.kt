package com.littlebridge.enrollplus.feature.notification.domain.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.notification.domain.model.NotificationPreferenceDto
import com.littlebridge.enrollplus.feature.notification.domain.model.NotificationPreferencesResponse
import com.littlebridge.enrollplus.feature.notification.domain.model.UpdatePreferenceRequest

interface NotificationPreferencesRepository {
    suspend fun getPreferences(token: String): NetworkResult<ApiResponse<NotificationPreferencesResponse>>
    suspend fun updatePreference(token: String, request: UpdatePreferenceRequest): NetworkResult<ApiResponse<NotificationPreferenceDto>>
    suspend fun resetPreference(token: String, category: String): NetworkResult<ApiResponse<Map<String, String>>>
}
