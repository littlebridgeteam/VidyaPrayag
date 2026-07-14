package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.AnalyticsApi
import com.littlebridge.enrollplus.feature.admin.domain.model.AnalyticsOverviewResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.StudentAnalyticsResponse
import com.littlebridge.enrollplus.feature.admin.domain.repository.AnalyticsRepository
import kotlinx.serialization.json.JsonElement

class AnalyticsRepositoryImpl(
    private val api: AnalyticsApi,
    private val cache: CacheManager,
) : AnalyticsRepository {
    override suspend fun getOverview(token: String): NetworkResult<ApiResponse<AnalyticsOverviewResponse>> =
        cacheFirstNetworkResult(cache, "admin_analytics_overview", ApiResponse.serializer(AnalyticsOverviewResponse.serializer())) { api.getOverview(token) }

    override suspend fun getStudentAnalytics(token: String, studentId: String): NetworkResult<ApiResponse<StudentAnalyticsResponse>> =
        cacheFirstNetworkResult(cache, "admin_student_analytics_$studentId", ApiResponse.serializer(StudentAnalyticsResponse.serializer())) { api.getStudentAnalytics(token, studentId) }

    override suspend fun getClassPerformance(token: String, className: String?): NetworkResult<ApiResponse<JsonElement>> =
        cacheFirstNetworkResult(cache, "admin_class_performance_${className ?: "all"}", ApiResponse.serializer(JsonElement.serializer())) { api.getClassPerformance(token, className) }

    override suspend fun getTeacherPerformance(token: String): NetworkResult<ApiResponse<JsonElement>> =
        cacheFirstNetworkResult(cache, "admin_teacher_performance", ApiResponse.serializer(JsonElement.serializer())) { api.getTeacherPerformance(token) }

    override suspend fun getSyllabusCoverage(token: String): NetworkResult<ApiResponse<JsonElement>> =
        cacheFirstNetworkResult(cache, "admin_syllabus_coverage", ApiResponse.serializer(JsonElement.serializer())) { api.getSyllabusCoverage(token) }

    override suspend fun getStudentCohort(token: String): NetworkResult<ApiResponse<JsonElement>> =
        cacheFirstNetworkResult(cache, "admin_student_cohort", ApiResponse.serializer(JsonElement.serializer())) { api.getStudentCohort(token) }
}
