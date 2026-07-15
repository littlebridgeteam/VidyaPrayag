package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentDailyLogEntryDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncementState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncementViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressViewModel
import com.littlebridge.enrollplus.feature.transport.presentation.TransportViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.components.VStaleChip
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.screens.teacher.TeacherKit.TeacherSpinner

@Composable
fun ParentHomeScreenV2(
    modifier: Modifier = Modifier,
    onDiscoverSchools: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenFees: () -> Unit = {},
    onOpenAcademics: () -> Unit = {},
    onOpenAcademicsTab: (String) -> Unit = {},
    onOpenMessages: () -> Unit = {},
    onOpenPulse: () -> Unit = {},
    onOpenTransport: () -> Unit = {},
    onOpenTutor: () -> Unit = {},
    onOpenScholarships: () -> Unit = {},
    onOpenIdCard: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    onOpenEvents: () -> Unit = {},
    onOpenReport: () -> Unit = {},
    onOpenPews: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    viewModel: ParentDashboardViewModel = koinViewModel(),
    academicsViewModel: ParentAcademicsViewModel = koinViewModel(),
    announcementsViewModel: ParentAnnouncementViewModel = koinViewModel(),
    trackViewModel: TrackProgressViewModel = koinViewModel(),
    transportViewModel: TransportViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val academics by academicsViewModel.state.collectAsStateV2()
    val announcements by announcementsViewModel.state.collectAsStateV2()
    val track by trackViewModel.state.collectAsStateV2()
    val transport by transportViewModel.state.collectAsStateV2()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(state.refreshEpoch) {
        if (state.refreshEpoch > 0) isRefreshing = false
    }

    LaunchedEffect(Unit) {
        if (state.children.isEmpty()) viewModel.load()
    }

    LaunchedEffect(state.selectedChild?.id) {
        state.selectedChild?.id?.let { childId ->
            academicsViewModel.selectChild(childId)
            academicsViewModel.loadDailySummary()
            transportViewModel.loadChildRoute(childId)
        }
    }

    // Re-derive live relation of today's periods every minute so the schedule
    // marks done classes and highlights the current class as the day progresses.
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60_000L)
            viewModel.refreshLiveClock()
        }
    }

    DisposableEffect(Unit) {
        onDispose { transportViewModel.stopPolling() }
    }

    ParentHomeContent(
        state = state,
        academics = academics,
        announcements = announcements,
        track = track,
        transportEnrolled = transport.childRoute != null,
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.refresh()
            state.selectedChild?.id?.let { childId ->
                academicsViewModel.selectChild(childId)
                academicsViewModel.loadDailySummary()
                transportViewModel.loadChildRoute(childId)
            }
        },
        onSelectChild = viewModel::selectChild,
        onOpenNotifications = onOpenNotifications,
        onOpenFees = onOpenFees,
        onOpenAcademics = onOpenAcademics,
        onOpenAcademicsTab = onOpenAcademicsTab,
        onOpenMessages = onOpenMessages,
        onOpenTransport = onOpenTransport,
        onOpenTutor = onOpenTutor,
        onOpenScholarships = onOpenScholarships,
        onOpenIdCard = onOpenIdCard,
        onOpenLibrary = onOpenLibrary,
        onOpenEvents = onOpenEvents,
        onOpenReport = onOpenReport,
        onOpenPews = onOpenPews,
        onDiscoverSchools = onDiscoverSchools,
        unreadNotificationsCount = unreadNotificationsCount,
        modifier = modifier,
    )
}

@Composable
private fun ParentHomeContent(
    state: ParentDashboardState,
    academics: ParentAcademicsState,
    announcements: ParentAnnouncementState,
    track: com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressState,
    transportEnrolled: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenAcademicsTab: (String) -> Unit,
    onOpenMessages: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenTutor: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenIdCard: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenReport: () -> Unit,
    onOpenPews: () -> Unit,
    onDiscoverSchools: () -> Unit,
    unreadNotificationsCount: Int,
    modifier: Modifier = Modifier,
) {
    VPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(VColors.cream)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp),
        ) {
            PortalTopHeader(
                parentName = track.accountName.ifBlank { "Parent" },
                childName = state.selectedChild?.name?.ifBlank { null } ?: "Your Child",
                children = state.children,
                selectedChild = state.selectedChild,
                onSelectChild = onSelectChild,
                onOpenNotifications = onOpenNotifications,
                unreadNotificationsCount = unreadNotificationsCount,
                greetingLead = "here's",
                greetingAccent = "${state.selectedChild?.name?.ifBlank { null } ?: "Your Child"}'s day",
            )

            if (state.isStale) {
                VStaleChip(modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp))
            }

            when {
                state.isLoading && state.children.isEmpty() -> HomeSkeleton()
                state.error != null && state.children.isEmpty() -> HomeError(message = state.error ?: "", onRetry = onRefresh)
                state.children.isEmpty() -> HomeEmpty(onDiscoverSchools = onDiscoverSchools)
                else -> HomeLoaded(
                    state = state,
                    academics = academics,
                    announcements = announcements,
                    track = track,
                    transportEnrolled = transportEnrolled,
                    onOpenFees = onOpenFees,
                    onOpenAcademics = onOpenAcademics,
                    onOpenAcademicsTab = onOpenAcademicsTab,
                    onOpenMessages = onOpenMessages,
                    onOpenTransport = onOpenTransport,
                    onOpenTutor = onOpenTutor,
                    onOpenScholarships = onOpenScholarships,
                    onOpenIdCard = onOpenIdCard,
                    onOpenLibrary = onOpenLibrary,
                    onOpenEvents = onOpenEvents,
                    onOpenReport = onOpenReport,
                    onOpenPews = onOpenPews,
                    unreadNotificationsCount = unreadNotificationsCount,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SKELETON / ERROR / EMPTY
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(VShapes.xxl)
                .background(VColors.lineSoft),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(VShapes.lg)
                .background(VColors.lineSoft),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp)
                        .clip(VShapes.lg)
                        .background(VColors.lineSoft),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(VShapes.lg)
                .background(VColors.lineSoft),
        )
    }
}

@Composable
private fun HomeError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(VColors.errorSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.AlertTriangle,
                contentDescription = null,
                tint = VColors.error,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = "Couldn't load home",
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
        Text(
            text = message,
            style = VTypography.caption,
            color = VColors.ink2,
        )
        Text(
            text = "Retry",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.violet,
            modifier = Modifier.clickable { onRetry() },
        )
    }
}

@Composable
private fun HomeEmpty(onDiscoverSchools: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                VIcons.HomePremium,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = "No child linked yet",
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
        Text(
            text = "Link a child to see their day, attendance, and school updates.",
            style = VTypography.caption,
            color = VColors.ink2,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Discover schools",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.violet,
            modifier = Modifier.clickable { onDiscoverSchools() },
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// LOADED
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeLoaded(
    state: ParentDashboardState,
    academics: ParentAcademicsState,
    announcements: ParentAnnouncementState,
    track: com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressState,
    transportEnrolled: Boolean,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenAcademicsTab: (String) -> Unit,
    onOpenMessages: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenTutor: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenIdCard: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenReport: () -> Unit,
    onOpenPews: () -> Unit,
    unreadNotificationsCount: Int,
) {
    val child = state.selectedChild
    val childName = child?.name?.ifBlank { null } ?: "Your Child"
    val schoolName = child?.schoolName?.ifBlank { null } ?: academics.dailySummary?.className?.ifBlank { null } ?: state.timetable?.className?.ifBlank { null } ?: "School"
    val attendanceRate = state.attendance?.attendanceRate
    val feesDue = state.fees?.outstandingFees ?: "₹0"
    val overdue = state.fees?.overdueCount ?: 0
    val pendingCount = academics.quizzes.count { it.status.uppercase() == "PENDING" }
    val unreadMessages = unreadNotificationsCount

    val priorityCards = rememberPriorityCards(
        feesDue = feesDue,
        overdue = overdue,
        attendanceRate = attendanceRate,
        pendingCount = pendingCount,
        unreadMessages = unreadMessages,
        onOpenFees = onOpenFees,
        onOpenAcademics = onOpenAcademics,
        onOpenAcademicsTab = onOpenAcademicsTab,
        onOpenMessages = onOpenMessages,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        HeroCard(
            childName = childName,
            schoolName = schoolName,
            level = child?.currentLevel ?: 0,
            overallProgress = child?.overallProgress?.toFloat() ?: 0f,
            attendanceRate = attendanceRate,
            avgGrade = markDisplayGrade(state.latestMark),
            pendingCount = pendingCount,
        )

        if (transportEnrolled) {
            TransportTrackingCard(onClick = onOpenTransport)
        }

        FilterChips()

        SectionHeader(title = "Priority")
        PriorityCarousel(cards = priorityCards)

        SectionHeader(title = "Today's Schedule", action = "Full timetable", onAction = { onOpenAcademicsTab("Timetable") })
        TodayScheduleCard(
            periods = state.todayPeriods,
            isLoading = state.timetableLoading,
            onOpenAcademics = { onOpenAcademicsTab("Timetable") },
        )

        SectionHeader(title = "Today's Summary")
        TodaySummaryCard(
            summary = academics.dailySummary,
            isLoading = academics.dailySummaryLoading,
        )

        SectionHeader(title = "School Updates", action = "All", onAction = onOpenEvents)
        UpdatesCard(
            announcements = announcements.announcements,
            isLoading = announcements.isLoading,
            onEventClick = onOpenEvents,
        )

        SectionHeader(title = "Premium Features")
        PremiumFeaturesGrid(
            onOpenTutor = onOpenTutor,
            onOpenReport = onOpenReport,
            onOpenPews = onOpenPews,
            onOpenLibrary = onOpenLibrary,
            onOpenCalendar = onOpenEvents,
        )

        Spacer(Modifier.height(24.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HERO CARD
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HeroCard(
    childName: String,
    schoolName: String,
    level: Int,
    overallProgress: Float,
    attendanceRate: Int?,
    avgGrade: String,
    pendingCount: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xxl)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        VColors.violet,
                        Color(0xFF4A30C4),
                        VColors.violetInk,
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                )
            )
            .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = schoolName,
                style = VTypography.caption.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = VColors.white.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 12.dp),
            )
            Box(
                modifier = Modifier
                    .clip(VShapes.full)
                    .background(VColors.white.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(
                    text = if (level > 0) "Level $level" else "Student",
                    style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                    color = VColors.white,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(VShapes.xl)
                    .background(VColors.white.copy(alpha = 0.2f))
                    .border(2.dp, VColors.white.copy(alpha = 0.25f), VShapes.xl),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = childName.take(1).uppercase(),
                    style = VTypography.h2.copy(fontSize = 24.sp),
                    color = VColors.white,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    childName,
                    style = VTypography.h3.copy(fontSize = 20.sp),
                    color = VColors.white,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(overallProgress.coerceIn(0f, 1f))
                        .height(5.dp)
                        .clip(VShapes.full)
                        .background(VColors.white),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(VShapes.lg)
                .background(VColors.white.copy(alpha = 0.08f))
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            HeroStat(
                value = attendanceRate?.let { "$it%" } ?: "—",
                label = "Attendance",
                modifier = Modifier.weight(1f),
            )
            HeroStat(
                value = avgGrade,
                label = "Avg Grade",
                modifier = Modifier.weight(1f),
            )
            HeroStat(
                value = pendingCount.toString(),
                label = "Pending",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HeroStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(VShapes.md)
            .background(VColors.white.copy(alpha = 0.06f))
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            value,
            style = VTypography.h2.copy(fontSize = 22.sp),
            color = VColors.white,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label.uppercase(),
            style = VTypography.caption.copy(fontSize = 9.sp),
            color = VColors.white.copy(alpha = 0.55f),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TRANSPORT CARD
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TransportTrackingCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.mintSoft)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(VColors.mint),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.Clock,
                contentDescription = null,
                tint = VColors.white,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Bus arriving soon",
                style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                color = VColors.ink,
            )
            Text(
                text = "GPS tracking active",
                style = VTypography.caption.copy(fontSize = 12.sp),
                color = VColors.ink2,
            )
        }
        Icon(
            imageVector = VIcons.ChevronRight,
            contentDescription = null,
            tint = VColors.ink3,
            modifier = Modifier.size(20.dp),
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// FILTER CHIPS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FilterChips() {
    var selected by remember { mutableStateOf("All") }
    val chips = listOf("All", "Academics", "Fees", "Attendance", "Transport", "Library")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(chips.size) { idx ->
            val label = chips[idx]
            val active = label == selected
            Box(
                modifier = Modifier
                    .clip(VShapes.full)
                    .background(if (active) VColors.ink else VColors.surfaceCard)
                    .border(1.dp, if (active) VColors.ink else VColors.line, VShapes.full)
                    .clickable { selected = label }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                    color = if (active) VColors.white else VColors.ink2,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION HEADER
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = VTypography.h3.copy(fontSize = 20.sp),
            color = VColors.ink,
            fontWeight = FontWeight.ExtraBold,
        )
        if (action != null) {
            Text(
                text = action,
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                color = VColors.violet,
                modifier = Modifier.clickable(onClick = onAction),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PRIORITY CAROUSEL
// ═══════════════════════════════════════════════════════════════════════════════

private data class PriorityItem(
    val id: String,
    val icon: ImageVector,
    val iconBg: Color,
    val title: String,
    val subtitle: String,
    val value: String,
    val badge: String,
    val badgeBg: Color,
    val urgency: Int,
    val onClick: () -> Unit,
)

@Composable
private fun rememberPriorityCards(
    feesDue: String,
    overdue: Int,
    attendanceRate: Int?,
    pendingCount: Int,
    unreadMessages: Int,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenAcademicsTab: (String) -> Unit,
    onOpenMessages: () -> Unit,
): List<PriorityItem> {
    return remember(feesDue, overdue, attendanceRate, pendingCount, unreadMessages) {
        val list = mutableListOf<PriorityItem>()
        val feesNumeric = feesDue.filter { it.isDigit() }.toIntOrNull() ?: 0

        list += PriorityItem(
            id = "fees",
            icon = VIcons.WalletPremium,
            iconBg = VColors.violet,
            title = "Fee Payment",
            subtitle = if (overdue > 0) "Overdue · $overdue dues" else "Q4 Tuition · Due soon",
            value = feesDue,
            badge = if (overdue > 0) "Pay Now" else "Due Soon",
            badgeBg = VColors.violet,
            urgency = if (overdue > 0) 100 else if (feesNumeric > 0) 60 else 20,
            onClick = onOpenFees,
        )

        list += PriorityItem(
            id = "attendance",
            icon = VIcons.Check,
            iconBg = VColors.mint,
            title = "Attendance",
            subtitle = "This month",
            value = attendanceRate?.let { "$it%" } ?: "—",
            badge = if ((attendanceRate ?: 100) < 75) "Low" else "On Track",
            badgeBg = if ((attendanceRate ?: 100) < 75) VColors.gold else VColors.mint,
            urgency = if ((attendanceRate ?: 100) < 75) 90 else 40,
            onClick = { onOpenAcademicsTab("Attendance") },
        )

        list += PriorityItem(
            id = "homework",
            icon = VIcons.ClipboardList,
            iconBg = VColors.gold,
            title = "Homework",
            subtitle = "Pending assignments",
            value = pendingCount.toString(),
            badge = if (pendingCount > 0) "Due Today" else "Done",
            badgeBg = if (pendingCount > 0) VColors.gold else VColors.mint,
            urgency = if (pendingCount > 0) 80 else 30,
            onClick = { onOpenAcademicsTab("Quizzes") },
        )

        list += PriorityItem(
            id = "messages",
            icon = VIcons.ChatPremium,
            iconBg = VColors.sky,
            title = "Messages",
            subtitle = "Unread from teachers",
            value = unreadMessages.toString(),
            badge = if (unreadMessages > 0) "Read" else "Inbox",
            badgeBg = VColors.sky,
            urgency = if (unreadMessages > 0) 70 else 10,
            onClick = onOpenMessages,
        )

        list.sortedByDescending { it.urgency }
    }
}

@Composable
private fun PriorityCarousel(cards: List<PriorityItem>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(cards.size) { idx ->
            val card = cards[idx]
            PriorityCard(card = card)
        }
    }
}

@Composable
private fun PriorityCard(card: PriorityItem) {
    Column(
        modifier = Modifier
            .width(240.dp)
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .clickable(onClick = card.onClick)
            .padding(18.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(VShapes.md)
                .background(card.iconBg)
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                card.icon,
                contentDescription = null,
                tint = VColors.white,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            card.title,
            style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 15.sp),
            color = VColors.ink,
        )
        Text(
            card.subtitle,
            style = VTypography.caption.copy(fontSize = 12.sp),
            color = VColors.ink2,
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                card.value,
                style = VTypography.h2.copy(fontSize = 24.sp),
                color = VColors.ink,
                fontWeight = FontWeight.ExtraBold,
            )
            Box(
                modifier = Modifier
                    .clip(VShapes.full)
                    .background(card.badgeBg)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            ) {
                Text(
                    card.badge,
                    style = VTypography.caption.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp),
                    color = VColors.white,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TODAY'S SCHEDULE
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TodayScheduleCard(
    periods: List<LivePeriod>,
    isLoading: Boolean,
    onOpenAcademics: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(20.dp),
    ) {
        val doneCount = periods.count { it.relation == -1 }
        val total = periods.size.coerceAtLeast(1)
        val progress = doneCount / total.toFloat()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(VShapes.full)
                    .background(VColors.lineSoft),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(4.dp)
                        .clip(VShapes.full)
                        .background(
                            Brush.horizontalGradient(listOf(VColors.violet, VColors.mint))
                        ),
                )
            }
            Text(
                text = "$doneCount of $total done",
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                color = VColors.ink2,
            )
        }

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                TeacherSpinner(24.dp)
            }
            periods.isEmpty() -> Text(
                text = "No classes scheduled today.",
                style = VTypography.caption,
                color = VColors.ink3,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                periods.forEach { period ->
                    ScheduleRow(period = period)
                }
            }
        }
    }
}

@Composable
private fun ScheduleRow(period: LivePeriod) {
    val isLive = period.relation == 0
    val isDone = period.relation == -1
    val status = when {
        isLive -> "Live"
        isDone -> "Done"
        else -> "Upcoming"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(if (isLive) VColors.violet else VColors.creamDeep)
            .clickable { /* opens academics */ }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(50.dp),
        ) {
            Text(
                text = period.startTime,
                style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp),
                color = if (isLive) VColors.white else VColors.ink,
            )
            Text(
                text = period.endTime,
                style = VTypography.caption.copy(fontSize = 10.sp),
                color = if (isLive) VColors.white.copy(alpha = 0.7f) else VColors.ink3,
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(36.dp)
                .background(if (isLive) VColors.white.copy(alpha = 0.2f) else VColors.line),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = period.subject,
                style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 15.sp),
                color = if (isLive) VColors.white else VColors.ink,
            )
            if (period.teacherName.isNotBlank()) {
                Text(
                    text = period.teacherName,
                    style = VTypography.caption.copy(fontSize = 12.sp),
                    color = if (isLive) VColors.white.copy(alpha = 0.8f) else VColors.ink2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (period.room.isNotBlank()) {
                Text(
                    text = "Room ${period.room}",
                    style = VTypography.caption.copy(fontSize = 10.sp),
                    color = if (isLive) VColors.white.copy(alpha = 0.6f) else VColors.ink3,
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(VShapes.full)
                .background(
                    when {
                        isLive -> VColors.white.copy(alpha = 0.2f)
                        isDone -> VColors.mintSoft
                        else -> VColors.violetSoft
                    }
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (isLive) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(VColors.mint),
                    )
                }
                Text(
                    status,
                    style = VTypography.caption.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp),
                    color = when {
                        isLive -> VColors.white
                        isDone -> VColors.success
                        else -> VColors.violet
                    },
                    letterSpacing = 0.5.sp,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TODAY'S SUMMARY
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TodaySummaryCard(
    summary: com.littlebridge.enrollplus.feature.parent.domain.model.ParentDailySummaryData?,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(20.dp),
    ) {
        val teacherEntries = summary?.entries?.filter { !it.isAiEstimated } ?: emptyList()
        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                TeacherSpinner(24.dp)
            }
            teacherEntries.isEmpty() -> Text(
                text = "No log updated by the teacher",
                style = VTypography.caption,
                color = VColors.ink3,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                teacherEntries.forEach { entry ->
                    SummaryEntryRow(entry = entry)
                }
            }
        }
    }
}

@Composable
private fun SummaryEntryRow(entry: ParentDailyLogEntryDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.creamDeep)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(VShapes.md)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.Bookmark,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = entry.subject,
                    style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 15.sp),
                    color = VColors.ink,
                )
            }
            if (entry.summaryText.isNotBlank()) {
                Text(
                    text = entry.summaryText,
                    style = VTypography.caption.copy(fontSize = 12.sp),
                    color = VColors.ink2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = "Coverage ${entry.coveragePct}%",
                style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                color = VColors.ink3,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SCHOOL UPDATES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun UpdatesCard(
    announcements: List<com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncement>,
    isLoading: Boolean,
    onEventClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(20.dp),
    ) {
        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                TeacherSpinner(24.dp)
            }
            announcements.isEmpty() -> Text(
                text = "No announcements yet",
                style = VTypography.caption,
                color = VColors.ink3,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                announcements.take(3).forEachIndexed { idx, announcement ->
                    UpdateItem(
                        announcement = announcement,
                        isFirst = idx == 0,
                        onEventClick = onEventClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun UpdateItem(
    announcement: com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncement,
    isFirst: Boolean,
    onEventClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.creamDeep)
            .clickable { onEventClick() }
            .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(VShapes.md)
                    .background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    VIcons.Megaphone,
                    contentDescription = null,
                    tint = VColors.violet,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = announcement.category.ifBlank { "Notice" },
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                        color = VColors.violet,
                    )
                    if (announcement.date.isNotBlank()) {
                        Text(
                            text = "·",
                            style = VTypography.caption.copy(fontSize = 11.sp),
                            color = VColors.ink3,
                        )
                        Text(
                            text = announcement.date,
                            style = VTypography.caption.copy(fontSize = 11.sp),
                            color = VColors.ink3,
                        )
                    }
                }
                Text(
                    text = announcement.title,
                    style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                    color = VColors.ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = announcement.description,
                    style = VTypography.caption.copy(fontSize = 12.sp),
                    color = VColors.ink2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (isFirst) {
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 54.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(VShapes.full)
                        .background(VColors.violet)
                        .clickable { onEventClick() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        "Register",
                        style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                        color = VColors.white,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PREMIUM FEATURES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun PremiumFeaturesGrid(
    onOpenTutor: () -> Unit,
    onOpenReport: () -> Unit,
    onOpenPews: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenCalendar: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PremiumFeatureCard(
                icon = VIcons.Sparkles,
                iconBg = VColors.violet,
                label = "AI Tutor",
                modifier = Modifier.weight(1f),
                onClick = onOpenTutor,
            )
            PremiumFeatureCard(
                icon = VIcons.FileText,
                iconBg = VColors.mint,
                label = "AI Report",
                modifier = Modifier.weight(1f),
                onClick = onOpenReport,
            )
            PremiumFeatureCard(
                icon = VIcons.Activity,
                iconBg = VColors.coral,
                label = "PEWS",
                modifier = Modifier.weight(1f),
                onClick = onOpenPews,
            )
            PremiumFeatureCard(
                icon = VIcons.BookOpen,
                iconBg = VColors.gold,
                label = "Library",
                modifier = Modifier.weight(1f),
                onClick = onOpenLibrary,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PremiumFeatureCard(
                icon = VIcons.Calendar,
                iconBg = VColors.sky,
                label = "Calendar",
                modifier = Modifier.weight(1f),
                onClick = onOpenCalendar,
            )
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun PremiumFeatureCard(
    icon: ImageVector,
    iconBg: Color,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(VShapes.md)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = VColors.white,
                modifier = Modifier.size(22.dp),
            )
        }
        Text(
            label,
            style = VTypography.caption.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp),
            color = VColors.ink,
            textAlign = TextAlign.Center,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// UTILS
// ═══════════════════════════════════════════════════════════════════════════════

private fun markDisplayGrade(mark: ParentMarkDto?): String {
    if (mark == null) return "—"
    val pct = if (mark.maxMarks > 0) ((mark.marks ?: 0.0) / mark.maxMarks * 100).roundToInt() else 0
    return when {
        pct >= 90 -> "A+"
        pct >= 80 -> "A"
        pct >= 70 -> "B+"
        pct >= 60 -> "B"
        pct >= 50 -> "C"
        else -> "D"
    }
}
