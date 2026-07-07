package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import com.littlebridge.enrollplus.feature.parent.domain.model.FeeData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentAttendanceData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentDailySummaryData
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
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentHomeScreenV2(
    modifier: Modifier = Modifier,
    onDiscoverSchools: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenFees: () -> Unit = {},
    onOpenAcademics: () -> Unit = {},
    onOpenMessages: () -> Unit = {},
    onOpenPulse: () -> Unit = {},
    onOpenTransport: () -> Unit = {},
    onOpenTutor: () -> Unit = {},
    onOpenScholarships: () -> Unit = {},
    onOpenIdCard: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    onOpenEvents: () -> Unit = {},
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

    DisposableEffect(Unit) {
        onDispose { transportViewModel.stopPolling() }
    }

    ParentHomeContent(
        state = state,
        academics = academics,
        announcements = announcements,
        track = track,
        transportEnrolled = transport.childRoute != null,
        onRetry = viewModel::load,
        onSelectChild = viewModel::selectChild,
        onOpenNotifications = onOpenNotifications,
        onOpenFees = onOpenFees,
        onOpenAcademics = onOpenAcademics,
        onOpenMessages = onOpenMessages,
        onOpenTransport = onOpenTransport,
        onOpenTutor = onOpenTutor,
        onOpenLeave = { /* TODO: wire leave overlay if available */ },
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
    onRetry: () -> Unit,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenTutor: () -> Unit,
    onOpenLeave: () -> Unit,
    onDiscoverSchools: () -> Unit,
    unreadNotificationsCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 120.dp),
    ) {
        HomeHeader(
            parentName = track.accountName.ifBlank { "Parent" },
            childName = state.selectedChild?.name?.ifBlank { null } ?: "Your Child",
            children = state.children,
            selectedChild = state.selectedChild,
            onSelectChild = onSelectChild,
            onOpenNotifications = onOpenNotifications,
            unreadNotificationsCount = unreadNotificationsCount,
        )

        when {
            state.isLoading && state.children.isEmpty() -> HomeSkeleton()
            state.error != null && state.children.isEmpty() -> HomeError(message = state.error ?: "", onRetry = onRetry)
            state.children.isEmpty() -> HomeEmpty(onDiscoverSchools = onDiscoverSchools)
            else -> HomeLoaded(
                state = state,
                academics = academics,
                announcements = announcements,
                transportEnrolled = transportEnrolled,
                onOpenFees = onOpenFees,
                onOpenAcademics = onOpenAcademics,
                onOpenMessages = onOpenMessages,
                onOpenTransport = onOpenTransport,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HEADER — Enroll+ branding + working child selector + notification bell
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HomeHeader(
    parentName: String,
    childName: String,
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    unreadNotificationsCount: Int,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Enroll+",
                style = VTypography.wordmark.copy(
                    fontSize = 22.sp,
                    color = VColors.violet,
                ),
                fontWeight = FontWeight.ExtraBold,
            )

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(VShapes.full)
                    .clickable(onClick = onOpenNotifications),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = VIcons.BellStroke,
                    contentDescription = "Notifications",
                    tint = VColors.ink,
                    modifier = Modifier.size(24.dp),
                )
                if (unreadNotificationsCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(VColors.error)
                            .border(2.dp, VColors.cream, CircleShape),
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Hi ${parentName.takeWhile { it != ' ' }.ifBlank { parentName }}",
            style = VTypography.caption.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = VColors.violet,
        )

        Spacer(Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = children.size > 1,
            ) { expanded = true },
        ) {
            Text(
                text = "here's",
                style = VTypography.h2.copy(fontSize = 28.sp),
                color = VColors.ink,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "$childName's",
                style = VTypography.h2.copy(fontSize = 28.sp),
                color = VColors.violet,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "day",
                style = VTypography.h2.copy(fontSize = 28.sp),
                color = VColors.ink,
                fontWeight = FontWeight.ExtraBold,
            )
            if (children.size > 1) {
                Icon(
                    imageVector = VIcons.ChevronDown,
                    contentDescription = "Select child",
                    tint = VColors.ink3,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = VColors.white,
            shape = VShapes.lg,
        ) {
            children.forEach { child ->
                DropdownMenuItem(
                    text = {
                        Text(
                            child.name,
                            style = VTypography.body,
                            color = if (child.id == selectedChild?.id) VColors.violet else VColors.ink,
                            fontWeight = if (child.id == selectedChild?.id) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    onClick = {
                        onSelectChild(child.id)
                        expanded = false
                    },
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
                imageVector = VIcons.HomePremium,
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
    transportEnrolled: Boolean,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenTransport: () -> Unit,
) {
    val child = state.selectedChild
    val childName = child?.name?.ifBlank { null } ?: "Your Child"
    val attendanceRate = state.attendance?.attendanceRate
    val feesDue = state.fees?.outstandingFees ?: "₹0"
    val pendingCount = academics.quizzes.size.coerceAtLeast(0)
    val markPct = state.latestMark?.let { m ->
        if (m.maxMarks > 0) ((m.marks ?: 0.0) / m.maxMarks * 100).roundToInt() else null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HeroCard(
            childName = childName,
            level = child?.currentLevel ?: 0,
            attendanceRate = attendanceRate,
            avgGrade = markDisplayGrade(state.latestMark),
            pendingCount = pendingCount,
            onOpenTransport = onOpenTransport,
        )

        if (transportEnrolled) {
            TransportTrackingCard(onClick = onOpenTransport)
        }

        FilterChips()

        SectionHeader(title = "Priority", action = "See all", onAction = { })
        PriorityCarousel(
            feesDue = feesDue,
            attendanceRate = attendanceRate,
            pendingCount = pendingCount,
            unreadMessages = 2,
            onOpenFees = onOpenFees,
            onOpenAcademics = onOpenAcademics,
            onOpenMessages = onOpenMessages,
        )

        SectionHeader(title = "Quick Stats")
        QuickStatsRow(
            feesDue = feesDue,
            attendanceRate = attendanceRate,
            pendingCount = pendingCount,
        )

        SectionHeader(title = "Today's Schedule", action = "Full timetable", onAction = onOpenAcademics)
        TodayScheduleCard(
            summary = academics.dailySummary,
            isLoading = academics.dailySummaryLoading,
        )

        SectionHeader(title = "Today's Summary")
        TodaySummaryCard(
            attendanceRate = attendanceRate,
            markPct = markPct,
            feesDue = feesDue,
            pendingCount = pendingCount,
        )

        SectionHeader(title = "School Updates", action = "All", onAction = { })
        UpdatesCard(
            announcements = announcements.announcements,
            isLoading = announcements.isLoading,
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
    level: Int,
    attendanceRate: Int?,
    avgGrade: String,
    pendingCount: Int,
    onOpenTransport: () -> Unit,
) {
    Box(
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
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LivePill()
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(VColors.white.copy(alpha = 0.15f))
                        .clickable(onClick = onOpenTransport),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = VIcons.User,
                        contentDescription = "Profile",
                        tint = VColors.white,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(VShapes.xl)
                        .background(VColors.white.copy(alpha = 0.2f))
                        .border(2.dp, VColors.white.copy(alpha = 0.25f), VShapes.xl),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = childName.take(1).uppercase(),
                        style = VTypography.h2.copy(fontSize = 26.sp),
                        color = VColors.white,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Column {
                    Text(
                        childName,
                        style = VTypography.h3.copy(fontSize = 20.sp),
                        color = VColors.white,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (level > 0) "Level $level" else "Student",
                        style = VTypography.caption.copy(fontSize = 13.sp),
                        color = VColors.white.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
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
}

@Composable
private fun LivePill() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(VShapes.full)
            .background(VColors.white.copy(alpha = 0.15f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(VColors.mint),
        )
        Text(
            text = "LIVE · IN SCHOOL",
            style = VTypography.caption.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.6.sp,
            ),
            color = VColors.white,
        )
    }
}

@Composable
private fun HeroStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(VShapes.md)
            .background(VColors.white.copy(alpha = 0.06f))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            value,
            style = VTypography.h2.copy(fontSize = 24.sp),
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
                text = "Bus arriving in 8 minutes",
                style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                color = VColors.ink,
            )
            Text(
                text = "Route 12 · GPS tracking active",
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

@Composable
private fun PriorityCarousel(
    feesDue: String,
    attendanceRate: Int?,
    pendingCount: Int,
    unreadMessages: Int,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenMessages: () -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            PriorityCard(
                icon = VIcons.WalletPremium,
                iconBg = VColors.violet,
                title = "Fee Payment",
                subtitle = "Q4 Tuition · Due soon",
                value = feesDue,
                badge = "Pay Now",
                badgeBg = VColors.violet,
                onClick = onOpenFees,
            )
        }
        item {
            PriorityCard(
                icon = VIcons.Check,
                iconBg = VColors.mint,
                title = "Attendance",
                subtitle = "This month",
                value = attendanceRate?.let { "$it%" } ?: "—",
                badge = "On Track",
                badgeBg = VColors.mint,
                onClick = onOpenAcademics,
            )
        }
        item {
            PriorityCard(
                icon = VIcons.ClipboardList,
                iconBg = VColors.gold,
                title = "Homework",
                subtitle = "Pending assignments",
                value = pendingCount.toString(),
                badge = "Due Today",
                badgeBg = VColors.gold,
                onClick = onOpenAcademics,
            )
        }
        item {
            PriorityCard(
                icon = VIcons.ChatPremium,
                iconBg = VColors.sky,
                title = "Messages",
                subtitle = "Unread from teachers",
                value = unreadMessages.toString(),
                badge = "Read",
                badgeBg = VColors.sky,
                onClick = onOpenMessages,
            )
        }
    }
}

@Composable
private fun PriorityCard(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    value: String,
    badge: String,
    badgeBg: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .clickable(onClick = onClick)
            .padding(20.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(VShapes.md)
                .background(iconBg)
                .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = VColors.white,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            title,
            style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
            color = VColors.ink,
        )
        Text(
            subtitle,
            style = VTypography.caption.copy(fontSize = 13.sp),
            color = VColors.ink2,
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                value,
                style = VTypography.h2.copy(fontSize = 26.sp),
                color = VColors.ink,
                fontWeight = FontWeight.ExtraBold,
            )
            Box(
                modifier = Modifier
                    .clip(VShapes.full)
                    .background(badgeBg)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    badge,
                    style = VTypography.caption.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp),
                    color = VColors.white,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUICK STATS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuickStatsRow(
    feesDue: String,
    attendanceRate: Int?,
    pendingCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        QuickStatCard(
            icon = VIcons.WalletPremium,
            iconBg = VColors.violetSoft,
            iconColor = VColors.violet,
            value = feesDue,
            label = "Fees Due",
            modifier = Modifier.weight(1f),
        )
        QuickStatCard(
            icon = VIcons.Check,
            iconBg = VColors.mintSoft,
            iconColor = VColors.mint,
            value = attendanceRate?.let { "$it%" } ?: "—",
            label = "Attendance",
            modifier = Modifier.weight(1f),
        )
        QuickStatCard(
            icon = VIcons.ClipboardList,
            iconBg = VColors.goldSoft,
            iconColor = VColors.gold,
            value = pendingCount.toString(),
            label = "Homework",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun QuickStatCard(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(VShapes.sm)
                .background(iconBg)
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            value,
            style = VTypography.h3.copy(fontSize = 20.sp),
            color = VColors.ink,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            label.uppercase(),
            style = VTypography.caption.copy(fontSize = 10.sp),
            color = VColors.ink3,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TODAY'S SCHEDULE
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TodayScheduleCard(
    summary: ParentDailySummaryData?,
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
        val entries = summary?.entries ?: emptyList()
        val doneCount = entries.count { it.coveragePct >= 80 }
        val total = entries.size.coerceAtLeast(1)
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
                CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(24.dp))
            }
            entries.isEmpty() -> Text(
                text = "No classes scheduled today.",
                style = VTypography.caption,
                color = VColors.ink3,
            )
            else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                entries.take(4).forEachIndexed { idx, entry ->
                    ScheduleRow(
                        entry = entry,
                        status = when (idx) {
                            0 -> "Live"
                            1 -> "Next"
                            else -> null
                        },
                        isLive = idx == 0,
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleRow(
    entry: ParentDailyLogEntryDto,
    status: String?,
    isLive: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(if (isLive) VColors.violet else VColors.creamDeep)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(52.dp),
        ) {
            Text(
                text = "${entry.coveragePct}%",
                style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
                color = if (isLive) VColors.white else VColors.ink,
            )
            Text(
                text = "Coverage",
                style = VTypography.caption.copy(fontSize = 9.sp),
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
                text = entry.subject,
                style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 15.sp),
                color = if (isLive) VColors.white else VColors.ink,
            )
            if (entry.summaryText.isNotBlank()) {
                Text(
                    text = entry.summaryText,
                    style = VTypography.caption.copy(fontSize = 12.sp),
                    color = if (isLive) VColors.white.copy(alpha = 0.8f) else VColors.ink2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (status != null) {
            Box(
                modifier = Modifier
                    .clip(VShapes.full)
                    .background(
                        if (isLive) VColors.white.copy(alpha = 0.2f) else VColors.violetSoft
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
                        color = if (isLive) VColors.white else VColors.violet,
                        letterSpacing = 0.5.sp,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TODAY'S SUMMARY
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TodaySummaryCard(
    attendanceRate: Int?,
    markPct: Int?,
    feesDue: String,
    pendingCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        SummaryItem(
            icon = VIcons.Check,
            iconColor = VColors.mint,
            value = attendanceRate?.let { "$it%" } ?: "—",
            label = "Attendance",
        )
        SummaryItem(
            icon = VIcons.Star,
            iconColor = VColors.gold,
            value = markPct?.let { "$it%" } ?: "—",
            label = "Latest Marks",
        )
        SummaryItem(
            icon = VIcons.WalletPremium,
            iconColor = VColors.violet,
            value = feesDue,
            label = "Fees Due",
        )
        SummaryItem(
            icon = VIcons.ClipboardList,
            iconColor = VColors.coral,
            value = pendingCount.toString(),
            label = "Pending",
        )
    }
}

@Composable
private fun SummaryItem(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            value,
            style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 15.sp),
            color = VColors.ink,
        )
        Text(
            label,
            style = VTypography.caption.copy(fontSize = 10.sp),
            color = VColors.ink3,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SCHOOL UPDATES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun UpdatesCard(
    announcements: List<com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncement>,
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
        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(24.dp))
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
                        showActions = idx == 0,
                    )
                }
            }
        }
    }
}

@Composable
private fun UpdateItem(
    announcement: com.littlebridge.enrollplus.feature.parent.presentation.ParentAnnouncement,
    showActions: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.creamDeep)
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
                Text(
                    text = "School Admin",
                    style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                    color = VColors.ink3,
                )
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
        if (showActions) {
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 54.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(VShapes.full)
                        .background(VColors.violet)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        "Register",
                        style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                        color = VColors.white,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(VShapes.full)
                        .background(VColors.lineSoft)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        "Dismiss",
                        style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
                        color = VColors.ink2,
                    )
                }
            }
        }
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

private fun FeeData?.isAllClear(): Boolean = this?.outstandingFees?.let { it == "₹0" || it == "0" || it.isBlank() } ?: true

private fun FeeData?.formattedOutstanding(): String = this?.outstandingFees ?: "₹0"
