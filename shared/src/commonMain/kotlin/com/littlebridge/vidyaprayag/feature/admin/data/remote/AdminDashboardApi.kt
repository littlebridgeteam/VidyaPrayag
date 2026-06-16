/*
 * File: AdminDashboardApi.kt
 * Module: feature.admin.data.remote
 *
 * Network client for the redesigned School home dashboard.
 *
 * Server routes (JWT — the bearer token is injected centrally by the shared
 * HttpClient auth plugin, so the `token` argument here is decorative and kept
 * only for signature parity with the rest of the admin APIs):
 *   GET /api/admin/dashboard/summary
 *   GET /api/admin/dashboard/analytics
 *   GET /api/admin/dashboard/activity
 */
package com.littlebridge.vidyaprayag.feature.admin.data.remote

import com.littlebridge.vidyaprayag.core.model.ApiResponse
import com.littlebridge.vidyaprayag.core.network.NetworkResult
import com.littlebridge.vidyaprayag.core.network.safeApiCall
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardActivity
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardAnalytics
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardSummary
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class AdminDashboardApi(
    private val client: HttpClient,
    private val baseUrl: String
) {
    private fun getUrl(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    suspend fun getSummary(token: String): NetworkResult<ApiResponse<DashboardSummary>> = safeApiCall {
        client.get(getUrl("api/admin/dashboard/summary"))
    }

    suspend fun getAnalytics(token: String): NetworkResult<ApiResponse<DashboardAnalytics>> = safeApiCall {
        client.get(getUrl("api/admin/dashboard/analytics"))
    }

    suspend fun getActivity(token: String): NetworkResult<ApiResponse<DashboardActivity>> = safeApiCall {
        client.get(getUrl("api/admin/dashboard/activity"))
    }
}
