package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.StaffApi
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateStaffRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.StaffDto
import com.littlebridge.enrollplus.feature.admin.domain.model.StaffListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateStaffRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.StaffRepository

class StaffRepositoryImpl(
    private val api: StaffApi,
    private val cache: CacheManager,
) : StaffRepository {

    override suspend fun getStaff(token: String, query: String?, department: String?): NetworkResult<ApiResponse<StaffListResponse>> =
        cacheFirstNetworkResult(cache, "admin_staff_${query ?: "all"}_${department ?: "all"}", ApiResponse.serializer(StaffListResponse.serializer())) { api.getStaff(token, query, department) }

    override suspend fun createStaff(token: String, request: CreateStaffRequest): NetworkResult<ApiResponse<StaffDto>> {
        val result = api.createStaff(token, request)
        if (result is NetworkResult.Success) invalidateStaffCache()
        return result
    }

    override suspend fun getStaffProfile(token: String, staffId: String): NetworkResult<ApiResponse<StaffDto>> =
        cacheFirstNetworkResult(cache, "admin_staff_profile_$staffId", ApiResponse.serializer(StaffDto.serializer())) { api.getStaffProfile(token, staffId) }

    override suspend fun updateStaff(token: String, staffId: String, request: UpdateStaffRequest): NetworkResult<ApiResponse<StaffDto>> {
        val result = api.updateStaff(token, staffId, request)
        if (result is NetworkResult.Success) {
            cache.delete("admin_staff_profile_$staffId")
            invalidateStaffCache()
        }
        return result
    }

    override suspend fun deleteStaff(token: String, staffId: String): NetworkResult<ApiResponse<Unit>> {
        val result = api.deleteStaff(token, staffId)
        if (result is NetworkResult.Success) {
            cache.delete("admin_staff_profile_$staffId")
            invalidateStaffCache()
        }
        return result
    }

    private suspend fun invalidateStaffCache() {
        cache.delete("admin_staff_all_all")
    }
}
