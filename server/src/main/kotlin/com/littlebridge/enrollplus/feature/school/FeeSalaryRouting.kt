/*
 * File: FeeSalaryRouting.kt
 * Module: feature.school
 *
 * Fee & Salary Management — Phase 1 (ledger-based, no payment gateway).
 *
 * Admin Fee Structure endpoints (JWT + school-scoped):
 *   GET    /api/v1/school/fees/structures              — list (optional classId filter)
 *   POST   /api/v1/school/fees/structures              — create
 *   PUT    /api/v1/school/fees/structures/{id}         — update
 *   DELETE /api/v1/school/fees/structures/{id}         — delete
 *
 * Admin Fee Additional Charges:
 *   GET    /api/v1/school/fees/charges                 — list (childId? month?)
 *   POST   /api/v1/school/fees/charges                 — create
 *   DELETE /api/v1/school/fees/charges/{id}            — delete
 *
 * Admin Fee Payment Tracking:
 *   GET    /api/v1/school/fees/students                — list students with fee status
 *   POST   /api/v1/school/fees/mark-paid               — mark fee record(s) as PAID
 *   POST   /api/v1/school/fees/generate                — generate monthly fee records
 *
 * Admin Fee Reminder Config:
 *   GET    /api/v1/school/fees/reminder-config         — get reminder day
 *   PUT    /api/v1/school/fees/reminder-config         — set reminder day
 *
 * Admin Salary:
 *   GET    /api/v1/school/salary                       — list (teacherId? month?)
 *   POST   /api/v1/school/salary                       — set/upsert salary
 *   PUT    /api/v1/school/salary/{id}/mark-paid        — mark salary as paid
 *
 * Teacher Salary:
 *   GET    /api/v1/teacher/salary                      — own salary history
 */
package com.littlebridge.enrollplus.feature.school

import com.littlebridge.enrollplus.core.created
import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.okMessage
import com.littlebridge.enrollplus.core.principalUserId
import com.littlebridge.enrollplus.core.principalUserUuid
import com.littlebridge.enrollplus.core.requireSchoolAdmin
import com.littlebridge.enrollplus.core.requireSchoolContext
import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.ChildrenTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.FeeAdditionalChargesTable
import com.littlebridge.enrollplus.db.FeeRecordsTable
import com.littlebridge.enrollplus.db.FeeReminderConfigTable
import com.littlebridge.enrollplus.db.FeeStructuresTable
import com.littlebridge.enrollplus.db.SalaryRecordsTable
import com.littlebridge.enrollplus.db.SchoolClassesTable
import com.littlebridge.enrollplus.feature.notifications.Notify
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

// ───────────────────────────── Fee Structure DTOs ─────────────────────────────

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

// ───────────────────────── Fee Additional Charge DTOs ─────────────────────────

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

// ──────────────────────── Fee Student List DTOs ───────────────────────────────

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

// ──────────────────────── Fee Reminder Config DTOs ────────────────────────────

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

// ─────────────────────────── Salary DTOs ──────────────────────────────────────

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

// ──────────────────────────── Helpers ─────────────────────────────────────────

private fun parseUuid(s: String?): UUID? = s?.let { runCatching { UUID.fromString(it) }.getOrNull() }

private fun money(amount: Double, currency: String): String {
    val symbol = when (currency.uppercase()) {
        "USD" -> "$"
        "INR" -> "₹"
        "EUR" -> "€"
        "GBP" -> "£"
        else -> "₹"
    }
    return "$symbol${"%,d".format(amount.toLong())}"
}

// ──────────────────────────── Routing ─────────────────────────────────────────

fun Route.feeSalaryRouting() {
    authenticate("jwt") {

        // ── Admin Fee Structures ──────────────────────────────────────────────
        route("/api/v1/school/fees/structures") {

            get {
                val ctx = call.requireSchoolContext() ?: return@get
                val classIdFilter = call.request.queryParameters["classId"]?.let { parseUuid(it) }
                val list = dbQuery {
                    FeeStructuresTable.selectAll()
                        .where {
                            val base: org.jetbrains.exposed.sql.Op<Boolean> = FeeStructuresTable.schoolId eq ctx.schoolId
                            if (classIdFilter != null) base and (FeeStructuresTable.classId eq classIdFilter) else base
                        }
                        .orderBy(FeeStructuresTable.title)
                        .map { row ->
                            FeeStructureDto(
                                id = row[FeeStructuresTable.id].value.toString(),
                                schoolId = row[FeeStructuresTable.schoolId].toString(),
                                classId = row[FeeStructuresTable.classId]?.toString(),
                                title = row[FeeStructuresTable.title],
                                description = row[FeeStructuresTable.description],
                                amount = row[FeeStructuresTable.amount],
                                currency = row[FeeStructuresTable.currency],
                                frequency = row[FeeStructuresTable.frequency],
                                isActive = row[FeeStructuresTable.isActive],
                            )
                        }
                }
                call.ok(FeeStructureListResponse(list), message = "Fee structures fetched")
            }

            post {
                val ctx = call.requireSchoolAdmin() ?: return@post
                val req = call.receive<CreateFeeStructureRequest>()
                if (req.title.isBlank() || req.amount < 0) {
                    call.fail("Title is required and amount must be >= 0", HttpStatusCode.BadRequest, "VALIDATION")
                    return@post
                }
                val classId = parseUuid(req.classId)
                val now = Instant.now()
                val newId = UUID.randomUUID()
                val dto = dbQuery {
                    val existing = FeeStructuresTable.selectAll()
                        .where {
                            (FeeStructuresTable.schoolId eq ctx.schoolId) and
                            (FeeStructuresTable.classId eq classId) and
                            (FeeStructuresTable.title eq req.title.trim())
                        }.firstOrNull()
                    if (existing != null) return@dbQuery null

                    FeeStructuresTable.insert {
                        it[FeeStructuresTable.id] = newId
                        it[FeeStructuresTable.schoolId] = ctx.schoolId
                        it[FeeStructuresTable.classId] = classId
                        it[FeeStructuresTable.title] = req.title.trim()
                        it[FeeStructuresTable.description] = req.description
                        it[FeeStructuresTable.amount] = req.amount
                        it[FeeStructuresTable.currency] = req.currency
                        it[FeeStructuresTable.frequency] = req.frequency
                        it[FeeStructuresTable.isActive] = true
                        it[FeeStructuresTable.createdAt] = now
                        it[FeeStructuresTable.updatedAt] = now
                    }
                    FeeStructureDto(
                        id = newId.toString(),
                        schoolId = ctx.schoolId.toString(),
                        classId = classId?.toString(),
                        title = req.title.trim(),
                        description = req.description,
                        amount = req.amount,
                        currency = req.currency,
                        frequency = req.frequency,
                        isActive = true,
                    )
                }
                if (dto != null) {
                    call.created(dto, "Fee structure created")
                } else {
                    call.fail("A fee structure with this title already exists", HttpStatusCode.Conflict, "DUPLICATE")
                }
            }

            put("/{id}") {
                val ctx = call.requireSchoolAdmin() ?: return@put
                val structId = parseUuid(call.parameters["id"]) ?: run {
                    call.fail("Invalid ID", HttpStatusCode.BadRequest, "BAD_REQUEST")
                    return@put
                }
                val req = call.receive<UpdateFeeStructureRequest>()
                if (req.title.isBlank() || req.amount < 0) {
                    call.fail("Title is required and amount must be >= 0", HttpStatusCode.BadRequest, "VALIDATION")
                    return@put
                }
                val now = Instant.now()
                val updated = dbQuery {
                    val count = FeeStructuresTable.update(
                        { (FeeStructuresTable.id eq structId) and (FeeStructuresTable.schoolId eq ctx.schoolId) }
                    ) {
                        it[FeeStructuresTable.title] = req.title.trim()
                        it[FeeStructuresTable.description] = req.description
                        it[FeeStructuresTable.amount] = req.amount
                        it[FeeStructuresTable.currency] = req.currency
                        it[FeeStructuresTable.frequency] = req.frequency
                        it[FeeStructuresTable.isActive] = req.isActive
                        it[FeeStructuresTable.updatedAt] = now
                    }
                    if (count == 0) return@dbQuery null
                    val r = FeeStructuresTable.selectAll().where { FeeStructuresTable.id eq structId }.first()
                    FeeStructureDto(
                        id = structId.toString(),
                        schoolId = ctx.schoolId.toString(),
                        classId = r[FeeStructuresTable.classId]?.toString(),
                        title = r[FeeStructuresTable.title],
                        description = r[FeeStructuresTable.description],
                        amount = r[FeeStructuresTable.amount],
                        currency = r[FeeStructuresTable.currency],
                        frequency = r[FeeStructuresTable.frequency],
                        isActive = r[FeeStructuresTable.isActive],
                    )
                }
                if (updated != null) {
                    call.ok(updated, message = "Fee structure updated")
                } else {
                    call.fail("Fee structure not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }

            delete("/{id}") {
                val ctx = call.requireSchoolAdmin() ?: return@delete
                val structId = parseUuid(call.parameters["id"]) ?: run {
                    call.fail("Invalid ID", HttpStatusCode.BadRequest, "BAD_REQUEST")
                    return@delete
                }
                val deleted = dbQuery {
                    FeeStructuresTable.deleteWhere {
                        (FeeStructuresTable.id eq structId) and (FeeStructuresTable.schoolId eq ctx.schoolId)
                    } > 0
                }
                if (deleted) {
                    call.okMessage("Fee structure deleted")
                } else {
                    call.fail("Fee structure not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
        }

        // ── Admin Fee Additional Charges ──────────────────────────────────────
        route("/api/v1/school/fees/charges") {

            get {
                val ctx = call.requireSchoolContext() ?: return@get
                val childIdFilter = call.request.queryParameters["childId"]?.let { parseUuid(it) }
                val monthFilter = call.request.queryParameters["month"]
                val list = dbQuery {
                    FeeAdditionalChargesTable.selectAll()
                        .where {
                            var cond: org.jetbrains.exposed.sql.Op<Boolean> = FeeAdditionalChargesTable.schoolId eq ctx.schoolId
                            if (childIdFilter != null) cond = cond and (FeeAdditionalChargesTable.childId eq childIdFilter)
                            if (monthFilter != null) cond = cond and (FeeAdditionalChargesTable.month eq monthFilter)
                            cond
                        }
                        .orderBy(FeeAdditionalChargesTable.createdAt, org.jetbrains.exposed.sql.SortOrder.DESC)
                        .map { row ->
                            val childName = ChildrenTable.selectAll()
                                .where { ChildrenTable.id eq row[FeeAdditionalChargesTable.childId] }
                                .firstOrNull()?.get(ChildrenTable.childName) ?: ""
                            FeeAdditionalChargeDto(
                                id = row[FeeAdditionalChargesTable.id].value.toString(),
                                childId = row[FeeAdditionalChargesTable.childId].toString(),
                                childName = childName,
                                classId = row[FeeAdditionalChargesTable.classId]?.toString(),
                                month = row[FeeAdditionalChargesTable.month],
                                title = row[FeeAdditionalChargesTable.title],
                                description = row[FeeAdditionalChargesTable.description],
                                amount = row[FeeAdditionalChargesTable.amount],
                                currency = row[FeeAdditionalChargesTable.currency],
                            )
                        }
                }
                call.ok(FeeAdditionalChargeListResponse(list), message = "Additional charges fetched")
            }

            post {
                val ctx = call.requireSchoolAdmin() ?: return@post
                val req = call.receive<CreateFeeAdditionalChargeRequest>()
                if (req.childId.isBlank() || req.month.isBlank() || req.title.isBlank()) {
                    call.fail("childId, month, and title are required", HttpStatusCode.BadRequest, "VALIDATION")
                    return@post
                }
                val childId = parseUuid(req.childId) ?: run {
                    call.fail("Invalid childId", HttpStatusCode.BadRequest, "BAD_REQUEST")
                    return@post
                }
                val classId = parseUuid(req.classId)
                val now = Instant.now()
                val newId = UUID.randomUUID()
                val childName = dbQuery {
                    ChildrenTable.selectAll().where { ChildrenTable.id eq childId }
                        .firstOrNull()?.get(ChildrenTable.childName) ?: ""
                }
                val dto = dbQuery {
                    FeeAdditionalChargesTable.insert {
                        it[FeeAdditionalChargesTable.id] = newId
                        it[FeeAdditionalChargesTable.schoolId] = ctx.schoolId
                        it[FeeAdditionalChargesTable.childId] = childId
                        it[FeeAdditionalChargesTable.classId] = classId
                        it[FeeAdditionalChargesTable.month] = req.month
                        it[FeeAdditionalChargesTable.title] = req.title.trim()
                        it[FeeAdditionalChargesTable.description] = req.description
                        it[FeeAdditionalChargesTable.amount] = req.amount
                        it[FeeAdditionalChargesTable.currency] = req.currency
                        it[FeeAdditionalChargesTable.createdAt] = now
                        it[FeeAdditionalChargesTable.updatedAt] = now
                    }
                    FeeAdditionalChargeDto(
                        id = newId.toString(),
                        childId = childId.toString(),
                        childName = childName,
                        classId = classId?.toString(),
                        month = req.month,
                        title = req.title.trim(),
                        description = req.description,
                        amount = req.amount,
                        currency = req.currency,
                    )
                }
                call.created(dto, "Additional charge added")
            }

            delete("/{id}") {
                val ctx = call.requireSchoolAdmin() ?: return@delete
                val chargeId = parseUuid(call.parameters["id"]) ?: run {
                    call.fail("Invalid ID", HttpStatusCode.BadRequest, "BAD_REQUEST")
                    return@delete
                }
                val deleted = dbQuery {
                    FeeAdditionalChargesTable.deleteWhere {
                        (FeeAdditionalChargesTable.id eq chargeId) and (FeeAdditionalChargesTable.schoolId eq ctx.schoolId)
                    } > 0
                }
                if (deleted) {
                    call.okMessage("Additional charge removed")
                } else {
                    call.fail("Charge not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
        }

        // ── Admin Fee Payment Tracking ────────────────────────────────────────
        route("/api/v1/school/fees/students") {

            get {
                val ctx = call.requireSchoolContext() ?: return@get
                val classIdFilter = call.request.queryParameters["classId"]?.let { parseUuid(it) }
                val sectionFilter = call.request.queryParameters["section"]
                val monthFilter = call.request.queryParameters["month"] ?: LocalDate.now().toString().substring(0, 7)
                val searchFilter = call.request.queryParameters["search"]?.trim()

                val students = dbQuery {
                    val children = ChildrenTable.selectAll()
                        .where {
                            val base: org.jetbrains.exposed.sql.Op<Boolean> = (ChildrenTable.schoolId eq ctx.schoolId) and (ChildrenTable.isActive eq true)
                            if (classIdFilter != null) base and (ChildrenTable.id eq classIdFilter) else base
                        }
                        .toList()

                    children.mapNotNull { child ->
                        val childId = child[ChildrenTable.id].value
                        val childName = child[ChildrenTable.childName]
                        val parentId = child[ChildrenTable.parentId]

                        if (searchFilter != null && !childName.contains(searchFilter, ignoreCase = true)) {
                            return@mapNotNull null
                        }

                        val feeRecords = FeeRecordsTable.selectAll()
                            .where {
                                (FeeRecordsTable.childId eq childId) and
                                (FeeRecordsTable.schoolId eq ctx.schoolId)
                            }.toList()

                        val monthRecords = feeRecords.filter { row ->
                            val dueDate = row[FeeRecordsTable.dueDate]
                            dueDate != null && dueDate.startsWith(monthFilter)
                        }

                        val totalAmount = monthRecords.sumOf { it[FeeRecordsTable.amount] }
                        val paidAmount = monthRecords.filter { it[FeeRecordsTable.status] == "PAID" }.sumOf { it[FeeRecordsTable.amount] }
                        val dueAmount = monthRecords.filter { it[FeeRecordsTable.status] in setOf("DUE", "OVERDUE") }.sumOf { it[FeeRecordsTable.amount] }
                        val status = when {
                            monthRecords.isEmpty() -> "NO_FEES"
                            dueAmount <= 0.0 -> "PAID"
                            paidAmount > 0.0 -> "PARTIAL"
                            else -> "DUE"
                        }

                        val feeItems = monthRecords.map { row ->
                            FeeItemDto(
                                id = row[FeeRecordsTable.id].value.toString(),
                                title = row[FeeRecordsTable.title],
                                description = row[FeeRecordsTable.description],
                                amount = row[FeeRecordsTable.amount],
                                status = row[FeeRecordsTable.status],
                                category = row[FeeRecordsTable.category],
                                month = row[FeeRecordsTable.dueDate]?.substring(0, 7),
                            )
                        }

                        FeeStudentDto(
                            childId = childId.toString(),
                            childName = childName,
                            parentId = parentId.toString(),
                            className = child[ChildrenTable.currentGrade],
                            section = null,
                            month = monthFilter,
                            totalAmount = totalAmount,
                            paidAmount = paidAmount,
                            dueAmount = dueAmount,
                            status = status,
                            feeItems = feeItems,
                        )
                    }
                }

                val totalDue = students.sumOf { it.dueAmount }
                val totalPaid = students.sumOf { it.paidAmount }
                call.ok(
                    FeeStudentListResponse(
                        students = students,
                        totalDue = totalDue,
                        totalPaid = totalPaid,
                        currency = "INR",
                    ),
                    message = "Fee students fetched"
                )
            }
        }

        route("/api/v1/school/fees/mark-paid") {
            post {
                val ctx = call.requireSchoolAdmin() ?: return@post
                val req = call.receive<MarkPaidRequest>()
                if (req.childId.isBlank() || req.months.isEmpty()) {
                    call.fail("childId and months are required", HttpStatusCode.BadRequest, "VALIDATION")
                    return@post
                }
                val childId = parseUuid(req.childId) ?: run {
                    call.fail("Invalid childId", HttpStatusCode.BadRequest, "BAD_REQUEST")
                    return@post
                }
                val now = Instant.now()
                val updatedCount = dbQuery {
                    var count = 0
                    for (month in req.months) {
                        val rows = FeeRecordsTable.selectAll()
                            .where {
                                (FeeRecordsTable.childId eq childId) and
                                (FeeRecordsTable.schoolId eq ctx.schoolId) and
                                (FeeRecordsTable.status inList listOf("DUE", "OVERDUE"))
                            }.toList()
                            .filter { row ->
                                val dueDate = row[FeeRecordsTable.dueDate]
                                dueDate != null && dueDate.startsWith(month)
                            }

                        for (row in rows) {
                            val feeId = row[FeeRecordsTable.id].value
                            FeeRecordsTable.update({ FeeRecordsTable.id eq feeId }) {
                                it[FeeRecordsTable.status] = "PAID"
                                it[FeeRecordsTable.updatedAt] = now
                            }
                            count++
                        }
                    }
                    count
                }
                call.ok(mapOf("updated" to updatedCount), message = "Fees marked as paid")
            }
        }

        route("/api/v1/school/fees/generate") {
            post {
                val ctx = call.requireSchoolAdmin() ?: return@post
                val req = call.receive<GenerateFeesRequest>()
                if (req.month.isBlank()) {
                    call.fail("month is required (YYYY-MM)", HttpStatusCode.BadRequest, "VALIDATION")
                    return@post
                }
                val classIdFilter = parseUuid(req.classId)
                val now = Instant.now()
                val dueDate = "${req.month}-05"

                val result = dbQuery {
                    val structures = FeeStructuresTable.selectAll()
                        .where {
                            val base: org.jetbrains.exposed.sql.Op<Boolean> = (FeeStructuresTable.schoolId eq ctx.schoolId) and (FeeStructuresTable.isActive eq true)
                            if (classIdFilter != null) base and (FeeStructuresTable.classId eq classIdFilter) else base
                        }
                        .toList()

                    if (structures.isEmpty()) return@dbQuery GenerateFeesResponse(0, 0)

                    val children = ChildrenTable.selectAll()
                        .where {
                            val base: org.jetbrains.exposed.sql.Op<Boolean> = (ChildrenTable.schoolId eq ctx.schoolId) and (ChildrenTable.isActive eq true)
                            if (classIdFilter != null) base and (ChildrenTable.id eq classIdFilter) else base
                        }
                        .toList()

                    var generated = 0
                    var skipped = 0

                    for (child in children) {
                        val childId = child[ChildrenTable.id].value
                        val parentId = child[ChildrenTable.parentId]

                        for (struct in structures) {
                            val title = struct[FeeStructuresTable.title]
                            val amount = struct[FeeStructuresTable.amount]
                            val currency = struct[FeeStructuresTable.currency]
                            val category = struct[FeeStructuresTable.title]

                            val existing = FeeRecordsTable.selectAll()
                                .where {
                                    (FeeRecordsTable.childId eq childId) and
                                    (FeeRecordsTable.schoolId eq ctx.schoolId) and
                                    (FeeRecordsTable.title eq title) and
                                    (FeeRecordsTable.dueDate eq dueDate)
                                }.firstOrNull()

                            if (existing != null) {
                                skipped++
                                continue
                            }

                            FeeRecordsTable.insert {
                                it[FeeRecordsTable.id] = UUID.randomUUID()
                                it[FeeRecordsTable.parentId] = parentId
                                it[FeeRecordsTable.childId] = childId
                                it[FeeRecordsTable.schoolId] = ctx.schoolId
                                it[FeeRecordsTable.title] = title
                                it[FeeRecordsTable.description] = struct[FeeStructuresTable.description]
                                it[FeeRecordsTable.amount] = amount
                                it[FeeRecordsTable.currency] = currency
                                it[FeeRecordsTable.dueDate] = dueDate
                                it[FeeRecordsTable.status] = "DUE"
                                it[FeeRecordsTable.category] = category
                                it[FeeRecordsTable.createdAt] = now
                                it[FeeRecordsTable.updatedAt] = now
                            }
                            generated++
                        }

                        val additionalCharges = FeeAdditionalChargesTable.selectAll()
                            .where {
                                (FeeAdditionalChargesTable.childId eq childId) and
                                (FeeAdditionalChargesTable.month eq req.month)
                            }.toList()

                        for (charge in additionalCharges) {
                            val chargeTitle = charge[FeeAdditionalChargesTable.title]
                            val existing = FeeRecordsTable.selectAll()
                                .where {
                                    (FeeRecordsTable.childId eq childId) and
                                    (FeeRecordsTable.schoolId eq ctx.schoolId) and
                                    (FeeRecordsTable.title eq chargeTitle) and
                                    (FeeRecordsTable.dueDate eq dueDate)
                                }.firstOrNull()

                            if (existing != null) {
                                skipped++
                                continue
                            }

                            FeeRecordsTable.insert {
                                it[FeeRecordsTable.id] = UUID.randomUUID()
                                it[FeeRecordsTable.parentId] = parentId
                                it[FeeRecordsTable.childId] = childId
                                it[FeeRecordsTable.schoolId] = ctx.schoolId
                                it[FeeRecordsTable.title] = chargeTitle
                                it[FeeRecordsTable.description] = charge[FeeAdditionalChargesTable.description]
                                it[FeeRecordsTable.amount] = charge[FeeAdditionalChargesTable.amount]
                                it[FeeRecordsTable.currency] = charge[FeeAdditionalChargesTable.currency]
                                it[FeeRecordsTable.dueDate] = dueDate
                                it[FeeRecordsTable.status] = "DUE"
                                it[FeeRecordsTable.category] = "Additional"
                                it[FeeRecordsTable.createdAt] = now
                                it[FeeRecordsTable.updatedAt] = now
                            }
                            generated++
                        }
                    }

                    GenerateFeesResponse(generated = generated, skipped = skipped)
                }
                call.ok(result, message = "Fees generated for ${req.month}")
            }
        }

        // ── Admin Fee Reminder Config ─────────────────────────────────────────
        route("/api/v1/school/fees/reminder-config") {

            get {
                val ctx = call.requireSchoolContext() ?: return@get
                val config = dbQuery {
                    val row = FeeReminderConfigTable.selectAll()
                        .where { FeeReminderConfigTable.schoolId eq ctx.schoolId }
                        .firstOrNull()
                    FeeReminderConfigDto(
                        reminderDay = row?.get(FeeReminderConfigTable.reminderDay) ?: 5,
                        isActive = row?.get(FeeReminderConfigTable.isActive) ?: true,
                    )
                }
                call.ok(config, message = "Reminder config fetched")
            }

            put {
                val ctx = call.requireSchoolAdmin() ?: return@put
                val req = call.receive<UpdateFeeReminderConfigRequest>()
                if (req.reminderDay < 1 || req.reminderDay > 28) {
                    call.fail("reminderDay must be between 1 and 28", HttpStatusCode.BadRequest, "VALIDATION")
                    return@put
                }
                val now = Instant.now()
                dbQuery {
                    val existing = FeeReminderConfigTable.selectAll()
                        .where { FeeReminderConfigTable.schoolId eq ctx.schoolId }
                        .firstOrNull()
                    if (existing != null) {
                        FeeReminderConfigTable.update({ FeeReminderConfigTable.schoolId eq ctx.schoolId }) {
                            it[FeeReminderConfigTable.reminderDay] = req.reminderDay
                            it[FeeReminderConfigTable.isActive] = req.isActive
                            it[FeeReminderConfigTable.updatedAt] = now
                        }
                    } else {
                        FeeReminderConfigTable.insert {
                            it[FeeReminderConfigTable.schoolId] = ctx.schoolId
                            it[FeeReminderConfigTable.reminderDay] = req.reminderDay
                            it[FeeReminderConfigTable.isActive] = req.isActive
                            it[FeeReminderConfigTable.updatedAt] = now
                        }
                    }
                }
                call.ok(
                    FeeReminderConfigDto(reminderDay = req.reminderDay, isActive = req.isActive),
                    message = "Reminder config updated"
                )
            }
        }

        // ── Admin Salary ──────────────────────────────────────────────────────
        route("/api/v1/school/salary") {

            get {
                val ctx = call.requireSchoolContext() ?: return@get
                val teacherIdFilter = call.request.queryParameters["teacherId"]?.let { parseUuid(it) }
                val monthFilter = call.request.queryParameters["month"]
                val records = dbQuery {
                    SalaryRecordsTable.selectAll()
                        .where {
                            var cond: org.jetbrains.exposed.sql.Op<Boolean> = SalaryRecordsTable.schoolId eq ctx.schoolId
                            if (teacherIdFilter != null) cond = cond and (SalaryRecordsTable.teacherId eq teacherIdFilter)
                            if (monthFilter != null) cond = cond and (SalaryRecordsTable.month eq monthFilter)
                            cond
                        }
                        .orderBy(SalaryRecordsTable.month, org.jetbrains.exposed.sql.SortOrder.DESC)
                        .map { row ->
                            val teacherName = AppUsersTable.selectAll()
                                .where { AppUsersTable.id eq row[SalaryRecordsTable.teacherId] }
                                .firstOrNull()?.get(AppUsersTable.fullName) ?: ""
                            SalaryRecordDto(
                                id = row[SalaryRecordsTable.id].value.toString(),
                                schoolId = row[SalaryRecordsTable.schoolId].toString(),
                                teacherId = row[SalaryRecordsTable.teacherId].toString(),
                                teacherName = teacherName,
                                month = row[SalaryRecordsTable.month],
                                baseSalary = row[SalaryRecordsTable.baseSalary],
                                allowances = row[SalaryRecordsTable.allowances],
                                deductions = row[SalaryRecordsTable.deductions],
                                netAmount = row[SalaryRecordsTable.netAmount],
                                currency = row[SalaryRecordsTable.currency],
                                status = row[SalaryRecordsTable.status],
                                paidAt = row[SalaryRecordsTable.paidAt]?.toString(),
                                notes = row[SalaryRecordsTable.notes],
                            )
                        }
                }
                call.ok(SalaryListResponse(records), message = "Salary records fetched")
            }

            post {
                val ctx = call.requireSchoolAdmin() ?: return@post
                val req = call.receive<SetSalaryRequest>()
                if (req.teacherId.isBlank() || req.month.isBlank()) {
                    call.fail("teacherId and month are required", HttpStatusCode.BadRequest, "VALIDATION")
                    return@post
                }
                val teacherId = parseUuid(req.teacherId) ?: run {
                    call.fail("Invalid teacherId", HttpStatusCode.BadRequest, "BAD_REQUEST")
                    return@post
                }
                val netAmount = req.baseSalary + req.allowances - req.deductions
                val now = Instant.now()
                val newId = UUID.randomUUID()
                val teacherName = dbQuery {
                    AppUsersTable.selectAll().where { AppUsersTable.id eq teacherId }
                        .firstOrNull()?.get(AppUsersTable.fullName) ?: ""
                }
                val dto = dbQuery {
                    val existing = SalaryRecordsTable.selectAll()
                        .where {
                            (SalaryRecordsTable.schoolId eq ctx.schoolId) and
                            (SalaryRecordsTable.teacherId eq teacherId) and
                            (SalaryRecordsTable.month eq req.month)
                        }.firstOrNull()

                    if (existing != null) {
                        val existingId = existing[SalaryRecordsTable.id].value
                        SalaryRecordsTable.update({ SalaryRecordsTable.id eq existingId }) {
                            it[SalaryRecordsTable.baseSalary] = req.baseSalary
                            it[SalaryRecordsTable.allowances] = req.allowances
                            it[SalaryRecordsTable.deductions] = req.deductions
                            it[SalaryRecordsTable.netAmount] = netAmount
                            it[SalaryRecordsTable.notes] = req.notes
                            it[SalaryRecordsTable.updatedAt] = now
                        }
                        SalaryRecordDto(
                            id = existingId.toString(),
                            schoolId = ctx.schoolId.toString(),
                            teacherId = teacherId.toString(),
                            teacherName = teacherName,
                            month = req.month,
                            baseSalary = req.baseSalary,
                            allowances = req.allowances,
                            deductions = req.deductions,
                            netAmount = netAmount,
                            currency = "INR",
                            status = existing[SalaryRecordsTable.status],
                            paidAt = existing[SalaryRecordsTable.paidAt]?.toString(),
                            notes = req.notes,
                        )
                    } else {
                        SalaryRecordsTable.insert {
                            it[SalaryRecordsTable.id] = newId
                            it[SalaryRecordsTable.schoolId] = ctx.schoolId
                            it[SalaryRecordsTable.teacherId] = teacherId
                            it[SalaryRecordsTable.month] = req.month
                            it[SalaryRecordsTable.baseSalary] = req.baseSalary
                            it[SalaryRecordsTable.allowances] = req.allowances
                            it[SalaryRecordsTable.deductions] = req.deductions
                            it[SalaryRecordsTable.netAmount] = netAmount
                            it[SalaryRecordsTable.notes] = req.notes
                            it[SalaryRecordsTable.createdAt] = now
                            it[SalaryRecordsTable.updatedAt] = now
                        }
                        SalaryRecordDto(
                            id = newId.toString(),
                            schoolId = ctx.schoolId.toString(),
                            teacherId = teacherId.toString(),
                            teacherName = teacherName,
                            month = req.month,
                            baseSalary = req.baseSalary,
                            allowances = req.allowances,
                            deductions = req.deductions,
                            netAmount = netAmount,
                            currency = "INR",
                            status = "UNPAID",
                            paidAt = null,
                            notes = req.notes,
                        )
                    }
                }
                call.ok(dto, message = "Salary set")
            }

            put("/{id}/mark-paid") {
                val ctx = call.requireSchoolAdmin() ?: return@put
                val salaryId = parseUuid(call.parameters["id"]) ?: run {
                    call.fail("Invalid ID", HttpStatusCode.BadRequest, "BAD_REQUEST")
                    return@put
                }
                val now = Instant.now()
                val updated = dbQuery {
                    val count = SalaryRecordsTable.update(
                        { (SalaryRecordsTable.id eq salaryId) and (SalaryRecordsTable.schoolId eq ctx.schoolId) }
                    ) {
                        it[SalaryRecordsTable.status] = "PAID"
                        it[SalaryRecordsTable.paidAt] = now
                        it[SalaryRecordsTable.updatedAt] = now
                    }
                    count > 0
                }
                if (updated) {
                    call.okMessage("Salary marked as paid")
                } else {
                    call.fail("Salary record not found", HttpStatusCode.NotFound, "NOT_FOUND")
                }
            }
        }
    }
}

// ─────────────────────── Teacher Salary Route ─────────────────────────────────

fun Route.teacherSalaryRouting() {
    authenticate("jwt") {
        route("/api/v1/teacher/salary") {
            get {
                val uid = call.principalUserUuid() ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized, "UNAUTHORIZED")
                    return@get
                }
                val records = dbQuery {
                    SalaryRecordsTable.selectAll()
                        .where { SalaryRecordsTable.teacherId eq uid }
                        .orderBy(SalaryRecordsTable.month, org.jetbrains.exposed.sql.SortOrder.DESC)
                        .map { row ->
                            SalaryRecordDto(
                                id = row[SalaryRecordsTable.id].value.toString(),
                                schoolId = row[SalaryRecordsTable.schoolId].toString(),
                                teacherId = row[SalaryRecordsTable.teacherId].toString(),
                                teacherName = "",
                                month = row[SalaryRecordsTable.month],
                                baseSalary = row[SalaryRecordsTable.baseSalary],
                                allowances = row[SalaryRecordsTable.allowances],
                                deductions = row[SalaryRecordsTable.deductions],
                                netAmount = row[SalaryRecordsTable.netAmount],
                                currency = row[SalaryRecordsTable.currency],
                                status = row[SalaryRecordsTable.status],
                                paidAt = row[SalaryRecordsTable.paidAt]?.toString(),
                                notes = row[SalaryRecordsTable.notes],
                            )
                        }
                }
                call.ok(TeacherSalaryResponse(records), message = "Salary history fetched")
            }
        }
    }
}
