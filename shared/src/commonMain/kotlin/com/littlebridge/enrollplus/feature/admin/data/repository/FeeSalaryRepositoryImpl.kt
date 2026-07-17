package com.littlebridge.enrollplus.feature.admin.data.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.feature.admin.data.remote.FeeSalaryApi
import com.littlebridge.enrollplus.feature.admin.domain.model.BulkCreateFeeAdditionalChargeRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateFeeAdditionalChargeRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateFeeLateFeeTierRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.CreateFeeStructureRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeAdditionalChargeListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeClassListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeLateFeeTierDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeLateFeeTierListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeReminderConfigDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeStructureDto
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeStructureListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeStudentListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.FeeTeacherListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.GenerateFeesRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.GenerateFeesResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.MarkPaidRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.SalaryListResponse
import com.littlebridge.enrollplus.feature.admin.domain.model.SalaryRecordDto
import com.littlebridge.enrollplus.feature.admin.domain.model.SetSalaryRequest
import com.littlebridge.enrollplus.feature.admin.domain.model.UpdateFeeLateFeeTierRequest
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

    override suspend fun bulkCreateAdditionalCharge(token: String, request: BulkCreateFeeAdditionalChargeRequest): NetworkResult<ApiResponse<Map<String, Int>>> =
        api.bulkCreateAdditionalCharge(token, request)

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

    override suspend fun getFeeClasses(token: String): NetworkResult<ApiResponse<FeeClassListResponse>> =
        api.getFeeClasses(token)

    override suspend fun getFeeTeachers(token: String): NetworkResult<ApiResponse<FeeTeacherListResponse>> =
        api.getFeeTeachers(token)

    override suspend fun getLateFeeTiers(token: String): NetworkResult<ApiResponse<FeeLateFeeTierListResponse>> =
        api.getLateFeeTiers(token)

    override suspend fun createLateFeeTier(token: String, request: CreateFeeLateFeeTierRequest): NetworkResult<ApiResponse<FeeLateFeeTierDto>> =
        api.createLateFeeTier(token, request)

    override suspend fun updateLateFeeTier(token: String, id: String, request: UpdateFeeLateFeeTierRequest): NetworkResult<ApiResponse<Unit>> =
        api.updateLateFeeTier(token, id, request)

    override suspend fun deleteLateFeeTier(token: String, id: String): NetworkResult<ApiResponse<Unit>> =
        api.deleteLateFeeTier(token, id)
}
