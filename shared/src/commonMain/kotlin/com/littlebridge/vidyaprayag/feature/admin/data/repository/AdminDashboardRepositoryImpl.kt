package com.littlebridge.vidyaprayag.feature.admin.data.repository

import com.littlebridge.vidyaprayag.core.model.ApiResponse
import com.littlebridge.vidyaprayag.core.network.NetworkResult
import com.littlebridge.vidyaprayag.feature.admin.data.remote.AdminDashboardApi
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardActivity
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardAnalytics
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardSummary
import com.littlebridge.vidyaprayag.feature.admin.domain.repository.AdminDashboardRepository

class AdminDashboardRepositoryImpl(
    private val api: AdminDashboardApi
) : AdminDashboardRepository {
    override suspend fun getSummary(token: String): NetworkResult<ApiResponse<DashboardSummary>> =
        api.getSummary(token)

    override suspend fun getAnalytics(token: String): NetworkResult<ApiResponse<DashboardAnalytics>> =
        api.getAnalytics(token)

    override suspend fun getActivity(token: String): NetworkResult<ApiResponse<DashboardActivity>> =
        api.getActivity(token)
}
