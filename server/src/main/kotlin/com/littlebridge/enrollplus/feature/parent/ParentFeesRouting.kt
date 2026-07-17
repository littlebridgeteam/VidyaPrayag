/*
 * File: ParentFeesRouting.kt
 * Module: feature.parent
 *
 * Endpoint: GET /api/v1/parent/fees   (JWT)
 *
 * Spec ref: parent_api_spec.artifact.md §Module: School Management §Screen: Fees
 *
 * Aggregates the parent's fee_records into the stats block expected by the
 * UI and appends fee-related announcements from a SCHOOL-SCOPED CMS key
 * app_config["parent_fees_announcements:<schoolId>"] (RA-26), falling back to
 * the legacy global key app_config["parent_fees_announcements"] and then to a
 * static default (so ops can edit deadlines without redeploying, and each
 * school can publish its own copy).
 *
 * Aggregation rules:
 *   total_collected = SUM(amount WHERE status = 'PAID')
 *   outstanding     = SUM(amount WHERE status IN ('DUE','OVERDUE'))
 *   overdue_count   = COUNT(* WHERE status = 'OVERDUE')
 *   progress        = total_collected / (total_collected + outstanding)
 *                     (returns 0.0 when no rows exist; coerced to [0,1])
 */
package com.littlebridge.enrollplus.feature.parent

import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.principalUserId
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.FeeAdditionalChargesTable
import com.littlebridge.enrollplus.db.FeeLateFeeTiersTable
import com.littlebridge.enrollplus.db.FeeRecordsTable
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

/**
 * Response DTO that MUST stay field-for-field identical to the client model
 * `shared/.../parent/domain/model/ParentFeatureModels.kt#FeeData`. The client
 * wraps this in `FeeResponse{success, data: FeeData}` and deserializes against
 * the canonical `{success, message, data}` envelope, so `data` here is exactly
 * `FeeData`. Any drift causes a kotlinx.serialization MissingFieldException and
 * crashes the Parent Fees tab on open (VM auto-loads in init).
 */
@Serializable
data class FeesAnnouncement(
    val id: String,
    val title: String,
    val time: String,
    val description: String,
    @SerialName("open_rate") val openRate: String,
    val engagement: String,
    val type: String
)

@Serializable
data class ParentFeeItemDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val amount: Double,
    val status: String,
    val category: String = "Tuition",
    val month: String? = null,
    val currency: String = "INR",
)

@Serializable
data class MonthlyFeeSummary(
    val month: String,
    @SerialName("total_amount") val totalAmount: Double,
    @SerialName("paid_amount") val paidAmount: Double,
    @SerialName("due_amount") val dueAmount: Double,
    val status: String,
    val items: List<ParentFeeItemDto> = emptyList(),
    val currency: String = "INR",
)

@Serializable
data class ParentFeesResponse(
    @SerialName("total_collected") val totalCollected: String,
    @SerialName("collection_progress") val collectionProgress: Float,
    @SerialName("outstanding_fees") val outstandingFees: String,
    @SerialName("overdue_count") val overdueCount: Int,
    val announcements: List<FeesAnnouncement>,
    @SerialName("fee_items") val feeItems: List<ParentFeeItemDto> = emptyList(),
    @SerialName("monthly_summary") val monthlySummary: List<MonthlyFeeSummary> = emptyList(),
)

private fun money(amount: Double, currency: String): String {
    // RA-25: India-first product — default to ₹ (INR), matching the
    // FeeRecordsTable.currency column default. Other codes still map correctly.
    val symbol = when (currency.uppercase()) {
        "USD" -> "$"
        "INR" -> "₹"
        "EUR" -> "€"
        "GBP" -> "£"
        else  -> "₹"
    }
    return "$symbol${"%,d".format(amount.toLong())}"
}

@Serializable
data class PayFeeRequest(
    val feeId: String,
    val paymentMethod: String? = null,
)

fun Route.parentFeesRouting() {
    authenticate("jwt") {
        route("/api/v1/parent") {
            get("/fees") {
                val uid = call.principalUserId()?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized); return@get
                }

                // RA-S05: optional ?child_id= scopes the fee aggregation to a
                // single child. Parsed defensively (a malformed id is ignored,
                // not an error). Filtering is applied in-memory after the
                // parent-scoped fetch to stay Postgres-portable (no extra WHERE
                // that would need an index) and to keep tenancy on parentId.
                val childIdFilter = call.request.queryParameters["child_id"]
                    ?.let { runCatching { UUID.fromString(it) }.getOrNull() }

                val today = LocalDate.now().toString()
                val now = Instant.now()

                // Auto-transition: mark DUE fees as OVERDUE when dueDate < today.
    // Also apply late fee tier charges based on days past due date.
    // This runs before the query so the returned stats reflect reality.
    dbQuery {
        // Fetch late fee tiers for the parent's school (active only)
        val schoolId = FeeRecordsTable.selectAll()
            .where { FeeRecordsTable.parentId eq uid }
            .firstOrNull()
            ?.get(FeeRecordsTable.schoolId)

        if (schoolId != null) {
            val tiers = FeeLateFeeTiersTable.selectAll()
                .where {
                    (FeeLateFeeTiersTable.schoolId eq schoolId) and
                    (FeeLateFeeTiersTable.isActive eq true)
                }
                .orderBy(FeeLateFeeTiersTable.daysAfterDue)
                .toList()

            val overdueFees = FeeRecordsTable.selectAll()
                .where {
                    (FeeRecordsTable.parentId eq uid) and
                    (FeeRecordsTable.status eq "DUE") and
                    (FeeRecordsTable.dueDate less today)
                }
                .toList()

            overdueFees.forEach { fee ->
                val feeId = fee[FeeRecordsTable.id].value
                val dueDateStr = fee[FeeRecordsTable.dueDate] ?: return@forEach
                val daysPast = try {
                    java.time.temporal.ChronoUnit.DAYS.between(
                        java.time.LocalDate.parse(dueDateStr),
                        java.time.LocalDate.now()
                    ).toInt()
                } catch (_: Exception) { 0 }

                val applicableTier = tiers.lastOrNull { it[FeeLateFeeTiersTable.daysAfterDue] <= daysPast }

                FeeRecordsTable.update({
                    FeeRecordsTable.id eq feeId
                }) {
                    it[FeeRecordsTable.status] = "OVERDUE"
                    it[FeeRecordsTable.updatedAt] = now
                }

                if (applicableTier != null) {
                    val tierAmount = applicableTier[FeeLateFeeTiersTable.amount]
                    val tierDays = applicableTier[FeeLateFeeTiersTable.daysAfterDue]
                    val childId = fee[FeeRecordsTable.childId]

                    if (childId != null) {
                        val alreadyApplied = FeeAdditionalChargesTable.selectAll()
                            .where {
                                (FeeAdditionalChargesTable.childId eq childId) and
                                (FeeAdditionalChargesTable.title eq "Late fee (${tierDays} days)")
                            }
                            .count() > 0

                        if (!alreadyApplied) {
                            val feeMonth = dueDateStr.substring(0, 7)
                            FeeAdditionalChargesTable.insert {
                                it[FeeAdditionalChargesTable.id] = UUID.randomUUID()
                                it[FeeAdditionalChargesTable.schoolId] = schoolId
                                it[FeeAdditionalChargesTable.childId] = childId
                                it[FeeAdditionalChargesTable.classId] = null
                                it[FeeAdditionalChargesTable.month] = feeMonth
                                it[FeeAdditionalChargesTable.title] = "Late fee (${tierDays} days)"
                                it[FeeAdditionalChargesTable.description] = "Auto-applied late fee for payment ${daysPast} days overdue"
                                it[FeeAdditionalChargesTable.amount] = tierAmount
                                it[FeeAdditionalChargesTable.currency] = fee[FeeRecordsTable.currency]
                                it[FeeAdditionalChargesTable.createdAt] = now
                                it[FeeAdditionalChargesTable.updatedAt] = now
                            }
                        }
                    }
                }
            }
        } else {
            // No schoolId — just do the simple overdue transition
            FeeRecordsTable.update({
                (FeeRecordsTable.parentId eq uid) and
                (FeeRecordsTable.status eq "DUE") and
                (FeeRecordsTable.dueDate less today)
            }) {
                it[FeeRecordsTable.status] = "OVERDUE"
                it[FeeRecordsTable.updatedAt] = now
            }
        }
    }

                val response = dbQuery {
                    val rows = FeeRecordsTable.selectAll()
                        .where { FeeRecordsTable.parentId eq uid }
                        .toList()
                        // RA-S05: when a child is selected, only that child's records
                        // count toward the stats. Records with a null child_id
                        // (school-wide / unassigned) are excluded from a per-child view.
                        .let { all ->
                            if (childIdFilter == null) all
                            else all.filter { it[FeeRecordsTable.childId] == childIdFilter }
                        }

                    // RA-25: fall back to INR (India-first) when the parent has no
                    // fee record yet, instead of USD.
                    val currency = rows.firstOrNull()?.get(FeeRecordsTable.currency) ?: "INR"
                    val collected = rows.filter { it[FeeRecordsTable.status] == "PAID" }
                        .sumOf { it[FeeRecordsTable.amount] }
                    val outstanding = rows.filter { it[FeeRecordsTable.status] in setOf("DUE", "OVERDUE") }
                        .sumOf { it[FeeRecordsTable.amount] }
                    val overdueCount = rows.count { it[FeeRecordsTable.status] == "OVERDUE" }

                    val total = collected + outstanding
                    val progress = if (total <= 0.0) 0f
                                   else (collected / total).coerceIn(0.0, 1.0).toFloat()

                    val feeItems = rows.map { row ->
                        ParentFeeItemDto(
                            id = row[FeeRecordsTable.id].value.toString(),
                            title = row[FeeRecordsTable.title],
                            description = row[FeeRecordsTable.description],
                            amount = row[FeeRecordsTable.amount],
                            status = row[FeeRecordsTable.status],
                            category = row[FeeRecordsTable.category],
                            month = row[FeeRecordsTable.dueDate]?.substring(0, 7),
                            currency = row[FeeRecordsTable.currency],
                        )
                    }

                    // ── Monthly summary: group fee items by month ──
                    val monthlySummary = feeItems
                        .groupBy { it.month ?: "Unknown" }
                        .map { (month, items) ->
                            val total = items.sumOf { it.amount }
                            val paid = items.filter { it.status == "PAID" }.sumOf { it.amount }
                            val due = items.filter { it.status in setOf("DUE", "OVERDUE") }.sumOf { it.amount }
                            val status = when {
                                due <= 0.0 -> "PAID"
                                items.any { it.status == "OVERDUE" } -> "OVERDUE"
                                else -> "DUE"
                            }
                            MonthlyFeeSummary(
                                month = month,
                                totalAmount = total,
                                paidAmount = paid,
                                dueAmount = due,
                                status = status,
                                items = items,
                                currency = currency,
                            )
                        }
                        .sortedByDescending { it.month }

                    // ── Announcements derived from actual fee data (one per month with unpaid fees) ──
                    val announcements = monthlySummary
                        .filter { it.dueAmount > 0.0 }
                        .map { ms ->
                            val type = if (ms.status == "OVERDUE") "Emergency" else "Payment"
                            val desc = "${ms.items.size} fee item(s) — ${money(ms.dueAmount, currency)} due"
                            FeesAnnouncement(
                                id = "month_${ms.month}",
                                title = "Fees due for ${ms.month}",
                                time = if (ms.status == "OVERDUE") "Overdue" else "Pending",
                                description = desc,
                                openRate = "0%",
                                engagement = "0",
                                type = type,
                            )
                        }

                    ParentFeesResponse(
                        totalCollected = money(collected, currency),
                        collectionProgress = progress,
                        outstandingFees = money(outstanding, currency),
                        overdueCount = overdueCount,
                        announcements = announcements,
                        feeItems = feeItems,
                        monthlySummary = monthlySummary,
                    )
                }

                call.ok(response, message = "Fee status fetched successfully")
            }

            post("/fees/pay") {
                val uid = call.principalUserId()?.let { runCatching { UUID.fromString(it) }.getOrNull() } ?: run {
                    call.fail("Invalid token", HttpStatusCode.Unauthorized); return@post
                }

                val req = runCatching { call.receive<PayFeeRequest>() }.getOrNull() ?: run {
                    call.fail("Invalid request body", HttpStatusCode.BadRequest, "INVALID_REQUEST"); return@post
                }

                val feeId = runCatching { UUID.fromString(req.feeId) }.getOrNull() ?: run {
                    call.fail("Invalid fee ID", HttpStatusCode.BadRequest, "INVALID_FEE_ID"); return@post
                }

                val now = Instant.now()
                val updated = dbQuery {
                    val ownership = FeeRecordsTable.selectAll()
                        .where { (FeeRecordsTable.id eq feeId) and (FeeRecordsTable.parentId eq uid) }
                        .singleOrNull() ?: return@dbQuery false

                    if (ownership[FeeRecordsTable.status] == "PAID") return@dbQuery true

                    FeeRecordsTable.update({ FeeRecordsTable.id eq feeId }) {
                        it[FeeRecordsTable.status] = "PAID"
                        it[FeeRecordsTable.updatedAt] = now
                    }
                    true
                }

                if (updated == false) {
                    call.fail("Fee record not found or not owned by this parent", HttpStatusCode.NotFound, "FEE_NOT_FOUND"); return@post
                }

                call.ok(
                    mapOf("feeId" to req.feeId, "status" to "PAID", "paidAt" to now.toString()),
                    message = "Payment recorded successfully",
                )
            }
        }
    }
}
