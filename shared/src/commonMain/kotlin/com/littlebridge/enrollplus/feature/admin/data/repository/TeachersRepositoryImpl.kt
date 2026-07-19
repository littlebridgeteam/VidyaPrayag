package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.TeachersApi
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateTeacherRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherAccountDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherCardListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherCredentialDto
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateTeacherRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.TeachersRepository

class TeachersRepositoryImpl(
    private val api: TeachersApi,
    private val cache: CacheManager,
) : TeachersRepository {

    override suspend fun getTeachers(
        token: String,
        page: Int,
        pageSize: Int
    ): NetworkResult<ApiResponse<TeacherCardListResponse>> =
        cacheFirstNetworkResult(cache, "admin_teachers_${page}_$pageSize", ApiResponse.serializer(TeacherCardListResponse.serializer())) { api.getTeachers(token, page, pageSize) }

    override suspend fun createTeacher(token: String, request: CreateTeacherRequest): NetworkResult<ApiResponse<TeacherAccountDto>> {
        val result = api.createTeacher(token, request)
        if (result is NetworkResult.Success) invalidateTeachersCache()
        return result
    }

    override suspend fun deleteTeacher(token: String, teacherId: String): NetworkResult<ApiResponse<Unit>> {
        val result = api.deleteTeacher(token, teacherId)
        if (result is NetworkResult.Success) invalidateTeachersCache()
        return result
    }

    private suspend fun invalidateTeachersCache() {
        for (page in 1..5) {
            cache.delete("admin_teachers_${page}_10")
            cache.delete("admin_teachers_${page}_100")
        }
    }

    override suspend fun resetTeacherPassword(token: String, teacherId: String): NetworkResult<ApiResponse<TeacherCredentialDto>> =
        api.resetTeacherPassword(token, teacherId)

    override suspend fun updateTeacher(token: String, teacherId: String, request: UpdateTeacherRequest): NetworkResult<ApiResponse<TeacherAccountDto>> =
        api.updateTeacher(token, teacherId, request)
}
