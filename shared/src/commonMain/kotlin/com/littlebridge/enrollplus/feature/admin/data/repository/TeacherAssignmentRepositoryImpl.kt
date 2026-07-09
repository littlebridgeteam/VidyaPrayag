package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.TeacherAssignmentApi
import com.littlebridge.enrollplus.feature.admin.domain.model.AssignTeacherClassesRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.AssignmentOptionsDto
import com.littlebridge.enrollplus.feature.admin.domain.model.BulkAssignResponseDto
import com.littlebridge.enrollplus.feature.admin.domain.model.TeacherAssignmentOverviewDto
import com.littlebridge.enrollplus.feature.admin.domain.repository.TeacherAssignmentRepository

class TeacherAssignmentRepositoryImpl(
    private val api: TeacherAssignmentApi,
    private val cache: CacheManager,
) : TeacherAssignmentRepository {

    override suspend fun getOverview(token: String, teacherId: String): NetworkResult<ApiResponse<TeacherAssignmentOverviewDto>> =
        cacheFirstNetworkResult(cache, "admin_teacher_assignment_overview_$teacherId", ApiResponse.serializer(TeacherAssignmentOverviewDto.serializer())) { api.getOverview(token, teacherId) }

    override suspend fun getOptions(token: String): NetworkResult<ApiResponse<AssignmentOptionsDto>> =
        cacheFirstNetworkResult(cache, "admin_teacher_assignment_options", ApiResponse.serializer(AssignmentOptionsDto.serializer())) { api.getOptions(token) }

    override suspend fun bulkAssign(token: String, teacherId: String, request: AssignTeacherClassesRequest): NetworkResult<ApiResponse<BulkAssignResponseDto>> =
        api.bulkAssign(token, teacherId, request)

    override suspend fun removeAssignment(token: String, teacherId: String, assignmentId: String): NetworkResult<ApiResponse<Unit>> =
        api.removeAssignment(token, teacherId, assignmentId)
}
