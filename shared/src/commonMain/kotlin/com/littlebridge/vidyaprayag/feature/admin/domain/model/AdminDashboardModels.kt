/*
 * File: AdminDashboardModels.kt
 * Module: feature.admin.domain.model
 *
 * DTOs for the redesigned School home dashboard.
 * Matches server: feature.school.AdminDashboardRouting.kt
 *
 *   GET /api/admin/dashboard/summary   → DashboardSummary
 *   GET /api/admin/dashboard/analytics → DashboardAnalytics
 *   GET /api/admin/dashboard/activity  → DashboardActivity
 *
 * All keys are camelCase to match the server's @Serializable data classes
 * (which use kotlinx default field names, NOT @SerialName snake_case). Every
 * field has a safe default so a partial / drifting payload still decodes — the
 * UI then renders an honest empty state for whatever is missing.
 */
package com.littlebridge.vidyaprayag.feature.admin.domain.model

import kotlinx.serialization.Serializable

// ── shared trend shapes ──────────────────────────────────────────────────────

@Serializable
data class DashTrend(
    val direction: String = "flat",   // up | down | flat
    val value: Double = 0.0
)

@Serializable
data class DashPctTrend(
    val direction: String = "flat",
    val percentage: Int = 0
)

// ── summary ──────────────────────────────────────────────────────────────────

@Serializable
data class DashSchool(
    val id: String = "",
    val name: String = "",
    val logoUrl: String? = null,
    val academicYear: String = "",
    val currentTerm: String = ""
)

@Serializable
data class DashAdmin(
    val id: String = "",
    val name: String = "Admin",
    val avatarUrl: String? = null
)

@Serializable
data class CampusMetric(
    val key: String = "",
    val label: String = "",
    val value: Int = 0,
    val unit: String = "",
    val trend: DashTrend = DashTrend()
)

@Serializable
data class CampusHealth(
    val status: String = "UNKNOWN",   // HEALTHY | WATCH | CRITICAL | UNKNOWN
    val message: String = "",
    val metrics: List<CampusMetric> = emptyList()
)

@Serializable
data class StudentStats(
    val total: Int = 0,
    val active: Int = 0,
    val newAdmissions: Int = 0,
    val trend: DashPctTrend = DashPctTrend()
)

@Serializable
data class TeacherStats(
    val total: Int = 0,
    val active: Int = 0,
    val newJoined: Int = 0,
    val trend: DashPctTrend = DashPctTrend()
)

@Serializable
data class CountStats(
    val total: Int = 0,
    val active: Int = 0
)

@Serializable
data class DashStatistics(
    val students: StudentStats = StudentStats(),
    val teachers: TeacherStats = TeacherStats(),
    val classes: CountStats = CountStats(),
    val subjects: CountStats = CountStats()
)

@Serializable
data class DashDepartment(
    val name: String = "",
    val teacherCount: Int = 0
)

@Serializable
data class TeacherInsight(
    val totalTeachers: Int = 0,
    val assignedTeachers: Int = 0,
    val pendingAssignment: Int = 0,
    val assignmentCoverage: Int = 0,
    val departments: List<DashDepartment> = emptyList()
)

@Serializable
data class QuickAction(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val enabled: Boolean = true,
    val permission: String = ""
)

@Serializable
data class DashboardSummary(
    val school: DashSchool = DashSchool(),
    val admin: DashAdmin = DashAdmin(),
    val campusHealth: CampusHealth = CampusHealth(),
    val statistics: DashStatistics = DashStatistics(),
    val teacherInsight: TeacherInsight = TeacherInsight(),
    val quickActions: List<QuickAction> = emptyList()
)

// ── analytics ────────────────────────────────────────────────────────────────

@Serializable
data class AttendanceTrend(
    val period: String = "monthly",
    val labels: List<String> = emptyList(),
    val values: List<Int> = emptyList()
)

@Serializable
data class StudentGrowth(
    val labels: List<String> = emptyList(),
    val values: List<Int> = emptyList()
)

@Serializable
data class TopClass(
    // Server serialises this under "class" via @SerialName; we mirror that here.
    @kotlinx.serialization.SerialName("class") val className: String = "",
    val score: Int = 0
)

@Serializable
data class ClassPerformance(
    val topClasses: List<TopClass> = emptyList()
)

@Serializable
data class AttendanceBreakdown(
    val present: Int = 0,
    val absent: Int = 0,
    val late: Int = 0
)

@Serializable
data class DashboardAnalytics(
    val attendanceTrend: AttendanceTrend = AttendanceTrend(),
    val studentGrowth: StudentGrowth = StudentGrowth(),
    val classPerformance: ClassPerformance = ClassPerformance(),
    val attendanceBreakdown: AttendanceBreakdown = AttendanceBreakdown()
)

// ── activity ─────────────────────────────────────────────────────────────────

@Serializable
data class DashAlert(
    val id: String = "",
    val type: String = "INFO",        // WARNING | INFO | CRITICAL
    val title: String = "",
    val description: String = "",
    val priority: String = "LOW",     // HIGH | MEDIUM | LOW
    val action: String = "",
    val createdAt: String = ""
)

@Serializable
data class DashActivity(
    val id: String = "",
    val type: String = "",
    val title: String = "",
    val description: String = "",
    val performedBy: String = "",
    val time: String = "",
    val createdAt: String = ""
)

@Serializable
data class DashboardActivity(
    val alerts: List<DashAlert> = emptyList(),
    val activities: List<DashActivity> = emptyList()
)
