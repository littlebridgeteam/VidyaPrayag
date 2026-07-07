package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
import com.littlebridge.enrollplus.ui.tokens.VMotion
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.locale.appString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

// ─────────────────────────────────────────────────────────────────────────────
// School Home — Command Desk
//
// EXACT design DNA from splash + landing + onboarding:
//   • VColors.cream background (splash, landing, onboarding)
//   • Enroll+ wordmark in top bar (landing pattern)
//   • Accent dot + accentLabel for role/school (landing pattern)
//   • VTypography.h2 for greeting (onboarding step title pattern)
//   • CreamCard: surfaceCard + 1dp border(line) + VShapes.lg + 16dp padding
//   • lineSoft dividers between items (onboarding pattern)
//   • MiniBadge: caption Bold + colored bg + VShapes.full (onboarding pattern)
//   • FilterChip: violet/surfaceTint + VShapes.full (onboarding pattern)
//   • VButton with ArrowForward for primary actions (all auth screens)
//   • Stagger entrance: 100/220/340ms with VMotion.durSlower (landing pattern)
//   • 24dp horizontal padding (onboarding pattern)
//   • 16dp spacedBy between cards (onboarding pattern)
//   • No violet header band (onboarding steps don't have one)
//   • No gradients, no gauges, no decorative icon-in-box patterns
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

            else -> CommandDesk(
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
// Command Desk — scrollable content with stagger entrance
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CommandDesk(
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
    // Stagger animation values — same pattern as landing screen
    val headerAlpha = remember { Animatable(0f) }
    val headerOffset = remember { Animatable(20f) }
    val cardsAlpha = remember { Animatable(0f) }
    val cardsOffset = remember { Animatable(20f) }

    LaunchedEffect(overview) {
        headerAlpha.snapTo(0f); headerOffset.snapTo(20f)
        cardsAlpha.snapTo(0f); cardsOffset.snapTo(20f)
        kotlinx.coroutines.coroutineScope {
            launch {
                delay(100)
                headerAlpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease))
                headerOffset.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease))
            }
            launch {
                delay(220)
                cardsAlpha.animateTo(1f, tween(VMotion.durSlower, easing = VMotion.ease))
                cardsOffset.animateTo(0f, tween(VMotion.durSlower, easing = VMotion.ease))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 100.dp),
    ) {
        // Header — wordmark + avatar row, then accent label + greeting
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp)
                .graphicsLayer(translationY = headerOffset.value)
                .alpha(headerAlpha.value),
        ) {
            DeskHeader(
                overview = overview,
                fallbackName = adminName,
                unreadCount = unreadCount,
                onNotifications = onOpenNotifications,
                onAvatar = onExit,
            )
        }

        // Cards section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .graphicsLayer(translationY = cardsOffset.value)
                .alpha(cardsAlpha.value),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val kpis = overview.kpis.filter { it.available }
            if (kpis.isNotEmpty()) {
                KpiGrid(kpis = kpis, onClick = onOpenAnalytics)
            }

            val insights = overview.insights
            if (insights.isNotEmpty()) {
                AttentionCard(insights = insights, onOpen = onOpenPews)
            }

            QuickActionsCard(
                onAnnouncement = onOpenNotifications,
                onEvent = onCreateEvent,
                onReports = onOpenReportPublish,
            )

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
// Desk Header — wordmark + avatar (landing top bar pattern)
//              accent dot + school name (landing accent label pattern)
//              greeting (onboarding step title pattern)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DeskHeader(
    overview: AdminDashboardOverview,
    fallbackName: String,
    unreadCount: Int,
    onNotifications: () -> Unit,
    onAvatar: () -> Unit,
) {
    val header = overview.header
    val name = header.adminName.takeIf { it.isNotBlank() } ?: fallbackName
    val schoolName = header.schoolName.takeIf { it.isNotBlank() } ?: "Your School"
    val greeting = header.greeting.takeIf { it.isNotBlank() } ?: "Welcome"

    // Top bar — wordmark + notification + avatar (landing pattern)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = buildAnnotatedString {
                append("Enroll")
                withStyle(SpanStyle(color = VColors.violet)) { append("+") }
            },
            style = VTypography.wordmark,
            color = VColors.ink,
            modifier = Modifier.weight(1f),
        )

        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VColors.violetSoft)
                    .clickable { onNotifications() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = VColors.violet,
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
                            fontSize = androidx.compose.ui.unit.TextUnit(9f, androidx.compose.ui.unit.TextUnitType.Sp),
                            fontWeight = FontWeight.Bold,
                        ),
                        color = VColors.white,
                    )
                }
            }
            Spacer(Modifier.size(10.dp))
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(VColors.violetSoft)
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

    Spacer(Modifier.height(20.dp))

    // Accent dot + school name (landing pattern)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(VColors.violet),
        )
        Text(
            text = schoolName,
            style = VTypography.accentLabel,
            color = VColors.violet,
        )
    }

    Spacer(Modifier.height(8.dp))

    // Greeting (onboarding step title pattern)
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, color = VColors.ink)) {
                append(greeting)
            }
            withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = VColors.ink2)) {
                append(", $name")
            }
        },
        style = VTypography.h2,
    )

    // Session info (onboarding "Step X of Y" pattern)
    val session = buildString {
        header.academicYear.takeIf { it.isNotBlank() }?.let { append(it) }
        header.currentTerm.takeIf { it.isNotBlank() }?.let {
            if (isNotEmpty()) append(" · ")
            append(it)
        }
    }
    if (session.isNotBlank()) {
        Spacer(Modifier.height(2.dp))
        Text(
            text = session,
            style = VTypography.caption,
            color = VColors.ink3,
        )
    }

    Spacer(Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(VColors.lineSoft),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared primitives — exact copies from onboarding
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
    val clickable = if (onClick != null) base.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
    ) { onClick() } else base
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
        modifier = Modifier.background(bg, VShapes.full).padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun CardDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(VColors.lineSoft),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// KPI Grid — 2-column metric cards inside CreamCard
// Big number (h3) + label (caption) + delta (caption bold, colored)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun KpiGrid(kpis: List<OverviewKpi>, onClick: () -> Unit) {
    CreamCard(onClick = onClick) {
        Text(
            text = "Today's Metrics",
            style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink3,
        )
        Spacer(Modifier.height(12.dp))
        val rows = kpis.chunked(2)
        rows.forEach { row ->
            if (row != rows.first()) Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                row.forEach { kpi ->
                    KpiMetric(kpi = kpi, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun RowScope.KpiMetric(kpi: OverviewKpi, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = formatKpiValue(kpi),
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = kpi.label,
            style = VTypography.caption,
            color = VColors.ink3,
            maxLines = 1,
        )
        if (kpi.deltaLabel.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = kpi.deltaLabel,
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                color = when (kpi.deltaDirection) {
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
        kpi.unit == "\u20B9" || kpi.unit == "INR" -> "\u20B9${if (v > 99999) "${v / 1000}k" else v}"
        v > 999 -> "${v / 1000}.${(v % 1000) / 100}k"
        else -> v.toString()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Attention Card — accent dot + count badge + insight list
// Uses lineSoft dividers between items (onboarding pattern)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AttentionCard(insights: List<OverviewInsight>, onOpen: () -> Unit) {
    val sorted = insights.sortedByDescending { severityWeight(it.severity) }
    val count = sorted.size

    CreamCard(onClick = onOpen) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(VColors.coral),
            )
            Spacer(Modifier.size(7.dp))
            Text(
                text = if (count == 1) "1 thing needs your attention" else "$count things need your attention",
                style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = VColors.ink,
                modifier = Modifier.weight(1f),
            )
            MiniBadge(text = "$count", color = VColors.coral, bg = VColors.coralSoft)
        }

        Spacer(Modifier.height(12.dp))

        sorted.take(3).forEachIndexed { idx, insight ->
            if (idx > 0) {
                CardDivider()
                Spacer(Modifier.height(10.dp))
            }
            val dotColor = when (insight.severity.uppercase()) {
                "HIGH" -> VColors.coral
                "MEDIUM" -> VColors.gold
                else -> VColors.violet
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                    modifier = Modifier.size(18.dp).padding(top = 2.dp),
                )
            }
            if (idx < minOf(sorted.size, 3) - 1) {
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

private fun severityWeight(severity: String): Int = when (severity.uppercase()) {
    "HIGH" -> 3
    "MEDIUM" -> 2
    else -> 1
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick Actions Card — tappable rows with lineSoft dividers
// Same pattern as onboarding's CreamCard with list items
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsCard(
    onAnnouncement: () -> Unit,
    onEvent: () -> Unit,
    onReports: () -> Unit,
) {
    CreamCard {
        Text(
            text = "Quick Actions",
            style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink3,
        )
        Spacer(Modifier.height(12.dp))
        ActionRow(label = "Send Announcement", onClick = onAnnouncement)
        CardDivider()
        Spacer(Modifier.height(4.dp))
        ActionRow(label = "Create Event", onClick = onEvent)
        CardDivider()
        Spacer(Modifier.height(4.dp))
        ActionRow(label = "Publish Reports", onClick = onReports)
    }
}

@Composable
private fun ActionRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(vertical = 10.dp),
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

// ─────────────────────────────────────────────────────────────────────────────
// Upcoming Card — date pills + event titles
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UpcomingCard(events: List<OverviewEvent>, onOpenCalendar: () -> Unit) {
    CreamCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Upcoming",
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                color = VColors.ink3,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "View all",
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                color = VColors.violet,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onOpenCalendar() },
            )
        }
        Spacer(Modifier.height(12.dp))

        events.take(3).forEachIndexed { idx, event ->
            if (idx > 0) {
                CardDivider()
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
// Activity Card — recent activity with lineSoft dividers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ActivityCard(activities: List<DashboardActivity>) {
    CreamCard {
        Text(
            text = "Recent Activity",
            style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink3,
        )
        Spacer(Modifier.height(12.dp))

        activities.take(3).forEachIndexed { idx, act ->
            if (idx > 0) {
                CardDivider()
                Spacer(Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Spacer(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(7.dp)
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
                        color = VColors.ink3.copy(alpha = 0.6f),
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
// Pulse Card — status sentence + score badge (no gauge)
// "Your school is healthy." + MiniBadge with score
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PulseCard(pulse: OverviewSchoolPulse, onClick: () -> Unit) {
    val statusText = when (pulse.status.uppercase()) {
        "EXCELLENT" -> "Your school is excelling."
        "HEALTHY" -> "Your school is healthy."
        "WATCH" -> "Your school needs attention."
        "CRITICAL" -> "Your school needs urgent attention."
        else -> pulse.message.takeIf { it.isNotBlank() } ?: "School status unavailable."
    }
    val (badgeColor, badgeBg) = when (pulse.status.uppercase()) {
        "EXCELLENT" -> VColors.success to VColors.successSoft
        "HEALTHY" -> VColors.mint to VColors.mintSoft
        "WATCH" -> VColors.gold to VColors.goldSoft
        "CRITICAL" -> VColors.coral to VColors.coralSoft
        else -> VColors.ink3 to VColors.surfaceTint
    }

    CreamCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = statusText,
                style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = VColors.ink,
                modifier = Modifier.weight(1f),
            )
            MiniBadge(text = "${pulse.score}", color = badgeColor, bg = badgeBg)
        }
    }
}
