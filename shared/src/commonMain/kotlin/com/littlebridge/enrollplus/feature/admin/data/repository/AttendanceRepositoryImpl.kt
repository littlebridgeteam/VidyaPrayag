package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.AttendanceApi
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceSaveRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.AttendanceRepository

class AttendanceRepositoryImpl(
    private val api: AttendanceApi,
    private val cache: CacheManager,
) : AttendanceRepository {

    override suspend fun getDailyAttendance(
        token: String,
        type: String,
        grade: String?,
        section: String?,
        date: String?
    ): NetworkResult<ApiResponse<AttendanceResponse>> =
        cacheFirstNetworkResult(cache, "admin_daily_attendance_${type}_${grade ?: "all"}_${section ?: "all"}_${date ?: "today"}", ApiResponse.serializer(AttendanceResponse.serializer())) { api.getDailyAttendance(token, type, grade, section, date) }

    override suspend fun saveDailyAttendance(
        token: String,
        request: AttendanceSaveRequest
    ): NetworkResult<ApiResponse<Map<String, Int>>> =
        api.saveDailyAttendance(token, request)
}
