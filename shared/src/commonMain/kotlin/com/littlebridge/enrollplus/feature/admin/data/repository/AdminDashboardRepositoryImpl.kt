package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.AdminDashboardApi
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardAnalytics
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardOverview
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardSummary
import com.littlebridge.enrollplus.feature.admin.domain.repository.AdminDashboardRepository
import com.littlebridge.enrollplus.feature.teacher.domain.model.PaceSnapshotsResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.PaceAlertsResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.PaceAlertResolveResponse

class AdminDashboardRepositoryImpl(
    private val api: AdminDashboardApi
) : AdminDashboardRepository {

    override suspend fun getSummary(token: String): NetworkResult<ApiResponse<AdminDashboardSummary>> =
        api.getSummary(token)

    override suspend fun getAnalytics(token: String): NetworkResult<ApiResponse<AdminDashboardAnalytics>> =
        api.getAnalytics(token)

    override suspend fun getActivity(token: String): NetworkResult<ApiResponse<AdminDashboardActivity>> =
        api.getActivity(token)

    override suspend fun getOverview(token: String): NetworkResult<ApiResponse<AdminDashboardOverview>> =
        api.getOverview(token)

    // ── Agentic Syllabus — pace monitoring (admin) ───────────────────────────
    override suspend fun getPaceSnapshots(token: String, classId: String?, section: String?): NetworkResult<PaceSnapshotsResponse> =
        api.getPaceSnapshots(token, classId, section)

    override suspend fun getPaceAlerts(token: String): NetworkResult<PaceAlertsResponse> =
        api.getPaceAlerts(token)

    override suspend fun resolvePaceAlert(token: String, alertId: String): NetworkResult<PaceAlertResolveResponse> =
        api.resolvePaceAlert(token, alertId)

    override suspend fun getPaceCoverage(token: String, classId: String?, section: String?): NetworkResult<PaceSnapshotsResponse> =
        api.getPaceCoverage(token, classId, section)

    override suspend fun recalculatePace(token: String): NetworkResult<PaceSnapshotsResponse> =
        api.recalculatePace(token)
}
