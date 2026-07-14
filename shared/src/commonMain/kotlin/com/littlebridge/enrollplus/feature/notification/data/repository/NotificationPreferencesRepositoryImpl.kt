package com.littlebridge.enrollplus.feature.notification.data.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.notification.data.remote.NotificationPreferencesApi
import com.littlebridge.enrollplus.feature.notification.domain.model.NotificationPreferenceDto
import com.littlebridge.enrollplus.feature.notification.domain.model.NotificationPreferencesResponse
import com.littlebridge.enrollplus.feature.notification.domain.model.UpdatePreferenceRequest
import com.littlebridge.enrollplus.feature.notification.domain.repository.NotificationPreferencesRepository

class NotificationPreferencesRepositoryImpl(
    private val api: NotificationPreferencesApi,
) : NotificationPreferencesRepository {

    override suspend fun getPreferences(
        token: String,
    ): NetworkResult<ApiResponse<NotificationPreferencesResponse>> =
        api.getPreferences(token)

    override suspend fun updatePreference(
        token: String,
        request: UpdatePreferenceRequest,
    ): NetworkResult<ApiResponse<NotificationPreferenceDto>> =
        api.updatePreference(token, request)

    override suspend fun resetPreference(
        token: String,
        category: String,
    ): NetworkResult<ApiResponse<Map<String, String>>> =
        api.resetPreference(token, category)
}
