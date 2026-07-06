package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Bookmark
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardAnalytics
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardOverview
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewAchievement
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewBirthday
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.min

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

    SchoolDashboardContent(
        modifier = modifier,
        adminName = adminName,
        unreadCount = notifications.unreadCount,
        loading = loading,
        error = error,
        overview = overview,
        analytics = analytics,
        activity = activity,
        calendarDashboard = calendarState.dashboard,
        onRetry = {
            viewModel.refresh()
            calendarViewModel.refresh()
        },
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

@Composable
private fun SchoolDashboardContent(
    modifier: Modifier = Modifier,
    adminName: String,
    unreadCount: Int,
    loading: Boolean,
    error: String?,
    overview: AdminDashboardOverview?,
    analytics: AdminDashboardAnalytics?,
    activity: AdminDashboardActivity?,
    calendarDashboard: com.littlebridge.enrollplus.feature.admin.domain.model.CalendarDashboardDto?,
    onRetry: () -> Unit,
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
    val isLoading = loading && overview == null
    val hasError = error != null && overview == null

    VPullRefresh(
        isRefreshing = loading && overview != null,
        onRefresh = onRetry,
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
                VErrorStateCream(message = error ?: "", onRetry = onRetry)
            }

            overview == null -> Column(
                Modifier.fillMaxSize().statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                VEmptyState(
                    title = "Nothing to show yet",
                    body = "Your dashboard will appear here once data is available.",
                    icon = Icons.Filled.TrendingUp,
                )
            }

            else -> DashboardScrollContent(
                adminName = adminName,
                unreadCount = unreadCount,
                overview = overview,
                analytics = analytics,
                activity = activity,
                calendarDashboard = calendarDashboard,
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
}

@Composable
private fun DashboardScrollContent(
    adminName: String,
    unreadCount: Int,
    overview: AdminDashboardOverview,
    analytics: AdminDashboardAnalytics?,
    activity: AdminDashboardActivity?,
    calendarDashboard: com.littlebridge.enrollplus.feature.admin.domain.model.CalendarDashboardDto?,
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
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        DashboardHeader(
            overview = overview,
            fallbackName = adminName,
            unreadCount = unreadCount,
            onNotifications = onOpenNotifications,
            onAvatar = onExit,
        )

        QuickActionsRow(
            onAnnouncement = onOpenNotifications,
            onEvent = onCreateEvent,
            onReports = onOpenAnalytics,
            onTransport = onOpenTransport,
        )

        overview.schoolPulse.let { pulse ->
            if (pulse.score > 0 || pulse.categories.isNotEmpty()) {
                SchoolPulseCard(pulse = pulse)
            }
        }

        val kpis = overview.kpis.filter { it.available }
        if (kpis.isNotEmpty()) {
            KpiGrid(kpis = kpis, onClick = onOpenAnalytics)
        }

        val insights = overview.insights
        if (insights.isNotEmpty()) {
            SmartInsightsCarousel(insights = insights)
        }

        overview.communication.let { comm ->
            CommunicationCard(
                unread = comm.unreadMessages,
                pending = comm.pendingQueries,
                announcements = comm.announcements,
                onOpenComms = onOpenNotifications,
            )
        }

        overview.events.takeIf { it.available && it.upcoming.isNotEmpty() }?.let { ev ->
            UpcomingEventsCard(
                upcoming = ev.upcoming,
                onOpenCalendar = onOpenCalendar,
            )
        }

        calendarDashboard?.let { cal ->
            if (cal.upcomingHighlights.isNotEmpty()) {
                CalendarHighlightsCard(
                    highlights = cal.upcomingHighlights,
                    onOpenCalendar = onOpenCalendar,
                )
            }
        }

        val activities = activity?.activities.orEmpty()
        if (activities.isNotEmpty()) {
            ActivityFeedCard(
                activities = activities.map {
                    ActivityItem(it.title, it.description, it.time)
                },
                onClick = onOpenNotifications,
            )
        }

        QuickLinksRow(
            onAnalytics = onOpenAnalytics,
            onPews = onOpenPews,
            onReportPublish = onOpenReportPublish,
            onReportEffectiveness = onOpenReportEffectiveness,
            onEvents = onOpenEvents,
        )

        overview.teacherSpotlight.takeIf { it.available }?.let { spot ->
            TeacherSpotlightCard(
                name = spot.name,
                department = spot.department,
                score = spot.score,
                highlight = spot.highlight,
                onClick = onOpenPews,
            )
        }

        overview.achievements.takeIf { it.available && it.items.isNotEmpty() }?.let { ach ->
            AchievementShowcase(items = ach.items)
        }

        overview.birthdays.takeIf { it.available }?.let { b ->
            BirthdayWidget(today = b.today, upcoming = b.upcoming)
        }
    }
}

@Composable
private fun DashboardHeader(
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$greeting, $name",
                style = VTypography.h2,
                color = VColors.ink,
            )
            Text(
                text = schoolName,
                style = VTypography.bodySmall,
                color = VColors.ink2,
            )
            if (session.isNotBlank()) {
                Text(
                    text = session,
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }
        }

        NotificationButton(count = unreadCount, onClick = onNotifications)

        Box(
            modifier = Modifier.clip(CircleShape).clickable { onAvatar() },
        ) {
            VAvatar(
                name = name,
                avatarSize = VAvatarSize.Lg,
                imageUrl = header.adminAvatarUrl,
            )
        }
    }
}

@Composable
private fun NotificationButton(count: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(VColors.surfaceTint)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = "Notifications",
            tint = VColors.ink,
            modifier = Modifier.size(20.dp),
        )
        if (count > 0) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(VColors.coral)
                    .align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    onAnnouncement: () -> Unit,
    onEvent: () -> Unit,
    onReports: () -> Unit,
    onTransport: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        QuickActionChip("Announce", Icons.Filled.Campaign, onAnnouncement)
        QuickActionChip("New Event", Icons.Filled.CalendarMonth, onEvent)
        QuickActionChip("Reports", Icons.Filled.TrendingUp, onReports)
        QuickActionChip("Transport", Icons.Filled.LocationOn, onTransport)
    }
}

@Composable
private fun QuickActionChip(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(VShapes.full)
            .background(VColors.surfaceCard)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Icon(icon, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(16.dp))
        Text(label, style = VTypography.caption, color = VColors.ink, maxLines = 1)
    }
}

@Composable
private fun SchoolPulseCard(pulse: OverviewSchoolPulse) {
    val animated by animateFloatAsState(
        targetValue = pulse.score / 100f,
        animationSpec = tween(900, easing = LinearEasing),
        label = "pulse",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.ink)
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "School Pulse",
                    style = VTypography.h3,
                    color = Color.White,
                )
                Text(
                    text = pulse.message,
                    style = VTypography.caption,
                    color = Color.White.copy(alpha = 0.8f),
                )
            }
            PulseGauge(score = pulse.score, animated = animated)
        }

        if (pulse.categories.any { it.available }) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pulse.categories.filter { it.available }.forEach { cat ->
                    PulseCategoryBar(cat)
                }
            }
        }
    }
}

@Composable
private fun PulseGauge(score: Int, animated: Float) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(72.dp)) {
            val strokeWidth = 6.dp.toPx()
            val arcSize = Size(size.minDimension - strokeWidth, size.minDimension - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            drawArc(
                color = Color.White.copy(alpha = 0.15f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            drawArc(
                color = VColors.mint,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score.toString(),
                style = VTypography.h2,
                color = Color.White,
            )
            Text(
                text = "/ 100",
                style = VTypography.caption,
                color = Color.White.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun PulseCategoryBar(cat: com.littlebridge.enrollplus.feature.admin.domain.model.OverviewPulseCategory) {
    val animated by animateFloatAsState(
        targetValue = cat.score / 100f,
        animationSpec = tween(700, easing = LinearEasing),
        label = "pulse-cat-${cat.key}",
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(cat.label, style = VTypography.caption, color = Color.White.copy(alpha = 0.8f))
            Text("${cat.score}%", style = VTypography.caption, color = Color.White)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(VShapes.full)
                .background(Color.White.copy(alpha = 0.12f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated.coerceIn(0f, 1f))
                    .height(4.dp)
                    .clip(VShapes.full)
                    .background(VColors.mint),
            )
        }
    }
}

@Composable
private fun KpiGrid(kpis: List<OverviewKpi>, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Key Metrics", style = VTypography.h3, color = VColors.ink)
        kpis.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { kpi ->
                    KpiCard(kpi = kpi, onClick = onClick, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun KpiCard(kpi: OverviewKpi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val deltaColor = when (kpi.deltaDirection) {
        "up" -> VColors.mint
        "down" -> VColors.coral
        else -> VColors.ink3
    }
    val deltaIcon = when (kpi.deltaDirection) {
        "up" -> "↑"
        "down" -> "↓"
        else -> "—"
    }

    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(kpi.label, style = VTypography.caption, color = VColors.ink3)
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = kpi.value.toString(),
                style = VTypography.h2,
                color = VColors.ink,
            )
            if (kpi.unit.isNotBlank()) {
                Text(
                    text = kpi.unit,
                    style = VTypography.caption,
                    color = VColors.ink3,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }
        if (kpi.deltaLabel.isNotBlank()) {
            Text(
                text = "$deltaIcon ${kpi.deltaLabel}",
                style = VTypography.caption,
                color = deltaColor,
            )
        }
    }
}

@Composable
private fun SmartInsightsCarousel(insights: List<OverviewInsight>) {
    val ordered = insights.sortedBy { severityRank(it.severity) }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Smart Insights", style = VTypography.h3, color = VColors.ink)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ordered.forEach { InsightCard(it) }
        }
    }
}

private fun severityRank(s: String): Int = when (s.uppercase()) {
    "HIGH" -> 0; "MEDIUM" -> 1; "LOW" -> 2; else -> 3
}

@Composable
private fun InsightCard(insight: OverviewInsight) {
    val accent = when (insight.type.uppercase()) {
        "ALERT" -> VColors.coral
        "REMINDER" -> VColors.gold
        "ACHIEVEMENT" -> VColors.mint
        else -> VColors.violet
    }
    val icon = when (insight.type.uppercase()) {
        "ALERT" -> Icons.Filled.Warning
        "REMINDER" -> Icons.Filled.Notifications
        "ACHIEVEMENT" -> Icons.Filled.Star
        else -> Icons.Filled.Campaign
    }

    Column(
        modifier = Modifier
            .widthIn(min = 230.dp, max = 280.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(VShapes.sm)
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
            }
            Text(insight.title, style = VTypography.bodySmall, color = VColors.ink, maxLines = 2)
        }
        Text(insight.description, style = VTypography.caption, color = VColors.ink2, maxLines = 3)
    }
}

@Composable
private fun CommunicationCard(
    unread: Int,
    pending: Int,
    announcements: Int,
    onOpenComms: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .clickable { onOpenComms() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Communication", style = VTypography.h3, color = VColors.ink)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CommStatItem(label = "Unread", value = unread, color = VColors.coral)
            CommStatItem(label = "Pending", value = pending, color = VColors.gold)
            CommStatItem(label = "Announcements", value = announcements, color = VColors.violet)
        }
    }
}

@Composable
private fun RowScope.CommStatItem(label: String, value: Int, color: Color) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value.toString(),
            style = VTypography.h3,
            color = color,
        )
        Text(label, style = VTypography.caption, color = VColors.ink3)
    }
}

@Composable
private fun UpcomingEventsCard(
    upcoming: List<OverviewEvent>,
    onOpenCalendar: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Upcoming Events", style = VTypography.h3, color = VColors.ink)
            Text(
                text = "View all",
                style = VTypography.caption,
                color = VColors.violet,
                modifier = Modifier.clickable { onOpenCalendar() },
            )
        }
        upcoming.take(3).forEach { event ->
            EventRow(event = event)
        }
    }
}

@Composable
private fun EventRow(event: OverviewEvent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(VShapes.sm)
                .background(
                    if (event.isHoliday) VColors.goldSoft else VColors.violetSoft
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (event.daysAway == 0) "Today" else "${event.daysAway}d",
                style = VTypography.caption,
                color = if (event.isHoliday) VColors.gold else VColors.violet,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, style = VTypography.bodySmall, color = VColors.ink, maxLines = 1)
            Text(event.date, style = VTypography.caption, color = VColors.ink3)
        }
    }
}

@Composable
private fun CalendarHighlightsCard(
    highlights: List<com.littlebridge.enrollplus.feature.admin.domain.model.AcademicCalendarEventDto>,
    onOpenCalendar: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Calendar Highlights", style = VTypography.h3, color = VColors.ink)
            Text(
                text = "View all",
                style = VTypography.caption,
                color = VColors.violet,
                modifier = Modifier.clickable { onOpenCalendar() },
            )
        }
        highlights.take(3).forEach { event ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(VShapes.sm)
                        .background(VColors.violetSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = VColors.violet,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(event.title, style = VTypography.bodySmall, color = VColors.ink, maxLines = 1)
                    Text(event.startDate, style = VTypography.caption, color = VColors.ink3)
                }
            }
        }
    }
}

data class ActivityItem(val title: String, val description: String, val time: String)

@Composable
private fun ActivityFeedCard(
    activities: List<ActivityItem>,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Recent Activity", style = VTypography.h3, color = VColors.ink)
        activities.take(4).forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(VColors.violet)
                        .align(Alignment.Top),
                    contentAlignment = Alignment.Center,
                ) {}
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(item.title, style = VTypography.bodySmall, color = VColors.ink, maxLines = 1)
                    Text(item.description, style = VTypography.caption, color = VColors.ink2, maxLines = 2)
                }
                Text(item.time, style = VTypography.caption, color = VColors.ink3)
            }
        }
    }
}

@Composable
private fun QuickLinksRow(
    onAnalytics: () -> Unit,
    onPews: () -> Unit,
    onReportPublish: () -> Unit,
    onReportEffectiveness: () -> Unit,
    onEvents: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickLinkCard(
            icon = Icons.Filled.TrendingUp,
            title = "Analytics Dashboard",
            description = "Attendance trends, growth & performance",
            onClick = onAnalytics,
        )
        QuickLinkCard(
            icon = Icons.Filled.Person,
            title = "Early Warning System",
            description = "At-risk students & intervention signals",
            onClick = onPews,
        )
        QuickLinkCard(
            icon = Icons.Filled.Bookmark,
            title = "Report Card Publishing",
            description = "Publish & track report card effectiveness",
            onClick = onReportPublish,
        )
        QuickLinkCard(
            icon = Icons.Filled.Star,
            title = "Report Effectiveness",
            description = "Track report card impact & engagement",
            onClick = onReportEffectiveness,
        )
        QuickLinkCard(
            icon = Icons.Filled.CalendarMonth,
            title = "Event Registration",
            description = "Manage event sign-ups & participation",
            onClick = onEvents,
        )
    }
}

@Composable
private fun QuickLinkCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(VShapes.sm)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = VTypography.bodySmall, color = VColors.ink)
            Text(description, style = VTypography.caption, color = VColors.ink3)
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = VColors.ink3,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun TeacherSpotlightCard(
    name: String,
    department: String,
    score: Int,
    highlight: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        VAvatar(name = name, avatarSize = VAvatarSize.Lg)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Teacher Spotlight", style = VTypography.caption, color = VColors.ink3)
            Text(name, style = VTypography.bodySmall, color = VColors.ink)
            Text(department, style = VTypography.caption, color = VColors.ink2)
            if (highlight.isNotBlank()) {
                Text(highlight, style = VTypography.caption, color = VColors.ink3, maxLines = 2)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(score.toString(), style = VTypography.h3, color = VColors.violet)
            Text("score", style = VTypography.caption, color = VColors.ink3)
        }
    }
}

@Composable
private fun AchievementShowcase(items: List<OverviewAchievement>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Achievements", style = VTypography.h3, color = VColors.ink)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items.forEach { AchievementCard(it) }
        }
    }
}

@Composable
private fun AchievementCard(achievement: OverviewAchievement) {
    val accent = when (achievement.category.uppercase()) {
        "SPORTS" -> VColors.mint
        "COMPETITION" -> VColors.sky
        else -> VColors.gold
    }
    Column(
        modifier = Modifier
            .widthIn(min = 200.dp, max = 260.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(VShapes.sm)
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
        }
        Text(achievement.studentName, style = VTypography.bodySmall, color = VColors.ink, maxLines = 1)
        Text(achievement.title, style = VTypography.caption, color = VColors.ink2, maxLines = 2)
    }
}

@Composable
private fun BirthdayWidget(
    today: List<OverviewBirthday>,
    upcoming: List<OverviewBirthday>,
) {
    if (today.isEmpty() && upcoming.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Celebrations", style = VTypography.h3, color = VColors.ink)
        if (today.isNotEmpty()) {
            today.take(3).forEach { BirthdayRow(it, isToday = true) }
        }
        if (upcoming.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            upcoming.take(3).forEach { BirthdayRow(it, isToday = false) }
        }
    }
}

@Composable
private fun BirthdayRow(birthday: OverviewBirthday, isToday: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        VAvatar(name = birthday.name, avatarSize = VAvatarSize.Sm)
        Text(
            text = birthday.name,
            style = VTypography.bodySmall,
            color = VColors.ink,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = if (isToday) "Today" else if (birthday.daysAway > 0) "in ${birthday.daysAway}d" else "",
            style = VTypography.caption,
            color = if (isToday) VColors.coral else VColors.ink3,
        )
    }
}

@Composable
private fun VErrorStateCream(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(VShapes.full)
                .background(VColors.coralSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = VColors.coral,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = message.ifBlank { "Something went wrong" },
            style = VTypography.body,
            color = VColors.ink2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .clip(VShapes.full)
                .background(VColors.violet)
                .clickable { onRetry() }
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Retry",
                style = VTypography.label,
                color = VColors.white,
            )
        }
    }
}
