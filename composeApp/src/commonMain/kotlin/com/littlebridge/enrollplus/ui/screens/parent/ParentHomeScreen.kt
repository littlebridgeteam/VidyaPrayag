package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardAlertDto
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.FeeData
import com.littlebridge.enrollplus.feature.parent.presentation.AttendanceDayState
import com.littlebridge.enrollplus.feature.parent.presentation.CoveredUnit
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.TodayAttendance
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.util.formatDateDisplay
import com.littlebridge.enrollplus.util.todayIso
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentHomeScreen(
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
    dashboardViewModel: ParentDashboardViewModel = koinViewModel(),
    messageViewModel: ParentMessageViewModel = koinViewModel(),
) {
    val state by dashboardViewModel.state.collectAsState()
    val messageState by messageViewModel.state.collectAsState()
    val unreadCount by messageViewModel.unreadCount.collectAsState()

    Scaffold(
        containerColor = VColors.cream,
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        when {
            state.isLoading && state.children.isEmpty() -> {
                LoadingState(Modifier.padding(padding))
            }
            state.error != null && state.children.isEmpty() -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = { dashboardViewModel.load() },
                    modifier = Modifier.padding(padding),
                )
            }
            state.children.isEmpty() && !state.isLoading -> {
                EmptyState(
                    onDiscoverSchools = onDiscoverSchools,
                    modifier = Modifier.padding(padding),
                )
            }
            else -> {
                ContentState(
                    state = state,
                    messageThreads = messageState.threads.take(3),
                    unreadCount = unreadCount,
                    onOpenNotifications = onOpenNotifications,
                    onOpenFees = onOpenFees,
                    onOpenAcademics = onOpenAcademics,
                    onOpenMessages = onOpenMessages,
                    onOpenEvents = onOpenEvents,
                    onOpenScholarships = onOpenScholarships,
                    onOpenTransport = onOpenTransport,
                    onOpenTutor = onOpenTutor,
                    onOpenPulse = onOpenPulse,
                    onOpenLibrary = onOpenLibrary,
                    onOpenIdCard = onOpenIdCard,
                    onSelectChild = { dashboardViewModel.selectChild(it) },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CONTENT
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ContentState(
    state: ParentDashboardState,
    messageThreads: List<com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto>,
    unreadCount: Int,
    onOpenNotifications: () -> Unit,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenTutor: () -> Unit,
    onOpenPulse: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenIdCard: () -> Unit,
    onSelectChild: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TopSection(
            greeting = state.greeting,
            child = state.selectedChild,
            today = state.today,
            attendanceRate = state.attendance?.attendanceRate ?: 0,
            overallProgress = state.selectedChild?.overallProgress ?: 0.0,
            unreadCount = unreadCount,
            children = state.children,
            selectedChildId = state.selectedChildId,
            onOpenNotifications = onOpenNotifications,
            onOpenAcademics = onOpenAcademics,
            onSelectChild = onSelectChild,
        )

        if (state.todayPeriods.isNotEmpty()) {
            TimelineSection(
                periods = state.todayPeriods,
                schoolDayEnded = state.schoolDayEnded,
            )
        }

        InsightRow(
            attendanceRate = state.attendance?.attendanceRate ?: 0,
            fees = state.fees,
            onOpenFees = onOpenFees,
            onOpenAcademics = onOpenAcademics,
        )

        QuickAccessGrid(
            onOpenAcademics = onOpenAcademics,
            onOpenMessages = onOpenMessages,
            onOpenTransport = onOpenTransport,
            onOpenTutor = onOpenTutor,
            onOpenScholarships = onOpenScholarships,
            onOpenIdCard = onOpenIdCard,
            onOpenLibrary = onOpenLibrary,
            onOpenEvents = onOpenEvents,
            latestMark = state.latestMark,
            unreadCount = unreadCount,
        )

        MessagesPreview(
            threads = messageThreads,
            onOpenMessages = onOpenMessages,
        )

        Spacer(Modifier.height(80.dp))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TOP SECTION — greeting + hero + child switcher + bell (all integrated)
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TopSection(
    greeting: String,
    child: DashboardChildSummary?,
    today: TodayAttendance,
    attendanceRate: Int,
    overallProgress: Double,
    unreadCount: Int,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    onOpenNotifications: () -> Unit,
    onOpenAcademics: () -> Unit,
    onSelectChild: (String) -> Unit,
) {
    if (child == null) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VColors.violet.copy(alpha = 0.08f),
                            VColors.cream,
                        ),
                        startY = 0f,
                        endY = size.height * 0.6f,
                    ),
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 0.dp),
        ) {
            // ── Row 1: Child switcher (left) + Bell (right) ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChildSwitcher(
                    child = child,
                    children = children,
                    selectedChildId = selectedChildId,
                    onSelectChild = onSelectChild,
                )
                NotificationBell(unreadCount = unreadCount, onClick = onOpenNotifications)
            }

            Spacer(Modifier.height(20.dp))

            // ── Greeting ──
            Text(
                text = greeting.ifBlank { "Hello" },
                style = VTypography.caption,
                color = VColors.ink3,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formatDateDisplay(todayIso()),
                style = VTypography.h2,
                color = VColors.ink,
                modifier = Modifier.padding(top = 2.dp),
            )

            Spacer(Modifier.height(20.dp))

            // ── Hero Card ──
            HeroCard(
                child = child,
                today = today,
                attendanceRate = attendanceRate,
                overallProgress = overallProgress,
                onOpenAcademics = onOpenAcademics,
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Child Switcher ──

@Composable
private fun ChildSwitcher(
    child: DashboardChildSummary,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    onSelectChild: (String) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.96f else 1f

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(VShapes.md)
                .background(VColors.white)
                .shadow(1.dp, VShapes.md, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) { if (children.size > 1) open = true }
                .padding(start = 6.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
        ) {
            ChildAvatar(child, 28.dp)
            Spacer(Modifier.width(8.dp))
            Text(
                text = child.name,
                style = VTypography.label,
                color = VColors.ink,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (children.size > 1) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = "Switch child",
                    tint = VColors.ink3,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        DropdownMenu(
            expanded = open,
            onDismissRequest = { open = false },
        ) {
            children.forEach { c ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ChildAvatar(c, 32.dp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = c.name,
                                    style = VTypography.body,
                                    color = VColors.ink,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "Grade ${c.currentLevel}",
                                    style = VTypography.caption,
                                    color = VColors.ink3,
                                )
                            }
                            if (c.id == selectedChildId) {
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = VColors.violet,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    },
                    onClick = {
                        onSelectChild(c.id)
                        open = false
                    },
                )
            }
        }
    }
}

// ── Notification Bell ──

@Composable
private fun NotificationBell(unreadCount: Int, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.92f else 1f

    Box(
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(1.dp, VShapes.full, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
            .clip(VShapes.full)
            .background(VColors.white)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = "Notifications",
            tint = VColors.ink2,
            modifier = Modifier.size(18.dp),
        )
        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(15.dp)
                    .clip(VShapes.full)
                    .background(VColors.coral),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                    style = VTypography.caption,
                    color = VColors.white,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ── Hero Card ──

@Composable
private fun HeroCard(
    child: DashboardChildSummary,
    today: TodayAttendance,
    attendanceRate: Int,
    overallProgress: Double,
    onOpenAcademics: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.98f else 1f
    val progressPct = (overallProgress * 100).toInt()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(6.dp, VShapes.xl, ambientColor = VColors.violet.copy(alpha = 0.08f), spotColor = VColors.ink.copy(alpha = 0.10f))
            .clip(VShapes.xl)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        VColors.violet,
                        VColors.violetHover,
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite,
                ),
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onOpenAcademics() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // Top row: avatar + name/grade + arrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(VShapes.full)
                        .background(VColors.white.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = child.name.split(" ").take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" },
                        style = VTypography.h3,
                        color = VColors.white,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = child.name,
                        style = VTypography.h3,
                        color = VColors.white,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Grade ${child.currentLevel}",
                        style = VTypography.caption,
                        color = VColors.white.copy(alpha = 0.7f),
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "View academics",
                    tint = VColors.white.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.height(16.dp))

            // Stat chips row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroChip(
                    label = today.label.ifBlank { today.state.name },
                    value = when (today.state) {
                        AttendanceDayState.Present -> "Present"
                        AttendanceDayState.Late -> "Late"
                        AttendanceDayState.Absent -> "Absent"
                        AttendanceDayState.Holiday -> "Holiday"
                        AttendanceDayState.Vacation -> "Vacation"
                        AttendanceDayState.Sunday -> "Sunday"
                        AttendanceDayState.NoData -> "—"
                    },
                )
                if (attendanceRate > 0) {
                    HeroChip(label = "Attendance", value = "$attendanceRate%")
                }
                if (progressPct > 0) {
                    HeroChip(label = "Progress", value = "$progressPct%")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Progress bar
            if (progressPct > 0) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Overall Progress",
                            style = VTypography.caption,
                            color = VColors.white.copy(alpha = 0.6f),
                        )
                        Text(
                            text = "$progressPct%",
                            style = VTypography.caption,
                            color = VColors.white,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(VShapes.full)
                            .background(VColors.white.copy(alpha = 0.15f)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressPct / 100f)
                                .height(5.dp)
                                .clip(VShapes.full)
                                .background(VColors.white),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroChip(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(VShapes.sm)
            .background(VColors.white.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = VTypography.caption,
            color = VColors.white.copy(alpha = 0.5f),
        )
        Text(
            text = value,
            style = VTypography.label,
            color = VColors.white,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ChildAvatar(child: DashboardChildSummary, size: androidx.compose.ui.unit.Dp) {
    val initials = child.name.split(" ").take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
    Box(
        modifier = Modifier
            .size(size)
            .clip(VShapes.full)
            .background(VColors.violetSoft),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = VTypography.h3,
            color = VColors.violet,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AttendanceTag(today: TodayAttendance) {
    val (bg, fg, text) = when (today.state) {
        AttendanceDayState.Present -> Triple(VColors.mintSoft, VColors.success, "Present")
        AttendanceDayState.Late -> Triple(VColors.goldSoft, VColors.ink, "Late")
        AttendanceDayState.Absent -> Triple(VColors.coralSoft, VColors.coral, "Absent")
        AttendanceDayState.Holiday -> Triple(VColors.skySoft, VColors.ink, "Holiday")
        AttendanceDayState.Vacation -> Triple(VColors.skySoft, VColors.ink, today.label.ifBlank { "Vacation" })
        AttendanceDayState.Sunday -> Triple(VColors.surfaceTint, VColors.ink2, "Sunday")
        AttendanceDayState.NoData -> Triple(VColors.surfaceTint, VColors.ink2, "Not Marked")
    }
    TagPill(text = text, bg = bg, fg = fg)
}

@Composable
private fun TagPill(text: String, bg: androidx.compose.ui.graphics.Color, fg: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(VShapes.full)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = VTypography.caption,
            color = fg,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TIMELINE
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TimelineSection(
    periods: List<LivePeriod>,
    schoolDayEnded: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (schoolDayEnded) "Today's Summary" else "Today's Schedule",
                style = VTypography.caption,
                color = VColors.ink3,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${periods.size} classes",
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }
        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, VShapes.lg, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
                .clip(VShapes.lg)
                .background(VColors.white)
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
        ) {
            Column {
                periods.forEachIndexed { index, period ->
                    TimelineRow(
                        period = period,
                        isLast = index == periods.lastIndex,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(period: LivePeriod, isLast: Boolean) {
    val (dotColor, dotBorder, statusBg, statusFg, statusText) = when (period.relation) {
        -1 -> Quintet(VColors.mint, VColors.mint, VColors.mintSoft, VColors.success, "Done")
        0 -> Quintet(VColors.violet, VColors.violet, VColors.violetSoft, VColors.violet, "Now")
        else -> Quintet(VColors.white, VColors.ink3, VColors.surfaceTint, VColors.ink3, "Next")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(VShapes.full)
                    .background(dotColor),
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(VColors.lineSoft),
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = period.startTime,
            style = VTypography.caption,
            color = VColors.ink2,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(44.dp),
        )

        Spacer(Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = period.subject,
                style = VTypography.body,
                color = VColors.ink,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (period.teacherName.isNotBlank()) {
                Text(
                    text = period.teacherName,
                    style = VTypography.caption,
                    color = VColors.ink3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(VShapes.full)
                .background(statusBg)
                .padding(horizontal = 8.dp, vertical = 3.dp),
        ) {
            Text(
                text = statusText,
                style = VTypography.caption,
                color = statusFg,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// INSIGHT ROW
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun InsightRow(
    attendanceRate: Int,
    fees: FeeData?,
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        InsightCard(
            icon = Icons.Filled.Check,
            iconBg = VColors.mintSoft,
            iconTint = VColors.success,
            value = if (attendanceRate > 0) "$attendanceRate%" else "--",
            label = "Attendance",
            sub = "This term",
            accentBar = VColors.mint,
            onClick = onOpenAcademics,
            modifier = Modifier.weight(1f),
        )
        InsightCard(
            icon = Icons.Filled.Payment,
            iconBg = VColors.coralSoft,
            iconTint = VColors.coral,
            value = fees?.outstandingFees?.takeIf { it.isNotBlank() } ?: "₹0",
            label = "Fees Due",
            sub = fees?.takeIf { it.overdueCount > 0 }?.let { "Overdue" } ?: "Clear",
            accentBar = if (fees?.overdueCount?.let { it > 0 } == true) VColors.coral else VColors.mint,
            onClick = onOpenFees,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun InsightCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: androidx.compose.ui.graphics.Color,
    iconTint: androidx.compose.ui.graphics.Color,
    value: String,
    label: String,
    sub: String,
    accentBar: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.97f else 1f

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(2.dp, VShapes.lg, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
            .clip(VShapes.lg)
            .background(VColors.white)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(VShapes.sm)
                        .background(iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = value,
                style = VTypography.h3,
                color = VColors.ink,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = label,
                style = VTypography.caption,
                color = VColors.ink3,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = sub,
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(3.dp)
                .background(accentBar.copy(alpha = 0.6f)),
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// QUICK ACCESS GRID
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuickAccessGrid(
    onOpenAcademics: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenTutor: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenIdCard: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenEvents: () -> Unit,
    latestMark: com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto?,
    unreadCount: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            text = "Quick Access",
            style = VTypography.caption,
            color = VColors.ink3,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))

        val tiles = listOf(
            TileData("Academics", Icons.Filled.School, VColors.violetSoft, VColors.violet,
                latestMark?.let { "${it.subject}: ${it.marks?.toInt() ?: 0}/${it.maxMarks}" } ?: "View progress",
                null, onOpenAcademics),
            TileData("Messages", Icons.Filled.Send, VColors.skySoft, VColors.sky,
                if (unreadCount > 0) "$unreadCount unread" else "No new messages",
                if (unreadCount > 0) unreadCount.toString() else null, onOpenMessages),
            TileData("Transport", Icons.Filled.Send, VColors.mintSoft, VColors.success,
                "Track bus", null, onOpenTransport),
            TileData("AI Tutor", Icons.Filled.MenuBook, VColors.violetSoft, VColors.violet,
                "Session available", null, onOpenTutor),
            TileData("Scholarships", Icons.Filled.ReceiptLong, VColors.goldSoft, VColors.gold,
                "Apply now", null, onOpenScholarships),
            TileData("ID Card", Icons.Filled.Badge, VColors.surfaceTint, VColors.ink2,
                "View QR", null, onOpenIdCard),
            TileData("Library", Icons.Filled.LocalLibrary, VColors.skySoft, VColors.sky,
                "Books issued", null, onOpenLibrary),
            TileData("Events", Icons.Filled.Event, VColors.violetSoft, VColors.violet,
                "Upcoming", null, onOpenEvents),
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            tiles.chunked(2).forEach { rowTiles ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    rowTiles.forEach { tile ->
                        AccessTile(tile, modifier = Modifier.weight(1f))
                    }
                    if (rowTiles.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private data class TileData(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconBg: androidx.compose.ui.graphics.Color,
    val iconTint: androidx.compose.ui.graphics.Color,
    val subText: String,
    val badge: String?,
    val onClick: () -> Unit,
)

@Composable
private fun AccessTile(tile: TileData, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.97f else 1f

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(2.dp, VShapes.lg, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
            .clip(VShapes.lg)
            .background(VColors.white)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { tile.onClick() }
            .padding(14.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(VShapes.sm)
                        .background(tile.iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = tile.icon,
                        contentDescription = null,
                        tint = tile.iconTint,
                        modifier = Modifier.size(16.dp),
                    )
                }
                if (tile.badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(VShapes.full)
                            .background(VColors.coralSoft)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = tile.badge,
                            style = VTypography.caption,
                            color = VColors.coral,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = tile.label,
                style = VTypography.body,
                color = VColors.ink,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = tile.subText,
                style = VTypography.caption,
                color = VColors.ink3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MESSAGES PREVIEW
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun MessagesPreview(
    threads: List<com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto>,
    onOpenMessages: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Messages",
                style = VTypography.caption,
                color = VColors.ink3,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "View all",
                style = VTypography.caption,
                color = VColors.violet,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(VShapes.sm)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onOpenMessages() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }

        if (threads.isEmpty()) {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, VShapes.lg, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
                    .clip(VShapes.lg)
                    .background(VColors.white)
                    .padding(16.dp),
            ) {
                Text(
                    text = "No messages yet",
                    style = VTypography.bodySmall,
                    color = VColors.ink3,
                )
            }
        } else {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, VShapes.lg, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
                    .clip(VShapes.lg)
                    .background(VColors.white),
            ) {
                Column {
                    threads.forEachIndexed { index, thread ->
                        MessageRow(
                            thread = thread,
                            onClick = onOpenMessages,
                            isLast = index == threads.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageRow(
    thread: com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto,
    onClick: () -> Unit,
    isLast: Boolean,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.99f else 1f

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(VShapes.full)
                    .background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = thread.senderName.firstOrNull()?.uppercase() ?: "",
                    style = VTypography.label,
                    color = VColors.violet,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = thread.senderName,
                    style = VTypography.body,
                    color = VColors.ink,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = thread.lastMessage,
                    style = VTypography.caption,
                    color = VColors.ink3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = thread.time,
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
                if (thread.unreadCount > 0) {
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(VShapes.full)
                            .background(VColors.violet)
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                    ) {
                        Text(
                            text = thread.unreadCount.toString(),
                            style = VTypography.caption,
                            color = VColors.white,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(start = 66.dp)
                    .background(VColors.lineSoft),
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// STATES
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth(0.5f).height(16.dp).clip(VShapes.sm).background(VColors.lineSoft))
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth(0.3f).height(22.dp).clip(VShapes.sm).background(VColors.lineSoft))
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(VShapes.lg)
                .background(VColors.lineSoft),
        )
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(VShapes.lg)
                .background(VColors.lineSoft),
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.weight(1f).height(120.dp).clip(VShapes.lg).background(VColors.lineSoft))
            Box(Modifier.weight(1f).height(120.dp).clip(VShapes.lg).background(VColors.lineSoft))
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Something went wrong",
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = VTypography.bodySmall,
            color = VColors.ink3,
        )
        Spacer(Modifier.height(20.dp))
        val interactionSource = remember { MutableInteractionSource() }
        val pressed by interactionSource.collectIsPressedAsState()
        val scale = if (pressed) 0.95f else 1f
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(VShapes.md)
                .background(VColors.violet)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) { onRetry() }
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Text(
                text = "Try Again",
                style = VTypography.body,
                color = VColors.white,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun EmptyState(
    onDiscoverSchools: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "No children linked yet",
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Discover and connect with your child's school",
            style = VTypography.bodySmall,
            color = VColors.ink3,
        )
        Spacer(Modifier.height(20.dp))
        val interactionSource = remember { MutableInteractionSource() }
        val pressed by interactionSource.collectIsPressedAsState()
        val scale = if (pressed) 0.95f else 1f
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(VShapes.md)
                .background(VColors.violet)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) { onDiscoverSchools() }
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Text(
                text = "Find School",
                style = VTypography.body,
                color = VColors.white,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// UTIL
// ════════════════════════════════════════════════════════════════════════════

private data class Quintet<A, B, C, D, E>(
    val a: A, val b: B, val c: C, val d: D, val e: E,
)

private operator fun <A, B, C, D, E> Quintet<A, B, C, D, E>.component1() = a
private operator fun <A, B, C, D, E> Quintet<A, B, C, D, E>.component2() = b
private operator fun <A, B, C, D, E> Quintet<A, B, C, D, E>.component3() = c
private operator fun <A, B, C, D, E> Quintet<A, B, C, D, E>.component4() = d
private operator fun <A, B, C, D, E> Quintet<A, B, C, D, E>.component5() = e
