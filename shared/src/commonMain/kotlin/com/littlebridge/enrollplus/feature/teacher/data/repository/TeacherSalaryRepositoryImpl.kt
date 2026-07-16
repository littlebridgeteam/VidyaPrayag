package com.littlebridge.enrollplus.feature.teacher.data.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherSalaryResponse
import com.littlebridge.enrollplus.feature.teacher.data.remote.TeacherSalaryApi
import com.littlebridge.enrollplus.feature.teacher.domain.model.EscalateFeeRequest
import com.littlebridge.enrollplus.feature.teacher.domain.model.TeacherFeeListResponse
import com.littlebridge.enrollplus.feature.teacher.domain.repository.TeacherSalaryRepository

class TeacherSalaryRepositoryImpl(
    private val api: TeacherSalaryApi,
) : TeacherSalaryRepository {

    override suspend fun getMySalary(token: String): NetworkResult<ApiResponse<TeacherSalaryResponse>> =
        api.getMySalary(token)

    override suspend fun getUnpaidFees(token: String, month: String?): NetworkResult<ApiResponse<TeacherFeeListResponse>> =
        api.getUnpaidFees(token, month)

    override suspend fun escalateFees(token: String, request: EscalateFeeRequest): NetworkResult<ApiResponse<Map<String, Int>>> =
        api.escalateFees(token, request)
}
