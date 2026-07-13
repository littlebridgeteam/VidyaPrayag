package com.littlebridge.enrollplus.feature.export.domain.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.export.domain.model.*

interface ExportRepository {
    suspend fun getExportTypes(token: String): NetworkResult<ApiResponse<ExportTypesResponse>>
    suspend fun listAssessments(token: String, classId: String?): NetworkResult<ApiResponse<ExportAssessmentsResponse>>
    suspend fun generateExport(token: String, request: ExportRequest): NetworkResult<ApiResponse<ExportResponse>>
}
