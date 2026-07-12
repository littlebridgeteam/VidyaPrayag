package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.SchoolProfileApi
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolProfileDto
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateSchoolProfileRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.SchoolProfileRepository

class SchoolProfileRepositoryImpl(
    private val api: SchoolProfileApi,
    private val cache: CacheManager,
) : SchoolProfileRepository {

    override suspend fun getProfile(token: String): NetworkResult<ApiResponse<SchoolProfileDto>> =
        cacheFirstNetworkResult(cache, "admin_school_profile", ApiResponse.serializer(SchoolProfileDto.serializer())) { api.getProfile(token) }

    override suspend fun updateProfile(
        token: String,
        request: UpdateSchoolProfileRequest
    ): NetworkResult<ApiResponse<SchoolProfileDto>> =
        api.updateProfile(token, request)
}
