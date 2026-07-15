package com.littlebridge.enrollplus.feature.teacher.data.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherSalaryResponse
import com.littlebridge.enrollplus.feature.teacher.data.remote.TeacherSalaryApi
import com.littlebridge.enrollplus.feature.teacher.domain.repository.TeacherSalaryRepository

class TeacherSalaryRepositoryImpl(
    private val api: TeacherSalaryApi,
) : TeacherSalaryRepository {

    override suspend fun getMySalary(token: String): NetworkResult<ApiResponse<TeacherSalaryResponse>> =
        api.getMySalary(token)
}
