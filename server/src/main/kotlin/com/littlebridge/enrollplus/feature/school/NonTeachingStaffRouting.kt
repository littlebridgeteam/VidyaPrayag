/*
 * File: NonTeachingStaffRouting.kt
 * Module: feature.school
 *
 * RA-S17: the Admin People tab demanded a third vertical — "Non-teaching staff"
 * (office, accounts, library, support, security, transport, …) — that did NOT
 * exist anywhere in the codebase (no table, no route, no model, no UI). This
 * builds the server end of that vertical end-to-end, mirroring the student
 * roster pattern (school-scoped, soft-delete, admin-gated writes).
 *
 * Non-teaching staff are roster records, NOT app_users — they do not log in.
 *
 * Endpoints (JWT + school-scoped; school_id resolved from JWT, never the body):
 *   GET    /api/v1/school/staff            list active staff (optional ?q=&department=)
 *   POST   /api/v1/school/staff            add a staff member (school-admin)
 *   GET    /api/v1/school/staff/{id}       single staff profile
 *   PATCH  /api/v1/school/staff/{id}       edit a staff member (school-admin)
 *   DELETE /api/v1/school/staff/{id}       soft-delete a staff member (school-admin)
 *
 * Every read/write is constrained to ctx.schoolId (IDOR-safe). Deletion is a
 * soft-delete (is_active=false) performed from the staff profile behind a
 * confirm dialog on the client (RA-S17 directive — no direct list-row delete).
 */
package com.littlebridge.enrollplus.feature.school

import com.littlebridge.enrollplus.core.created
import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.okMessage
import com.littlebridge.enrollplus.core.requireSchoolAdmin
import com.littlebridge.enrollplus.core.requireSchoolContext
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.NonTeachingStaffTable
import com.littlebridge.enrollplus.db.StaffCheckInsTable
import com.littlebridge.enrollplus.db.StaffShiftsTable
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

// ───────────────────────────── DTOs ─────────────────────────────

@Serializable
data class StaffDto(
    val id: String,
    @SerialName("full_name") val fullName: String,
    val role: String,
    val department: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    // People Tab enrichment — new fields for enriched card UI.
    // All defaulted so older clients keep parsing; all DERIVED server-side.
    @SerialName("employee_id") val employeeId: String? = null,
    val shift: String? = null,
    val status: String = "active",
    @SerialName("joined_year") val joinedYear: String? = null,
    // Formatted "MMM yyyy" join label (e.g. "Jan 2020") for the profile hero /
    // Professional details. DERIVED from created_at server-side.
    @SerialName("joined_date") val joinedDate: String? = null,
    @SerialName("today_items") val todayItems: List<TodayItemDto> = emptyList()
)

@Serializable
data class StaffListResponse(val staff: List<StaffDto>)

@Serializable
data class StaffPaginationDto(
    val page: Int,
    val pageSize: Int,
    val totalRecords: Int,
    val hasNext: Boolean
)

@Serializable
data class StaffListPaginatedResponse(
    val staff: List<StaffDto>,
    val pagination: StaffPaginationDto
)

@Serializable
data class CreateStaffRequest(
    @SerialName("full_name") val fullName: String,
    val role: String,
    val department: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null
)

@Serializable
data class UpdateStaffRequest(
    @SerialName("full_name") val fullName: String? = null,
    val role: String? = null,
    val department: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null
)

// ─────────────────────────── helpers ────────────────────────────

private fun staffRowToDto(row: ResultRow): StaffDto =
    StaffDto(
        id = row[NonTeachingStaffTable.id].value.toString(),
        fullName = row[NonTeachingStaffTable.fullName],
        role = row[NonTeachingStaffTable.role],
        department = row[NonTeachingStaffTable.department],
        phone = row[NonTeachingStaffTable.phone],
        email = row[NonTeachingStaffTable.email],
        address = row[NonTeachingStaffTable.address],
        photoUrl = row[NonTeachingStaffTable.photoUrl],
        employeeId = row[NonTeachingStaffTable.employeeId],
        status = if (row[NonTeachingStaffTable.isActive]) "active" else "inactive",
        joinedYear = runCatching {
            row[NonTeachingStaffTable.createdAt].toString().take(4)
        }.getOrNull(),
        // "MMM yyyy" — e.g. "Jan 2020" — derived from created_at.
        joinedDate = runCatching {
            val created = row[NonTeachingStaffTable.createdAt]
            val ld = java.time.LocalDate.ofInstant(created, java.time.ZoneOffset.UTC)
            val month = ld.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
            "$month ${ld.year}"
        }.getOrNull()
    )

/**
 * People Tab: enrich a roster [StaffDto] with shift info and today items.
 * Runs inside the caller's Exposed transaction.
 */
private fun enrichStaffForList(schoolId: UUID, dto: StaffDto): StaffDto {
    val staffId = runCatching { UUID.fromString(dto.id) }.getOrNull() ?: return dto

    // Shift info from staff_shifts table
    val shiftInfo = StaffShiftsTable.selectAll().where {
        (StaffShiftsTable.schoolId eq schoolId) and
            (StaffShiftsTable.staffId eq staffId) and
            (StaffShiftsTable.isActive eq true)
    }.firstOrNull()
    val shiftLabel = shiftInfo?.let {
        val name = it[StaffShiftsTable.shiftName]
        val start = it[StaffShiftsTable.startTime]
        val end = it[StaffShiftsTable.endTime]
        "$name ($start–$end)"
    }

    // Today items: check-in status, leave requests
    val today = LocalDate.now()
    val items = mutableListOf<TodayItemDto>()

    // Check-in today
    val checkInToday = StaffCheckInsTable.selectAll().where {
        (StaffCheckInsTable.schoolId eq schoolId) and
            (StaffCheckInsTable.staffId eq staffId) and
            (StaffCheckInsTable.date eq today)
    }.firstOrNull()

    if (checkInToday != null) {
        val checkInAt = checkInToday[StaffCheckInsTable.checkInAt]
        val checkOutAt = checkInToday[StaffCheckInsTable.checkOutAt]
        val timeStr = checkInAt.toString().take(5)
        if (checkOutAt != null) {
            items += TodayItemDto("green", "Checked in $timeStr, checked out")
        } else {
            items += TodayItemDto("green", "Checked in $timeStr")
        }
    } else {
        if (dto.status == "active") {
            items += TodayItemDto("red", "Not checked in")
        }
    }

    return dto.copy(
        shift = shiftLabel,
        todayItems = items
    )
}

// ─────────────────────────── routing ────────────────────────────

fun Route.nonTeachingStaffRouting() {
    authenticate("jwt") {
        route("/api/v1/school/staff") {

            // ---- roster: active non-teaching staff in the caller's school ----
            // RA-S17: optional `q` (name/role/department search) and `department`
            // filter, applied in-memory after the scoped fetch so the SQL stays
            // Postgres-portable.
            get {
                val ctx = call.requireSchoolContext() ?: return@get
                val q = call.request.queryParameters["q"]?.trim()?.takeIf { it.isNotBlank() }?.lowercase()
                val dept = call.request.queryParameters["department"]?.trim()?.takeIf { it.isNotBlank() }
                val pageParam = call.request.queryParameters["page"]?.toIntOrNull()
                val pageSizeParam = call.request.queryParameters["pageSize"]?.toIntOrNull()
                val isPaginated = pageParam != null

                if (isPaginated) {
                    val page = (pageParam ?: 1).coerceAtLeast(1)
                    val pageSize = (pageSizeParam ?: 10).coerceIn(1, 100)
                    val offset = (page - 1).toLong() * pageSize

                    val result = dbQuery {
                        val totalRecords = NonTeachingStaffTable.selectAll()
                            .where {
                                (NonTeachingStaffTable.schoolId eq ctx.schoolId) and
                                    (NonTeachingStaffTable.isActive eq true)
                            }
                            .count()

                        val staff = NonTeachingStaffTable.selectAll()
                            .where {
                                (NonTeachingStaffTable.schoolId eq ctx.schoolId) and
                                    (NonTeachingStaffTable.isActive eq true)
                            }
                            .orderBy(NonTeachingStaffTable.fullName to SortOrder.ASC)
                            .limit(pageSize, offset)
                            .map(::staffRowToDto)
                            .filter { s ->
                                (dept == null || s.department.equals(dept, ignoreCase = true)) &&
                                    (q == null ||
                                        s.fullName.lowercase().contains(q) ||
                                        s.role.lowercase().contains(q) ||
                                        (s.department?.lowercase()?.contains(q) == true))
                            }
                            .map { enrichStaffForList(ctx.schoolId, it) }

                        StaffListPaginatedResponse(
                            staff = staff,
                            pagination = StaffPaginationDto(
                                page = page,
                                pageSize = pageSize,
                                totalRecords = totalRecords.toInt(),
                                hasNext = offset + staff.size < totalRecords
                            )
                        )
                    }
                    call.ok(result, message = "Staff fetched")
                } else {
                    val staff = dbQuery {
                        NonTeachingStaffTable.selectAll()
                            .where {
                                (NonTeachingStaffTable.schoolId eq ctx.schoolId) and
                                    (NonTeachingStaffTable.isActive eq true)
                            }
                            .orderBy(NonTeachingStaffTable.fullName to SortOrder.ASC)
                            .map(::staffRowToDto)
                            .filter { s ->
                                (dept == null || s.department.equals(dept, ignoreCase = true)) &&
                                    (q == null ||
                                        s.fullName.lowercase().contains(q) ||
                                        s.role.lowercase().contains(q) ||
                                        (s.department?.lowercase()?.contains(q) == true))
                            }
                            .map { enrichStaffForList(ctx.schoolId, it) }
                    }
                    call.ok(StaffListResponse(staff), message = "Staff fetched")
                }
            }

            // ---- add a staff member (school-admin) ----
            post {
                val ctx = call.requireSchoolAdmin() ?: return@post
                val req = runCatching { call.receive<CreateStaffRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid body"); return@post }
                if (req.fullName.isBlank() || req.role.isBlank()) {
                    call.fail("Name and role are required.")
                    return@post
                }
                val now = Instant.now()
                val dto = dbQuery {
                    val newId = NonTeachingStaffTable.insert {
                        it[schoolId] = ctx.schoolId
                        it[fullName] = req.fullName.trim()
                        it[role] = req.role.trim()
                        it[department] = req.department?.takeIf { d -> d.isNotBlank() }?.trim()
                        it[phone] = req.phone?.takeIf { p -> p.isNotBlank() }?.trim()
                        it[email] = req.email?.takeIf { e -> e.isNotBlank() }?.trim()
                        it[address] = req.address?.takeIf { a -> a.isNotBlank() }?.trim()
                        it[isActive] = true
                        it[createdAt] = now
                        it[updatedAt] = now
                    } get NonTeachingStaffTable.id
                    NonTeachingStaffTable.selectAll().where { NonTeachingStaffTable.id eq newId }.first().let(::staffRowToDto)
                }
                call.created(dto, message = "Staff member added")
            }

            // ---- single staff profile ----
            get("{id}") {
                val ctx = call.requireSchoolContext() ?: return@get
                val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid staff id"); return@get }
                val dto = dbQuery {
                    NonTeachingStaffTable.selectAll()
                        .where {
                            (NonTeachingStaffTable.id eq id) and
                                (NonTeachingStaffTable.schoolId eq ctx.schoolId) and
                                (NonTeachingStaffTable.isActive eq true)
                        }
                        .firstOrNull()?.let(::staffRowToDto)
                }
                if (dto == null) {
                    call.fail("Staff member not found in your school", HttpStatusCode.NotFound, "STAFF_NOT_FOUND")
                    return@get
                }
                call.ok(dto, message = "Staff profile fetched")
            }

            // ---- edit a staff member (school-admin) ----
            patch("{id}") {
                val ctx = call.requireSchoolAdmin() ?: return@patch
                val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid staff id"); return@patch }
                val req = runCatching { call.receive<UpdateStaffRequest>() }.getOrNull()
                    ?: run { call.fail("Invalid body"); return@patch }
                val now = Instant.now()
                val dto = dbQuery {
                    val exists = NonTeachingStaffTable.selectAll()
                        .where {
                            (NonTeachingStaffTable.id eq id) and
                                (NonTeachingStaffTable.schoolId eq ctx.schoolId) and
                                (NonTeachingStaffTable.isActive eq true)
                        }
                        .firstOrNull() ?: return@dbQuery null
                    NonTeachingStaffTable.update({
                        (NonTeachingStaffTable.id eq id) and (NonTeachingStaffTable.schoolId eq ctx.schoolId)
                    }) {
                        req.fullName?.takeIf { v -> v.isNotBlank() }?.let { v -> it[fullName] = v.trim() }
                        req.role?.takeIf { v -> v.isNotBlank() }?.let { v -> it[role] = v.trim() }
                        if (req.department != null) it[department] = req.department.takeIf { d -> d.isNotBlank() }?.trim()
                        if (req.phone != null) it[phone] = req.phone.takeIf { p -> p.isNotBlank() }?.trim()
                        if (req.email != null) it[email] = req.email.takeIf { e -> e.isNotBlank() }?.trim()
                        if (req.address != null) it[address] = req.address.takeIf { a -> a.isNotBlank() }?.trim()
                        it[updatedAt] = now
                    }
                    NonTeachingStaffTable.selectAll().where { NonTeachingStaffTable.id eq id }.first().let(::staffRowToDto)
                }
                if (dto == null) {
                    call.fail("Staff member not found in your school", HttpStatusCode.NotFound, "STAFF_NOT_FOUND")
                    return@patch
                }
                call.ok(dto, message = "Staff member updated")
            }

            // ---- soft-delete a staff member (school-admin) ----
            delete("{id}") {
                val ctx = call.requireSchoolAdmin() ?: return@delete
                val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("Invalid staff id"); return@delete }
                val n = dbQuery {
                    NonTeachingStaffTable.update({
                        (NonTeachingStaffTable.id eq id) and (NonTeachingStaffTable.schoolId eq ctx.schoolId)
                    }) {
                        it[isActive] = false
                        it[updatedAt] = Instant.now()
                    }
                }
                if (n == 0) {
                    call.fail("Staff member not found in your school", HttpStatusCode.NotFound, "STAFF_NOT_FOUND")
                    return@delete
                }
                call.okMessage("Staff member removed")
            }
        }
    }
}
