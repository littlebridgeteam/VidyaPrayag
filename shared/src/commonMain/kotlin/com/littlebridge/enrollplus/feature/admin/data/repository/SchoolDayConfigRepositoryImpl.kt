package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.SchoolDayConfigApi
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateSchoolDayConfigRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolDayConfigDto
import com.littlebridge.enrollplus.feature.admin.domain.model.SchoolDayConfigListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateSchoolDayConfigRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.SchoolDayConfigRepository

class SchoolDayConfigRepositoryImpl(
    private val api: SchoolDayConfigApi,
    private val cache: CacheManager,
) : SchoolDayConfigRepository {

    override suspend fun list(token: String): NetworkResult<ApiResponse<SchoolDayConfigListResponse>> =
        cacheFirstNetworkResult(cache, "admin_school_day_configs", ApiResponse.serializer(SchoolDayConfigListResponse.serializer())) { api.list(token) }

    override suspend fun getById(token: String, id: String): NetworkResult<ApiResponse<SchoolDayConfigDto>> =
        cacheFirstNetworkResult(cache, "admin_school_day_config_$id", ApiResponse.serializer(SchoolDayConfigDto.serializer())) { api.getById(token, id) }

    override suspend fun create(token: String, request: CreateSchoolDayConfigRequest): NetworkResult<ApiResponse<SchoolDayConfigDto>> =
        api.create(token, request)

    override suspend fun update(token: String, id: String, request: UpdateSchoolDayConfigRequest): NetworkResult<ApiResponse<SchoolDayConfigDto>> =
        api.update(token, id, request)

    override suspend fun deactivate(token: String, id: String): NetworkResult<ApiResponse<Unit>> =
        api.deactivate(token, id)
}
