package com.littlebridge.enrollplus.feature.admin.domain.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceSaveRequest

interface AttendanceRepository {
    suspend fun getDailyAttendance(
        token: String,
        type: String = "student",
        grade: String? = null,
        section: String? = null,
        date: String? = null
    ): NetworkResult<ApiResponse<AttendanceResponse>>

    suspend fun saveDailyAttendance(
        token: String,
        request: AttendanceSaveRequest
    ): NetworkResult<ApiResponse<Map<String, Int>>>
}
