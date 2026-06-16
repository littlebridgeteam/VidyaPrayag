package com.littlebridge.vidyaprayag.feature.admin.domain.repository

import com.littlebridge.vidyaprayag.core.model.ApiResponse
import com.littlebridge.vidyaprayag.core.network.NetworkResult
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardActivity
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardAnalytics
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardSummary

/**
 * Data access for the redesigned School home dashboard
 * (GET /api/admin/dashboard/{summary,analytics,activity}).
 */
interface AdminDashboardRepository {
    suspend fun getSummary(token: String): NetworkResult<ApiResponse<DashboardSummary>>
    suspend fun getAnalytics(token: String): NetworkResult<ApiResponse<DashboardAnalytics>>
    suspend fun getActivity(token: String): NetworkResult<ApiResponse<DashboardActivity>>
}
