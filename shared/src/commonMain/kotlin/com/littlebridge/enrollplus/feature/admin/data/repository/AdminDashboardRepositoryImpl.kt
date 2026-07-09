package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
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
    private val api: AdminDashboardApi,
    private val cache: CacheManager,
) : AdminDashboardRepository {

    override suspend fun getSummary(token: String): NetworkResult<ApiResponse<AdminDashboardSummary>> =
        cacheFirstNetworkResult(cache, "admin_dashboard_summary", ApiResponse.serializer(AdminDashboardSummary.serializer())) { api.getSummary(token) }

    override suspend fun getAnalytics(token: String): NetworkResult<ApiResponse<AdminDashboardAnalytics>> =
        cacheFirstNetworkResult(cache, "admin_dashboard_analytics", ApiResponse.serializer(AdminDashboardAnalytics.serializer())) { api.getAnalytics(token) }

    override suspend fun getActivity(token: String): NetworkResult<ApiResponse<AdminDashboardActivity>> =
        cacheFirstNetworkResult(cache, "admin_dashboard_activity", ApiResponse.serializer(AdminDashboardActivity.serializer())) { api.getActivity(token) }

    override suspend fun getOverview(token: String): NetworkResult<ApiResponse<AdminDashboardOverview>> =
        cacheFirstNetworkResult(cache, "admin_dashboard_overview", ApiResponse.serializer(AdminDashboardOverview.serializer())) { api.getOverview(token) }

    // ── Agentic Syllabus — pace monitoring (admin) ───────────────────────────
    override suspend fun getPaceSnapshots(token: String, classId: String?, section: String?): NetworkResult<PaceSnapshotsResponse> =
        cacheFirstNetworkResult(cache, "admin_pace_snapshots_${classId ?: "all"}_${section ?: "all"}", PaceSnapshotsResponse.serializer()) { api.getPaceSnapshots(token, classId, section) }

    override suspend fun getPaceAlerts(token: String): NetworkResult<PaceAlertsResponse> =
        cacheFirstNetworkResult(cache, "admin_pace_alerts", PaceAlertsResponse.serializer()) { api.getPaceAlerts(token) }

    override suspend fun resolvePaceAlert(token: String, alertId: String): NetworkResult<PaceAlertResolveResponse> =
        api.resolvePaceAlert(token, alertId)

    override suspend fun getPaceCoverage(token: String, classId: String?, section: String?): NetworkResult<PaceSnapshotsResponse> =
        cacheFirstNetworkResult(cache, "admin_pace_coverage_${classId ?: "all"}_${section ?: "all"}", PaceSnapshotsResponse.serializer()) { api.getPaceCoverage(token, classId, section) }

    override suspend fun recalculatePace(token: String): NetworkResult<PaceSnapshotsResponse> =
        api.recalculatePace(token)
}
