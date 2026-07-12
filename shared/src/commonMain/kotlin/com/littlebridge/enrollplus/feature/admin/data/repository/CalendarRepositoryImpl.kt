package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.CalendarApi
import com.littlebridge.enrollplus.feature.admin.domain.model.CalendarResponse
import com.littlebridge.enrollplus.feature.admin.domain.repository.CalendarRepository

class CalendarRepositoryImpl(
    private val api: CalendarApi,
    private val cache: CacheManager,
) : CalendarRepository {

    override suspend fun getCalendar(
        token: String,
        date: String?,
        viewType: String,
        endpoint: String
    ): NetworkResult<ApiResponse<CalendarResponse>> =
        cacheFirstNetworkResult(cache, "admin_calendar_${endpoint}_${viewType}_${date ?: "all"}", ApiResponse.serializer(CalendarResponse.serializer())) { api.getCalendar(token, date, viewType, endpoint) }
}
