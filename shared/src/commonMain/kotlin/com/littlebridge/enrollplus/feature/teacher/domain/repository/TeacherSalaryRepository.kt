package com.littlebridge.enrollplus.feature.teacher.domain.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherSalaryResponse

interface TeacherSalaryRepository {
    suspend fun getMySalary(token: String): NetworkResult<ApiResponse<TeacherSalaryResponse>>
}
