package com.littlebridge.enrollplus.feature.admin.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Fee Structure ─────────────────────────────────────────────────────────────

@Serializable
data class FeeStructureDto(
    val id: String,
    @SerialName("school_id") val schoolId: String,
    @SerialName("class_id") val classId: String? = null,
    val title: String,
    val description: String? = null,
    val amount: Double,
    val currency: String = "INR",
    val frequency: String = "MONTHLY",
    @SerialName("is_active") val isActive: Boolean = true,
)

@Serializable
data class FeeStructureListResponse(
    val structures: List<FeeStructureDto>,
)

@Serializable
data class CreateFeeStructureRequest(
    @SerialName("class_id") val classId: String? = null,
    val title: String,
    val description: String? = null,
    val amount: Double,
    val currency: String = "INR",
    val frequency: String = "MONTHLY",
)

@Serializable
data class UpdateFeeStructureRequest(
    val title: String,
    val description: String? = null,
    val amount: Double,
    val currency: String = "INR",
    val frequency: String = "MONTHLY",
    @SerialName("is_active") val isActive: Boolean = true,
)

// ── Fee Additional Charges ────────────────────────────────────────────────────

@Serializable
data class FeeAdditionalChargeDto(
    val id: String,
    @SerialName("child_id") val childId: String,
    @SerialName("child_name") val childName: String = "",
    @SerialName("class_id") val classId: String? = null,
    val month: String,
    val title: String,
    val description: String? = null,
    val amount: Double,
    val currency: String = "INR",
)

@Serializable
data class FeeAdditionalChargeListResponse(
    val charges: List<FeeAdditionalChargeDto>,
)

@Serializable
data class CreateFeeAdditionalChargeRequest(
    @SerialName("child_id") val childId: String,
    @SerialName("class_id") val classId: String? = null,
    val month: String,
    val title: String,
    val description: String? = null,
    val amount: Double,
    val currency: String = "INR",
)

// ── Fee Student List (Payment Tracking) ───────────────────────────────────────

@Serializable
data class FeeItemDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val amount: Double,
    val status: String,
    val category: String = "Tuition",
    val month: String? = null,
)

@Serializable
data class FeeStudentDto(
    @SerialName("child_id") val childId: String,
    @SerialName("child_name") val childName: String,
    @SerialName("parent_id") val parentId: String,
    @SerialName("class_name") val className: String? = null,
    val section: String? = null,
    val month: String,
    @SerialName("total_amount") val totalAmount: Double,
    @SerialName("paid_amount") val paidAmount: Double,
    @SerialName("due_amount") val dueAmount: Double,
    val status: String,
    @SerialName("fee_items") val feeItems: List<FeeItemDto> = emptyList(),
)

@Serializable
data class FeeStudentListResponse(
    val students: List<FeeStudentDto>,
    @SerialName("total_due") val totalDue: Double,
    @SerialName("total_paid") val totalPaid: Double,
    val currency: String = "INR",
)

@Serializable
data class MarkPaidRequest(
    @SerialName("child_id") val childId: String,
    val months: List<String>,
)

@Serializable
data class GenerateFeesRequest(
    val month: String,
    @SerialName("class_id") val classId: String? = null,
)

@Serializable
data class GenerateFeesResponse(
    val generated: Int,
    val skipped: Int,
)

// ── Fee Reminder Config ───────────────────────────────────────────────────────

@Serializable
data class FeeReminderConfigDto(
    @SerialName("reminder_day") val reminderDay: Int,
    @SerialName("is_active") val isActive: Boolean,
)

@Serializable
data class UpdateFeeReminderConfigRequest(
    @SerialName("reminder_day") val reminderDay: Int,
    @SerialName("is_active") val isActive: Boolean = true,
)

// ── Salary ────────────────────────────────────────────────────────────────────

@Serializable
data class SalaryRecordDto(
    val id: String,
    @SerialName("school_id") val schoolId: String,
    @SerialName("teacher_id") val teacherId: String,
    @SerialName("teacher_name") val teacherName: String = "",
    val month: String,
    @SerialName("base_salary") val baseSalary: Double,
    val allowances: Double = 0.0,
    val deductions: Double = 0.0,
    @SerialName("net_amount") val netAmount: Double,
    val currency: String = "INR",
    val status: String = "UNPAID",
    @SerialName("paid_at") val paidAt: String? = null,
    val notes: String? = null,
)

@Serializable
data class SalaryListResponse(
    val records: List<SalaryRecordDto>,
)

@Serializable
data class SetSalaryRequest(
    @SerialName("teacher_id") val teacherId: String,
    val month: String,
    @SerialName("base_salary") val baseSalary: Double,
    val allowances: Double = 0.0,
    val deductions: Double = 0.0,
    val notes: String? = null,
)

@Serializable
data class TeacherSalaryResponse(
    val records: List<SalaryRecordDto>,
)
