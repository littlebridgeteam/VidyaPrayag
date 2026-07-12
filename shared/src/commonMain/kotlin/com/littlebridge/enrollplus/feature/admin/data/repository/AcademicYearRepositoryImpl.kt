/*
 * File: AcademicYearRepositoryImpl.kt
 * Module: feature.admin.data.repository
 */
package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.AcademicYearApi
import com.littlebridge.enrollplus.feature.admin.domain.model.AcademicYearDto
import com.littlebridge.enrollplus.feature.admin.domain.model.AcademicYearsListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateAcademicYearRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateAcademicYearRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.AcademicYearRepository

class AcademicYearRepositoryImpl(
    private val api: AcademicYearApi,
    private val cache: CacheManager,
) : AcademicYearRepository {

    override suspend fun getYears(token: String): NetworkResult<ApiResponse<AcademicYearsListResponse>> =
        cacheFirstNetworkResult(cache, "admin_academic_years", ApiResponse.serializer(AcademicYearsListResponse.serializer())) { api.getYears(token) }

    override suspend fun createYear(token: String, request: CreateAcademicYearRequest): NetworkResult<ApiResponse<AcademicYearDto>> =
        api.createYear(token, request)

    override suspend fun updateYear(token: String, yearId: String, request: UpdateAcademicYearRequest): NetworkResult<ApiResponse<AcademicYearDto>> =
        api.updateYear(token, yearId, request)
}
