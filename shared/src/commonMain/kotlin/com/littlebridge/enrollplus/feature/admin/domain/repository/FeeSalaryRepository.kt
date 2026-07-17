package com.littlebridge.enrollplus.feature.admin.domain.repository

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
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

interface FeeSalaryRepository {
    suspend fun getFeeStructures(token: String, classId: String? = null): NetworkResult<ApiResponse<FeeStructureListResponse>>
    suspend fun createFeeStructure(token: String, request: CreateFeeStructureRequest): NetworkResult<ApiResponse<FeeStructureDto>>
    suspend fun updateFeeStructure(token: String, id: String, request: UpdateFeeStructureRequest): NetworkResult<ApiResponse<FeeStructureDto>>
    suspend fun deleteFeeStructure(token: String, id: String): NetworkResult<ApiResponse<Unit>>

    suspend fun getAdditionalCharges(token: String, childId: String? = null, month: String? = null): NetworkResult<ApiResponse<FeeAdditionalChargeListResponse>>
    suspend fun createAdditionalCharge(token: String, request: CreateFeeAdditionalChargeRequest): NetworkResult<ApiResponse<Unit>>
    suspend fun bulkCreateAdditionalCharge(token: String, request: BulkCreateFeeAdditionalChargeRequest): NetworkResult<ApiResponse<Map<String, Int>>>
    suspend fun deleteAdditionalCharge(token: String, id: String): NetworkResult<ApiResponse<Unit>>

    suspend fun getFeeStudents(token: String, classId: String? = null, section: String? = null, month: String? = null, search: String? = null): NetworkResult<ApiResponse<FeeStudentListResponse>>
    suspend fun markFeesPaid(token: String, request: MarkPaidRequest): NetworkResult<ApiResponse<Map<String, Int>>>
    suspend fun generateFees(token: String, request: GenerateFeesRequest): NetworkResult<ApiResponse<GenerateFeesResponse>>

    suspend fun getReminderConfig(token: String): NetworkResult<ApiResponse<FeeReminderConfigDto>>
    suspend fun updateReminderConfig(token: String, request: UpdateFeeReminderConfigRequest): NetworkResult<ApiResponse<FeeReminderConfigDto>>

    suspend fun getSalaryRecords(token: String, teacherId: String? = null, month: String? = null): NetworkResult<ApiResponse<SalaryListResponse>>
    suspend fun setSalary(token: String, request: SetSalaryRequest): NetworkResult<ApiResponse<SalaryRecordDto>>
    suspend fun markSalaryPaid(token: String, id: String): NetworkResult<ApiResponse<Unit>>

    suspend fun getFeeClasses(token: String): NetworkResult<ApiResponse<FeeClassListResponse>>
    suspend fun getFeeTeachers(token: String): NetworkResult<ApiResponse<FeeTeacherListResponse>>

    suspend fun getLateFeeTiers(token: String): NetworkResult<ApiResponse<FeeLateFeeTierListResponse>>
    suspend fun createLateFeeTier(token: String, request: CreateFeeLateFeeTierRequest): NetworkResult<ApiResponse<FeeLateFeeTierDto>>
    suspend fun updateLateFeeTier(token: String, id: String, request: UpdateFeeLateFeeTierRequest): NetworkResult<ApiResponse<Unit>>
    suspend fun deleteLateFeeTier(token: String, id: String): NetworkResult<ApiResponse<Unit>>
}
