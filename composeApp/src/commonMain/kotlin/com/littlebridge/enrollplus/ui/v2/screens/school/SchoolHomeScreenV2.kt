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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardAnalytics
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardOverview
import com.littlebridge.enrollplus.feature.admin.domain.model.DashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewEvent
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
// School Home — Morning Briefing
//
// Design philosophy:
//   • Text-first. Numbers and words carry the meaning, not widgets.
//   • No card soup. Sections separated by whitespace + background shifts.
//   • No decorative icon-in-box patterns. Icons only where they inform.
//   • One hero number. Rest is context.
//   • Action items first. A principal opens the app to act.
//   • Copywriting > gauges. "Your school is healthy" > a progress ring.
//   • Inspired by Linear, Stripe, Notion — not by AI dashboard templates.
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
                )
            }

            else -> MorningBriefing(
                overview = overview,
                activity = activity,
                adminName = adminName,
                unreadCount = notifications.unreadCount,
                onOpenNotifications = onOpenNotifications,
                onOpenCalendar = onOpenCalendar,
                onOpenAnalytics = onOpenAnalytics,
                onOpenPews = onOpenPews,
                onOpenTransport = onOpenTransport,
                onOpenReportPublish = onOpenReportPublish,
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
// Morning Briefing — the scrollable content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MorningBriefing(
    overview: AdminDashboardOverview,
    activity: AdminDashboardActivity?,
    adminName: String,
    unreadCount: Int,
    onOpenNotifications: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenPews: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenReportPublish: () -> Unit,
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
        BriefingHeader(
            overview = overview,
            fallbackName = adminName,
            unreadCount = unreadCount,
            onNotifications = onOpenNotifications,
            onAvatar = onExit,
        )

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            val kpis = overview.kpis.filter { it.available }
            if (kpis.isNotEmpty()) {
                HeroMetric(kpis = kpis, onClick = onOpenAnalytics)
            }

            val insights = overview.insights
            if (insights.isNotEmpty()) {
                ActionItems(insights = insights, onOpenPews = onOpenPews)
            }

            QuickActions(
                onAnnouncement = onOpenNotifications,
                onEvent = onCreateEvent,
                onReports = onOpenReportPublish,
            )

            overview.events.takeIf { it.available && it.upcoming.isNotEmpty() }?.let { ev ->
                UpcomingSection(events = ev.upcoming, onOpenCalendar = onOpenCalendar)
            }

            val activities = activity?.activities.orEmpty()
            if (activities.isNotEmpty()) {
                ActivitySection(activities = activities)
            }

            overview.schoolPulse.let { pulse ->
                if (pulse.score > 0) {
                    PulseSentence(pulse = pulse, onClick = onOpenAnalytics)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Briefing Header — compact violet band
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BriefingHeader(
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.violet)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = greeting,
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                    color = VColors.white.copy(alpha = 0.65f),
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
                        modifier = Modifier.size(18.dp),
                    )
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
                                fontSize = TextUnit(9f, TextUnitType.Sp),
                                fontWeight = FontWeight.Bold,
                            ),
                            color = VColors.white,
                        )
                    }
                }
                Spacer(Modifier.size(12.dp))
            }

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

        Text(
            text = schoolName,
            style = VTypography.body,
            color = VColors.white.copy(alpha = 0.85f),
            modifier = Modifier.padding(horizontal = 24.dp),
        )
        if (session.isNotBlank()) {
            Text(
                text = session,
                style = VTypography.caption,
                color = VColors.white.copy(alpha = 0.55f),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp),
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero Metric — one big number commands attention, rest is context
// No card border. Just text on cream background.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeroMetric(kpis: List<OverviewKpi>, onClick: () -> Unit) {
    val hero = kpis.firstOrNull() ?: return
    val heroValue = formatHeroValue(hero)
    val heroColor = when {
        hero.key.contains("attendance", ignoreCase = true) -> {
            when {
                hero.value >= 90 -> VColors.success
                hero.value >= 75 -> VColors.gold
                else -> VColors.coral
            }
        }
        hero.deltaDirection == "up" -> VColors.success
        hero.deltaDirection == "down" -> VColors.coral
        else -> VColors.ink
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = hero.label,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink3,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = heroValue,
            style = VTypography.h1.copy(fontSize = TextUnit(40f, TextUnitType.Sp)),
            color = heroColor,
        )
        if (hero.deltaLabel.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = hero.deltaLabel,
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                color = when (hero.deltaDirection) {
                    "up" -> VColors.success
                    "down" -> VColors.coral
                    else -> VColors.ink3
                },
            )
        }

        val secondary = kpis.drop(1).take(2)
        if (secondary.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                secondary.forEach { kpi ->
                    Column {
                        Text(
                            text = formatHeroValue(kpi),
                            style = VTypography.h3.copy(fontSize = TextUnit(20f, TextUnitType.Sp)),
                            color = VColors.ink,
                        )
                        Text(
                            text = kpi.label,
                            style = VTypography.caption,
                            color = VColors.ink3,
                        )
                    }
                }
            }
        }
    }
}

private fun formatHeroValue(kpi: OverviewKpi): String {
    val v = kpi.value
    return when {
        kpi.unit == "%" -> "$v%"
        kpi.unit == "₹" || kpi.unit == "INR" -> "₹${if (v > 99999) "${v / 1000}k" else v}"
        v > 999 -> "${v / 1000}.${(v % 1000) / 100}k"
        else -> v.toString()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Action Items — "3 things need your attention"
// Colored dot + text. No icon-in-box. Tappable rows.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ActionItems(insights: List<OverviewInsight>, onOpenPews: () -> Unit) {
    val sorted = insights.sortedByDescending { severityWeight(it.severity) }
    val count = sorted.size

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (count == 1) "1 thing needs your attention" else "$count things need your attention",
                style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = VColors.ink,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(12.dp))

        sorted.take(3).forEach { insight ->
            ActionRow(insight = insight, onClick = onOpenPews)
        }
    }
}

@Composable
private fun ActionRow(insight: OverviewInsight, onClick: () -> Unit) {
    val dotColor = when (insight.severity.uppercase()) {
        "HIGH" -> VColors.coral
        "MEDIUM" -> VColors.gold
        else -> VColors.violet
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Spacer(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = insight.title,
                style = VTypography.bodySmall,
                color = VColors.ink,
            )
            if (insight.description.isNotBlank()) {
                Text(
                    text = insight.description,
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }
        }
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = VColors.ink3.copy(alpha = 0.4f),
            modifier = Modifier.size(18.dp).padding(top = 4.dp),
        )
    }
}

private fun severityWeight(severity: String): Int = when (severity.uppercase()) {
    "HIGH" -> 3
    "MEDIUM" -> 2
    else -> 1
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick Actions — 3 wide tappable rows on creamDeep background
// No icon boxes. Just label + chevron. Whitespace as luxury.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActions(
    onAnnouncement: () -> Unit,
    onEvent: () -> Unit,
    onReports: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.creamDeep)
            .padding(vertical = 4.dp),
    ) {
        ActionTileRow(label = "Send Announcement", onClick = onAnnouncement)
        DividerLine()
        ActionTileRow(label = "Create Event", onClick = onEvent)
        DividerLine()
        ActionTileRow(label = "Publish Reports", onClick = onReports)
    }
}

@Composable
private fun ActionTileRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = VColors.ink3.copy(alpha = 0.4f),
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(VColors.lineSoft),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Upcoming — section label + text-first event list
// Date pill is the only visual element. No icons.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UpcomingSection(events: List<OverviewEvent>, onOpenCalendar: () -> Unit) {
    Column {
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

        events.take(3).forEach { event ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Activity — minimal timeline, text only
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ActivitySection(activities: List<DashboardActivity>) {
    Column {
        Text(
            text = "Recent Activity",
            style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink,
        )
        Spacer(Modifier.height(12.dp))

        activities.take(3).forEach { act ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "·",
                    style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink3.copy(alpha = 0.4f),
                    modifier = Modifier.padding(top = 1.dp),
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
                        color = VColors.ink3.copy(alpha = 0.6f),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Pulse Sentence — status in words, not gauges
// "Your school is healthy." with score as a small badge.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PulseSentence(pulse: OverviewSchoolPulse, onClick: () -> Unit) {
    val statusText = when (pulse.status.uppercase()) {
        "EXCELLENT" -> "Your school is excelling."
        "HEALTHY" -> "Your school is healthy."
        "WATCH" -> "Your school needs attention."
        "CRITICAL" -> "Your school needs urgent attention."
        else -> pulse.message.takeIf { it.isNotBlank() } ?: "School status unavailable."
    }
    val statusColor = when (pulse.status.uppercase()) {
        "EXCELLENT" -> VColors.success
        "HEALTHY" -> VColors.mint
        "WATCH" -> VColors.gold
        "CRITICAL" -> VColors.coral
        else -> VColors.ink3
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = statusText,
            style = VTypography.h3.copy(fontSize = TextUnit(20f, TextUnitType.Sp)),
            color = VColors.ink,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${pulse.score}",
            style = VTypography.bodySmall.copy(fontWeight = FontWeight.ExtraBold),
            color = statusColor,
            modifier = Modifier
                .background(statusColor.copy(alpha = 0.1f), VShapes.full)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
