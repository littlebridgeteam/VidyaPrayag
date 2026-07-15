package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.FeeSalaryApi
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateFeeAdditionalChargeRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateFeeStructureRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeAdditionalChargeListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeReminderConfigDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeStructureDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeStructureListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeStudentListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.GenerateFeesRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.GenerateFeesResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.MarkPaidRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.SalaryListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.SalaryRecordDto
import com.littlebridge.enrollplus.feature.admin.domain.model.SetSalaryRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateFeeReminderConfigRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateFeeStructureRequest
import com.littlebridge.enrollplus.feature.admin.domain.repository.FeeSalaryRepository

class FeeSalaryRepositoryImpl(
    private val api: FeeSalaryApi,
) : FeeSalaryRepository {

    override suspend fun getFeeStructures(token: String, classId: String?): NetworkResult<ApiResponse<FeeStructureListResponse>> =
        api.getFeeStructures(token, classId)

    override suspend fun createFeeStructure(token: String, request: CreateFeeStructureRequest): NetworkResult<ApiResponse<FeeStructureDto>> =
        api.createFeeStructure(token, request)

    override suspend fun updateFeeStructure(token: String, id: String, request: UpdateFeeStructureRequest): NetworkResult<ApiResponse<FeeStructureDto>> =
        api.updateFeeStructure(token, id, request)

    override suspend fun deleteFeeStructure(token: String, id: String): NetworkResult<ApiResponse<Unit>> =
        api.deleteFeeStructure(token, id)

    override suspend fun getAdditionalCharges(token: String, childId: String?, month: String?): NetworkResult<ApiResponse<FeeAdditionalChargeListResponse>> =
        api.getAdditionalCharges(token, childId, month)

    override suspend fun createAdditionalCharge(token: String, request: CreateFeeAdditionalChargeRequest): NetworkResult<ApiResponse<Unit>> =
        api.createAdditionalCharge(token, request)

    override suspend fun deleteAdditionalCharge(token: String, id: String): NetworkResult<ApiResponse<Unit>> =
        api.deleteAdditionalCharge(token, id)

    override suspend fun getFeeStudents(token: String, classId: String?, section: String?, month: String?, search: String?): NetworkResult<ApiResponse<FeeStudentListResponse>> =
        api.getFeeStudents(token, classId, section, month, search)

    override suspend fun markFeesPaid(token: String, request: MarkPaidRequest): NetworkResult<ApiResponse<Map<String, Int>>> =
        api.markFeesPaid(token, request)

    override suspend fun generateFees(token: String, request: GenerateFeesRequest): NetworkResult<ApiResponse<GenerateFeesResponse>> =
        api.generateFees(token, request)

    override suspend fun getReminderConfig(token: String): NetworkResult<ApiResponse<FeeReminderConfigDto>> =
        api.getReminderConfig(token)

    override suspend fun updateReminderConfig(token: String, request: UpdateFeeReminderConfigRequest): NetworkResult<ApiResponse<FeeReminderConfigDto>> =
        api.updateReminderConfig(token, request)

    override suspend fun getSalaryRecords(token: String, teacherId: String?, month: String?): NetworkResult<ApiResponse<SalaryListResponse>> =
        api.getSalaryRecords(token, teacherId, month)

    override suspend fun setSalary(token: String, request: SetSalaryRequest): NetworkResult<ApiResponse<SalaryRecordDto>> =
        api.setSalary(token, request)

    override suspend fun markSalaryPaid(token: String, id: String): NetworkResult<ApiResponse<Unit>> =
        api.markSalaryPaid(token, id)
}
