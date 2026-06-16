package com.littlebridge.vidyaprayag.ui.v2.screens.school

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.vidyaprayag.feature.admin.domain.model.CampusHealth
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardActivity
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardAnalytics
import com.littlebridge.vidyaprayag.feature.admin.domain.model.DashboardSummary
import com.littlebridge.vidyaprayag.feature.admin.domain.model.OnboardingStep
import com.littlebridge.vidyaprayag.feature.admin.domain.model.QuickAction
import com.littlebridge.vidyaprayag.feature.admin.domain.model.TeacherInsight
import com.littlebridge.vidyaprayag.feature.admin.presentation.DashboardOnboardingStatus
import com.littlebridge.vidyaprayag.feature.admin.presentation.SchoolDashboardViewModel
import com.littlebridge.vidyaprayag.feature.parent.presentation.NotificationsViewModel
import com.littlebridge.vidyaprayag.ui.v2.components.VAvatar
import com.littlebridge.vidyaprayag.ui.v2.components.VBadge
import com.littlebridge.vidyaprayag.ui.v2.components.VBadgeTone
import com.littlebridge.vidyaprayag.ui.v2.components.VCard
import com.littlebridge.vidyaprayag.ui.v2.components.VChartDatum
import com.littlebridge.vidyaprayag.ui.v2.components.VBars
import com.littlebridge.vidyaprayag.ui.v2.components.VDonut
import com.littlebridge.vidyaprayag.ui.v2.components.VIcons
import com.littlebridge.vidyaprayag.ui.v2.components.VLabel
import com.littlebridge.vidyaprayag.ui.v2.components.VLegendDot
import com.littlebridge.vidyaprayag.ui.v2.components.VProgressBar
import com.littlebridge.vidyaprayag.ui.v2.components.VSparkline
import com.littlebridge.vidyaprayag.ui.v2.components.VStatusDot
import com.littlebridge.vidyaprayag.ui.v2.screens.VStateHost
import com.littlebridge.vidyaprayag.ui.v2.screens.collectAsStateV2
import com.littlebridge.vidyaprayag.ui.v2.theme.VTheme
import com.littlebridge.vidyaprayag.ui.v2.theme.colored
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

/**
 * SchoolHomeScreenV2 — the redesigned school-admin home, fully driven by the
 * backend. The onboarding hero (greeting + progress + steps) still comes from
 * [SchoolDashboardViewModel]'s `/user/details`-backed flows, and the rich
 * operational surfaces are populated from the three new dashboard endpoints:
 *
 *   GET /api/admin/dashboard/summary   → school/admin header, CampusHealthCard,
 *                                        statistics cards, teacher insight,
 *                                        quick actions
 *   GET /api/admin/dashboard/analytics → attendance-trend graph, student-growth,
 *                                        class-performance, attendance breakdown
 *   GET /api/admin/dashboard/activity  → alerts + recent-activity feed
 *
 * Every section renders an honest empty/skeleton state when its data is missing
 * (LAW 6). The dashboard fetch is additive and fail-soft, so it never blocks the
 * core onboarding flow. Frozen V* primitives + VTheme only.
 */
@Composable
fun SchoolHomeScreenV2(
    modifier: Modifier = Modifier,
    onOpenNotifications: () -> Unit = {},
    onOpenCalendar: () -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenPews: () -> Unit = {},
    onExit: () -> Unit = {},
    viewModel: SchoolDashboardViewModel = koinViewModel(),
    // RA-S06: account-level notifications feed (GET /api/v1/notifications is
    // JWT-scoped, role-agnostic) drives the header bell's unread dot.
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
) {
    val adminName by viewModel.adminName.collectAsStateV2()
    val notifications by notificationsViewModel.state.collectAsStateV2()
    val progress by viewModel.progress.collectAsStateV2()
    val steps by viewModel.steps.collectAsStateV2()
    val onboardingStatus by viewModel.onboardingStatus.collectAsStateV2()
    val isLoading by viewModel.isLoading.collectAsStateV2()
    val errorMessage by viewModel.errorMessage.collectAsStateV2()
    val summary by viewModel.summary.collectAsStateV2()
    val analytics by viewModel.analytics.collectAsStateV2()
    val activity by viewModel.activity.collectAsStateV2()

    SchoolHomeContent(
        adminName = adminName,
        progress = progress,
        steps = steps,
        onboardingStatus = onboardingStatus,
        isLoading = isLoading,
        errorMessage = errorMessage,
        unreadCount = notifications.unreadCount,
        summary = summary,
        analytics = analytics,
        activity = activity,
        onRetry = viewModel::refresh,
        onOpenNotifications = onOpenNotifications,
        onOpenCalendar = onOpenCalendar,
        onOpenAnalytics = onOpenAnalytics,
        onOpenPews = onOpenPews,
        onExit = onExit,
        modifier = modifier,
    )
}

@Composable
private fun SchoolHomeContent(
    adminName: String,
    progress: Float,
    steps: List<OnboardingStep>,
    onboardingStatus: DashboardOnboardingStatus,
    isLoading: Boolean,
    errorMessage: String?,
    unreadCount: Int,
    summary: DashboardSummary?,
    analytics: DashboardAnalytics?,
    activity: DashboardActivity?,
    onRetry: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenPews: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = VTheme.colors
    val completed = onboardingStatus == DashboardOnboardingStatus.COMPLETED
    val schoolName = summary?.school?.name?.takeIf { it.isNotBlank() } ?: "School console"
    val termLine = summary?.school?.let {
        listOfNotNull(
            it.academicYear.takeIf { y -> y.isNotBlank() },
            it.currentTerm.takeIf { t -> t.isNotBlank() },
        ).joinToString(" · ")
    }?.takeIf { it.isNotBlank() }

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // ── Header row ────────────────────────────────────────────────────────
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(c.teal), contentAlignment = Alignment.Center) {
                    Icon(VIcons.GraduationCap, contentDescription = null, tint = Color(0xFF080808), modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(schoolName, style = VTheme.type.h4.colored(c.ink), maxLines = 1)
                    Text(
                        termLine ?: if (completed) "Campus live" else "Setup in progress",
                        style = VTheme.type.caption.colored(c.ink2),
                        maxLines = 1,
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(c.ink.copy(alpha = 0.06f)).clickable { onOpenNotifications() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VIcons.Bell, contentDescription = "Notifications", tint = c.ink, modifier = Modifier.size(18.dp))
                    if (unreadCount > 0) {
                        Box(Modifier.align(Alignment.TopEnd).padding(8.dp).size(6.dp).clip(CircleShape).background(c.danger))
                    }
                }
                Box(Modifier.clickable { onExit() }) {
                    VAvatar(name = summary?.admin?.name ?: adminName, size = 36.dp)
                }
            }
        }

        VStateHost(
            loading = isLoading,
            error = errorMessage,
            isEmpty = false,
            onRetry = onRetry,
            skeleton = { com.littlebridge.vidyaprayag.ui.v2.screens.SkeletonDashboard() },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // ── Greeting ───────────────────────────────────────────────────
                val greetName = summary?.admin?.name?.takeIf { it.isNotBlank() } ?: adminName
                Column {
                    Text("Welcome, $greetName", style = VTheme.type.h1.colored(c.ink))
                    Text(
                        if (completed) "Your campus is live. Manage everything from here."
                        else "Let's finish setting up your school.",
                        style = VTheme.type.body.colored(c.ink2),
                    )
                }

                // ── Campus health (REAL — with trend graph) ────────────────────
                summary?.campusHealth?.let { health ->
                    CampusHealthCard(health = health, attendanceTrend = analytics?.attendanceTrend?.values ?: emptyList())
                }

                // ── Onboarding hero (REAL data) ────────────────────────────────
                VCard {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        VLabel(if (completed) "Onboarding complete" else "Onboarding progress")
                        VBadge(
                            text = "${(progress * 100).roundToInt()}%",
                            tone = if (completed) VBadgeTone.Success else VBadgeTone.Arctic,
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    VProgressBar(
                        value = progress * 100f,
                        tone = if (completed) VBadgeTone.Success else VBadgeTone.Arctic,
                    )
                    Spacer(Modifier.height(14.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        steps.forEach { step -> OnboardingStepRow(step) }
                    }
                }

                // ── Statistics cards (REAL) ────────────────────────────────────
                summary?.statistics?.let { stats ->
                    Column {
                        Text("At a glance", style = VTheme.type.h3.colored(c.ink), modifier = Modifier.padding(bottom = 8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = VIcons.Users,
                                label = "Students",
                                value = stats.students.total,
                                sub = "${stats.students.active} active",
                                trendDir = stats.students.trend.direction,
                                trendText = "+${stats.students.newAdmissions} new",
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = VIcons.GraduationCap,
                                label = "Teachers",
                                value = stats.teachers.total,
                                sub = "${stats.teachers.active} active",
                                trendDir = stats.teachers.trend.direction,
                                trendText = "+${stats.teachers.newJoined} new",
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = VIcons.School,
                                label = "Classes",
                                value = stats.classes.total,
                                sub = "${stats.classes.active} active",
                                trendDir = "flat",
                                trendText = null,
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                icon = VIcons.BookOpen,
                                label = "Subjects",
                                value = stats.subjects.total,
                                sub = "${stats.subjects.active} active",
                                trendDir = "flat",
                                trendText = null,
                            )
                        }
                    }
                }

                // ── Teacher insight (REAL — coverage donut) ────────────────────
                summary?.teacherInsight?.let { insight ->
                    if (insight.totalTeachers > 0) TeacherInsightCard(insight)
                }

                // ── Quick actions (REAL — permission-aware) ────────────────────
                summary?.quickActions?.takeIf { it.isNotEmpty() }?.let { actions ->
                    QuickActionsRow(
                        actions = actions,
                        onAction = { qa ->
                            // Route the two analytics-bound actions to existing screens;
                            // the rest are surfaced as enabled entry points the host
                            // portal can wire as features land (no dead Coming-Soon).
                            when (qa.id) {
                                "REPORTS" -> onOpenAnalytics()
                                else -> Unit
                            }
                        },
                    )
                }

                // ── Analytics charts (REAL) ────────────────────────────────────
                analytics?.let { a -> AnalyticsSection(a, onOpenAnalytics) }

                // ── Activity & alerts (REAL) ───────────────────────────────────
                activity?.let { act -> ActivitySection(act) }

                // ── Early-warning radar (REAL — opens the at-risk cohort) ──────
                Column {
                    Text("Early-warning radar", style = VTheme.type.h3.colored(c.ink), modifier = Modifier.padding(bottom = 8.dp))
                    MetricEntryCard(
                        icon = VIcons.AlertTriangle,
                        title = "PEWS — Predictive Early Warning",
                        description = "Attendance, marks and risk signals surface at-risk students before exam season.",
                        onClick = onOpenPews,
                        preview = { PewsPreview() },
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CampusHealthCard — the FIXED graph card. The previous design had no working
// graph; this renders a real VSparkline of the attendance trend with a status
// pill and a metric strip. When there's no trend data we show an honest empty
// micro-state instead of a broken/blank canvas.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CampusHealthCard(
    health: CampusHealth,
    attendanceTrend: List<Int>,
) {
    val c = VTheme.colors
    val (dotColor, tone) = when (health.status.uppercase()) {
        "HEALTHY" -> c.successInk to VBadgeTone.Success
        "WATCH" -> c.warningInk to VBadgeTone.Warning
        "CRITICAL" -> c.dangerInk to VBadgeTone.Danger
        else -> c.ink3 to VBadgeTone.Neutral
    }
    VCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VStatusDot(color = dotColor, ring = true)
                VLabel("Campus health")
            }
            VBadge(text = health.status.replaceFirstChar { it.uppercase() }, tone = tone)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            health.message.ifBlank { "Campus health will appear once data is available." },
            style = VTheme.type.body.colored(c.ink2),
        )

        // The fixed graph: a real attendance sparkline (values are 0..100 %).
        Spacer(Modifier.height(14.dp))
        if (attendanceTrend.size >= 2) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(c.cream)
                    .padding(12.dp),
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("ATTENDANCE TREND", style = VTheme.type.label.colored(c.ink3).copy(fontWeight = FontWeight.Bold))
                        Text("${attendanceTrend.last()}%", style = VTheme.type.dataSm.colored(c.tealDeep).copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(Modifier.height(8.dp))
                    VSparkline(
                        values = attendanceTrend.map { it.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        width = 280.dp,
                        height = 44.dp,
                    )
                }
            }
        }

        // Metric strip (attendance / fee collection — only those the server sent).
        if (health.metrics.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                health.metrics.forEach { m ->
                    Column(Modifier.weight(1f)) {
                        Text(m.label, style = VTheme.type.caption.colored(c.ink3), maxLines = 1)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                if (m.unit == "percentage") "${m.value}%" else m.value.toString(),
                                style = VTheme.type.h3.colored(c.ink),
                            )
                            TrendChip(m.trend.direction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendChip(direction: String) {
    val c = VTheme.colors
    val (icon, tint) = when (direction.lowercase()) {
        "up" -> VIcons.TrendingUp to c.successInk
        "down" -> VIcons.TrendingUp to c.dangerInk // rotated visual not needed; tint conveys it
        else -> VIcons.TrendingUp to c.ink3
    }
    if (direction.lowercase() == "flat") return
    Icon(icon, contentDescription = direction, tint = tint, modifier = Modifier.size(14.dp))
}

// ─────────────────────────────────────────────────────────────────────────────
// StatCard — one statistic tile (Students / Teachers / Classes / Subjects).
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: Int,
    sub: String,
    trendDir: String,
    trendText: String?,
    modifier: Modifier = Modifier,
) {
    val c = VTheme.colors
    VCard(modifier = modifier) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(c.teal.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = c.tealDeep, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(value.toString(), style = VTheme.type.h2.colored(c.ink))
        Text(label, style = VTheme.type.caption.colored(c.ink2))
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(sub, style = VTheme.type.label.colored(c.ink3))
            if (trendText != null && trendDir.lowercase() == "up") {
                Text(trendText, style = VTheme.type.label.colored(c.successInk).copy(fontWeight = FontWeight.SemiBold))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TeacherInsightCard — assignment coverage donut + department breakdown.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TeacherInsightCard(insight: TeacherInsight) {
    val c = VTheme.colors
    Column {
        Text("Teacher insight", style = VTheme.type.h3.colored(c.ink), modifier = Modifier.padding(bottom = 8.dp))
        VCard {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                VDonut(
                    data = listOf(
                        VChartDatum("Assigned", insight.assignedTeachers.toFloat(), c.tealDeep),
                        VChartDatum("Pending", insight.pendingAssignment.toFloat(), c.warning),
                    ),
                    size = 96.dp,
                    thickness = 12.dp,
                    center = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${insight.assignmentCoverage}%", style = VTheme.type.h3.colored(c.ink))
                            Text("covered", style = VTheme.type.label.colored(c.ink3))
                        }
                    },
                )
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    VLegendDot(color = c.tealDeep, label = "Assigned", value = insight.assignedTeachers.toString())
                    VLegendDot(color = c.warning, label = "Pending", value = insight.pendingAssignment.toString())
                    Text(
                        "${insight.totalTeachers} teachers in total",
                        style = VTheme.type.caption.colored(c.ink2),
                    )
                }
            }
            if (insight.departments.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                VLabel("Departments")
                Spacer(Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    insight.departments.take(5).forEach { d ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(d.name, style = VTheme.type.body.colored(c.ink), maxLines = 1)
                            Text("${d.teacherCount}", style = VTheme.type.bodyStrong.colored(c.ink2))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// QuickActionsRow — permission-aware action tiles.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun QuickActionsRow(
    actions: List<QuickAction>,
    onAction: (QuickAction) -> Unit,
) {
    val c = VTheme.colors
    Column {
        Text("Quick actions", style = VTheme.type.h3.colored(c.ink), modifier = Modifier.padding(bottom = 8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            actions.take(4).forEach { qa ->
                val icon = when (qa.id) {
                    "ADD_TEACHER" -> VIcons.GraduationCap
                    "ADD_STUDENT" -> VIcons.Users
                    "CREATE_CLASS" -> VIcons.School
                    "REPORTS" -> VIcons.TrendingUp
                    else -> VIcons.Plus
                }
                val enabled = qa.enabled
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (enabled) c.teal.copy(alpha = 0.12f) else c.cream)
                        .then(if (enabled) Modifier.clickable { onAction(qa) } else Modifier)
                        .padding(vertical = 12.dp, horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        icon,
                        contentDescription = qa.title,
                        tint = if (enabled) c.tealDeep else c.ink3,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        qa.title,
                        style = VTheme.type.label.colored(if (enabled) c.ink else c.ink3).copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AnalyticsSection — student-growth bars, class-performance, attendance donut.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AnalyticsSection(a: DashboardAnalytics, onOpenAnalytics: () -> Unit) {
    val c = VTheme.colors
    Column {
        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Analytics", style = VTheme.type.h3.colored(c.ink))
            Row(Modifier.clickable { onOpenAnalytics() }, verticalAlignment = Alignment.CenterVertically) {
                Text("View all", style = VTheme.type.caption.colored(c.tealDeep))
                Icon(VIcons.ChevronRight, contentDescription = null, tint = c.tealDeep, modifier = Modifier.size(16.dp))
            }
        }

        // Student growth (cumulative) — bars.
        if (a.studentGrowth.values.size >= 2) {
            VCard {
                VLabel("Student growth")
                Spacer(Modifier.height(12.dp))
                VBars(
                    data = a.studentGrowth.labels.zip(a.studentGrowth.values).map { (l, v) ->
                        VChartDatum(l, v.toFloat())
                    },
                    height = 120.dp,
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        // Class performance + attendance breakdown.
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Top classes.
            VCard(modifier = Modifier.weight(1f)) {
                VLabel("Top classes")
                Spacer(Modifier.height(10.dp))
                if (a.classPerformance.topClasses.isEmpty()) {
                    Text("No exam data yet.", style = VTheme.type.caption.colored(c.ink3))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        a.classPerformance.topClasses.take(4).forEach { tc ->
                            Column {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(tc.className, style = VTheme.type.caption.colored(c.ink), maxLines = 1)
                                    Text("${tc.score}", style = VTheme.type.dataSm.colored(c.ink2).copy(fontWeight = FontWeight.SemiBold))
                                }
                                Spacer(Modifier.height(4.dp))
                                VProgressBar(value = tc.score.toFloat(), tone = VBadgeTone.Arctic, height = 5.dp)
                            }
                        }
                    }
                }
            }

            // Attendance breakdown donut.
            VCard(modifier = Modifier.weight(1f)) {
                VLabel("Attendance today")
                Spacer(Modifier.height(10.dp))
                val b = a.attendanceBreakdown
                if (b.present + b.absent + b.late == 0) {
                    Text("No records yet.", style = VTheme.type.caption.colored(c.ink3))
                } else {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        VDonut(
                            data = listOf(
                                VChartDatum("Present", b.present.toFloat(), c.successInk),
                                VChartDatum("Late", b.late.toFloat(), c.warningInk),
                                VChartDatum("Absent", b.absent.toFloat(), c.dangerInk),
                            ),
                            size = 92.dp,
                            thickness = 12.dp,
                            center = {
                                Text("${b.present}%", style = VTheme.type.bodyStrong.colored(c.ink))
                            },
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    VLegendDot(color = c.successInk, label = "Present", value = "${b.present}%")
                    Spacer(Modifier.height(4.dp))
                    VLegendDot(color = c.warningInk, label = "Late", value = "${b.late}%")
                    Spacer(Modifier.height(4.dp))
                    VLegendDot(color = c.dangerInk, label = "Absent", value = "${b.absent}%")
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ActivitySection — alerts (actionable) + recent-activity feed.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ActivitySection(act: DashboardActivity) {
    val c = VTheme.colors

    if (act.alerts.isNotEmpty()) {
        Column {
            Text("Alerts", style = VTheme.type.h3.colored(c.ink), modifier = Modifier.padding(bottom = 8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                act.alerts.forEach { alert ->
                    val (icon, tint) = when (alert.type.uppercase()) {
                        "CRITICAL" -> VIcons.AlertTriangle to c.dangerInk
                        "WARNING" -> VIcons.AlertTriangle to c.warningInk
                        else -> VIcons.AlertCircle to c.tealDeep
                    }
                    VCard {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                Modifier.size(36.dp).clip(CircleShape).background(tint.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                            }
                            Column(Modifier.weight(1f)) {
                                Text(alert.title, style = VTheme.type.bodyStrong.colored(c.ink), maxLines = 1)
                                Text(alert.description, style = VTheme.type.caption.colored(c.ink2), maxLines = 2)
                            }
                            VBadge(
                                text = alert.priority.replaceFirstChar { it.uppercase() },
                                tone = when (alert.priority.uppercase()) {
                                    "HIGH" -> VBadgeTone.Danger
                                    "MEDIUM" -> VBadgeTone.Warning
                                    else -> VBadgeTone.Neutral
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    Column {
        Text("Recent activity", style = VTheme.type.h3.colored(c.ink), modifier = Modifier.padding(bottom = 8.dp))
        VCard {
            if (act.activities.isEmpty()) {
                Text("No recent activity yet.", style = VTheme.type.caption.colored(c.ink3))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    act.activities.take(6).forEach { a ->
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                Modifier.padding(top = 6.dp).size(8.dp).clip(CircleShape).background(c.teal),
                            )
                            Column(Modifier.weight(1f)) {
                                Text(a.title, style = VTheme.type.bodyStrong.colored(c.ink), maxLines = 1)
                                if (a.description.isNotBlank()) {
                                    Text(a.description, style = VTheme.type.caption.colored(c.ink2), maxLines = 2)
                                }
                            }
                            Text(a.time, style = VTheme.type.label.colored(c.ink3), maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

/**
 * RA-24: a tappable entry card that opens an existing, backend-backed screen.
 * Frozen V* primitives only.
 */
@Composable
private fun MetricEntryCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    preview: (@Composable () -> Unit)? = null,
) {
    val c = VTheme.colors
    VCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(c.teal.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = c.tealDeep, modifier = Modifier.size(18.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = VTheme.type.bodyStrong.colored(c.ink))
                Text(description, style = VTheme.type.caption.colored(c.ink2))
            }
            Icon(VIcons.ChevronRight, contentDescription = null, tint = c.ink3, modifier = Modifier.size(18.dp))
        }
        if (preview != null) {
            Spacer(Modifier.height(12.dp))
            preview()
        }
    }
}

@Composable
private fun OnboardingStepRow(step: OnboardingStep) {
    val c = VTheme.colors
    val (icon, tint) = when {
        step.isCompleted -> VIcons.Check to c.successInk
        step.status.equals(OnboardingStep.STATUS_LOCKED, ignoreCase = true) -> VIcons.Lock to c.ink3
        else -> VIcons.ClipboardList to c.ink2
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.size(32.dp).clip(CircleShape).background(c.ink.copy(alpha = 0.06f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(step.title, style = VTheme.type.bodyStrong.colored(c.ink))
            if (step.description.isNotBlank()) {
                Text(step.description, style = VTheme.type.caption.colored(c.ink2))
            }
        }
        if (step.isCompleted) {
            VBadge(text = "Done", tone = VBadgeTone.Success)
        }
    }
}
