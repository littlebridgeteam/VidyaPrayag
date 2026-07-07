package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardActivity
import com.littlebridge.enrollplus.feature.admin.domain.model.AdminDashboardOverview
import com.littlebridge.enrollplus.feature.admin.domain.model.DashboardActivity
import com.littlebridge.enrollplus.util.MONTH_SHORT
import com.littlebridge.enrollplus.util.dayOfWeek
import com.littlebridge.enrollplus.util.nowMinutesOfDay
import com.littlebridge.enrollplus.util.parseIsoDate
import com.littlebridge.enrollplus.util.todayIso
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewAchievement
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewBirthday
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewEvent
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewFeeAnalytics
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewInsight
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewKpi
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewParentEngagement
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewSchoolPulse
import com.littlebridge.enrollplus.feature.admin.domain.model.OverviewTeacherSpotlight
import com.littlebridge.enrollplus.feature.admin.presentation.AcademicCalendarPlatformViewModel
import com.littlebridge.enrollplus.feature.admin.presentation.SchoolDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.NotificationsViewModel
import com.littlebridge.enrollplus.platform.rememberNotificationPermissionLauncher
import com.littlebridge.enrollplus.presentation.PermissionViewModel
import com.littlebridge.enrollplus.ui.components.VAvatar
import com.littlebridge.enrollplus.ui.components.VAvatarSize
import com.littlebridge.enrollplus.ui.components.VButton
import com.littlebridge.enrollplus.ui.components.VButtonVariant
import com.littlebridge.enrollplus.ui.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.components.VEmptyState
import com.littlebridge.enrollplus.ui.components.VPullRefresh
import com.littlebridge.enrollplus.ui.components.skeletons.SkeletonDashboard
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

// ─────────────────────────────────────────────────────────────────────────────
// School Home — Command Desk v2
//
// EXACT design DNA from onboarding steps 4, 5, 6:
//   • VColors.cream background
//   • CreamCard: surfaceCard + 1dp border(line) + VShapes.lg + 16dp padding
//   • VTypography.label (13sp SemiBold, ink3) for section labels
//   • VTypography.body.copy(fontWeight = FontWeight.Bold) for card titles (ink)
//   • VTypography.caption (12sp Medium, ink3) for secondary text
//   • MiniBadge: caption Bold + colored bg + VShapes.full
//   • SimpleAvatar: violetSoft bg circle with initials in violet
//   • lineSoft dividers between card items
//   • VProgressBar for progress
//   • creamDeep for table-like backgrounds
//   • FilterChip for selections
//   • Coverage % with h3 ExtraBold, color-coded (success/violet/gold)
//
// 4-stage system:
//   1. Loading → Skeleton matching real layout
//   2. Content → Real data + pull-to-refresh + staggered entrance
//   3. Empty → Plain-language message + optional action button
//   4. Error → Plain-language message + Retry button
//
// Animation: AnimatedContent with slide+fade (onboarding step transition pattern, 280ms)
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

    val adminName = dashboardState.adminName
    val loading = dashboardState.isLoading
    val error = dashboardState.errorMessage
    val overview = dashboardState.overview
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

    // 4-stage system
    val stage = when {
        loading && overview == null -> Stage.Loading
        error != null && overview == null -> Stage.Error
        overview == null -> Stage.Empty
        else -> Stage.Content
    }

    VPullRefresh(
        isRefreshing = loading && overview != null,
        onRefresh = { viewModel.refresh(); calendarViewModel.refresh() },
        modifier = modifier.fillMaxSize().background(VColors.cream),
    ) {
        // AnimatedContent with slide+fade — same pattern as onboarding step transitions (280ms)
        AnimatedContent(
            targetState = stage,
            transitionSpec = {
                val dur = 280
                val enter = fadeIn(tween(dur)) + slideInHorizontally(
                    animationSpec = tween(dur),
                    initialOffsetX = { it / 8 },
                )
                val exit = fadeOut(tween(dur)) + slideOutHorizontally(
                    animationSpec = tween(dur),
                    targetOffsetX = { -it / 8 },
                )
                enter togetherWith exit
            },
            label = "homeStage",
        ) { current ->
            when (current) {
                Stage.Loading -> LoadingState()
                Stage.Error -> ErrorState(
                    message = error ?: "Something went wrong.",
                    onRetry = { viewModel.refresh(); calendarViewModel.refresh() },
                )
                Stage.Empty -> EmptyState()
                Stage.Content -> {
                    val ov = overview!!
                    CommandDesk(
                        overview = ov,
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
}

private enum class Stage { Loading, Content, Empty, Error }

// ─────────────────────────────────────────────────────────────────────────────
// Stage 1: Loading — skeleton matching real content layout
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LoadingState() {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().padding(top = 16.dp),
    ) { SkeletonDashboard() }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stage 4: Error — plain-language message + Retry button
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        VEmptyState(
            title = "Couldn't load dashboard",
            body = message,
            actionLabel = "Retry",
            onAction = onRetry,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stage 3: Empty — plain-language message, no illustration
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState() {
    Column(
        Modifier.fillMaxSize().statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        VEmptyState(
            title = "Nothing to show yet",
            body = "Your dashboard will appear here once data is available.",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Command Desk — scrollable content with per-card stagger entrance
//
// Animation:
//   • Header: 100ms delay, VMotion.durSlower (700ms) fade+slide
//   • Each card: 220ms + index*60ms delay, VMotion.durSlower fade+slide
//   • Cards cascade in one-by-one — feels alive, not cheap
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(bottom = 120.dp),
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
        ) {
            DeskHeader(
                overview = overview,
                fallbackName = adminName,
                unreadCount = unreadCount,
                onNotifications = onOpenNotifications,
                onAvatar = onExit,
            )
        }

        // Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            val kpis = overview.kpis.filter { it.available }
            if (kpis.isNotEmpty()) {
                KpiGrid(kpis = kpis, onClick = onOpenAnalytics)
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

            QuickActionsCard(
                onAnnouncement = onOpenNotifications,
                onEvent = onCreateEvent,
                onReports = onOpenReportPublish,
            )

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
}


// ─────────────────────────────────────────────────────────────────────────────
// Desk Header — premium top section
//
//   • Enroll+ wordmark left (10% larger), notification bell right
//   • Premium card: school name overline, greeting headline, session chips, live pill
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

    // Time-based greeting
    val hour = nowMinutesOfDay() / 60
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Welcome back"
    }

    // Today's date: "Mon, 25 Jun 2026"
    val todayIso = todayIso()
    val (ty, tm, td) = parseIsoDate(todayIso) ?: Triple(0, 0, 0)
    val dowNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val dow = if (ty > 0) dowNames[dayOfWeek(ty, tm, td)] else ""
    val monName = MONTH_SHORT.getOrNull(tm - 1) ?: ""
    val todayStr = if (ty > 0) "$dow, $td $monName $ty" else ""

    // Top bar — wordmark + notification bell
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = buildAnnotatedString {
                append("Enroll")
                withStyle(SpanStyle(color = VColors.violet)) { append("+") }
            },
            style = VTypography.wordmark.copy(fontSize = 17.6.sp),
            color = VColors.ink,
            modifier = Modifier.weight(1f),
        )

        // Notification bell — 15% smaller, badge positioned to overflow
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VColors.violetSoft)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onNotifications() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                VIcons.BellStroke,
                contentDescription = "Notifications",
                tint = VColors.violet,
                modifier = Modifier.size(18.dp),
            )
        }
        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .offset(x = (-4).dp, y = 2.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(VColors.coral)
                    .border(1.5.dp, VColors.cream, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                    style = VTypography.caption.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                    ),
                    color = VColors.white,
                )
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    // Premium header card — school name, date, greeting, admin name, session chips
    CreamCard(tint = VColors.surfaceCard) {
        // School name overline with accent dot
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(VColors.violet),
            )
            Text(
                text = schoolName.uppercase(),
                style = VTypography.caption.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                ),
                color = VColors.violet,
            )
        }

        // Today's date
        if (todayStr.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = todayStr,
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }

        Spacer(Modifier.height(12.dp))

        // Greeting — time-based, bold headline
        Text(
            text = greeting,
            style = VTypography.h2.copy(fontWeight = FontWeight.ExtraBold),
            color = VColors.ink,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = name,
            style = VTypography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = VColors.ink2,
        )

        // Session chips — academic year + term as inline pills
        val hasYear = header.academicYear.isNotBlank()
        val hasTerm = header.currentTerm.isNotBlank()
        if (hasYear || hasTerm) {
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (hasYear) {
                    Text(
                        text = header.academicYear,
                        style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                        color = VColors.ink2,
                        modifier = Modifier
                            .clip(VShapes.sm)
                            .background(VColors.creamDeep)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
                if (hasTerm) {
                    Text(
                        text = header.currentTerm,
                        style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                        color = VColors.violet,
                        modifier = Modifier
                            .clip(VShapes.sm)
                            .background(VColors.violetSoft)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared primitives — exact copies from onboarding
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CreamCard(
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
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = VTypography.label,
        color = VColors.ink3,
    )
}

@Composable
private fun CardSectionHeader(
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
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(15.dp),
            )
        }
        Text(
            text = label,
            style = VTypography.label.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// KPI Grid — 2-column metrics in CreamCard
// Number in h3 ExtraBold + label in caption + delta in caption Bold colored
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun KpiGrid(kpis: List<OverviewKpi>, onClick: () -> Unit) {
    CreamCard(onClick = onClick) {
        CardSectionHeader("Today's Metrics", VIcons.LayoutDashboard)
        Spacer(Modifier.height(14.dp))
        val rows = kpis.chunked(2)
        rows.forEachIndexed { rowIdx, row ->
            if (rowIdx > 0) Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                row.forEach { kpi -> KpiMetric(kpi = kpi, modifier = Modifier.weight(1f)) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            if (rowIdx < rows.lastIndex) {
                Spacer(Modifier.height(10.dp))
                CardDivider()
            }
        }
    }
}

@Composable
private fun RowScope.KpiMetric(kpi: OverviewKpi, modifier: Modifier = Modifier) {
    val accentColor = when (kpi.deltaDirection) {
        "up" -> VColors.success
        "down" -> VColors.coral
        else -> VColors.violet
    }
    Column(modifier = modifier) {
        Text(
            text = formatKpiValue(kpi),
            style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold),
            color = accentColor,
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
                color = accentColor,
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
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AttentionCard(insights: List<OverviewInsight>, onOpen: () -> Unit) {
    val sorted = insights.sortedByDescending { severityWeight(it.severity) }
    val count = sorted.size
    val hasHigh = sorted.any { it.severity.uppercase() == "HIGH" }

    CreamCard(
        onClick = onOpen,
        tint = if (hasHigh) VColors.coralSoft else VColors.surfaceCard,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CardSectionHeader(
                if (count == 1) "1 thing needs your attention" else "$count things need your attention",
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
// Fee Analytics Card — collection rate + collected/pending
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FeeAnalyticsCard(fa: OverviewFeeAnalytics, onClick: () -> Unit) {
    val rateColor = when {
        fa.collectionRate >= 90 -> VColors.success
        fa.collectionRate >= 70 -> VColors.violet
        else -> VColors.gold
    }
    CreamCard(onClick = onClick) {
        CardSectionHeader("Fee Collection", VIcons.Wallet, iconTint = VColors.violet)
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
// Parent Engagement Card — active parents % + leaderboard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ParentEngagementCard(pe: OverviewParentEngagement, onClick: () -> Unit) {
    val engagementColor = if (pe.activeParentsPct >= 70) VColors.success else VColors.gold
    CreamCard(onClick = onClick) {
        CardSectionHeader("Parent Engagement", VIcons.UsersGroup, iconTint = VColors.sky, iconBg = VColors.skySoft)
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
// Quick Actions Card — tappable rows with lineSoft dividers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsCard(
    onAnnouncement: () -> Unit,
    onEvent: () -> Unit,
    onReports: () -> Unit,
) {
    CreamCard {
        CardSectionHeader("Quick Actions", VIcons.Sparkles, iconTint = VColors.gold, iconBg = VColors.goldSoft)
        Spacer(Modifier.height(14.dp))
        ActionRow(label = "Send Announcement", icon = VIcons.Megaphone, iconTint = VColors.violet, iconBg = VColors.violetSoft, onClick = onAnnouncement)
        CardDivider(); Spacer(Modifier.height(4.dp))
        ActionRow(label = "Create Event", icon = VIcons.Calendar, iconTint = VColors.sky, iconBg = VColors.skySoft, onClick = onEvent)
        CardDivider(); Spacer(Modifier.height(4.dp))
        ActionRow(label = "Publish Reports", icon = VIcons.FileText, iconTint = VColors.gold, iconBg = VColors.goldSoft, onClick = onReports)
    }
}

@Composable
private fun ActionRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = VColors.violet,
    iconBg: Color = VColors.violetSoft,
    onClick: () -> Unit,
) {
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
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(VShapes.sm)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(17.dp),
            )
        }
        Spacer(Modifier.size(12.dp))
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
// Teacher Spotlight Card — avatar + name + highlight (onboarding teacher pattern)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TeacherSpotlightCard(ts: OverviewTeacherSpotlight, onClick: () -> Unit) {
    CreamCard(onClick = onClick) {
        CardSectionHeader("Teacher Spotlight", VIcons.GraduationCap)
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
// Upcoming Card — date pills + event titles
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UpcomingCard(events: List<OverviewEvent>, onOpenCalendar: () -> Unit) {
    CreamCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CardSectionHeader("Upcoming", VIcons.Calendar)
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
// Achievements Card — student achievements with category badges
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AchievementsCard(achievements: List<OverviewAchievement>) {
    CreamCard(tint = VColors.goldSoft) {
        CardSectionHeader("Achievements", VIcons.Star, iconTint = VColors.gold, iconBg = VColors.goldSoft)
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
// Birthdays Card — today + upcoming birthdays
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BirthdaysCard(birthdays: List<OverviewBirthday>) {
    CreamCard(tint = VColors.coralSoft) {
        CardSectionHeader("Birthdays", VIcons.Heart, iconTint = VColors.coral, iconBg = VColors.coralSoft)
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
// Activity Card — recent activity with lineSoft dividers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ActivityCard(activities: List<DashboardActivity>) {
    CreamCard {
        CardSectionHeader("Recent Activity", VIcons.History, iconTint = VColors.ink3, iconBg = VColors.creamDeep)
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
// Pulse Card — status sentence + score badge (no gauge)
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

    CreamCard(onClick = onClick, tint = cardTint) {
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
