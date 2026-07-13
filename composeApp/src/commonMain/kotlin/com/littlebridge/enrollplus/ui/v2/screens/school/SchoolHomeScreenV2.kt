package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardOverview
import com.littlebridge.enrollplus.feature.admin.domain.model.DashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewAchievement
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewBirthday
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewEvent
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewFeeAnalytics
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewInsight
import com.littlebridge.enrollplus.feature.admin.domain.model.DailyDigest
import com.littlebridge.enrollplus.feature.admin.domain.model.DigestTask
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewKpi
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewParentEngagement
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewSchoolPulse
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewTeacherSpotlight
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarPlatformViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.PinnedScreensViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.platform.rememberNotificationPermissionLauncher
import com.littlebridge.enrollplus.presentation.PermissionViewModel
import com.littlebridge.enrollplus.ui.v2.components.PinButton
import com.littlebridge.enrollplus.ui.v2.components.ShimmerBox
import com.littlebridge.enrollplus.ui.v2.components.VBackOnlineBanner
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VGlassCard
import com.littlebridge.enrollplus.ui.v2.components.VOfflineBanner
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonDashboard
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import com.littlebridge.enrollplus.ui.v2.locale.appString
import com.littlebridge.enrollplus.util.MONTH_SHORT
import com.littlebridge.enrollplus.util.dayOfWeek
import com.littlebridge.enrollplus.util.nowMinutesOfDay
import com.littlebridge.enrollplus.util.parseIsoDate
import com.littlebridge.enrollplus.util.todayIso
import org.koin.compose.viewmodel.koinViewModel

private val EmptyDigest = DailyDigest(
    headline = "",
    focus = "",
    tasks = emptyList(),
)

private val ROUTE_ICON_MAP = mapOf(
    "overlay_notifications" to VIcons.Bell,
    "overlay_messages" to VIcons.Chat,
    "overlay_link_requests" to VIcons.UsersGroup,
    "overlay_leave_requests" to VIcons.Calendar,
    "overlay_daily_attendance" to VIcons.Check,
    "overlay_calendar" to VIcons.Calendar,
    "settings_fees" to VIcons.Wallet,
)

private val SHORTCUT_LABELS = mapOf(
    "tab_people" to "People",
    "tab_records" to "Records",
    "tab_comms" to "Communications",
    "tab_settings" to "Settings",
    "overlay_notifications" to "Notifications",
    "overlay_messages" to "Messages",
    "overlay_link_requests" to "Link Requests",
    "overlay_leave_requests" to "Leave Requests",
    "overlay_daily_attendance" to "Attendance",
    "overlay_calendar" to "Calendar",
    "overlay_events" to "Events",
    "overlay_analytics" to "Analytics",
    "overlay_fees" to "Fees",
    "overlay_branding" to "Branding",
    "overlay_profile" to "Profile",
)

// ─────────────────────────────────────────────────────────────────────────────
// School Home — Command Desk v3 (complete rewrite)
//
// New concept:
//   1. Clean header — no card wrapper, just text on cream background
//   2. Quick shortcut chips — horizontal scroll, individually colored
//   3. Individual KPI mini-cards — each KPI gets its own card with icon
//   4. Attention/insights — compact list inside a single card
//   5. Fee + Engagement — compact cards with circular % badge
//   6. Teacher spotlight + Upcoming events — compact rows
//   7. Achievements + Birthdays — tinted cards
//   8. Activity feed — timeline dots
//   9. School pulse — status sentence + score
//
// 4-stage system: Loading → Content → Empty → Error
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
    onOpenPinnedScreen: (String) -> Unit = {},
    onExit: () -> Unit = {},
    viewModel: SchoolDashboardViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
    calendarViewModel: AcademicCalendarPlatformViewModel = koinViewModel(),
    permissionVm: PermissionViewModel = koinViewModel(),
    pinnedVm: PinnedScreensViewModel = koinViewModel(),
) {
    val dashboardState by viewModel.state.collectAsState()
    val notifications by notificationsViewModel.state.collectAsState()
    val pinnedScreens by pinnedVm.screens.collectAsState()
    var commandPaletteVisible by remember { mutableStateOf(false) }

    val adminName = dashboardState.adminName
    val loading = dashboardState.isLoading
    val error = dashboardState.errorMessage
    val overview = dashboardState.overview
    val activity = dashboardState.activity
    val digest = dashboardState.digest ?: EmptyDigest
    val isDigestLoading = dashboardState.isDigestLoading

    LaunchedEffect(dashboardState.pinnedScreens) {
        pinnedVm.setInitial(dashboardState.pinnedScreens)
    }

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

    val stage = when {
        loading && overview == null -> Stage.Loading
        error != null && overview == null -> Stage.Error
        overview == null -> Stage.Empty
        else -> Stage.Content
    }

    VPullRefresh(
        isRefreshing = dashboardState.isRefreshing,
        onRefresh = { viewModel.refresh(); calendarViewModel.refresh() },
        modifier = modifier.fillMaxSize().background(brush = homeBackgroundGradient()),
    ) {
        Box(Modifier.fillMaxSize()) {
            // Track offline→online transition for the "Back online" confirmation.
            var wasOffline by remember { mutableStateOf(dashboardState.isOffline) }
            var showBackOnline by remember { mutableStateOf(false) }
            LaunchedEffect(dashboardState.isOffline) {
                if (wasOffline && !dashboardState.isOffline) {
                    showBackOnline = true
                }
                wasOffline = dashboardState.isOffline
            }
            LaunchedEffect(showBackOnline) {
                if (showBackOnline) {
                    kotlinx.coroutines.delay(2500L)
                    showBackOnline = false
                }
            }

            VStateHost(
                loading = loading && overview == null,
                error = if (error != null && overview == null) error else null,
                isEmpty = overview == null && !loading && error == null,
                emptyTitle = "Nothing to show yet",
                emptyBody = "Your dashboard will appear here once data is available.",
                onRetry = { viewModel.refresh(); calendarViewModel.refresh() },
                skeleton = { SkeletonDashboard() },
                modifier = Modifier.fillMaxSize(),
            ) {
                val ov = overview!!
                CommandDesk(
                    overview = ov,
                    activity = activity,
                    adminName = adminName,
                    digest = digest,
                    isDigestLoading = isDigestLoading,
                    pinnedScreens = pinnedScreens,
                    unreadCount = notifications.unreadCount,
                    onOpenNotifications = onOpenNotifications,
                    onOpenCalendar = onOpenCalendar,
                    onOpenAnalytics = onOpenAnalytics,
                    onOpenPews = onOpenPews,
                    onOpenTransport = onOpenTransport,
                    onOpenReportPublish = onOpenReportPublish,
                    onOpenEvents = onOpenEvents,
                    onCreateEvent = onCreateEvent,
                    onOpenPinnedScreen = onOpenPinnedScreen,
                    onOpenCommandPalette = { commandPaletteVisible = true },
                    onUnpin = pinnedVm::unpin,
                    onExit = onExit,
                )
            }

            // Offline indicator overlay — animated slide-in/out so it never jumps.
            AnimatedVisibility(
                visible = dashboardState.isOffline,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp,
                        ),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    VOfflineBanner(isOffline = true)
                }
            }

            // "Back online" transient confirmation.
            AnimatedVisibility(
                visible = showBackOnline,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp,
                        ),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    VBackOnlineBanner()
                }
            }
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
        icon = VIcons.Bell,
    )

    HomeCommandPalette(
        visible = commandPaletteVisible,
        onDismiss = { commandPaletteVisible = false },
        onSelect = onOpenPinnedScreen,
    )
}

private enum class Stage { Loading, Content, Empty, Error }

// ─────────────────────────────────────────────────────────────────────────────
// Command Desk — main scrollable content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CommandDesk(
    overview: AdminDashboardOverview,
    activity: AdminDashboardActivity?,
    adminName: String,
    digest: DailyDigest,
    isDigestLoading: Boolean,
    pinnedScreens: List<String>,
    unreadCount: Int,
    onOpenNotifications: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenPews: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenReportPublish: () -> Unit,
    onOpenEvents: () -> Unit,
    onCreateEvent: () -> Unit,
    onOpenPinnedScreen: (String) -> Unit,
    onOpenCommandPalette: () -> Unit,
    onUnpin: (String) -> Unit,
    onExit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 120.dp),
    ) {
        HomeHero(
            overview = overview,
            fallbackName = adminName,
            digest = digest,
            isDigestLoading = isDigestLoading,
            unreadCount = unreadCount,
            onNotifications = onOpenNotifications,
            onOpenCommandPalette = onOpenCommandPalette,
            onDigestTask = { routeId ->
                routeId?.let(onOpenPinnedScreen)
            },
        )

        PinnedShortcutsRow(
            pinnedScreens = pinnedScreens,
            onOpen = onOpenPinnedScreen,
            onUnpin = onUnpin,
        )

        QuickShortcuts(
            onAnnouncement = onOpenNotifications,
            onEvent = onCreateEvent,
            onReports = onOpenReportPublish,
            onCalendar = onOpenCalendar,
            onTransport = onOpenTransport,
        )

        val kpis = overview.kpis.filter { it.available }
        if (kpis.isNotEmpty()) {
            KpiMiniCardGrid(kpis = kpis, onClick = onOpenAnalytics)
        }

        val insights = overview.insights
        if (insights.isNotEmpty()) {
            AttentionCard(insights = insights, onOpen = onOpenPews)
        }

        overview.feeAnalytics.takeIf { it.available }?.let { fa ->
            FeeAnalyticsCard(fa = fa, onClick = onOpenAnalytics)
        }

        overview.parentEngagement.takeIf { it.available }?.let { pe ->
            ParentEngagementCard(pe = pe, onClick = onOpenAnalytics)
        }

        overview.teacherSpotlight.takeIf { it.available }?.let { ts ->
            TeacherSpotlightCard(ts = ts, onClick = onOpenPews)
        }

        overview.events.takeIf { it.available && it.upcoming.isNotEmpty() }?.let { ev ->
            UpcomingCard(events = ev.upcoming, onOpenCalendar = onOpenCalendar)
        }

        overview.achievements.takeIf { it.available && it.items.isNotEmpty() }?.let { ach ->
            AchievementsCard(achievements = ach.items)
        }

        overview.birthdays.takeIf { it.available }?.let { bd ->
            if (bd.today.isNotEmpty() || bd.upcoming.isNotEmpty()) {
                BirthdaysCard(birthdays = bd.today + bd.upcoming)
            }
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

// ─────────────────────────────────────────────────────────────────────────────
// 1. Home Hero — greeting, command search chip, and daily digest card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHero(
    overview: AdminDashboardOverview,
    fallbackName: String,
    digest: DailyDigest,
    isDigestLoading: Boolean,
    unreadCount: Int,
    onNotifications: () -> Unit,
    onOpenCommandPalette: () -> Unit,
    onDigestTask: (String?) -> Unit,
) {
    val header = overview.header
    val name = header.adminName.takeIf { it.isNotBlank() } ?: fallbackName
    val schoolName = header.schoolName.takeIf { it.isNotBlank() } ?: "Your School"

    val hour = nowMinutesOfDay() / 60
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Welcome back"
    }

    val todayIso = todayIso()
    val (ty, tm, td) = parseIsoDate(todayIso) ?: Triple(0, 0, 0)
    val dowNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val dow = if (ty > 0) dowNames[dayOfWeek(ty, tm, td)] else ""
    val monName = MONTH_SHORT.getOrNull(tm - 1) ?: ""
    val todayStr = if (ty > 0) "$dow, $td $monName $ty" else ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$greeting, $name",
                    style = VTheme.type.h2,
                    color = VTheme.colors.ink,
                    fontSize = 22.sp,
                )
                Text(
                    text = todayStr,
                    style = VTheme.type.caption,
                    color = VTheme.colors.ink3,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(VTheme.colors.card)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNotifications,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = VIcons.Bell,
                    contentDescription = "Notifications",
                    tint = VTheme.colors.ink,
                    modifier = Modifier.size(22.dp),
                )
                if (unreadCount > 0) {
                    VBadge(
                        text = unreadCount.coerceAtMost(99).toString(),
                        tone = VBadgeTone.Danger,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                    )
                }
            }
        }

        Text(
            text = schoolName,
            style = VTheme.type.body,
            color = VTheme.colors.ink3,
            modifier = Modifier.padding(top = 4.dp),
        )

        SearchChip(
            onClick = onOpenCommandPalette,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 6.dp),
        )
    }

    DailyDigestCard(
        digest = digest,
        isLoading = isDigestLoading,
        onTaskClick = onDigestTask,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
    )
}

@Composable
private fun SearchChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(VTheme.colors.card.copy(alpha = 0.7f))
            .border(
                width = 0.5.dp,
                color = VTheme.colors.border1.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = VIcons.Search,
            contentDescription = null,
            tint = VTheme.colors.ink3,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = "Jump to screen...",
            style = VTheme.type.body,
            color = VTheme.colors.ink3,
            modifier = Modifier.padding(start = 10.dp).weight(1f),
        )
        Icon(
            imageVector = VIcons.ArrowRight,
            contentDescription = null,
            tint = VTheme.colors.ink3,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun DailyDigestCard(
    digest: DailyDigest,
    isLoading: Boolean,
    onTaskClick: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    VGlassCard(
        modifier = modifier,
        backgroundBrush = heroGradient(),
        padding = 18.dp,
    ) {
        if (isLoading && digest.tasks.isEmpty()) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(12.dp))
        } else {
            Column {
                Text(
                    text = digest.headline.ifBlank { "Good day, Admin" },
                    style = VTheme.type.h3,
                    color = VTheme.colors.ink,
                )
                if (digest.focus.isNotBlank()) {
                    Text(
                        text = digest.focus,
                        style = VTheme.type.body,
                        color = VTheme.colors.ink3,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                if (digest.tasks.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    digest.tasks.forEach { task ->
                        DigestTaskRow(task = task, onClick = { onTaskClick(task.routeId) })
                        if (task != digest.tasks.last()) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DigestTaskRow(
    task: DigestTask,
    onClick: () -> Unit,
) {
    val badgeTone = when (task.priority.lowercase()) {
        "urgent" -> VBadgeTone.Danger
        "success" -> VBadgeTone.Success
        else -> VBadgeTone.Arctic
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(VTheme.colors.accentTint),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ROUTE_ICON_MAP[task.routeId] ?: VIcons.Star,
                contentDescription = null,
                tint = VTheme.colors.accent,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.label,
                style = VTheme.type.body,
                color = VTheme.colors.ink,
                fontWeight = FontWeight.SemiBold,
            )
        }
        VBadge(
            text = task.actionLabel,
            tone = badgeTone,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. Pinned shortcuts — user-curated horizontal row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PinnedShortcutsRow(
    pinnedScreens: List<String>,
    onOpen: (String) -> Unit,
    onUnpin: (String) -> Unit,
) {
    if (pinnedScreens.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text = "Pinned",
            style = VTheme.type.label,
            color = VTheme.colors.ink3,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            pinnedScreens.forEach { routeId ->
                PinnedShortcutChip(
                    routeId = routeId,
                    onClick = { onOpen(routeId) },
                    onUnpin = { onUnpin(routeId) },
                )
            }
        }
    }
}

@Composable
private fun PinnedShortcutChip(
    routeId: String,
    onClick: () -> Unit,
    onUnpin: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(VTheme.colors.card.copy(alpha = 0.85f))
            .border(
                width = 0.5.dp,
                color = VTheme.colors.border1.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = ROUTE_ICON_MAP[routeId] ?: VIcons.Star,
            contentDescription = null,
            tint = VTheme.colors.accent,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = SHORTCUT_LABELS[routeId] ?: routeId,
            style = VTheme.type.body,
            color = VTheme.colors.ink,
        )
        PinButton(
            pinned = true,
            onClick = onUnpin,
            modifier = Modifier.size(24.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. Quick Shortcuts — horizontal scrollable chips
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickShortcuts(
    onAnnouncement: () -> Unit,
    onEvent: () -> Unit,
    onReports: () -> Unit,
    onCalendar: () -> Unit,
    onTransport: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ShortcutChip("Announce", VIcons.Megaphone, VColors.violet, VColors.violetSoft, onAnnouncement)
        ShortcutChip("Add Event", VIcons.Calendar, VColors.sky, VColors.skySoft, onEvent)
        ShortcutChip("Reports", VIcons.FileText, VColors.gold, VColors.goldSoft, onReports)
        ShortcutChip("Calendar", VIcons.Calendar, VColors.mint, VColors.mintSoft, onCalendar)
        ShortcutChip("Transport", VIcons.MapPin, VColors.coral, VColors.coralSoft, onTransport)
    }
}

@Composable
private fun ShortcutChip(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(VShapes.sm)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
        }
        Text(
            text = label,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
            maxLines = 1,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. KPI Mini-Cards — each KPI gets its own individual card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun KpiMiniCardGrid(kpis: List<OverviewKpi>, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val rows = kpis.chunked(2)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { kpi ->
                    KpiMiniCard(kpi = kpi, onClick = onClick, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun KpiMiniCard(
    kpi: OverviewKpi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accentColor = when (kpi.deltaDirection) {
        "up" -> VColors.success
        "down" -> VColors.coral
        else -> VColors.violet
    }
    val icon = when (kpi.key) {
        "students" -> VIcons.UsersGroup
        "teachers" -> VIcons.GraduationCap
        "attendance" -> VIcons.ListChecks
        "fees" -> VIcons.Wallet
        "parents" -> VIcons.Heart
        "approvals" -> VIcons.ShieldCheck
        "events" -> VIcons.Calendar
        else -> VIcons.Target
    }

    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(VShapes.sm)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }
            if (kpi.deltaLabel.isNotBlank()) {
                Text(
                    text = kpi.deltaLabel,
                    style = VTypography.caption.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = accentColor,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = formatKpiValue(kpi),
            style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold),
            color = VColors.ink,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = kpi.label,
            style = VTypography.caption,
            color = VColors.ink3,
            maxLines = 1,
        )
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
// Shared card primitive
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PremiumCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    tint: Color = VColors.surfaceCard,
    content: @Composable () -> Unit,
) {
    val base = modifier
        .fillMaxWidth()
        .background(tint, VShapes.lg)
        .border(1.dp, VColors.line, VShapes.lg)
    val clickable = if (onClick != null) base.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
    ) { onClick() } else base
    Column(modifier = clickable.padding(16.dp)) { content() }
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
        modifier = Modifier.fillMaxWidth().height(1.dp).background(VColors.lineSoft),
    )
}

@Composable
private fun CardHeader(
    label: String,
    icon: ImageVector,
    iconTint: Color = VColors.violet,
    iconBg: Color = VColors.violetSoft,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(VShapes.sm)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(15.dp))
        }
        Text(
            text = label,
            style = VTypography.label.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. Attention Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AttentionCard(insights: List<OverviewInsight>, onOpen: () -> Unit) {
    val sorted = insights.sortedByDescending { severityWeight(it.severity) }
    val count = sorted.size
    val hasHigh = sorted.any { it.severity.uppercase() == "HIGH" }

    PremiumCard(
        onClick = onOpen,
        tint = if (hasHigh) VColors.coralSoft else VColors.surfaceCard,
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CardHeader(
                if (count == 1) "1 item needs attention" else "$count items need attention",
                VIcons.AlertCircle,
                iconTint = if (hasHigh) VColors.coral else VColors.gold,
                iconBg = if (hasHigh) VColors.coralSoft else VColors.goldSoft,
            )
            Spacer(Modifier.weight(1f))
            MiniBadge(text = "$count", color = VColors.coral, bg = VColors.white)
        }

        Spacer(Modifier.height(14.dp))

        sorted.take(4).forEachIndexed { idx, insight ->
            if (idx > 0) { CardDivider(); Spacer(Modifier.height(10.dp)) }
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
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
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
            if (idx < minOf(sorted.size, 4) - 1) { Spacer(Modifier.height(10.dp)) }
        }
    }
}

private fun severityWeight(severity: String): Int = when (severity.uppercase()) {
    "HIGH" -> 3; "MEDIUM" -> 2; else -> 1
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. Fee Analytics Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FeeAnalyticsCard(fa: OverviewFeeAnalytics, onClick: () -> Unit) {
    val rateColor = when {
        fa.collectionRate >= 90 -> VColors.success
        fa.collectionRate >= 70 -> VColors.violet
        else -> VColors.gold
    }
    PremiumCard(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        CardHeader("Fee Collection", VIcons.Wallet, iconTint = VColors.violet)
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(rateColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${fa.collectionRate}%",
                    style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
                    color = rateColor,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "\u20B9${formatAmount(fa.totalCollected)}",
                    style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "\u20B9${formatAmount(fa.pending)} pending",
                    style = VTypography.caption,
                    color = VColors.coral,
                )
            }
        }
    }
}

private fun formatAmount(v: Double): String = when {
    v >= 10000000 -> "${(v / 10000000).let { kotlin.math.round(it * 10) / 10 }}Cr"
    v >= 100000 -> "${(v / 100000).let { kotlin.math.round(it * 10) / 10 }}L"
    v >= 1000 -> "${(v / 1000).toInt()}k"
    else -> v.toInt().toString()
}

// ─────────────────────────────────────────────────────────────────────────────
// Parent Engagement Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ParentEngagementCard(pe: OverviewParentEngagement, onClick: () -> Unit) {
    val engagementColor = if (pe.activeParentsPct >= 70) VColors.success else VColors.gold
    PremiumCard(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        CardHeader("Parent Engagement", VIcons.UsersGroup, iconTint = VColors.sky, iconBg = VColors.skySoft)
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(engagementColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${pe.activeParentsPct}%",
                    style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
                    color = engagementColor,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${pe.activeParents} of ${pe.totalParents} parents active",
                    style = VTypography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = VColors.ink,
                )
                if (pe.mostEngagedClass.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Top: ${pe.mostEngagedClass}",
                        style = VTypography.caption,
                        color = VColors.violet,
                    )
                }
            }
        }
        if (pe.leaderboard.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            CardDivider()
            Spacer(Modifier.height(10.dp))
            pe.leaderboard.take(3).forEachIndexed { idx, lc ->
                if (idx > 0) Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = lc.className,
                        style = VTypography.caption,
                        color = VColors.ink2,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${lc.score}%",
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                        color = VColors.violet,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. Teacher Spotlight Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TeacherSpotlightCard(ts: OverviewTeacherSpotlight, onClick: () -> Unit) {
    PremiumCard(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        CardHeader("Teacher Spotlight", VIcons.GraduationCap)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                val initials = ts.name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
                Text(initials, style = VTypography.label.copy(fontWeight = FontWeight.ExtraBold), color = VColors.violet)
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ts.name,
                    style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                )
                if (ts.department.isNotBlank()) {
                    Text(ts.department, style = VTypography.caption, color = VColors.ink3)
                }
            }
            MiniBadge(text = "${ts.score}", color = VColors.violet, bg = VColors.violetSoft)
        }
        if (ts.highlight.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = ts.highlight,
                style = VTypography.caption,
                color = VColors.ink2,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. Upcoming Events Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UpcomingCard(events: List<OverviewEvent>, onOpenCalendar: () -> Unit) {
    PremiumCard(
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CardHeader("Upcoming", VIcons.Calendar)
            Spacer(Modifier.weight(1f))
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
        events.take(4).forEachIndexed { idx, event ->
            if (idx > 0) { CardDivider(); Spacer(Modifier.height(10.dp)) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = if (event.daysAway == 0) "Today" else if (event.daysAway == 1) "1d" else "${event.daysAway}d",
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
                    Text(event.title, style = VTypography.bodySmall, color = VColors.ink, maxLines = 1)
                    Text(event.date, style = VTypography.caption, color = VColors.ink3)
                }
            }
            if (idx < minOf(events.size, 4) - 1) { Spacer(Modifier.height(10.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 8. Achievements Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AchievementsCard(achievements: List<OverviewAchievement>) {
    PremiumCard(
        tint = VColors.goldSoft,
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        CardHeader("Achievements", VIcons.Star, iconTint = VColors.gold, iconBg = VColors.goldSoft)
        Spacer(Modifier.height(12.dp))
        achievements.take(3).forEachIndexed { idx, ach ->
            if (idx > 0) { CardDivider(); Spacer(Modifier.height(10.dp)) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Spacer(
                    modifier = Modifier.padding(top = 6.dp).size(7.dp).clip(CircleShape).background(VColors.gold),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ach.studentName,
                        style = VTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                    )
                    Text(ach.title, style = VTypography.caption, color = VColors.ink2)
                    if (ach.detail.isNotBlank()) {
                        Text(ach.detail, style = VTypography.caption, color = VColors.ink3)
                    }
                }
                val (badgeColor, badgeBg) = when (ach.category.uppercase()) {
                    "SPORTS" -> VColors.sky to VColors.skySoft
                    "COMPETITION" -> VColors.coral to VColors.coralSoft
                    else -> VColors.violet to VColors.violetSoft
                }
                MiniBadge(text = ach.category.take(3).uppercase(), color = badgeColor, bg = badgeBg)
            }
            if (idx < minOf(achievements.size, 3) - 1) { Spacer(Modifier.height(10.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 9. Birthdays Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BirthdaysCard(birthdays: List<OverviewBirthday>) {
    PremiumCard(
        tint = VColors.coralSoft,
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        CardHeader("Birthdays", VIcons.Heart, iconTint = VColors.coral, iconBg = VColors.coralSoft)
        Spacer(Modifier.height(12.dp))
        birthdays.take(4).forEachIndexed { idx, bd ->
            if (idx > 0) { CardDivider(); Spacer(Modifier.height(10.dp)) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(VColors.goldSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    val initials = bd.name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
                    Text(initials, style = VTypography.caption.copy(fontWeight = FontWeight.Bold), color = VColors.gold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(bd.name, style = VTypography.bodySmall, color = VColors.ink)
                    Text(
                        text = if (bd.isToday) "Today \uD83C\uDF89" else bd.date,
                        style = VTypography.caption,
                        color = VColors.ink3,
                    )
                }
                MiniBadge(
                    text = bd.role.take(3).uppercase(),
                    color = VColors.ink3,
                    bg = VColors.surfaceTint,
                )
            }
            if (idx < minOf(birthdays.size, 4) - 1) { Spacer(Modifier.height(10.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 10. Activity Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ActivityCard(activities: List<DashboardActivity>) {
    PremiumCard(
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        CardHeader("Recent Activity", VIcons.History, iconTint = VColors.ink3, iconBg = VColors.creamDeep)
        Spacer(Modifier.height(12.dp))
        activities.take(4).forEachIndexed { idx, act ->
            if (idx > 0) { CardDivider(); Spacer(Modifier.height(10.dp)) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Spacer(
                    modifier = Modifier.padding(top = 6.dp).size(7.dp).clip(CircleShape)
                        .background(VColors.violet.copy(alpha = 0.3f)),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(act.title, style = VTypography.bodySmall, color = VColors.ink, maxLines = 1)
                    if (act.description.isNotBlank()) {
                        Text(act.description, style = VTypography.caption, color = VColors.ink3, maxLines = 2)
                    }
                    Text(act.time, style = VTypography.caption, color = VColors.ink3.copy(alpha = 0.6f))
                }
            }
            if (idx < minOf(activities.size, 4) - 1) { Spacer(Modifier.height(10.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 11. Pulse Card
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
    val badgeColor = when (pulse.status.uppercase()) {
        "EXCELLENT" -> VColors.success
        "HEALTHY" -> VColors.mint
        "WATCH" -> VColors.gold
        "CRITICAL" -> VColors.coral
        else -> VColors.ink3
    }
    val cardTint = when (pulse.status.uppercase()) {
        "EXCELLENT" -> VColors.successSoft
        "HEALTHY" -> VColors.mintSoft
        "WATCH" -> VColors.goldSoft
        "CRITICAL" -> VColors.coralSoft
        else -> VColors.surfaceCard
    }

    PremiumCard(
        onClick = onClick,
        tint = cardTint,
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
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
            MiniBadge(text = "${pulse.score}", color = badgeColor, bg = VColors.white)
        }
        if (pulse.categories.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            CardDivider()
            Spacer(Modifier.height(12.dp))
            pulse.categories.filter { it.available }.take(4).forEachIndexed { idx, cat ->
                if (idx > 0) Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(cat.label, style = VTypography.caption, color = VColors.ink2, modifier = Modifier.weight(1f))
                    Text("${cat.score}", style = VTypography.caption.copy(fontWeight = FontWeight.Bold), color = badgeColor)
                }
            }
        }
    }
}
