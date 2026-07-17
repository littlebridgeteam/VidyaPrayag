package com.littlebridge.enrollplus.feature.teacher.domain.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherSalaryResponse
import com.littlebridge.enrollplus.feature.teacher.domain.model.EscalateFeeRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherFeeListResponse

interface TeacherSalaryRepository {
    suspend fun getMySalary(token: String): NetworkResult<ApiResponse<TeacherSalaryResponse>>
    suspend fun getUnpaidFees(token: String, month: String? = null): NetworkResult<ApiResponse<TeacherFeeListResponse>>
    suspend fun escalateFees(token: String, request: EscalateFeeRequest): NetworkResult<ApiResponse<Map<String, Int>>>
}
