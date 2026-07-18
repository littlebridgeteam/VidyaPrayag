package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardOverview
import com.littlebridge.enrollplus.feature.admin.domain.model.DashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.DailyDigest
import com.littlebridge.enrollplus.feature.admin.domain.model.DigestTask
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewEvent
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewFeeAnalytics
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewInsight
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewKpi
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewSchoolPulse
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarPlatformViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.PinnedScreensViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.platform.rememberNotificationPermissionLauncher
import com.littlebridge.enrollplus.presentation.PermissionViewModel
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
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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
// School Home — Redesigned Command Desk
//
// Layout:
//   1. Header — hamburger, greeting, school name, bell, avatar
//   2. Search bar with scan icon
//   3. Today's Overview purple gradient card
//   4. Quick actions 2×2 grid
//   5. Stats row — Students / Teachers with sparklines
//   6. Attendance / Fees row with circular progress
//   7. Attention items section
//   8. Fee Collection + Upcoming Events split
//   9. Recent Activity
//  10. School Health bar
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
    onCreateAnnouncement: () -> Unit = {},
    onOpenApprovals: () -> Unit = {},
    onOpenPinnedScreen: (String) -> Unit = {},
    onExit: () -> Unit = {},
    viewModel: SchoolDashboardViewModel = koinViewModel(),
    notificationsViewModel: NotificationsViewModel = koinViewModel(),
    calendarViewModel: AcademicCalendarPlatformViewModel = koinViewModel(),
    permissionVm: PermissionViewModel = koinViewModel(),
    pinnedVm: PinnedScreensViewModel = koinViewModel(),
) {
    val dashboardState by viewModel.state.collectAsStateV2()
    val notifications by notificationsViewModel.state.collectAsStateV2()
    val pinnedScreens by pinnedVm.screens.collectAsStateV2()
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

    val showRationale by permissionVm.showNotificationRationale.collectAsStateV2()
    val launchPermission by permissionVm.launchPermissionRequest.collectAsStateV2()

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
                val ov = overview ?: return@VStateHost
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
                    onCreateAnnouncement = onCreateAnnouncement,
                    onOpenApprovals = onOpenApprovals,
                    onOpenPinnedScreen = onOpenPinnedScreen,
                    onOpenCommandPalette = { commandPaletteVisible = true },
                    onExit = onExit,
                )
            }

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
    onCreateAnnouncement: () -> Unit,
    onOpenApprovals: () -> Unit,
    onOpenPinnedScreen: (String) -> Unit,
    onOpenCommandPalette: () -> Unit,
    onExit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 120.dp),
    ) {
        HomeHeader(
            overview = overview,
            fallbackName = adminName,
            unreadCount = unreadCount,
            onNotifications = onOpenNotifications,
            onOpenMenu = onOpenCommandPalette,
            onExit = onExit,
        )

        SearchBar(
            onClick = onOpenCommandPalette,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
        )

        TodayOverviewCard(
            digest = digest,
            isLoading = isDigestLoading,
            unreadCount = unreadCount,
            onPendingNotifications = onOpenNotifications,
            onMarkAttendance = onOpenPews,
            onViewAllTasks = onOpenCommandPalette,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        )

        QuickActionsGrid(
            onAnnouncement = onCreateAnnouncement,
            onEvent = onCreateEvent,
            onReports = onOpenReportPublish,
            onCalendar = onOpenCalendar,
        )

        val studentsKpi = overview.kpis.find { it.key == "students" && it.available }
        val teachersKpi = overview.kpis.find { it.key == "teachers" && it.available }
        if (studentsKpi != null || teachersKpi != null) {
            StatsRow(
                studentsKpi = studentsKpi,
                teachersKpi = teachersKpi,
                onClick = onOpenAnalytics,
            )
        }

        val attendanceKpi = overview.kpis.find { it.key == "attendance" && it.available }
        val feesKpi = overview.kpis.find { it.key == "fees" && it.available }
        val feeAnalytics = overview.feeAnalytics
        if (attendanceKpi != null || feesKpi != null || feeAnalytics.available) {
            AttendanceFeesRow(
                attendanceKpi = attendanceKpi,
                feesKpi = feesKpi,
                feeAnalytics = feeAnalytics,
                onClick = onOpenAnalytics,
            )
        }

        val insights = overview.insights
        if (insights.isNotEmpty()) {
            AttentionSection(
                insights = insights,
                onOpen = onOpenPews,
            )
        }

        if (feeAnalytics.available || (overview.events.available && overview.events.upcoming.isNotEmpty())) {
            FeeEventsRow(
                feeAnalytics = feeAnalytics,
                events = overview.events.upcoming,
                onOpenAnalytics = onOpenAnalytics,
                onOpenCalendar = onOpenCalendar,
            )
        }

        val activities = activity?.activities.orEmpty()
        if (activities.isNotEmpty()) {
            RecentActivitySection(activities = activities)
        }

        overview.schoolPulse.let { pulse ->
            if (pulse.score > 0) {
                SchoolHealthBar(pulse = pulse, onClick = onOpenAnalytics)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1. Home Header — hamburger, greeting, school name, bell, avatar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeHeader(
    overview: AdminDashboardOverview,
    fallbackName: String,
    unreadCount: Int,
    onNotifications: () -> Unit,
    onOpenMenu: () -> Unit,
    onExit: () -> Unit,
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = VIcons.Menu,
            contentDescription = "Menu",
            tint = VTheme.colors.ink,
            modifier = Modifier
                .size(28.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onOpenMenu,
                ),
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$greeting, $name \uD83D\uDC4B",
                style = VTheme.type.h2,
                color = VTheme.colors.ink,
                fontSize = 20.sp,
                maxLines = 1,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp),
            ) {
                Text(
                    text = schoolName,
                    style = VTheme.type.body,
                    color = VTheme.colors.ink3,
                )
                Icon(
                    imageVector = VIcons.ChevronDown,
                    contentDescription = null,
                    tint = VTheme.colors.ink3,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
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
                        .padding(2.dp),
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(VTheme.colors.card)
                .border(1.dp, VTheme.colors.border1.copy(alpha = 0.5f), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onExit,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (!header.adminAvatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = header.adminAvatarUrl,
                    contentDescription = "Admin profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(
                    imageVector = VIcons.User,
                    contentDescription = "Admin profile",
                    tint = VTheme.colors.ink3,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. Search Bar — with scan icon
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(VTheme.colors.card)
            .border(
                width = 0.5.dp,
                color = VTheme.colors.border1.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
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
            text = "Search students, events, announcements...",
            style = VTheme.type.body,
            color = VTheme.colors.placeholder,
            modifier = Modifier.padding(start = 10.dp).weight(1f),
        )
        Icon(
            imageVector = VIcons.Scan,
            contentDescription = "Scan",
            tint = VTheme.colors.ink3,
            modifier = Modifier.size(20.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. Today's Overview Card — purple gradient with task summary
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TodayOverviewCard(
    digest: DailyDigest,
    isLoading: Boolean,
    unreadCount: Int,
    onPendingNotifications: () -> Unit,
    onMarkAttendance: () -> Unit,
    onViewAllTasks: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VGlassCard(
        modifier = modifier,
        backgroundBrush = overviewGradient(),
        padding = 18.dp,
    ) {
        if (isLoading && digest.tasks.isEmpty()) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(140.dp), shape = RoundedCornerShape(12.dp))
        } else {
            Column {
                Text(
                    text = "Today's Overview",
                    style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White.copy(alpha = 0.7f),
                )
                Spacer(Modifier.height(4.dp))
                val headline = digest.headline.ifBlank { "You have ${digest.tasks.size} important tasks to complete" }
                Text(
                    text = headline,
                    style = VTheme.type.h3,
                    color = Color.White,
                    maxLines = 3,
                )
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OverviewActionButton(
                        label = "Pending Notifications",
                        icon = VIcons.Bell,
                        badgeCount = unreadCount,
                        onClick = onPendingNotifications,
                        modifier = Modifier.weight(1f),
                    )
                    OverviewActionButton(
                        label = "Mark Attendance",
                        icon = VIcons.Check,
                        onClick = onMarkAttendance,
                        modifier = Modifier.weight(1f),
                    )
                    OverviewActionChip(
                        label = "View All Tasks",
                        onClick = onViewAllTasks,
                    )
                }
            }
        }
    }
}

@Composable
private fun OverviewActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF82B60)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$badgeCount",
                    style = VTypography.caption.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = label,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White,
            maxLines = 1,
            modifier = Modifier.weight(1f, fill = false),
        )
        Icon(
            imageVector = VIcons.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun OverviewActionChip(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White,
            maxLines = 1,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. Quick Actions Grid — 2×2 layout
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsGrid(
    onAnnouncement: () -> Unit,
    onEvent: () -> Unit,
    onReports: () -> Unit,
    onCalendar: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickActionCard("Announce", VIcons.Megaphone, VColors.violet, VColors.violetSoft, onAnnouncement, Modifier.weight(1f))
            QuickActionCard("Add Event", VIcons.Calendar, VColors.sky, VColors.skySoft, onEvent, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickActionCard("Reports", VIcons.FileText, VColors.gold, VColors.goldSoft, onReports, Modifier.weight(1f))
            QuickActionCard("Calendar", VIcons.Calendar, VColors.mint, VColors.mintSoft, onCalendar, Modifier.weight(1f))
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(VShapes.sm)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Text(
            text = label,
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
            maxLines = 1,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. Stats Row — Students / Teachers with mini sparkline charts
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(
    studentsKpi: OverviewKpi?,
    teachersKpi: OverviewKpi?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (studentsKpi != null) {
            KpiSparklineCard(kpi = studentsKpi, onClick = onClick, modifier = Modifier.weight(1f))
        }
        if (teachersKpi != null) {
            KpiSparklineCard(kpi = teachersKpi, onClick = onClick, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun KpiSparklineCard(
    kpi: OverviewKpi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accentColor = when (kpi.deltaDirection) {
        "up" -> VColors.success
        "down" -> VColors.coral
        else -> VColors.violet
    }

    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(14.dp),
    ) {
        Text(
            text = kpi.label,
            style = VTypography.caption,
            color = VColors.ink3,
        )
        Spacer(Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = formatKpiValue(kpi),
                style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold),
                color = VColors.ink,
            )
            if (kpi.deltaLabel.isNotBlank()) {
                Text(
                    text = kpi.deltaLabel,
                    style = VTypography.caption.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = accentColor,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Sparkline(
            data = generateSparklineData(kpi.value),
            lineColor = accentColor,
            fillColor = accentColor.copy(alpha = 0.1f),
            modifier = Modifier.fillMaxWidth().height(28.dp),
        )
    }
}

private fun generateSparklineData(baseValue: Int): List<Float> {
    val seed = baseValue.toLong()
    return List(12) { i ->
        val phase = (seed + i * 37) % 100
        0.3f + (phase.toFloat() / 100f) * 0.5f + kotlin.math.sin(i.toDouble() * 0.8).toFloat() * 0.1f
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. Attendance / Fees row with circular progress
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AttendanceFeesRow(
    attendanceKpi: OverviewKpi?,
    feesKpi: OverviewKpi?,
    feeAnalytics: OverviewFeeAnalytics,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (attendanceKpi != null) {
            CircularStatCard(
                label = attendanceKpi.label,
                value = formatKpiValue(attendanceKpi),
                deltaLabel = attendanceKpi.deltaLabel,
                deltaDirection = attendanceKpi.deltaDirection,
                progress = attendanceKpi.value.toFloat() / 100f,
                progressColor = VColors.mint,
                onClick = onClick,
                modifier = Modifier.weight(1f),
            )
        }
        if (feeAnalytics.available) {
            CircularStatCard(
                label = "Fees Collected",
                value = "\u20B9${formatAmount(feeAnalytics.totalCollected)}",
                deltaLabel = "${feeAnalytics.collectionRate}% of total",
                deltaDirection = "up",
                progress = feeAnalytics.collectionRate.toFloat() / 100f,
                progressColor = VColors.violet,
                onClick = onClick,
                modifier = Modifier.weight(1f),
            )
        } else if (feesKpi != null) {
            CircularStatCard(
                label = feesKpi.label,
                value = formatKpiValue(feesKpi),
                deltaLabel = feesKpi.deltaLabel,
                deltaDirection = feesKpi.deltaDirection,
                progress = feesKpi.value.toFloat().coerceIn(0f, 1f),
                progressColor = VColors.violet,
                onClick = onClick,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CircularStatCard(
    label: String,
    value: String,
    deltaLabel: String,
    deltaDirection: String,
    progress: Float,
    progressColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accentColor = when (deltaDirection) {
        "up" -> VColors.success
        "down" -> VColors.coral
        else -> VColors.violet
    }

    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(14.dp),
    ) {
        Text(
            text = label,
            style = VTypography.caption,
            color = VColors.ink3,
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = value,
                    style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold),
                    color = VColors.ink,
                )
                if (deltaLabel.isNotBlank()) {
                    Text(
                        text = deltaLabel,
                        style = VTypography.caption.copy(fontSize = 11.sp),
                        color = accentColor,
                    )
                }
            }
            CircularProgress(
                progress = progress.coerceIn(0f, 1f),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 7. Attention Section — pink background, numbered items
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AttentionSection(
    insights: List<OverviewInsight>,
    onOpen: () -> Unit,
) {
    val sorted = insights.sortedByDescending { severityWeight(it.severity) }
    val count = sorted.size
    val hasHigh = sorted.any { it.severity.uppercase() == "HIGH" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(VShapes.lg)
            .background(
                if (hasHigh) Color(0xFFFFE8E8) else Color(0xFFFFF3E8),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onOpen,
            )
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = VIcons.AlertCircle,
                contentDescription = null,
                tint = if (hasHigh) VColors.coral else VColors.gold,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "$count ITEMS NEED ATTENTION",
                style = VTypography.label.copy(fontWeight = FontWeight.Bold),
                color = if (hasHigh) VColors.coral else VColors.gold,
            )
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(VColors.white),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$count",
                    style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                    color = VColors.coral,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        sorted.take(3).forEachIndexed { idx, insight ->
            if (idx > 0) {
                Spacer(Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val dotColor = when (insight.severity.uppercase()) {
                    "HIGH" -> VColors.coral
                    "MEDIUM" -> VColors.gold
                    else -> VColors.violet
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = insight.title,
                        style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
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
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 8. Fee Collection + Upcoming Events — side-by-side split
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FeeEventsRow(
    feeAnalytics: OverviewFeeAnalytics,
    events: List<OverviewEvent>,
    onOpenAnalytics: () -> Unit,
    onOpenCalendar: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (feeAnalytics.available) {
            FeeCollectionMiniCard(
                fa = feeAnalytics,
                onClick = onOpenAnalytics,
                modifier = Modifier.weight(1f),
            )
        }
        if (events.isNotEmpty()) {
            UpcomingEventsMiniCard(
                events = events,
                onOpenCalendar = onOpenCalendar,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun FeeCollectionMiniCard(
    fa: OverviewFeeAnalytics,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rateColor = when {
        fa.collectionRate >= 90 -> VColors.success
        fa.collectionRate >= 70 -> VColors.violet
        else -> VColors.gold
    }
    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(VShapes.sm)
                    .background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(VIcons.Wallet, contentDescription = null, tint = VColors.violet, modifier = Modifier.size(14.dp))
            }
            Text(
                text = "FEE COLLECTION",
                style = VTypography.label.copy(fontWeight = FontWeight.Bold),
                color = VColors.ink,
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CircularProgress(
                progress = fa.collectionRate.toFloat() / 100f,
                color = rateColor,
                trackColor = rateColor.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp),
            )
            Column {
                Text(
                    text = "\u20B9${formatAmount(fa.totalCollected)}",
                    style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
                    color = VColors.ink,
                )
                Text(
                    text = "\u20B9${formatAmount(fa.pending)} pending",
                    style = VTypography.caption,
                    color = VColors.coral,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Sparkline(
            data = fa.trend.map { it.value.toFloat() / 100f }.ifEmpty { generateSparklineData(fa.collectionRate) },
            lineColor = VColors.mint,
            fillColor = VColors.mint.copy(alpha = 0.1f),
            modifier = Modifier.fillMaxWidth().height(20.dp),
        )
    }
}

@Composable
private fun UpcomingEventsMiniCard(
    events: List<OverviewEvent>,
    onOpenCalendar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(VShapes.sm)
                        .background(VColors.skySoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(VIcons.Calendar, contentDescription = null, tint = VColors.sky, modifier = Modifier.size(14.dp))
                }
                Text(
                    text = "UPCOMING EVENTS",
                    style = VTypography.label.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                )
            }
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
        events.take(3).forEachIndexed { idx, event ->
            if (idx > 0) {
                Spacer(Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (event.isHoliday) VColors.goldSoft else VColors.violetSoft)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (event.daysAway == 0) "Today" else "${event.daysAway}d",
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        color = if (event.isHoliday) VColors.gold else VColors.violet,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(event.title, style = VTypography.caption, color = VColors.ink, maxLines = 1)
                    Text(event.date, style = VTypography.caption, color = VColors.ink3)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 9. Recent Activity — with View All
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RecentActivitySection(activities: List<DashboardActivity>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = VIcons.Activity,
                    contentDescription = null,
                    tint = VColors.violet,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "RECENT ACTIVITY",
                    style = VTypography.label.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "View all",
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                color = VColors.violet,
            )
        }
        Spacer(Modifier.height(12.dp))
        activities.take(3).forEachIndexed { idx, act ->
            if (idx > 0) {
                Spacer(Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = act.title,
                    style = VTypography.body,
                    color = VColors.ink,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                )
                Text(
                    text = act.time,
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(VColors.violet),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 10. School Health Bar — green horizontal status bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SchoolHealthBar(
    pulse: OverviewSchoolPulse,
    onClick: () -> Unit,
) {
    val statusText = when (pulse.status.uppercase()) {
        "EXCELLENT" -> "Your school is excelling."
        "HEALTHY" -> "Your school is healthy."
        "WATCH" -> "Your school needs attention."
        "CRITICAL" -> "Your school needs urgent attention."
        else -> pulse.message.takeIf { it.isNotBlank() } ?: "School status unavailable."
    }
    val subText = when (pulse.status.uppercase()) {
        "EXCELLENT", "HEALTHY" -> "Keep up the good work!"
        else -> "Review items needing attention."
    }
    val statusColor = when (pulse.status.uppercase()) {
        "EXCELLENT" -> VColors.success
        "HEALTHY" -> VColors.mint
        "WATCH" -> VColors.gold
        "CRITICAL" -> VColors.coral
        else -> VColors.ink3
    }

    val attendanceCat = pulse.categories.find { it.key == "attendance" && it.available }
    val feesCat = pulse.categories.find { it.key.contains("fee", ignoreCase = true) && it.available }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(VShapes.lg)
            .background(brush = healthBarGradient())
            .border(1.dp, VColors.successSoft, VShapes.lg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = VIcons.ShieldCheck,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = statusText,
                    style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                    color = VColors.ink,
                )
                Text(
                    text = subText,
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }
            if (attendanceCat != null || feesCat != null) {
                Spacer(Modifier.width(12.dp))
                if (attendanceCat != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Attendance",
                            style = VTypography.caption.copy(fontSize = 10.sp),
                            color = VColors.ink3,
                        )
                        Text(
                            text = "${attendanceCat.score}%",
                            style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                            color = statusColor,
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                }
                if (feesCat != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Fee Collection",
                            style = VTypography.caption.copy(fontSize = 10.sp),
                            color = VColors.ink3,
                        )
                        Text(
                            text = "${feesCat.score}%",
                            style = VTypography.caption.copy(fontWeight = FontWeight.Bold),
                            color = statusColor,
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared card primitives
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
    Column(modifier = clickable.fillMaxWidth().padding(16.dp)) { content() }
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

// ─────────────────────────────────────────────────────────────────────────────
// Canvas composables — Sparkline & Circular Progress
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun Sparkline(
    data: List<Float>,
    lineColor: Color,
    fillColor: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 2f,
) {
    if (data.size < 2) return
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stepX = w / (data.size - 1)
        val minY = data.min()
        val maxY = data.max().coerceAtLeast(minY + 0.01f)
        val rangeY = maxY - minY

        val linePath = Path()
        val fillPath = Path()

        data.forEachIndexed { idx, value ->
            val x = idx * stepX
            val normalized = (value - minY) / rangeY
            val y = h - (normalized * h * 0.85f) - h * 0.075f

            if (idx == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, h)
                fillPath.lineTo(x, y)
            } else {
                val prevX = (idx - 1) * stepX
                val prevValue = data[idx - 1]
                val prevNorm = (prevValue - minY) / rangeY
                val prevY = h - (prevNorm * h * 0.85f) - h * 0.075f
                val cpX = (prevX + x) / 2f
                linePath.cubicTo(cpX, prevY, cpX, y, x, y)
                fillPath.cubicTo(cpX, prevY, cpX, y, x, y)
            }
        }

        fillPath.lineTo(w, h)
        fillPath.close()

        drawPath(fillPath, fillColor)
        drawPath(linePath, lineColor, style = Stroke(width = strokeWidth.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun CircularProgress(
    progress: Float,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 5f,
) {
    Canvas(modifier = modifier) {
        val sweep = progress.coerceIn(0f, 1f) * 360f
        val stroke = strokeWidth.dp.toPx()
        val arcSize = Size(size.width - stroke, size.height - stroke)
        val topLeft = Offset(stroke / 2f, stroke / 2f)

        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun formatKpiValue(kpi: OverviewKpi): String {
    val v = kpi.value
    return when {
        kpi.unit == "%" -> "$v%"
        kpi.unit == "\u20B9" || kpi.unit == "INR" -> "\u20B9${if (v > 99999) "${v / 1000}k" else v}"
        v > 999 -> "${v / 1000}.${(v % 1000) / 100}k"
        else -> v.toString()
    }
}

private fun formatAmount(v: Double): String = when {
    v >= 10000000 -> "${(v / 10000000).let { kotlin.math.round(it * 10) / 10 }}Cr"
    v >= 100000 -> "${(v / 100000).let { kotlin.math.round(it * 10) / 10 }}L"
    v >= 1000 -> "${(v / 1000).toInt()}k"
    else -> v.toInt().toString()
}

private fun severityWeight(severity: String): Int = when (severity.uppercase()) {
    "HIGH" -> 3; "MEDIUM" -> 2; else -> 1
}
