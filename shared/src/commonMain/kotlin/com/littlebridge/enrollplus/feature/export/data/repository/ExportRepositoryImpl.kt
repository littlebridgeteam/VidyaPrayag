package com.littlebridge.enrollplus.feature.export.data.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.export.data.remote.ExportApi
import com.littlebridge.enrollplus.feature.export.domain.model.*
import com.littlebridge.enrollplus.feature.export.domain.repository.ExportRepository

class ExportRepositoryImpl(
    private val api: ExportApi,
) : ExportRepository {
    override suspend fun getExportTypes(token: String) = api.getExportTypes(token)
    override suspend fun listAssessments(token: String, classId: String?) = api.listAssessments(token, classId)
    override suspend fun generateExport(token: String, request: ExportRequest) = api.generateExport(token, request)
}
