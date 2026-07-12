package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.LeaveRequestsApi
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateLeaveRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.LeaveRequestDto
import com.littlebridge.enrollplus.feature.admin.domain.model.LeaveRequestsResponse
import com.littlebridge.enrollplus.feature.admin.domain.repository.LeaveRequestsRepository

class LeaveRequestsRepositoryImpl(
    private val api: LeaveRequestsApi,
    private val cache: CacheManager,
) : LeaveRequestsRepository {

    override suspend fun getLeaveRequests(
        token: String,
        type: String,
        status: String?
    ): NetworkResult<ApiResponse<LeaveRequestsResponse>> =
        cacheFirstNetworkResult(cache, "admin_leave_requests_${type}_${status ?: "all"}", ApiResponse.serializer(LeaveRequestsResponse.serializer())) { api.getLeaveRequests(token, type, status) }

    override suspend fun createLeaveRequest(
        token: String,
        request: CreateLeaveRequest
    ): NetworkResult<ApiResponse<LeaveRequestDto>> = api.createLeaveRequest(token, request)

    override suspend fun updateLeaveStatus(
        token: String,
        id: String,
        status: String
    ): NetworkResult<ApiResponse<LeaveRequestDto>> = api.updateLeaveStatus(token, id, status)
}
