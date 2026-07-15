package com.littlebridge.enrollplus.feature.admin.data.remote

import com.littlebridge.enrollplus.core.model.ApiResponse
import com.littlebridge.enrollplus.core.network.NetworkResult
import com.littlebridge.enrollplus.core.network.safeApiCall
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
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FeeSalaryApi(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    private fun getUrl(path: String): String {
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val cleanPath = if (path.startsWith("/")) path.substring(1) else path
        return "$base$cleanPath"
    }

    // ── Fee Structures ────────────────────────────────────────────────────────

    suspend fun getFeeStructures(
        token: String,
        classId: String? = null,
    ): NetworkResult<ApiResponse<FeeStructureListResponse>> = safeApiCall {
        client.get(getUrl("api/v1/school/fees/structures")) {
            classId?.takeIf { it.isNotBlank() }?.let { parameter("classId", it) }
        }
    }

    suspend fun createFeeStructure(
        token: String,
        request: CreateFeeStructureRequest,
    ): NetworkResult<ApiResponse<FeeStructureDto>> = safeApiCall {
        client.post(getUrl("api/v1/school/fees/structures")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun updateFeeStructure(
        token: String,
        id: String,
        request: UpdateFeeStructureRequest,
    ): NetworkResult<ApiResponse<FeeStructureDto>> = safeApiCall {
        client.put(getUrl("api/v1/school/fees/structures/$id")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun deleteFeeStructure(
        token: String,
        id: String,
    ): NetworkResult<ApiResponse<Unit>> = safeApiCall {
        client.delete(getUrl("api/v1/school/fees/structures/$id"))
    }

    // ── Fee Additional Charges ────────────────────────────────────────────────

    suspend fun getAdditionalCharges(
        token: String,
        childId: String? = null,
        month: String? = null,
    ): NetworkResult<ApiResponse<FeeAdditionalChargeListResponse>> = safeApiCall {
        client.get(getUrl("api/v1/school/fees/charges")) {
            childId?.takeIf { it.isNotBlank() }?.let { parameter("childId", it) }
            month?.takeIf { it.isNotBlank() }?.let { parameter("month", it) }
        }
    }

    suspend fun createAdditionalCharge(
        token: String,
        request: CreateFeeAdditionalChargeRequest,
    ): NetworkResult<ApiResponse<Unit>> = safeApiCall {
        client.post(getUrl("api/v1/school/fees/charges")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun deleteAdditionalCharge(
        token: String,
        id: String,
    ): NetworkResult<ApiResponse<Unit>> = safeApiCall {
        client.delete(getUrl("api/v1/school/fees/charges/$id"))
    }

    // ── Fee Payment Tracking ──────────────────────────────────────────────────

    suspend fun getFeeStudents(
        token: String,
        classId: String? = null,
        section: String? = null,
        month: String? = null,
        search: String? = null,
    ): NetworkResult<ApiResponse<FeeStudentListResponse>> = safeApiCall {
        client.get(getUrl("api/v1/school/fees/students")) {
            classId?.takeIf { it.isNotBlank() }?.let { parameter("classId", it) }
            section?.takeIf { it.isNotBlank() }?.let { parameter("section", it) }
            month?.takeIf { it.isNotBlank() }?.let { parameter("month", it) }
            search?.takeIf { it.isNotBlank() }?.let { parameter("search", it) }
        }
    }

    suspend fun markFeesPaid(
        token: String,
        request: MarkPaidRequest,
    ): NetworkResult<ApiResponse<Map<String, Int>>> = safeApiCall {
        client.post(getUrl("api/v1/school/fees/mark-paid")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun generateFees(
        token: String,
        request: GenerateFeesRequest,
    ): NetworkResult<ApiResponse<GenerateFeesResponse>> = safeApiCall {
        client.post(getUrl("api/v1/school/fees/generate")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    // ── Fee Reminder Config ───────────────────────────────────────────────────

    suspend fun getReminderConfig(
        token: String,
    ): NetworkResult<ApiResponse<FeeReminderConfigDto>> = safeApiCall {
        client.get(getUrl("api/v1/school/fees/reminder-config"))
    }

    suspend fun updateReminderConfig(
        token: String,
        request: UpdateFeeReminderConfigRequest,
    ): NetworkResult<ApiResponse<FeeReminderConfigDto>> = safeApiCall {
        client.put(getUrl("api/v1/school/fees/reminder-config")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    // ── Salary ────────────────────────────────────────────────────────────────

    suspend fun getSalaryRecords(
        token: String,
        teacherId: String? = null,
        month: String? = null,
    ): NetworkResult<ApiResponse<SalaryListResponse>> = safeApiCall {
        client.get(getUrl("api/v1/school/salary")) {
            teacherId?.takeIf { it.isNotBlank() }?.let { parameter("teacherId", it) }
            month?.takeIf { it.isNotBlank() }?.let { parameter("month", it) }
        }
    }

    suspend fun setSalary(
        token: String,
        request: SetSalaryRequest,
    ): NetworkResult<ApiResponse<SalaryRecordDto>> = safeApiCall {
        client.post(getUrl("api/v1/school/salary")) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun markSalaryPaid(
        token: String,
        id: String,
    ): NetworkResult<ApiResponse<Unit>> = safeApiCall {
        client.put(getUrl("api/v1/school/salary/$id/mark-paid"))
    }
}
