/*
 * File: AdminDashboardDigestRouting.kt
 * Module: feature.school
 *
 * Endpoint (JWT + school-scoped via requireSchoolContext):
 *   GET /api/admin/dashboard/digest
 *
 * Returns a short "today's focus" summary for the redesigned SchoolHomeScreenV2
 * hero. All values are computed from existing tables; no new schema is required.
 */
package com.littlebridge.enrollplus.feature.school

import com.littlebridge.enrollplus.core.ok
import com.littlebridge.enrollplus.core.requireSchoolContext
import com.littlebridge.enrollplus.db.AcademicCalendarTable
import com.littlebridge.enrollplus.db.AppUsersTable
import com.littlebridge.enrollplus.db.AttendanceRecordsTable
import com.littlebridge.enrollplus.db.DatabaseFactory.dbQuery
import com.littlebridge.enrollplus.db.FeeRecordsTable
import com.littlebridge.enrollplus.db.LeaveRequestsTable
import com.littlebridge.enrollplus.db.MessageThreadsTable
import com.littlebridge.enrollplus.db.NotificationsTable
import com.littlebridge.enrollplus.db.ParentChildLinksTable
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class DigestTaskDto(
    val id: String,
    val label: String,
    val icon: String,
    @SerialName("action_label") val actionLabel: String,
    @SerialName("route_id") val routeId: String?,
    val count: Int = 0,
    val priority: String = "normal", // normal | urgent | success
)

@Serializable
data class DailyDigestDto(
    val headline: String,
    val focus: String,
    val tasks: List<DigestTaskDto>,
)

fun Route.adminDashboardDigestRouting() {
    authenticate("jwt") {
        route("/api/admin/dashboard") {
            get("/digest") {
                val ctx = call.requireSchoolContext() ?: return@get
                val today = LocalDate.now()
                val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
                val plusThreeStr = today.plusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE)

                val digest = dbQuery {
                    val adminName = AppUsersTable
                        .selectAll()
                        .where { AppUsersTable.id eq ctx.userId }
                        .singleOrNull()
                        ?.let { it[AppUsersTable.fullName] }
                        ?.trim()
                        ?.substringBefore(" ")
                        ?: "Admin"

                    val hour = today.atStartOfDay().let { java.time.LocalTime.now().hour }
                    val greeting = when (hour) {
                        in 5..11 -> "Good morning"
                        in 12..16 -> "Good afternoon"
                        in 17..21 -> "Good evening"
                        else -> "Welcome back"
                    }

                    val unreadNotifications = NotificationsTable
                        .selectAll()
                        .where {
                            (NotificationsTable.userId eq ctx.userId) and
                                (NotificationsTable.isRead eq false) and
                                (NotificationsTable.archivedAt.isNull())
                        }
                        .count()
                        .toInt()

                    val pendingLinks = ParentChildLinksTable
                        .selectAll()
                        .where {
                            (ParentChildLinksTable.schoolId eq ctx.schoolId) and
                                (
                                    (ParentChildLinksTable.status eq "pending") or
                                        (ParentChildLinksTable.status eq "needs_review")
                                    )
                        }
                        .count()
                        .toInt()

                    val pendingLeaves = LeaveRequestsTable
                        .selectAll()
                        .where {
                            (LeaveRequestsTable.schoolId eq ctx.schoolId) and
                                (LeaveRequestsTable.status eq "Pending")
                        }
                        .count()
                        .toInt()

                    val feeDue = FeeRecordsTable
                        .selectAll()
                        .where {
                            (FeeRecordsTable.schoolId eq ctx.schoolId) and
                                ((FeeRecordsTable.status eq "DUE") or (FeeRecordsTable.status eq "OVERDUE"))
                        }
                        .count()
                        .toInt()

                    val attendanceMarkedToday = AttendanceRecordsTable
                        .selectAll()
                        .where {
                            (AttendanceRecordsTable.schoolId eq ctx.schoolId) and
                                (AttendanceRecordsTable.date eq today) and
                                (AttendanceRecordsTable.type eq "student")
                        }
                        .count() > 0

                    val upcomingEvents = AcademicCalendarTable
                        .selectAll()
                        .where {
                            (AcademicCalendarTable.schoolId eq ctx.schoolId) and
                                (AcademicCalendarTable.date greaterEq todayStr) and
                                (AcademicCalendarTable.date lessEq plusThreeStr)
                        }
                        .count()
                        .toInt()

                    val pendingThreads = MessageThreadsTable
                        .selectAll()
                        .where {
                            (MessageThreadsTable.ownerUserId eq ctx.userId) and
                                (MessageThreadsTable.isRead eq false) and
                                (MessageThreadsTable.isArchived eq false)
                        }
                        .count()
                        .toInt()

                    val tasks = buildList {
                        if (pendingLinks > 0) {
                            add(
                                DigestTaskDto(
                                    id = "pending_links",
                                    label = "$pendingLinks pending link request${if (pendingLinks == 1) "" else "s"}",
                                    icon = "UsersGroup",
                                    actionLabel = "Review",
                                    routeId = "overlay_link_requests",
                                    count = pendingLinks,
                                    priority = "urgent",
                                )
                            )
                        }
                        if (pendingLeaves > 0) {
                            add(
                                DigestTaskDto(
                                    id = "pending_leaves",
                                    label = "$pendingLeaves leave request${if (pendingLeaves == 1) "" else "s"} pending",
                                    icon = "Calendar",
                                    actionLabel = "Review",
                                    routeId = "overlay_leave_requests",
                                    count = pendingLeaves,
                                    priority = "normal",
                                )
                            )
                        }
                        if (feeDue > 0) {
                            add(
                                DigestTaskDto(
                                    id = "fee_due",
                                    label = "$feeDue fee record${if (feeDue == 1) "" else "s"} due",
                                    icon = "Wallet",
                                    actionLabel = "Collect",
                                    routeId = "settings_fees",
                                    count = feeDue,
                                    priority = "urgent",
                                )
                            )
                        }
                        if (unreadNotifications > 0) {
                            add(
                                DigestTaskDto(
                                    id = "unread_notifications",
                                    label = "$unreadNotifications unread notification${if (unreadNotifications == 1) "" else "s"}",
                                    icon = "Bell",
                                    actionLabel = "Open",
                                    routeId = "overlay_notifications",
                                    count = unreadNotifications,
                                    priority = "normal",
                                )
                            )
                        }
                        if (pendingThreads > 0) {
                            add(
                                DigestTaskDto(
                                    id = "pending_messages",
                                    label = "$pendingThreads unread message${if (pendingThreads == 1) "" else "s"}",
                                    icon = "Chat",
                                    actionLabel = "Reply",
                                    routeId = "overlay_messages",
                                    count = pendingThreads,
                                    priority = "normal",
                                )
                            )
                        }
                        if (!attendanceMarkedToday) {
                            add(
                                DigestTaskDto(
                                    id = "attendance_today",
                                    label = "Attendance not marked today",
                                    icon = "CheckCircle",
                                    actionLabel = "Mark",
                                    routeId = "overlay_daily_attendance",
                                    count = 0,
                                    priority = "urgent",
                                )
                            )
                        }
                        if (upcomingEvents > 0) {
                            add(
                                DigestTaskDto(
                                    id = "upcoming_events",
                                    label = "$upcomingEvents event${if (upcomingEvents == 1) "" else "s"} in the next 3 days",
                                    icon = "Sparkles",
                                    actionLabel = "View",
                                    routeId = "overlay_calendar",
                                    count = upcomingEvents,
                                    priority = "normal",
                                )
                            )
                        }
                        if (isEmpty()) {
                            add(
                                DigestTaskDto(
                                    id = "all_clear",
                                    label = "All caught up",
                                    icon = "Check",
                                    actionLabel = "Explore",
                                    routeId = null,
                                    count = 0,
                                    priority = "success",
                                )
                            )
                        }
                    }

                    val focus = when {
                        pendingLinks > 0 || pendingLeaves > 0 || feeDue > 0 -> {
                            val items = listOfNotNull(
                                pendingLinks.takeIf { it > 0 }?.let { "$it approval${if (it == 1) "" else "s"}" },
                                pendingLeaves.takeIf { it > 0 }?.let { "$it leave" },
                                feeDue.takeIf { it > 0 }?.let { "$it fee" },
                            )
                            "Today: ${items.joinToString(", ")} pending your review."
                        }
                        !attendanceMarkedToday -> "Start the day by marking attendance."
                        upcomingEvents > 0 -> "You have $upcomingEvents upcoming event${if (upcomingEvents == 1) "" else "s"} this week."
                        else -> "You're all caught up. Here's your campus snapshot."
                    }

                    DailyDigestDto(
                        headline = "$greeting, $adminName",
                        focus = focus,
                        tasks = tasks.take(3),
                    )
                }

                call.ok(digest, message = "Daily digest fetched successfully")
            }
        }
    }
}
