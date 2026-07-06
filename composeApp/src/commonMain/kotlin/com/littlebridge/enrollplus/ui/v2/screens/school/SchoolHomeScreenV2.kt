package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardAnalytics
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardOverview
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewInsight
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewKpi
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewSchoolPulse
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarPlatformViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.platform.rememberNotificationPermissionLauncher
import com.littlebridge.enrollplus.presentation.PermissionViewModel
import com.littlebridge.enrollplus.ui.components.VAvatar
import com.littlebridge.enrollplus.ui.components.VAvatarSize
import com.littlebridge.enrollplus.ui.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.components.VEmptyState
import com.littlebridge.enrollplus.ui.components.VPullRefresh
import com.littlebridge.enrollplus.ui.components.skeletons.SkeletonDashboard
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

// ─────────────────────────────────────────────────────────────────────────────
// School Home — Command Center
// Inspired by the onboarding screen's design language:
//   • Violet hero header (like onboarding completion)
//   • CreamCard pattern (surfaceCard + 1dp border + lg shape)
//   • Generous 24dp padding, 16dp spacing
//   • Strong typography hierarchy
//   • No gradients, no gauges, no bouncy cards, no clutter
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SchoolHomeScreenV2(
    modifier: Modifier = Modifier,
    onOpenNotifications: () -> Unit = {},
    onOpenCalendar: () -> Unit = {},
    onOpenAnalytics: () -> Unit = {},
    onOpenPews: () -> Unit = {},
    onOpenTransport: () -> Unit = {},
    onOpenReportPublish: () -> Unit = {},
    onOpenReportEffectiveness: () -> Unit = {},
    onOpenEvents: () -> Unit = {},
    onCreateEvent: () -> Unit = {},
    onExit: () -> Unit = {},
    viewModel: SchoolDashboardViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
    calendarViewModel: AcademicCalendarPlatformViewModel = koinViewModel(),
    permissionVm: PermissionViewModel = koinViewModel(),
) {
    val dashboardState by viewModel.state.collectAsState()
    val notifications by notificationsViewModel.state.collectAsState()
    val calendarState by calendarViewModel.state.collectAsState()

    val adminName = dashboardState.adminName
    val loading = dashboardState.isLoading
    val error = dashboardState.errorMessage
    val overview = dashboardState.overview
    val analytics = dashboardState.analytics
    val activity = dashboardState.activity

    val showRationale by permissionVm.showNotificationRationale.collectAsState()
    val launchPermission by permissionVm.launchPermissionRequest.collectAsState()

    val permissionLauncher = rememberNotificationPermissionLauncher { granted ->
        permissionVm.onPermissionResult(granted)
    }

    LaunchedEffect(launchPermission) {
        if (launchPermission) {
            permissionVm.consumeLaunchPermissionRequest()
            permissionLauncher.launch()
        }
    }

    LaunchedEffect(Unit) {
        permissionVm.checkNotificationPermission()
    }

    val isLoading = loading && overview == null
    val hasError = error != null && overview == null

    VPullRefresh(
        isRefreshing = loading && overview != null,
        onRefresh = { viewModel.refresh(); calendarViewModel.refresh() },
        modifier = modifier.fillMaxSize().background(VColors.cream),
    ) {
        when {
            isLoading -> Column(
                Modifier.fillMaxSize().statusBarsPadding().padding(top = 16.dp),
            ) { SkeletonDashboard() }

            hasError -> Column(
                Modifier.fillMaxSize().statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(120.dp))
                VEmptyState(
                    title = "Couldn't load dashboard",
                    body = error ?: "Pull down to retry.",
                    icon = Icons.Filled.Warning,
                    actionLabel = "Retry",
                    onAction = { viewModel.refresh(); calendarViewModel.refresh() },
                )
            }

            overview == null -> Column(
                Modifier.fillMaxSize().statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                VEmptyState(
                    title = "Nothing to show yet",
                    body = "Your dashboard will appear here once data is available.",
                    icon = Icons.Filled.School,
                )
            }

            else -> CommandCenter(
                overview = overview,
                analytics = analytics,
                activity = activity,
                adminName = adminName,
                unreadCount = notifications.unreadCount,
                onOpenNotifications = onOpenNotifications,
                onOpenCalendar = onOpenCalendar,
                onOpenAnalytics = onOpenAnalytics,
                onOpenPews = onOpenPews,
                onOpenTransport = onOpenTransport,
                onOpenReportPublish = onOpenReportPublish,
                onOpenReportEffectiveness = onOpenReportEffectiveness,
                onOpenEvents = onOpenEvents,
                onCreateEvent = onCreateEvent,
                onExit = onExit,
            )
        }
    }

    VConfirmDialog(
        visible = showRationale,
        title = appString(StringKeys.HOME_NOTIF_RATIONALE_TITLE),
        message = appString(StringKeys.HOME_NOTIF_RATIONALE_MSG),
        confirmLabel = appString(StringKeys.HOME_NOTIF_ENABLE),
        onConfirm = permissionVm::requestNotificationPermission,
        onDismiss = permissionVm::declineNotifications,
        cancelLabel = appString(StringKeys.HOME_NOTIF_NOT_NOW),
        icon = Icons.Filled.Notifications,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Command Center — the scrollable dashboard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CommandCenter(
    overview: AdminDashboardOverview,
    analytics: AdminDashboardAnalytics?,
    activity: AdminDashboardActivity?,
    adminName: String,
    unreadCount: Int,
    onOpenNotifications: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenPews: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenReportPublish: () -> Unit,
    onOpenReportEffectiveness: () -> Unit,
    onOpenEvents: () -> Unit,
    onCreateEvent: () -> Unit,
    onExit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp),
    ) {
        HeroHeader(
            overview = overview,
            fallbackName = adminName,
            unreadCount = unreadCount,
            onNotifications = onOpenNotifications,
            onAvatar = onExit,
        )

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val kpis = overview.kpis.filter { it.available }
            if (kpis.isNotEmpty()) {
                FocusCard(kpis = kpis, onClick = onOpenAnalytics)
            }

            QuickActions(
                onAnnouncement = onOpenNotifications,
                onEvent = onCreateEvent,
                onReports = onOpenReportPublish,
                onTransport = onOpenTransport,
            )

            val insights = overview.insights
            if (insights.isNotEmpty()) {
                NeedsAttentionCard(insights = insights)
            }

            overview.events.takeIf { it.available && it.upcoming.isNotEmpty() }?.let { ev ->
                UpcomingCard(events = ev.upcoming, onOpenCalendar = onOpenCalendar)
            }

            val activities = activity?.activities.orEmpty()
            if (activities.isNotEmpty()) {
                ActivityCard(activities = activities)
            }

            overview.schoolPulse.let { pulse ->
                if (pulse.score > 0) {
                    PulseCard(pulse = pulse, onClick = onOpenAnalytics)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero Header — violet band, like onboarding completion screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeroHeader(
    overview: AdminDashboardOverview,
    fallbackName: String,
    unreadCount: Int,
    onNotifications: () -> Unit,
    onAvatar: () -> Unit,
) {
    val header = overview.header
    val name = header.adminName.takeIf { it.isNotBlank() } ?: fallbackName
    val greeting = header.greeting.takeIf { it.isNotBlank() } ?: "Welcome"
    val schoolName = header.schoolName.takeIf { it.isNotBlank() } ?: "Your School"
    val session = buildString {
        header.academicYear.takeIf { it.isNotBlank() }?.let { append(it) }
        header.currentTerm.takeIf { it.isNotBlank() }?.let {
            if (isNotEmpty()) append(" · ")
            append(it)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.violet)
            .statusBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 28.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = greeting,
                        style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                        color = VColors.white.copy(alpha = 0.7f),
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = name,
                        style = VTypography.h2,
                        color = VColors.white,
                    )
                }

                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(VColors.white.copy(alpha = 0.15f))
                            .clickable { onNotifications() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = VColors.white,
                            modifier = Modifier.size(20.dp),
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(VColors.coral),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                    style = VTypography.caption.copy(
                                        fontSize = androidx.compose.ui.unit.TextUnit(9f, androidx.compose.ui.unit.TextUnitType.Sp),
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = VColors.white,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.size(12.dp))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(VColors.white.copy(alpha = 0.18f))
                        .border(1.dp, VColors.white.copy(alpha = 0.25f), CircleShape)
                        .clickable { onAvatar() },
                    contentAlignment = Alignment.Center,
                ) {
                    VAvatar(
                        name = name,
                        avatarSize = VAvatarSize.Md,
                        imageUrl = header.adminAvatarUrl,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = schoolName,
                style = VTypography.body,
                color = VColors.white.copy(alpha = 0.88f),
            )
            if (session.isNotBlank()) {
                Text(
                    text = session,
                    style = VTypography.caption,
                    color = VColors.white.copy(alpha = 0.6f),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Focus Card — 3 key metrics in a clean row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FocusCard(kpis: List<OverviewKpi>, onClick: () -> Unit) {
    CreamCard(onClick = onClick) {
        Text(
            text = "Today's Focus",
            style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink3,
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            kpis.take(3).forEach { kpi ->
                FocusMetric(
                    label = kpi.label,
                    value = formatKpiValue(kpi),
                    delta = kpi.deltaLabel.takeIf { it.isNotBlank() },
                    deltaDirection = kpi.deltaDirection,
                )
            }
            if (kpis.size < 3) {
                repeat(3 - kpis.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun RowScope.FocusMetric(
    label: String,
    value: String,
    delta: String?,
    deltaDirection: String,
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = value,
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = VTypography.caption,
            color = VColors.ink3,
            maxLines = 1,
        )
        if (delta != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = delta,
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                color = when (deltaDirection) {
                    "up" -> VColors.success
                    "down" -> VColors.coral
                    else -> VColors.ink3
                },
            )
        }
    }
}

private fun formatKpiValue(kpi: OverviewKpi): String {
    val v = kpi.value
    return when {
        kpi.unit == "%" -> "$v%"
        kpi.unit == "₹" || kpi.unit == "INR" -> "₹${if (v > 99999) "${v / 1000}k" else v}"
        v > 999 -> "${v / 1000}.${(v % 1000) / 100}k"
        else -> v.toString()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick Actions — 2×2 grid of clean action tiles
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActions(
    onAnnouncement: () -> Unit,
    onEvent: () -> Unit,
    onReports: () -> Unit,
    onTransport: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ActionTile(
            icon = Icons.Filled.Campaign,
            label = "Announce",
            onClick = onAnnouncement,
            modifier = Modifier.weight(1f),
        )
        ActionTile(
            icon = Icons.Filled.CalendarMonth,
            label = "New Event",
            onClick = onEvent,
            modifier = Modifier.weight(1f),
        )
    }
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ActionTile(
            icon = Icons.Filled.School,
            label = "Reports",
            onClick = onReports,
            modifier = Modifier.weight(1f),
        )
        ActionTile(
            icon = Icons.Filled.Person,
            label = "Transport",
            onClick = onTransport,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ActionTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(VShapes.sm)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = label,
            style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Needs Attention — insights/alerts ordered by severity
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NeedsAttentionCard(insights: List<OverviewInsight>) {
    val sorted = insights.sortedByDescending { severityWeight(it.severity) }
    CreamCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Needs Attention",
                style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = VColors.ink,
                modifier = Modifier.weight(1f),
            )
            MiniBadge(
                text = "${sorted.size}",
                color = VColors.violet,
                bg = VColors.violetSoft,
            )
        }
        Spacer(Modifier.height(12.dp))

        sorted.take(4).forEachIndexed { idx, insight ->
            if (idx > 0) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.lineSoft))
                Spacer(Modifier.height(8.dp))
            }
            InsightRow(insight = insight)
            if (idx < minOf(sorted.size, 4) - 1) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun InsightRow(insight: OverviewInsight) {
    val (icon, color, bg) = insightVisuals(insight.type, insight.severity)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(VShapes.sm)
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = insight.title,
                style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = VColors.ink,
            )
            if (insight.description.isNotBlank()) {
                Text(
                    text = insight.description,
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }
            if (insight.action.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = insight.action,
                    style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                    color = VColors.violet,
                )
            }
        }
    }
}

private fun severityWeight(severity: String): Int = when (severity.uppercase()) {
    "HIGH" -> 3
    "MEDIUM" -> 2
    else -> 1
}

private fun insightVisuals(type: String, severity: String): Triple<ImageVector, Color, Color> {
    return when (type.uppercase()) {
        "ALERT" -> Triple(
            Icons.Filled.Warning,
            if (severity == "HIGH") VColors.coral else VColors.gold,
            if (severity == "HIGH") VColors.coralSoft else VColors.goldSoft,
        )
        "ACHIEVEMENT" -> Triple(
            Icons.Filled.Check,
            VColors.success,
            VColors.successSoft,
        )
        "REMINDER" -> Triple(
            Icons.Filled.CalendarMonth,
            VColors.violet,
            VColors.violetSoft,
        )
        else -> Triple(
            Icons.Filled.Notifications,
            VColors.ink3,
            VColors.surfaceTint,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Upcoming — next 2-3 events, minimal
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UpcomingCard(
    events: List<com.littlebridge.enrollplus.feature.admin.domain.model.OverviewEvent>,
    onOpenCalendar: () -> Unit,
) {
    CreamCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Upcoming",
                style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = VColors.ink,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "View all",
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                color = VColors.violet,
                modifier = Modifier.clickable { onOpenCalendar() },
            )
        }
        Spacer(Modifier.height(12.dp))

        events.take(3).forEachIndexed { idx, event ->
            if (idx > 0) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.lineSoft))
                Spacer(Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = if (event.daysAway == 0) "Today" else "${event.daysAway}d",
                    style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                    color = if (event.isHoliday) VColors.gold else VColors.violet,
                    modifier = Modifier
                        .background(
                            if (event.isHoliday) VColors.goldSoft else VColors.violetSoft,
                            VShapes.full,
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        style = VTypography.bodySmall,
                        color = VColors.ink,
                        maxLines = 1,
                    )
                    Text(
                        text = event.date,
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                }
            }
            if (idx < minOf(events.size, 3) - 1) {
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Activity — last 3 activities, minimal timeline
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ActivityCard(activities: List<com.littlebridge.enrollplus.feature.admin.domain.model.DashboardActivity>) {
    CreamCard {
        Text(
            text = "Recent Activity",
            style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink,
        )
        Spacer(Modifier.height(12.dp))

        activities.take(3).forEachIndexed { idx, act ->
            if (idx > 0) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(VColors.lineSoft))
                Spacer(Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .padding(top = 6.dp)
                        .clip(CircleShape)
                        .background(VColors.violet.copy(alpha = 0.3f)),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = act.title,
                        style = VTypography.bodySmall,
                        color = VColors.ink,
                        maxLines = 1,
                    )
                    if (act.description.isNotBlank()) {
                        Text(
                            text = act.description,
                            style = VTypography.caption,
                            color = VColors.ink3,
                            maxLines = 2,
                        )
                    }
                    Text(
                        text = act.time,
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                }
            }
            if (idx < minOf(activities.size, 3) - 1) {
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Pulse Card — school health score, clean and simple
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PulseCard(pulse: OverviewSchoolPulse, onClick: () -> Unit) {
    val animatedScore by animateFloatAsState(
        targetValue = pulse.score / 100f,
        animationSpec = tween(600, easing = LinearEasing),
        label = "pulse-score",
    )

    val statusColor = when (pulse.status.uppercase()) {
        "EXCELLENT" -> VColors.success
        "HEALTHY" -> VColors.mint
        "WATCH" -> VColors.gold
        "CRITICAL" -> VColors.coral
        else -> VColors.ink3
    }

    CreamCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "School Pulse",
                    style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = pulse.message.takeIf { it.isNotBlank() } ?: pulse.status,
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }
            Text(
                text = "${pulse.score}",
                style = VTypography.h2.copy(fontSize = androidx.compose.ui.unit.TextUnit(32f, androidx.compose.ui.unit.TextUnitType.Sp)),
                color = statusColor,
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(VShapes.full)
                .background(VColors.lineSoft),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedScore)
                    .height(6.dp)
                    .clip(VShapes.full)
                    .background(statusColor),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared UI primitives — matching onboarding's CreamCard, MiniBadge
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CreamCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val base = modifier
        .fillMaxWidth()
        .background(VColors.surfaceCard, VShapes.lg)
        .border(1.dp, VColors.line, VShapes.lg)

    val clickable = if (onClick != null) base.clickable { onClick() } else base

    Column(
        modifier = clickable.padding(16.dp),
    ) { content() }
}

@Composable
private fun MiniBadge(text: String, color: Color, bg: Color) {
    Text(
        text = text,
        style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
        color = color,
        modifier = Modifier
            .background(bg, VShapes.full)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}
