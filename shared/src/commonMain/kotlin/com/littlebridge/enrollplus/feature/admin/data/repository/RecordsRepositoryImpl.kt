package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.cache.CacheManager
import com.littlebridge.enrollplus.core.cache.cacheFirstNetworkResult
import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.RecordsApi
import com.littlebridge.enrollplus.feature.admin.domain.model.AttendanceSummaryDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeLedgerDto
import com.littlebridge.enrollplus.feature.admin.domain.model.MarksSummaryDto
import com.littlebridge.enrollplus.feature.admin.domain.repository.RecordsRepository

class RecordsRepositoryImpl(
    private val api: RecordsApi,
    private val cache: CacheManager,
) : RecordsRepository {

    override suspend fun getAttendanceSummary(token: String): NetworkResult<ApiResponse<AttendanceSummaryDto>> =
        cacheFirstNetworkResult(cache, "admin_records_attendance_summary", ApiResponse.serializer(AttendanceSummaryDto.serializer())) { api.getAttendanceSummary(token) }

    override suspend fun getMarksSummary(token: String): NetworkResult<ApiResponse<MarksSummaryDto>> =
        cacheFirstNetworkResult(cache, "admin_records_marks_summary", ApiResponse.serializer(MarksSummaryDto.serializer())) { api.getMarksSummary(token) }

    override suspend fun getFeeLedger(token: String): NetworkResult<ApiResponse<FeeLedgerDto>> =
        cacheFirstNetworkResult(cache, "admin_records_fee_ledger", ApiResponse.serializer(FeeLedgerDto.serializer())) { api.getFeeLedger(token) }
}
