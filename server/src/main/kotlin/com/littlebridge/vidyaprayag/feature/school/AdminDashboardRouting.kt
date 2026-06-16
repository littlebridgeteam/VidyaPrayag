/*
 * File: AdminDashboardRouting.kt
 * Module: feature.school
 *
 * Endpoints (all JWT, school-scoped via requireSchoolContext):
 *   GET /api/admin/dashboard/summary   — homepage hero data
 *   GET /api/admin/dashboard/analytics — chart series for the dashboard graphs
 *   GET /api/admin/dashboard/activity  — alerts + recent activity feed
 *
 * These power the redesigned SchoolHomeScreenV2 (CampusHealthCard graph,
 * statistics cards, teacher-insight, quick actions, analytics charts and the
 * activity/alerts feed). Every number is a REAL aggregate computed from the
 * caller's school data — never fabricated. When a feed has no data yet the
 * response returns honest zeros / empty lists so the UI can render an empty
 * state instead of a fiction (LAW 6: honesty).
 *
 * Schema reuse (task #7 — analyze before creating new columns):
 *   - schools (name, logo_url, academic_year_start_month) → school block
 *   - app_users (full_name, profile_pic_url)              → admin block
 *   - attendance_records (status/type/date)               → attendance metrics
 *   - students (is_active, created_at)                    → student statistics
 *   - faculty (is_active, department, created_at)         → teacher statistics + insight
 *   - school_classes                                      → class statistics
 *   - school_subjects (via school_classes.id)            → subject statistics
 *   - teacher_subject_assignments                         → assignment coverage
 *   - admission_enquiries (status='new')                  → pending-admission alert
 *   - notifications (recipient = admin)                   → recent activity feed
 * NO new columns are required: every field maps to an existing column. The
 * `fee_collection` campus-health metric is intentionally omitted unless real
 * fee data exists for the school (we do not invent a percentage).
 *
 * NOTE: Do NOT write a literal slash-star sequence inside this block comment —
 * Kotlin block comments nest, so an inner one would swallow the rest of the file.
 */
package com.littlebridge.vidyaprayag.feature.school

import com.littlebridge.vidyaprayag.core.SchoolContext
import com.littlebridge.vidyaprayag.core.ok
import com.littlebridge.vidyaprayag.core.requireSchoolContext
import com.littlebridge.vidyaprayag.db.AdmissionEnquiriesTable
import com.littlebridge.vidyaprayag.db.AppUsersTable
import com.littlebridge.vidyaprayag.db.AttendanceRecordsTable
import com.littlebridge.vidyaprayag.db.DatabaseFactory.dbQuery
import com.littlebridge.vidyaprayag.db.ExamResultsTable
import com.littlebridge.vidyaprayag.db.FacultyTable
import com.littlebridge.vidyaprayag.db.FeeRecordsTable
import com.littlebridge.vidyaprayag.db.NotificationsTable
import com.littlebridge.vidyaprayag.db.SchoolClassesTable
import com.littlebridge.vidyaprayag.db.SchoolSubjectsTable
import com.littlebridge.vidyaprayag.db.SchoolsTable
import com.littlebridge.vidyaprayag.db.StudentsTable
import com.littlebridge.vidyaprayag.db.TeacherSubjectAssignmentsTable
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

// =====================================================================
// DTOs — match the contract in the task spec (camelCase JSON keys).
// =====================================================================

@Serializable
data class TrendDto(
    val direction: String,            // "up" | "down" | "flat"
    val value: Double
)

@Serializable
data class PctTrendDto(
    val direction: String,            // "up" | "down" | "flat"
    val percentage: Int
)

@Serializable
data class DashSchoolDto(
    val id: String,
    val name: String,
    val logoUrl: String? = null,
    val academicYear: String,
    val currentTerm: String
)

@Serializable
data class DashAdminDto(
    val id: String,
    val name: String,
    val avatarUrl: String? = null
)

@Serializable
data class CampusMetricDto(
    val key: String,
    val label: String,
    val value: Int,
    val unit: String,
    val trend: TrendDto
)

@Serializable
data class CampusHealthDto(
    val status: String,               // HEALTHY | WATCH | CRITICAL | UNKNOWN
    val message: String,
    val metrics: List<CampusMetricDto>
)

@Serializable
data class StudentStatsDto(
    val total: Int,
    val active: Int,
    val newAdmissions: Int,
    val trend: PctTrendDto
)

@Serializable
data class TeacherStatsDto(
    val total: Int,
    val active: Int,
    val newJoined: Int,
    val trend: PctTrendDto
)

@Serializable
data class CountStatsDto(
    val total: Int,
    val active: Int
)

@Serializable
data class StatisticsDto(
    val students: StudentStatsDto,
    val teachers: TeacherStatsDto,
    val classes: CountStatsDto,
    val subjects: CountStatsDto
)

@Serializable
data class DepartmentDto(
    val name: String,
    val teacherCount: Int
)

@Serializable
data class TeacherInsightDto(
    val totalTeachers: Int,
    val assignedTeachers: Int,
    val pendingAssignment: Int,
    val assignmentCoverage: Int,
    val departments: List<DepartmentDto>
)

@Serializable
data class QuickActionDto(
    val id: String,
    val title: String,
    val subtitle: String,
    val enabled: Boolean,
    val permission: String
)

@Serializable
data class DashboardSummaryResponse(
    val school: DashSchoolDto,
    val admin: DashAdminDto,
    val campusHealth: CampusHealthDto,
    val statistics: StatisticsDto,
    val teacherInsight: TeacherInsightDto,
    val quickActions: List<QuickActionDto>
)

// ---- analytics ----

@Serializable
data class AttendanceTrendDto(
    val period: String,
    val labels: List<String>,
    val values: List<Int>
)

@Serializable
data class StudentGrowthDto(
    val labels: List<String>,
    val values: List<Int>
)

@Serializable
data class TopClassDto(
    @SerialName("class") val className: String,
    val score: Int
)

@Serializable
data class ClassPerformanceDto(
    val topClasses: List<TopClassDto>
)

@Serializable
data class AttendanceBreakdownDto(
    val present: Int,
    val absent: Int,
    val late: Int
)

@Serializable
data class DashboardAnalyticsResponse(
    val attendanceTrend: AttendanceTrendDto,
    val studentGrowth: StudentGrowthDto,
    val classPerformance: ClassPerformanceDto,
    val attendanceBreakdown: AttendanceBreakdownDto
)

// ---- activity ----

@Serializable
data class AlertDto(
    val id: String,
    val type: String,                 // WARNING | INFO | CRITICAL
    val title: String,
    val description: String,
    val priority: String,             // HIGH | MEDIUM | LOW
    val action: String,
    val createdAt: String
)

@Serializable
data class ActivityDto(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val performedBy: String,
    val time: String,
    val createdAt: String
)

@Serializable
data class DashboardActivityResponse(
    val alerts: List<AlertDto>,
    val activities: List<ActivityDto>
)

// =====================================================================
// helpers
// =====================================================================

private val MONTHS = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

private val ISO_LOCAL = DateTimeFormatter.ISO_LOCAL_DATE_TIME

/**
 * Derive a human-readable academic year ("2026-27") from the school's
 * `academic_year_start_month` (e.g. "April") and today's date. When the start
 * month is unknown we anchor on April (the common Indian academic year). The
 * year flips on the start month so "Jun 2026" with an April start reads 2026-27.
 */
private fun academicYear(startMonthName: String?, today: LocalDate): String {
    val startMonth = monthIndex(startMonthName) ?: 4 // default: April
    val startYear = if (today.monthValue >= startMonth) today.year else today.year - 1
    val endShort = (startYear + 1) % 100
    return "$startYear-${endShort.toString().padStart(2, '0')}"
}

private fun monthIndex(name: String?): Int? {
    if (name.isNullOrBlank()) return null
    name.trim().toIntOrNull()?.let { if (it in 1..12) return it }
    val n = name.trim().lowercase()
    return MONTHS.indexOfFirst { it.lowercase() == n.take(3) }.takeIf { it >= 0 }?.plus(1)
}

/**
 * Current term derived purely from how far we are into the academic year:
 *   first ~4 months → Term 1, next ~4 → Term 2, remainder → Term 3.
 * No fabricated calendar — just a deterministic split so the UI has a label.
 */
private fun currentTerm(startMonthName: String?, today: LocalDate): String {
    val startMonth = monthIndex(startMonthName) ?: 4
    val monthsIn = ((today.monthValue - startMonth) + 12) % 12
    return when {
        monthsIn < 4 -> "Term 1"
        monthsIn < 8 -> "Term 2"
        else -> "Term 3"
    }
}

private fun trendOf(current: Double, previous: Double): TrendDto {
    val delta = current - previous
    val dir = when {
        delta > 0.0001 -> "up"
        delta < -0.0001 -> "down"
        else -> "flat"
    }
    return TrendDto(dir, kotlin.math.round(kotlin.math.abs(delta) * 10) / 10.0)
}

private fun pctTrendOf(current: Int, previous: Int): PctTrendDto {
    val dir = when {
        current > previous -> "up"
        current < previous -> "down"
        else -> "flat"
    }
    val pct = if (previous <= 0) {
        if (current > 0) 100 else 0
    } else {
        kotlin.math.round(kotlin.math.abs(current - previous) * 100.0 / previous).toInt()
    }
    return PctTrendDto(dir, pct)
}

/** Present-rate (0..100) over the window [from, to] for one attendance type. */
private fun attendancePctBetween(
    schoolId: UUID, type: String, from: LocalDate, to: LocalDate
): Pair<Int, Int> {
    val rows = AttendanceRecordsTable.selectAll()
        .where {
            (AttendanceRecordsTable.schoolId eq schoolId) and
                (AttendanceRecordsTable.type eq type)
        }
        .toList()
        .filter {
            runCatching {
                val d = LocalDate.parse(it[AttendanceRecordsTable.date])
                !d.isBefore(from) && !d.isAfter(to)
            }.getOrDefault(false)
        }
    if (rows.isEmpty()) return 0 to 0
    val present = rows.count { it[AttendanceRecordsTable.status].equals("PRESENT", true) }
    return (present * 100) / rows.size to rows.size
}

// =====================================================================
// route
// =====================================================================

fun Route.adminDashboardRouting() {
    authenticate("jwt") {
        route("/api/admin/dashboard") {

            // ─────────────────────────────────────────────────────────────
            // GET /api/admin/dashboard/summary
            // ─────────────────────────────────────────────────────────────
            get("/summary") {
                val ctx = call.requireSchoolContext() ?: return@get
                val payload = dbQuery { buildSummary(ctx) }
                call.ok(payload, message = "Dashboard summary fetched successfully")
            }

            // ─────────────────────────────────────────────────────────────
            // GET /api/admin/dashboard/analytics
            // ─────────────────────────────────────────────────────────────
            get("/analytics") {
                val ctx = call.requireSchoolContext() ?: return@get
                val payload = dbQuery { buildAnalytics(ctx.schoolId) }
                call.ok(payload, message = "Dashboard analytics fetched successfully")
            }

            // ─────────────────────────────────────────────────────────────
            // GET /api/admin/dashboard/activity
            // ─────────────────────────────────────────────────────────────
            get("/activity") {
                val ctx = call.requireSchoolContext() ?: return@get
                val payload = dbQuery { buildActivity(ctx) }
                call.ok(payload, message = "Dashboard activity fetched successfully")
            }
        }
    }
}

// =====================================================================
// summary builder (runs inside dbQuery)
// =====================================================================

private fun buildSummary(ctx: SchoolContext): DashboardSummaryResponse {
    val schoolId = ctx.schoolId
    val today = LocalDate.now()

    // ---- school block ----
    val schoolRow = SchoolsTable.selectAll()
        .where { SchoolsTable.id eq schoolId }
        .singleOrNull()
    val schoolName = schoolRow?.get(SchoolsTable.name) ?: "Your School"
    val logoUrl = schoolRow?.get(SchoolsTable.logoUrl)
    val startMonth = schoolRow?.get(SchoolsTable.academicYearStartMonth)

    val school = DashSchoolDto(
        id = schoolId.toString(),
        name = schoolName,
        logoUrl = logoUrl,
        academicYear = academicYear(startMonth, today),
        currentTerm = currentTerm(startMonth, today)
    )

    // ---- admin block ----
    val adminRow = AppUsersTable.selectAll()
        .where { AppUsersTable.id eq ctx.userId }
        .singleOrNull()
    val admin = DashAdminDto(
        id = ctx.userId.toString(),
        name = adminRow?.get(AppUsersTable.fullName)?.takeIf { it.isNotBlank() } ?: "Admin",
        avatarUrl = adminRow?.get(AppUsersTable.profilePicUrl)
    )

    // ---- statistics: students ----
    val studentRows = StudentsTable.selectAll()
        .where { StudentsTable.schoolId eq schoolId }
        .toList()
    val totalStudents = studentRows.size
    val activeStudents = studentRows.count { it[StudentsTable.isActive] }
    // new admissions = students created in the last 30 days
    val cutoff30 = today.minusDays(30)
    val newStudents = studentRows.count {
        runCatching {
            it[StudentsTable.createdAt].atZone(java.time.ZoneOffset.UTC).toLocalDate().isAfter(cutoff30)
        }.getOrDefault(false)
    }
    val prevStudents = (totalStudents - newStudents).coerceAtLeast(0)
    val students = StudentStatsDto(
        total = totalStudents,
        active = activeStudents,
        newAdmissions = newStudents,
        trend = pctTrendOf(totalStudents, prevStudents)
    )

    // ---- statistics: teachers (faculty) ----
    val facultyRows = FacultyTable.selectAll()
        .where { FacultyTable.schoolId eq schoolId }
        .toList()
    val totalTeachers = facultyRows.size
    val activeTeachers = facultyRows.count { it[FacultyTable.isActive] }
    val newTeachers = facultyRows.count {
        runCatching {
            it[FacultyTable.createdAt].atZone(java.time.ZoneOffset.UTC).toLocalDate().isAfter(cutoff30)
        }.getOrDefault(false)
    }
    val prevTeachers = (totalTeachers - newTeachers).coerceAtLeast(0)
    val teachers = TeacherStatsDto(
        total = totalTeachers,
        active = activeTeachers,
        newJoined = newTeachers,
        trend = pctTrendOf(totalTeachers, prevTeachers)
    )

    // ---- statistics: classes ----
    val classRows = SchoolClassesTable.selectAll()
        .where { SchoolClassesTable.schoolId eq schoolId }
        .toList()
    val classIds = classRows.map { it[SchoolClassesTable.id].value }
    val classes = CountStatsDto(total = classRows.size, active = classRows.size)

    // ---- statistics: subjects (joined via class ids) ----
    // Portable OR-reduce instead of `inList` (Exposed-version-safe, matching the
    // rest of the codebase — see AnnouncementRouting / ParentRouting).
    val subjectCount = if (classIds.isEmpty()) 0 else {
        val classFilter = classIds
            .map { cid -> SchoolSubjectsTable.classId eq cid }
            .reduce { acc, op -> acc or op }
        SchoolSubjectsTable.selectAll().where { classFilter }.count().toInt()
    }
    val subjects = CountStatsDto(total = subjectCount, active = subjectCount)

    val statistics = StatisticsDto(students, teachers, classes, subjects)

    // ---- teacher insight ----
    // "assigned" = distinct teachers that appear in teacher_subject_assignments.
    val assignmentRows = TeacherSubjectAssignmentsTable.selectAll()
        .where {
            (TeacherSubjectAssignmentsTable.schoolId eq schoolId) and
                (TeacherSubjectAssignmentsTable.isActive eq true)
        }
        .toList()
    val assignedTeacherKeys = assignmentRows.mapNotNull { row ->
        row[TeacherSubjectAssignmentsTable.teacherId]?.toString()
            ?: row[TeacherSubjectAssignmentsTable.teacherName]?.takeIf { it.isNotBlank() }
    }.toSet()
    val assignedTeachers = assignedTeacherKeys.size.coerceAtMost(totalTeachers).let {
        if (totalTeachers == 0) 0 else it
    }
    val pendingAssignment = (totalTeachers - assignedTeachers).coerceAtLeast(0)
    val coverage = if (totalTeachers == 0) 0 else (assignedTeachers * 100) / totalTeachers
    val departments = facultyRows
        .mapNotNull { it[FacultyTable.department]?.takeIf { d -> d.isNotBlank() } }
        .groupingBy { it }
        .eachCount()
        .map { DepartmentDto(it.key, it.value) }
        .sortedByDescending { it.teacherCount }
    val teacherInsight = TeacherInsightDto(
        totalTeachers = totalTeachers,
        assignedTeachers = assignedTeachers,
        pendingAssignment = pendingAssignment,
        assignmentCoverage = coverage,
        departments = departments
    )

    // ---- campus health ----
    val (attLast7, att7n) = attendancePctBetween(schoolId, "student", today.minusDays(6), today)
    val (attPrev7, _) = attendancePctBetween(schoolId, "student", today.minusDays(13), today.minusDays(7))
    val metrics = ArrayList<CampusMetricDto>()
    if (att7n > 0) {
        metrics.add(
            CampusMetricDto(
                key = "attendance",
                label = "Attendance",
                value = attLast7,
                unit = "percentage",
                trend = trendOf(attLast7.toDouble(), attPrev7.toDouble())
            )
        )
    }
    // Fee collection — only when real fee data exists (never fabricate a %).
    val feeRows = FeeRecordsTable.selectAll()
        .where { FeeRecordsTable.schoolId eq schoolId }
        .toList()
    if (feeRows.isNotEmpty()) {
        val collected = feeRows.filter { it[FeeRecordsTable.status].equals("PAID", true) }
            .sumOf { it[FeeRecordsTable.amount] }
        val billed = feeRows.sumOf { it[FeeRecordsTable.amount] }
        val feePct = if (billed <= 0.0) 0 else kotlin.math.round(collected * 100.0 / billed).toInt()
        metrics.add(
            CampusMetricDto(
                key = "fee_collection",
                label = "Fee Collection",
                value = feePct,
                unit = "percentage",
                trend = TrendDto("flat", 0.0)
            )
        )
    }
    val health = computeCampusHealth(metrics, pendingAssignment)

    return DashboardSummaryResponse(
        school = school,
        admin = admin,
        campusHealth = health,
        statistics = statistics,
        teacherInsight = teacherInsight,
        quickActions = defaultQuickActions(ctx.role)
    )
}

private fun computeCampusHealth(metrics: List<CampusMetricDto>, pendingAssignment: Int): CampusHealthDto {
    if (metrics.isEmpty()) {
        return CampusHealthDto(
            status = "UNKNOWN",
            message = "Add attendance and fee data to see campus health.",
            metrics = emptyList()
        )
    }
    val attendance = metrics.firstOrNull { it.key == "attendance" }?.value
    val status = when {
        attendance == null -> "WATCH"
        attendance >= 90 && pendingAssignment == 0 -> "HEALTHY"
        attendance >= 75 -> "WATCH"
        else -> "CRITICAL"
    }
    val message = when (status) {
        "HEALTHY" -> "Everything looks stable today"
        "WATCH" -> if (pendingAssignment > 0)
            "$pendingAssignment teacher(s) still need class assignments"
        else "Attendance is dipping — keep an eye on it"
        "CRITICAL" -> "Attendance is low this week — needs attention"
        else -> "Campus health will appear once data is available"
    }
    return CampusHealthDto(status, message, metrics)
}

/**
 * Quick actions surfaced on the home screen. `enabled` reflects whether the
 * caller's role may perform the action (school_staff cannot create accounts).
 */
private fun defaultQuickActions(role: String): List<QuickActionDto> {
    val canManageAccounts = role == "school_admin" || role == "admin"
    return listOf(
        QuickActionDto("ADD_TEACHER", "Add Teacher", "Create staff profile", canManageAccounts, "teacher.create"),
        QuickActionDto("ADD_STUDENT", "Add Student", "New admission", true, "student.create"),
        QuickActionDto("CREATE_CLASS", "Create Class", "Setup classroom", canManageAccounts, "class.create"),
        QuickActionDto("REPORTS", "Reports", "View analytics", true, "report.view")
    )
}

// =====================================================================
// analytics builder (runs inside dbQuery)
// =====================================================================

private fun buildAnalytics(schoolId: UUID): DashboardAnalyticsResponse {
    val today = LocalDate.now()

    // ---- attendance trend: last 7 months, student present-rate as % ----
    val attendanceRows = AttendanceRecordsTable.selectAll()
        .where {
            (AttendanceRecordsTable.schoolId eq schoolId) and
                (AttendanceRecordsTable.type eq "student")
        }
        .toList()
    val attByMonth = attendanceRows.groupBy {
        runCatching {
            val d = LocalDate.parse(it[AttendanceRecordsTable.date]); d.year to d.monthValue
        }.getOrNull()
    }
    val trendLabels = ArrayList<String>(7)
    val trendValues = ArrayList<Int>(7)
    for (back in 6 downTo 0) {
        val d = today.minusMonths(back.toLong())
        trendLabels.add(MONTHS[d.monthValue - 1])
        val pool = attByMonth[d.year to d.monthValue].orEmpty()
        val pct = if (pool.isEmpty()) 0
        else (pool.count { it[AttendanceRecordsTable.status].equals("PRESENT", true) } * 100) / pool.size
        trendValues.add(pct)
    }
    val attendanceTrend = AttendanceTrendDto("monthly", trendLabels, trendValues)

    // ---- student growth: cumulative active-student count at each of last 6 months ----
    val studentRows = StudentsTable.selectAll()
        .where { StudentsTable.schoolId eq schoolId }
        .toList()
    val growthLabels = ArrayList<String>(6)
    val growthValues = ArrayList<Int>(6)
    for (back in 5 downTo 0) {
        val monthEnd = today.minusMonths(back.toLong()).withDayOfMonth(1).plusMonths(1).minusDays(1)
        growthLabels.add(MONTHS[monthEnd.monthValue - 1])
        val countByThen = studentRows.count {
            runCatching {
                val created = it[StudentsTable.createdAt].atZone(java.time.ZoneOffset.UTC).toLocalDate()
                !created.isAfter(monthEnd)
            }.getOrDefault(true) // legacy rows w/o parseable date count as pre-existing
        }
        growthValues.add(countByThen)
    }
    val studentGrowth = StudentGrowthDto(growthLabels, growthValues)

    // ---- class performance: top classes by avg exam score ----
    val examRows = ExamResultsTable.selectAll()
        .where { ExamResultsTable.schoolId eq schoolId }
        .toList()
    val topClasses = examRows.groupBy { it[ExamResultsTable.className] }
        .mapNotNull { (className, recs) ->
            val scores = recs.mapNotNull { parseScoreLocal(it[ExamResultsTable.score]) }
            if (scores.isEmpty()) null else TopClassDto(className, scores.average().toInt())
        }
        .sortedByDescending { it.score }
        .take(5)
    val classPerformance = ClassPerformanceDto(topClasses)

    // ---- attendance breakdown: today (fallback: most recent day with data) ----
    val breakdown = attendanceBreakdownFor(attendanceRows)

    return DashboardAnalyticsResponse(
        attendanceTrend = attendanceTrend,
        studentGrowth = studentGrowth,
        classPerformance = classPerformance,
        attendanceBreakdown = breakdown
    )
}

private fun attendanceBreakdownFor(rows: List<org.jetbrains.exposed.sql.ResultRow>): AttendanceBreakdownDto {
    if (rows.isEmpty()) return AttendanceBreakdownDto(0, 0, 0)
    // Use the most recent date that actually has records.
    val latestDate = rows.mapNotNull { it[AttendanceRecordsTable.date] }
        .maxByOrNull { it } ?: return AttendanceBreakdownDto(0, 0, 0)
    val day = rows.filter { it[AttendanceRecordsTable.date] == latestDate }
    val total = day.size.takeIf { it > 0 } ?: return AttendanceBreakdownDto(0, 0, 0)
    val present = day.count { it[AttendanceRecordsTable.status].equals("PRESENT", true) }
    val late = day.count { it[AttendanceRecordsTable.status].equals("LATE", true) }
    val absent = (total - present - late).coerceAtLeast(0)
    return AttendanceBreakdownDto(
        present = (present * 100) / total,
        absent = (absent * 100) / total,
        late = (late * 100) / total
    )
}

/** Local score parser (mirrors SchoolAnalyticsRouting.parseScore which is private there). */
private fun parseScoreLocal(raw: String?): Double? {
    if (raw.isNullOrBlank()) return null
    val s = raw.trim()
    s.toDoubleOrNull()?.let { return it.coerceIn(0.0, 100.0) }
    return when (s.uppercase()) {
        "A+" -> 95.0; "A" -> 88.0; "B+" -> 82.0; "B" -> 75.0
        "C+" -> 68.0; "C" -> 60.0; "D" -> 50.0; "E", "F" -> 35.0
        else -> null
    }
}

// =====================================================================
// activity builder (runs inside dbQuery)
// =====================================================================

private fun buildActivity(ctx: SchoolContext): DashboardActivityResponse {
    val schoolId = ctx.schoolId
    val now = LocalDate.now()

    // ---- alerts: computed from real, actionable backlog ----
    val alerts = ArrayList<AlertDto>()

    // Unassigned teachers (faculty with no active subject assignment).
    val totalTeachers = FacultyTable.selectAll()
        .where { (FacultyTable.schoolId eq schoolId) and (FacultyTable.isActive eq true) }
        .count().toInt()
    val assignmentRows = TeacherSubjectAssignmentsTable.selectAll()
        .where {
            (TeacherSubjectAssignmentsTable.schoolId eq schoolId) and
                (TeacherSubjectAssignmentsTable.isActive eq true)
        }
        .toList()
    val assignedCount = assignmentRows.mapNotNull { row ->
        row[TeacherSubjectAssignmentsTable.teacherId]?.toString()
            ?: row[TeacherSubjectAssignmentsTable.teacherName]?.takeIf { it.isNotBlank() }
    }.toSet().size.coerceAtMost(totalTeachers)
    val unassigned = (totalTeachers - assignedCount).coerceAtLeast(0)
    if (unassigned > 0) {
        alerts.add(
            AlertDto(
                id = "alert_unassigned_teachers",
                type = "WARNING",
                title = "$unassigned teacher(s) unassigned",
                description = "Assign teachers to pending classes",
                priority = "HIGH",
                action = "ASSIGN_TEACHER",
                createdAt = now.atStartOfDay().format(ISO_LOCAL)
            )
        )
    }

    // Pending admission enquiries.
    val pendingAdmissions = AdmissionEnquiriesTable.selectAll()
        .where {
            (AdmissionEnquiriesTable.schoolId eq schoolId) and
                (AdmissionEnquiriesTable.status eq "new")
        }
        .count().toInt()
    if (pendingAdmissions > 0) {
        alerts.add(
            AlertDto(
                id = "alert_pending_admissions",
                type = "INFO",
                title = "$pendingAdmissions pending admission(s)",
                description = "Review new student applications",
                priority = "MEDIUM",
                action = "VIEW_ADMISSIONS",
                createdAt = now.atStartOfDay().format(ISO_LOCAL)
            )
        )
    }

    // ---- activities: real notifications addressed to this admin ----
    val activities = NotificationsTable.selectAll()
        .where { NotificationsTable.userId eq ctx.userId }
        .toList()
        .sortedByDescending { it[NotificationsTable.createdAt] }
        .take(15)
        .map { row ->
            val createdAt = row[NotificationsTable.createdAt]
            val createdLocal = createdAt.atZone(java.time.ZoneOffset.UTC).toLocalDateTime()
            ActivityDto(
                id = row[NotificationsTable.id].value.toString(),
                type = row[NotificationsTable.category].uppercase(),
                title = row[NotificationsTable.title],
                description = row[NotificationsTable.body],
                performedBy = "System",
                time = relativeTime(createdAt),
                createdAt = createdLocal.format(ISO_LOCAL)
            )
        }

    return DashboardActivityResponse(alerts = alerts, activities = activities)
}

/** "10 minutes ago" / "1 hour ago" / "3 days ago" from an instant. */
private fun relativeTime(instant: java.time.Instant): String {
    val secs = java.time.Duration.between(instant, java.time.Instant.now()).seconds.coerceAtLeast(0)
    return when {
        secs < 60 -> "just now"
        secs < 3600 -> "${secs / 60} minute(s) ago"
        secs < 86400 -> "${secs / 3600} hour(s) ago"
        else -> "${secs / 86400} day(s) ago"
    }
}
