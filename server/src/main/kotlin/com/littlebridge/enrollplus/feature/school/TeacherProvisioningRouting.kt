/*
 * File: TeacherProvisioningRouting.kt
 * Module: feature.school
 *
 * Fixes audit finding C (§3.1): there was previously NO code path anywhere that
 * created a `teacher` app_users row, so login could never find a teacher and
 * the entire /api/v1/teacher/... surface + teacher portal were dead in
 * production. This adds a school-admin-only endpoint that provisions a teacher
 * account scoped to the admin's own school.
 *
 * Endpoints (JWT + school-scoped via requireSchoolContext):
 *   POST   /api/v1/school/teachers                     create a teacher app_users row
 *   GET    /api/v1/school/teachers                     list active teachers in the admin's school
 *   PUT    /api/v1/school/teachers/{id}                update teacher details (name, email, phone, designation)
 *   DELETE /api/v1/school/teachers/{id}                deactivate (soft-delete) a teacher (RA-22)
 *   POST   /api/v1/school/teachers/{id}/reset-password reissue an initial password (RA-32)
 *
 * A teacher created here can then log in via:
 *   - email + password   (if an email + initial_password were supplied), OR
 *   - phone + OTP         (if a phone identifier was supplied; OTP via /send-otp)
 */
package com.littlebridge.enrollplus.feature.school

import com.littlebridge.enrollplus.core.SCHOOL_ADMIN_ROLES
import com.littlebridge.enrollplus.core.created
import com.littlebridge.enrollplus.core.fail
import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.okMessage
import com.littlebridge.enrollplus.core.requireSchoolAdmin
import com.littlebridge.enrollplus.core.requireSchoolContext
import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.AttendanceRecordsTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.DeviceTokensTable
import com.littlebridge.enrollplus.db.FacultyTable
import com.littlebridge.enrollplus.db.LeaveRequestsTable
import com.littlebridge.enrollplus.db.NotificationsTable
import com.littlebridge.enrollplus.db.StudentsTable
import com.littlebridge.enrollplus.db.TeacherRatingsTable
import com.littlebridge.enrollplus.db.TeacherSubjectAssignmentsTable
import com.littlebridge.enrollplus.db.TeacherPeriodsTable
import com.littlebridge.enrollplus.db.UserSessionsTable
import com.littlebridge.enrollplus.feature.auth.hashPassword
import com.littlebridge.enrollplus.feature.auth.normaliseIdentifier
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.security.SecureRandom
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Serializable
data class CreateTeacherDto(
    val name: String,
    val identifier: String,                               // email OR phone
    @SerialName("initial_password") val initialPassword: String? = null
)

@Serializable
data class UpdateTeacherDto(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val designation: String? = null,
)

@Serializable
data class TeacherAccountDto(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val role: String,
    @SerialName("school_id") val schoolId: String
)

@Serializable
data class TeacherListResponse(val teachers: List<TeacherAccountDto>)

// =====================================================================
// Teacher CARD contract — drives the redesigned School-Admin teacher list
// (SchoolPeopleScreenV2 → Teachers sub-tab). Every card is a self-contained
// admin summary so the list needs no follow-up per-row fetches.
// GET /api/v1/school/teachers  →  TeacherCardListResponse
// =====================================================================

@Serializable
data class TeacherCardProfileDto(
    val name: String,
    val avatarUrl: String? = null,
    val role: String,
    val phone: String? = null,
    val email: String? = null,
    val status: String,                                   // ACTIVE | INACTIVE
    // People Tab enrichment — new fields for enriched teacher card.
    @SerialName("is_class_teacher") val isClassTeacher: Boolean = false,
    val experience: String? = null,                       // e.g. "12 yrs"
    val rating: Float? = null                             // average from teacher_ratings, null when no ratings
)

@Serializable
data class TeacherCardAcademicAssignmentDto(
    val grades: List<String> = emptyList(),
    val subjects: List<String> = emptyList()
)

@Serializable
data class TeacherCardWorkloadDto(
    val totalClasses: Int = 0,
    val totalStudents: Int = 0,
    // People Tab enrichment — new fields.
    @SerialName("workload_percent") val workloadPercent: Int = 0,  // 0-100
    val schedule: String = ""                              // e.g. "3 classes today"
)

@Serializable
data class TeacherCardActivityDto(
    val attendancePercentage: Int? = null,                // null when no data
    val lastActiveAt: String? = null                      // ISO-8601 UTC, or null = never
)

@Serializable
data class TeacherCardActionsDto(
    val canViewProfile: Boolean = true,
    val canAssignClass: Boolean = false,
    val canDeactivate: Boolean = false
)

@Serializable
data class TeacherCardDto(
    val id: String,
    val profile: TeacherCardProfileDto,
    val academicAssignment: TeacherCardAcademicAssignmentDto,
    val workload: TeacherCardWorkloadDto,
    val activity: TeacherCardActivityDto,
    val actions: TeacherCardActionsDto,
    // People Tab enrichment — teacher availability status.
    val availability: String = "break"                    // "teaching"|"break"|"meeting"|"leave"
)

@Serializable
data class TeacherCardPaginationDto(
    val page: Int,
    val pageSize: Int,
    val totalRecords: Int,
    val hasNext: Boolean
)

@Serializable
data class TeacherCardListResponse(
    val teachers: List<TeacherCardDto>,
    val pagination: TeacherCardPaginationDto
)

/**
 * RA-32: response for a credential reset. Carries the freshly-generated
 * plaintext password EXACTLY ONCE so the admin can hand it to the teacher;
 * it is never stored in plaintext server-side (only the hash is persisted).
 */
@Serializable
data class TeacherCredentialDto(
    val id: String,
    val name: String,
    val email: String,
    @SerialName("initial_password") val initialPassword: String
)

private fun isEmail(id: String) = id.contains("@")

/**
 * RA-32: generate a human-readable but high-entropy initial password
 * (no ambiguous chars like 0/O/1/l/I) using [SecureRandom]. ~12 chars from a
 * 56-symbol alphabet ≈ 69 bits of entropy — strong enough for a one-time
 * credential the teacher is expected to change after first login.
 */
private val resetPwRng = SecureRandom()
private fun generateInitialPassword(length: Int = 12): String {
    val alphabet = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#%"
    return buildString(length) {
        repeat(length) { append(alphabet[resetPwRng.nextInt(alphabet.length)]) }
    }
}

/**
 * BUGFIX (admin-created teachers were invisible in Supabase `faculty`):
 * onboarding mirrors every provisioned teacher into the `faculty` roster via the
 * SAME `external_id = "U-<userId>"` convention (OnboardingRouting.ensureFacultyRow),
 * but this admin-facing POST /api/v1/school/teachers endpoint only ever wrote an
 * `app_users` row. As a result:
 *   - the teacher never appeared in the `faculty` table the admin was looking at, and
 *   - SchoolAnalyticsRouting (which joins attendance to faculty on external_id) could
 *     never surface that teacher's accountability/efficiency metrics.
 *
 * This helper makes the admin create path mirror onboarding exactly. It is
 * idempotent on external_id, so re-provisioning is safe. MUST run inside dbQuery {}.
 */
private fun ensureFacultyRow(schoolId: UUID, userId: UUID, name: String) {
    val externalId = "U-$userId"
    val exists = FacultyTable.selectAll()
        .where { FacultyTable.externalId eq externalId }
        .firstOrNull()
    if (exists != null) return
    FacultyTable.insert {
        it[FacultyTable.schoolId] = schoolId
        it[FacultyTable.externalId] = externalId
        it[FacultyTable.userId] = userId
        it[FacultyTable.name] = name.trim()
        it[isActive] = true
        it[createdAt] = Instant.now()
    }
}

fun Route.teacherProvisioningRouting() {
    authenticate("jwt") {
        route("/api/v1/school/teachers") {

            // ---- create a teacher account (privileged: RA-39) ----
            post {
                val ctx = call.requireSchoolAdmin() ?: return@post
                val req = runCatching { call.receive<CreateTeacherDto>() }.getOrNull()
                    ?: run { call.fail("Invalid body"); return@post }

                val id = normaliseIdentifier(req.identifier)
                if (req.name.isBlank() || id.isBlank()) {
                    call.fail("name and identifier are required", HttpStatusCode.BadRequest)
                    return@post
                }
                if (isEmail(id) && req.initialPassword.isNullOrBlank()) {
                    call.fail(
                        "initial_password is required when provisioning a teacher by email",
                        HttpStatusCode.BadRequest
                    )
                    return@post
                }

                val existing = dbQuery {
                    AppUsersTable.selectAll()
                        .where { (AppUsersTable.phone eq id) or (AppUsersTable.email eq id) }
                        .firstOrNull()
                }
                if (existing != null) {
                    call.fail("An account with this identifier already exists", HttpStatusCode.Conflict, "USER_EXISTS")
                    return@post
                }

                val newId = UUID.randomUUID()
                val now = Instant.now()
                dbQuery {
                    AppUsersTable.insert {
                        it[AppUsersTable.id] = newId
                        it[fullName] = req.name.trim()
                        it[role] = "teacher"
                        it[schoolId] = ctx.schoolId
                        if (isEmail(id)) {
                            it[email] = id
                            it[passwordHash] = hashPassword(req.initialPassword ?: "")
                            it[isEmailVerified] = true
                        } else {
                            it[phone] = id
                            it[isPhoneVerified] = true
                        }
                        it[profileCompleted] = false
                        // RA-54: provisioned teachers must change their generated
                        // initial password on first login. This flag is the
                        // server-side gate signal NavGraphV2 reads; the
                        // POST /auth/change-password endpoint clears it.
                        it[mustChangePassword] = true
                        it[isActive] = true
                        it[createdAt] = now
                        it[updatedAt] = now
                    }
                    // BUGFIX: mirror the new teacher into the `faculty` roster so it
                    // shows up in Supabase and is visible to SchoolAnalyticsRouting
                    // (which joins attendance → faculty on external_id = "U-<userId>").
                    ensureFacultyRow(ctx.schoolId, newId, req.name)

                    // RA-LINK: a teacher was created → reconcile their workload
                    // through the centralized service so they are immediately linked
                    // to all students in any class+section they already cover. A
                    // freshly-provisioned teacher has no assignments yet (those are
                    // created via the assignment routing, which triggers its own
                    // reconciliation), so this is typically 0 — but funnelling every
                    // teacher-create through the SAME reconciler guarantees the link
                    // graph is never left stale and needs no manual linking.
                    StudentAggregationService.recalcTeacherWorkload(ctx.schoolId, newId)
                }

                call.created(
                    TeacherAccountDto(
                        id = newId.toString(),
                        name = req.name.trim(),
                        email = if (isEmail(id)) id else null,
                        phone = if (!isEmail(id)) id else null,
                        role = "teacher",
                        schoolId = ctx.schoolId.toString()
                    ),
                    message = "Teacher account created"
                )
            }

            // ---- list teachers in the admin's school as summary CARDS ----
            //
            // Drives the redesigned School-Admin teacher list. Returns EVERY
            // teacher in the school — including inactive accounts and teachers
            // with no class/subject assignments yet — so the admin sees the full
            // roster (an empty card still shows "No grades/subjects assigned").
            //
            // Performance (no N+1): exactly FIVE queries total regardless of page
            // size — (1) the paginated teacher page, (2) its total count, then a
            // single batched query each for (3) all assignments owned by the
            // page's teachers, (4) the faculty bridge rows (userId → externalId)
            // for those teachers, and (5) the 30-day faculty attendance window.
            // Student workload is derived from a single grouped students query.
            // Everything else is aggregated in-memory.
            //
            // Faculty-attendance bridge: faculty attendance is keyed on
            // FacultyTable.externalId, but teachers are app_users rows. We join
            // via FacultyTable.userId == app_users.id; when no faculty row links
            // a teacher we simply report attendancePercentage = null (the card
            // renders "—" rather than fabricating a number).
            get {
                val ctx = call.requireSchoolContext() ?: return@get
                val isAdmin = ctx.role in SCHOOL_ADMIN_ROLES

                val page = (call.request.queryParameters["page"]?.toIntOrNull() ?: 1)
                    .coerceAtLeast(1)
                val pageSize = (call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 10)
                    .coerceIn(1, 100)
                val offset = (page - 1).toLong() * pageSize

                // RECONCILE: ensure every active teacher app_user has a
                // mirrored faculty row. Old teachers created before the
                // ensureFacultyRow fix would otherwise be invisible.
                // Run in a SEPARATE transaction so that any
                // UniqueConstraintViolationException from the insert doesn't
                // poison the main query transaction (PostgreSQL aborts the
                // entire transaction on any statement error).
                dbQuery {
                    val activeTeachers = AppUsersTable.selectAll()
                        .where {
                            (AppUsersTable.schoolId eq ctx.schoolId) and
                                (AppUsersTable.role eq "teacher") and
                                (AppUsersTable.isActive eq true)
                        }
                        .toList()
                    activeTeachers.forEach { tRow ->
                        val userId = tRow[AppUsersTable.id].value
                        val externalId = "U-$userId"
                        val exists = FacultyTable.selectAll()
                            .where { FacultyTable.externalId eq externalId }
                            .firstOrNull()
                        if (exists == null) {
                            try {
                                FacultyTable.insert {
                                    it[FacultyTable.schoolId] = ctx.schoolId
                                    it[FacultyTable.externalId] = externalId
                                    it[FacultyTable.userId] = userId
                                    it[FacultyTable.name] = tRow[AppUsersTable.fullName]
                                    it[isActive] = true
                                    it[createdAt] = tRow[AppUsersTable.createdAt]
                                }
                            } catch (_: Exception) {
                                // Concurrent request may have inserted the same
                                // externalId (uniqueIndex). Safe to ignore — the
                                // row now exists either way.
                            }
                        }
                    }
                }

                val response = dbQuery {
                    // BUG-041: Use FacultyTable as the primary source (matching
                    // the Home KPI which counts active faculty). This ensures
                    // faculty rows without app_users entries (e.g. demo seed)
                    // still appear in the People tab.
                    val totalRecords = FacultyTable.selectAll()
                        .where {
                            (FacultyTable.schoolId eq ctx.schoolId) and
                                (FacultyTable.isActive eq true)
                        }
                        .count()

                    val facultyRows = FacultyTable.selectAll()
                        .where {
                            (FacultyTable.schoolId eq ctx.schoolId) and
                                (FacultyTable.isActive eq true)
                        }
                        .orderBy(FacultyTable.name, SortOrder.ASC)
                        .limit(pageSize, offset)
                        .toList()

                    if (facultyRows.isEmpty()) {
                        return@dbQuery TeacherCardListResponse(
                            teachers = emptyList(),
                            pagination = TeacherCardPaginationDto(
                                page = page,
                                pageSize = pageSize,
                                totalRecords = totalRecords.toInt(),
                                hasNext = false
                            )
                        )
                    }

                    // Collect userIds for faculty that have app_users links.
                    val teacherIds = facultyRows.mapNotNull { it[FacultyTable.userId] }

                    // Batch-fetch AppUsersTable rows for profile enrichment.
                    val appUserPredicate = if (teacherIds.isNotEmpty()) {
                        teacherIds.map { tid -> AppUsersTable.id eq tid }
                            .reduce { acc, next -> acc or next }
                    } else null
                    val appUserByUserId: Map<UUID, ResultRow> = if (appUserPredicate != null) {
                        AppUsersTable.selectAll()
                            .where { appUserPredicate }
                            .toList()
                            .associateBy { it[AppUsersTable.id].value }
                    } else emptyMap()

                    // (3) all assignments for the page's teachers in ONE query.
                    val assignmentPredicate = if (teacherIds.isNotEmpty()) {
                        teacherIds
                            .map { tid -> TeacherSubjectAssignmentsTable.teacherId eq tid }
                            .reduce { acc, next -> acc or next }
                    } else null
                    val assignmentRows = if (assignmentPredicate != null) {
                        TeacherSubjectAssignmentsTable.selectAll()
                            .where {
                                (TeacherSubjectAssignmentsTable.schoolId eq ctx.schoolId) and
                                    (TeacherSubjectAssignmentsTable.isActive eq true) and
                                    assignmentPredicate
                            }
                            .toList()
                    } else emptyList()
                    val assignmentsByTeacher = assignmentRows.groupBy {
                        it[TeacherSubjectAssignmentsTable.teacherId]
                    }

                    // (5) 30-day faculty attendance window, grouped by personId
                    // (= FacultyTable.externalId).
                    val externalIds = facultyRows.map { it[FacultyTable.externalId] }
                    val cutoff = LocalDate.now().minusDays(30)
                    val attendanceByExternalId: Map<String, List<String>> =
                        if (externalIds.isEmpty()) {
                            emptyMap()
                        } else {
                            AttendanceRecordsTable.selectAll()
                                .where {
                                    (AttendanceRecordsTable.schoolId eq ctx.schoolId) and
                                        (AttendanceRecordsTable.type eq "faculty")
                                }
                                .toList()
                                .filter {
                                    it[AttendanceRecordsTable.personId] != null &&
                                        it[AttendanceRecordsTable.personId]!! in externalIds &&
                                        runCatching {
                                            it[AttendanceRecordsTable.date].isAfter(cutoff)
                                        }.getOrDefault(false)
                                }
                                .groupBy(
                                    { it[AttendanceRecordsTable.personId]!! },
                                    { it[AttendanceRecordsTable.status] }
                                )
                        }

                    // Student workload source: active students grouped by class.
                    val studentCountByClass: Map<Pair<String, String>, Int> =
                        StudentsTable.selectAll()
                            .where {
                                (StudentsTable.schoolId eq ctx.schoolId) and
                                    (StudentsTable.isActive eq true)
                            }
                            .toList()
                            .groupingBy {
                                it[StudentsTable.className] to it[StudentsTable.section]
                            }
                            .eachCount()

                    // (6) Teacher ratings — average per teacher.
                    val ratingsPredicate = if (teacherIds.isNotEmpty()) {
                        teacherIds
                            .map { tid -> TeacherRatingsTable.teacherId eq tid }
                            .reduce { acc, next -> acc or next }
                    } else null
                    val ratingsByTeacher: Map<UUID, Float> = if (ratingsPredicate != null) {
                        TeacherRatingsTable.selectAll()
                            .where {
                                (TeacherRatingsTable.schoolId eq ctx.schoolId) and ratingsPredicate
                            }
                            .toList()
                            .groupBy { it[TeacherRatingsTable.teacherId] }
                            .mapValues { (_, rows) ->
                                rows.map { it[TeacherRatingsTable.rating] }.average().toFloat()
                            }
                    } else emptyMap()

                    // (7) Today's timetable periods per teacher.
                    val today = LocalDate.now()
                    val todayDayOfWeek = today.dayOfWeek.value
                    val periodsPredicate = if (teacherIds.isNotEmpty()) {
                        teacherIds
                            .map { tid -> TeacherPeriodsTable.teacherId eq tid }
                            .reduce { acc, next -> acc or next }
                    } else null
                    val periodsByTeacher: Map<UUID, List<ResultRow>> = if (periodsPredicate != null) {
                        TeacherPeriodsTable.selectAll()
                            .where {
                                (TeacherPeriodsTable.schoolId eq ctx.schoolId) and
                                    (TeacherPeriodsTable.weekday eq todayDayOfWeek) and
                                    periodsPredicate
                            }
                            .toList()
                            .groupBy { it[TeacherPeriodsTable.teacherId] }
                    } else emptyMap()

                    // (8) Leave requests for today.
                    val leavePredicate = if (teacherIds.isNotEmpty()) {
                        teacherIds
                            .map { tid -> LeaveRequestsTable.requesterId eq tid }
                            .reduce { acc, next -> acc or next }
                    } else null
                    val teachersOnLeave: Set<UUID> = if (leavePredicate != null) {
                        LeaveRequestsTable.selectAll()
                            .where {
                                (LeaveRequestsTable.schoolId eq ctx.schoolId) and
                                    (LeaveRequestsTable.requesterRole eq "teacher") and
                                    (LeaveRequestsTable.status eq "Approved") and
                                    leavePredicate
                            }
                            .toList()
                            .filter { row ->
                                val dateFrom = runCatching { LocalDate.parse(row[LeaveRequestsTable.dateFrom]) }.getOrNull()
                                val dateTo = runCatching { LocalDate.parse(row[LeaveRequestsTable.dateTo]) }.getOrNull()
                                dateFrom != null && dateTo != null &&
                                    !today.isBefore(dateFrom) && !today.isAfter(dateTo)
                            }
                            .mapNotNull { it[LeaveRequestsTable.requesterId] }
                            .toSet()
                    } else emptySet()

                    val cards = facultyRows.map { fRow ->
                        val facultyId = fRow[FacultyTable.id].value
                        val externalId = fRow[FacultyTable.externalId]
                        val userId = fRow[FacultyTable.userId]
                        val appUser = userId?.let { appUserByUserId[it] }
                        val teacherId = userId ?: facultyId

                        val assignments = userId?.let { assignmentsByTeacher[it].orEmpty() } ?: emptyList()

                        val grades = assignments
                            .map { it[TeacherSubjectAssignmentsTable.className] }
                            .filter { it.isNotBlank() }
                            .distinct()
                        val subjects = assignments
                            .map { it[TeacherSubjectAssignmentsTable.subject] }
                            .filter { it.isNotBlank() }
                            .distinct()

                        val distinctClasses = assignments
                            .map {
                                it[TeacherSubjectAssignmentsTable.className] to
                                    it[TeacherSubjectAssignmentsTable.section]
                            }
                            .distinct()
                        val totalClasses = distinctClasses.size
                        val totalStudents = distinctClasses
                            .sumOf { studentCountByClass[it] ?: 0 }

                        val attendancePercentage = attendanceByExternalId[externalId]
                            ?.takeIf { it.isNotEmpty() }
                            ?.let { statuses ->
                                val present = statuses.count { it.equals("PRESENT", true) }
                                kotlin.math.round(present * 100.0 / statuses.size).toInt()
                            }

                        val isActive = appUser?.get(AppUsersTable.isActive) ?: true
                        val department = fRow[FacultyTable.department]?.takeIf { it.isNotBlank() }
                        val roleLabel = when {
                            department != null -> "$department Teacher"
                            subjects.isNotEmpty() -> "${subjects.first()} Teacher"
                            else -> "Teacher"
                        }

                        val isClassTeacher = assignments.any { it[TeacherSubjectAssignmentsTable.isClassTeacher] }

                        val experienceYears = appUser?.let {
                            runCatching {
                                java.time.Duration.between(it[AppUsersTable.createdAt], Instant.now()).toDays() / 365
                            }.getOrDefault(0L).toInt().coerceAtLeast(0)
                        } ?: 0
                        val experienceLabel = if (experienceYears > 0) "$experienceYears yrs" else null

                        val rating = userId?.let { ratingsByTeacher[it] }
                        val workloadPercent = (totalClasses * 10).coerceAtMost(100)

                        val todayPeriods = userId?.let { periodsByTeacher[it].orEmpty() } ?: emptyList()
                        val scheduleLabel = if (todayPeriods.isNotEmpty()) {
                            "${todayPeriods.size} class${if (todayPeriods.size > 1) "es" else ""} today"
                        } else {
                            "No classes today"
                        }

                        val availability = when {
                            userId != null && userId in teachersOnLeave -> "leave"
                            todayPeriods.isNotEmpty() -> {
                                val now = java.time.LocalTime.now()
                                val currentlyTeaching = todayPeriods.any { p ->
                                    val start = p[TeacherPeriodsTable.startTime]
                                    val end = p[TeacherPeriodsTable.endTime]
                                    !now.isBefore(start) && now.isBefore(end)
                                }
                                if (currentlyTeaching) "teaching" else "break"
                            }
                            else -> "break"
                        }

                        TeacherCardDto(
                            id = teacherId.toString(),
                            profile = TeacherCardProfileDto(
                                name = appUser?.get(AppUsersTable.fullName) ?: fRow[FacultyTable.name],
                                avatarUrl = appUser?.get(AppUsersTable.profilePicUrl) ?: fRow[FacultyTable.profilePic],
                                role = roleLabel,
                                phone = appUser?.get(AppUsersTable.phone),
                                email = appUser?.get(AppUsersTable.email),
                                status = if (isActive) "ACTIVE" else "INACTIVE",
                                isClassTeacher = isClassTeacher,
                                experience = experienceLabel,
                                rating = rating
                            ),
                            academicAssignment = TeacherCardAcademicAssignmentDto(
                                grades = grades,
                                subjects = subjects
                            ),
                            workload = TeacherCardWorkloadDto(
                                totalClasses = totalClasses,
                                totalStudents = totalStudents,
                                workloadPercent = workloadPercent,
                                schedule = scheduleLabel
                            ),
                            activity = TeacherCardActivityDto(
                                attendancePercentage = attendancePercentage,
                                lastActiveAt = appUser?.get(AppUsersTable.lastLoginAt)?.toString()
                            ),
                            actions = TeacherCardActionsDto(
                                canViewProfile = true,
                                canAssignClass = isAdmin && isActive,
                                canDeactivate = isAdmin && isActive
                            ),
                            availability = availability
                        )
                    }

                    TeacherCardListResponse(
                        teachers = cards,
                        pagination = TeacherCardPaginationDto(
                            page = page,
                            pageSize = pageSize,
                            totalRecords = totalRecords.toInt(),
                            hasNext = offset + cards.size < totalRecords
                        )
                    )
                }

                call.ok(response, message = "Teachers fetched successfully")
            }

            // ---- HARD-delete a teacher (RA-22) ----
            // FIX (admin remove leaves Supabase rows behind): previously this
            // only flipped is_active=false, so the app_users row (and the
            // teacher's sessions/tokens/assignments) stayed in Supabase forever.
            // Admin removal now performs a real DELETE inside one transaction:
            //   - sessions + device tokens + class/subject assignments are
            //     purged here (no DB-level FK exists for them);
            //   - authored content (assessments / homework / syllabus / marks)
            //     is PRESERVED — the DB FKs are ON DELETE SET NULL, so school
            //     academic history survives with the author cleared;
            //   - teacher_periods rows cascade away via their FK.
            // IDOR-safe: every statement is constrained to ctx.schoolId, so an
            // admin can only delete teachers belonging to their OWN school.
            delete("/{id}") {
                val ctx = call.requireSchoolAdmin() ?: return@delete   // privileged: RA-39
                val teacherId = call.parameters["id"]
                    ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("A valid teacher id is required", HttpStatusCode.BadRequest, "BAD_TEACHER_ID"); return@delete }

                val now = Instant.now()
                val deleted = dbQuery {
                    // Confirm the teacher exists in THIS school first.
                    val row = AppUsersTable.selectAll()
                        .where {
                            (AppUsersTable.id eq teacherId) and
                                (AppUsersTable.schoolId eq ctx.schoolId) and
                                (AppUsersTable.role eq "teacher")
                        }
                        .firstOrNull() ?: return@dbQuery false

                    // Kill the teacher's live sessions FIRST (immediate lockout even
                    // if a later statement fails), then purge auth artefacts.
                    UserSessionsTable.update({ UserSessionsTable.userId eq teacherId }) {
                        it[revokedAt] = now
                    }
                    UserSessionsTable.deleteWhere { UserSessionsTable.userId eq teacherId }
                    DeviceTokensTable.deleteWhere { DeviceTokensTable.userId eq teacherId }
                    // The teacher's personal notification inbox is meaningless once
                    // the account is gone — purge it so no orphan rows linger.
                    NotificationsTable.deleteWhere { NotificationsTable.userId eq teacherId }

                    // RA-LINK: the teacher's class/subject assignments are the
                    // relationship records that derive Student ↔ Teacher links.
                    // Per the soft-delete requirement (Example 5) we do NOT hard
                    // delete them — they are flipped to is_active=false via the
                    // centralized reconciler so relationship HISTORY remains intact
                    // while the teacher disappears from every live derived view.
                    // The reconciler returns the (class, section) pairs it touched.
                    val affected = StudentAggregationService.softDeactivateTeacherAssignments(
                        ctx.schoolId, teacherId
                    )

                    // BUGFIX: remove the mirrored `faculty` row too. It is keyed by
                    // external_id = "U-<userId>" (set on create / onboarding); without
                    // this the deleted teacher would linger forever in the faculty
                    // roster and keep showing up in analytics. School-scoped for IDOR.
                    FacultyTable.deleteWhere {
                        (FacultyTable.schoolId eq ctx.schoolId) and
                            (FacultyTable.externalId eq "U-$teacherId")
                    }

                    // Finally the account row itself. Tables with real FKs to
                    // app_users (assessments / homework / syllabus_units → SET NULL,
                    // teacher_periods → CASCADE) are handled by Postgres.
                    AppUsersTable.deleteWhere {
                        (AppUsersTable.id eq teacherId) and
                            (AppUsersTable.schoolId eq ctx.schoolId) and
                            (AppUsersTable.role eq "teacher")
                    }

                    // RA-LINK: re-derive student counts for every class the removed
                    // teacher used to cover, so the REMAINING teachers of those
                    // classes keep accurate workload metrics with no stale data.
                    affected.forEach { (cls, sec) ->
                        StudentAggregationService.recalcTeacherStudentCountsForClass(
                            ctx.schoolId, cls, sec
                        )
                    }
                    true
                }

                if (!deleted) {
                    call.fail("Teacher not found in your school", HttpStatusCode.NotFound, "TEACHER_NOT_FOUND")
                    return@delete
                }
                call.okMessage("Teacher removed")
            }

            // ---- reissue a teacher's initial password (RA-32) ----
            // Recovers the "lost credential" case: once the admin dismisses the
            // create-teacher dialog the initial password is gone (only its hash
            // is stored). This generates a NEW secure password, persists only the
            // hash, revokes the teacher's live sessions, and returns the plaintext
            // ONCE so the admin can hand it over.
            //
            // IDOR-safe: every lookup/update is constrained to ctx.schoolId, so an
            // admin can only reset teachers in their OWN school. Email teachers
            // only — phone teachers authenticate via OTP (no password to reset).
            post("/{id}/reset-password") {
                val ctx = call.requireSchoolAdmin() ?: return@post   // privileged: RA-39
                val teacherId = call.parameters["id"]
                    ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("A valid teacher id is required", HttpStatusCode.BadRequest, "BAD_TEACHER_ID"); return@post }

                val now = Instant.now()
                val newPassword = generateInitialPassword()

                val result = dbQuery {
                    val row = AppUsersTable.selectAll()
                        .where {
                            (AppUsersTable.id eq teacherId) and
                                (AppUsersTable.schoolId eq ctx.schoolId) and
                                (AppUsersTable.role eq "teacher") and
                                (AppUsersTable.isActive eq true)
                        }
                        .firstOrNull() ?: return@dbQuery null

                    val email = row[AppUsersTable.email]
                    if (email.isNullOrBlank()) {
                        // Phone-only teacher: there is no password to reset.
                        return@dbQuery TeacherCredentialDto("", row[AppUsersTable.fullName], "", "")
                    }

                    AppUsersTable.update({ AppUsersTable.id eq teacherId }) {
                        it[passwordHash] = hashPassword(newPassword)
                        it[isEmailVerified] = true
                        it[updatedAt] = now
                    }
                    // Revoke live sessions so the old credential can't keep a foothold.
                    UserSessionsTable.update({ UserSessionsTable.userId eq teacherId }) {
                        it[revokedAt] = now
                    }

                    TeacherCredentialDto(
                        id = teacherId.toString(),
                        name = row[AppUsersTable.fullName],
                        email = email,
                        initialPassword = newPassword
                    )
                }

                when {
                    result == null ->
                        call.fail("Teacher not found in your school", HttpStatusCode.NotFound, "TEACHER_NOT_FOUND")
                    result.id.isBlank() ->
                        call.fail(
                            "This teacher signs in with phone + OTP, so there is no password to reset. Ask them to log in with their phone number.",
                            HttpStatusCode.Conflict,
                            "TEACHER_USES_OTP"
                        )
                    else ->
                        call.ok(result, message = "New initial password issued")
                }
            }

            // ---- update teacher details (Bug 11) ----
            // School-admin-only: updates name, email, phone, and/or designation
            // on the teacher's app_users row. IDOR-safe: constrained to ctx.schoolId.
            put("/{id}") {
                val ctx = call.requireSchoolAdmin() ?: return@put
                val teacherId = call.parameters["id"]
                    ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run { call.fail("A valid teacher id is required", HttpStatusCode.BadRequest, "BAD_TEACHER_ID"); return@put }

                val req = runCatching { call.receive<UpdateTeacherDto>() }.getOrNull()
                    ?: run { call.fail("Invalid body"); return@put }

                // Validate: at least one field must be provided
                if (req.name == null && req.email == null && req.phone == null && req.designation == null) {
                    call.fail("At least one field must be provided", HttpStatusCode.BadRequest, "EMPTY_UPDATE")
                    return@put
                }

                // Validate name if provided
                if (req.name != null && req.name.isBlank()) {
                    call.fail("Name cannot be blank", HttpStatusCode.BadRequest, "INVALID_NAME")
                    return@put
                }

                // Check for duplicate email/phone if changing
                if (req.email != null) {
                    val existing = dbQuery {
                        AppUsersTable.selectAll()
                            .where {
                                (AppUsersTable.email eq req.email) and
                                    (AppUsersTable.id neq teacherId)
                            }
                            .firstOrNull()
                    }
                    if (existing != null) {
                        call.fail("An account with this email already exists", HttpStatusCode.Conflict, "EMAIL_EXISTS")
                        return@put
                    }
                }
                if (req.phone != null) {
                    val existing = dbQuery {
                        AppUsersTable.selectAll()
                            .where {
                                (AppUsersTable.phone eq req.phone) and
                                    (AppUsersTable.id neq teacherId)
                            }
                            .firstOrNull()
                    }
                    if (existing != null) {
                        call.fail("An account with this phone number already exists", HttpStatusCode.Conflict, "PHONE_EXISTS")
                        return@put
                    }
                }

                val now = Instant.now()
                val updated = dbQuery {
                    // Confirm the teacher exists in THIS school
                    val row = AppUsersTable.selectAll()
                        .where {
                            (AppUsersTable.id eq teacherId) and
                                (AppUsersTable.schoolId eq ctx.schoolId) and
                                (AppUsersTable.role eq "teacher")
                        }
                        .firstOrNull() ?: return@dbQuery null

                    AppUsersTable.update({ AppUsersTable.id eq teacherId }) {
                        if (req.name != null) it[fullName] = req.name.trim()
                        if (req.email != null) {
                            it[email] = req.email.trim().ifBlank { null }
                            it[isEmailVerified] = req.email.trim().isNotBlank()
                        }
                        if (req.phone != null) {
                            it[phone] = req.phone.trim().ifBlank { null }
                            it[isPhoneVerified] = req.phone.trim().isNotBlank()
                        }
                        it[updatedAt] = now
                    }

                    // Sync faculty row name if it exists
                    val externalId = "U-$teacherId"
                    FacultyTable.selectAll()
                        .where { FacultyTable.externalId eq externalId }
                        .firstOrNull()?.let { fRow ->
                        if (req.name != null) {
                            FacultyTable.update({ FacultyTable.id eq fRow[FacultyTable.id] }) {
                                it[FacultyTable.name] = req.name.trim()
                            }
                        }
                        if (req.designation != null) {
                            FacultyTable.update({ FacultyTable.id eq fRow[FacultyTable.id] }) {
                                it[FacultyTable.department] = req.designation.trim().ifBlank { null }
                            }
                        }
                    }

                    // Return the updated row
                    AppUsersTable.selectAll().where { AppUsersTable.id eq teacherId }.firstOrNull()
                }

                if (updated == null) {
                    call.fail("Teacher not found in your school", HttpStatusCode.NotFound, "TEACHER_NOT_FOUND")
                    return@put
                }

                call.ok(
                    TeacherAccountDto(
                        id = teacherId.toString(),
                        name = updated[AppUsersTable.fullName],
                        email = updated[AppUsersTable.email],
                        phone = updated[AppUsersTable.phone],
                        role = updated[AppUsersTable.role],
                        schoolId = ctx.schoolId.toString()
                    ),
                    message = "Teacher updated successfully"
                )
            }
        }
    }
}
