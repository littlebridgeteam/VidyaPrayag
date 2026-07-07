package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardAlertDto
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.FeeData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto
import com.littlebridge.enrollplus.feature.parent.presentation.AttendanceDayState
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
    onOpenProfile: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
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
                    unreadNotificationsCount = unreadNotificationsCount,
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
                    onOpenProfile = onOpenProfile,
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
    messageThreads: List<ParentMessageThreadDto>,
    unreadCount: Int,
    unreadNotificationsCount: Int,
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
    onOpenProfile: () -> Unit,
    onSelectChild: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        HeroSection(
            child = state.selectedChild,
            children = state.children,
            selectedChildId = state.selectedChildId,
            today = state.today,
            periods = state.todayPeriods,
            schoolDayEnded = state.schoolDayEnded,
            unreadNotificationsCount = unreadNotificationsCount,
            onSelectChild = onSelectChild,
            onOpenNotifications = onOpenNotifications,
            onOpenProfile = onOpenProfile,
            onOpenAcademics = onOpenAcademics,
        )

        if (state.alerts.isNotEmpty()) {
            AlertStrip(alerts = state.alerts, onOpenPulse = onOpenPulse)
        }

        MetricRow(
            attendanceRate = state.attendance?.attendanceRate ?: 0,
            fees = state.fees,
            latestMark = state.latestMark,
            onOpenAcademics = onOpenAcademics,
            onOpenFees = onOpenFees,
        )

        if (state.todayPeriods.isNotEmpty()) {
            ScheduleSection(
                periods = state.todayPeriods,
                schoolDayEnded = state.schoolDayEnded,
            )
        }

        QuickActionsSection(
            onOpenAcademics = onOpenAcademics,
            onOpenMessages = onOpenMessages,
            onOpenTransport = onOpenTransport,
            onOpenTutor = onOpenTutor,
            onOpenScholarships = onOpenScholarships,
            onOpenIdCard = onOpenIdCard,
            onOpenLibrary = onOpenLibrary,
            onOpenEvents = onOpenEvents,
            unreadCount = unreadCount,
            latestMark = state.latestMark,
        )

        MessagesSection(
            threads = messageThreads,
            onOpenMessages = onOpenMessages,
        )

        Spacer(Modifier.height(100.dp))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// HERO — full-bleed dark violet with integrated child switcher + live status
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun HeroSection(
    child: DashboardChildSummary?,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    today: TodayAttendance,
    periods: List<LivePeriod>,
    schoolDayEnded: Boolean,
    unreadNotificationsCount: Int,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAcademics: () -> Unit,
) {
    if (child == null) return

    val currentPeriod = periods.firstOrNull { it.relation == 0 }
    val nextPeriod = periods.firstOrNull { it.relation == 1 }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VColors.violetInk,
                            VColors.violet,
                            VColors.violetHover,
                        ),
                        startY = 0f,
                        endY = size.height,
                    ),
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        ) {
            // ── Row 1: Child switcher + Bell ──
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
                BellButton(
                    unreadCount = unreadNotificationsCount,
                    onClick = onOpenNotifications,
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Greeting ──
            Text(
                text = formatDateDisplay(todayIso()),
                style = VTypography.caption,
                color = VColors.white.copy(alpha = 0.6f),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = child.name,
                style = VTypography.h2,
                color = VColors.white,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Grade ${child.currentLevel}",
                style = VTypography.bodySmall,
                color = VColors.white.copy(alpha = 0.7f),
            )

            Spacer(Modifier.height(20.dp))

            // ── Live status pill ──
            val statusLabel = when {
                schoolDayEnded -> "School ended"
                currentPeriod != null -> "In class now"
                nextPeriod != null -> "On break"
                today.state == AttendanceDayState.Holiday -> "Holiday"
                today.state == AttendanceDayState.Vacation -> "Vacation"
                today.state == AttendanceDayState.Sunday -> "No school"
                else -> "No schedule"
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(VShapes.full)
                    .background(VColors.white.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(VShapes.full)
                        .background(
                            when {
                                currentPeriod != null -> VColors.mint
                                schoolDayEnded -> VColors.white.copy(alpha = 0.5f)
                                nextPeriod != null -> VColors.gold
                                else -> VColors.white.copy(alpha = 0.5f)
                            },
                        ),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = statusLabel,
                    style = VTypography.caption,
                    color = VColors.white,
                    fontWeight = FontWeight.Bold,
                )
            }

            // ── Current/next class detail ──
            if (currentPeriod != null) {
                Spacer(Modifier.height(16.dp))
                LiveClassRow(
                    label = "NOW",
                    subject = currentPeriod.subject,
                    detail = currentPeriod.teacherName,
                    time = "${currentPeriod.startTime} – ${currentPeriod.endTime}",
                    room = currentPeriod.room,
                    onClick = onOpenAcademics,
                )
            } else if (nextPeriod != null) {
                Spacer(Modifier.height(16.dp))
                LiveClassRow(
                    label = "NEXT",
                    subject = nextPeriod.subject,
                    detail = nextPeriod.teacherName,
                    time = "Starts ${nextPeriod.startTime}",
                    room = nextPeriod.room,
                    onClick = onOpenAcademics,
                )
            }

            // ── Period progress dots ──
            if (periods.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    periods.forEach { p ->
                        val color = when (p.relation) {
                            -1 -> VColors.mint.copy(alpha = 0.6f)
                            0 -> VColors.white
                            else -> VColors.white.copy(alpha = 0.2f)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(VShapes.full)
                                .background(color),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChildSwitcher(
    child: DashboardChildSummary,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    onSelectChild: (String) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    val canSwitch = children.size > 1
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.96f else 1f

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(VShapes.full)
                .background(VColors.white.copy(alpha = 0.15f))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = canSwitch,
                ) { if (canSwitch) open = true }
                .padding(start = 4.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
        ) {
            ChildAvatar(child, 28.dp, dark = true)
            Spacer(Modifier.width(8.dp))
            Text(
                text = child.name,
                style = VTypography.label,
                color = VColors.white,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (canSwitch) {
                Spacer(Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = "Switch child",
                    tint = VColors.white.copy(alpha = 0.7f),
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
                            ChildAvatar(c, 32.dp, dark = false)
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

@Composable
private fun BellButton(unreadCount: Int, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.92f else 1f

    Box(
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.full)
            .background(VColors.white.copy(alpha = 0.15f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = "Notifications",
            tint = VColors.white,
            modifier = Modifier.size(18.dp),
        )
        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(15.dp)
                    .clip(VShapes.full)
                    .background(VColors.coral)
                    .border(1.5.dp, VColors.violet, VShapes.full),
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

@Composable
private fun LiveClassRow(
    label: String,
    subject: String,
    detail: String,
    time: String,
    room: String,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.98f else 1f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.lg)
            .background(VColors.white.copy(alpha = 0.1f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Box(
            modifier = Modifier
                .clip(VShapes.sm)
                .background(VColors.white.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = label,
                style = VTypography.caption,
                color = VColors.white,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject,
                style = VTypography.body,
                color = VColors.white,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (detail.isNotBlank()) {
                Text(
                    text = detail,
                    style = VTypography.caption,
                    color = VColors.white.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = time,
                style = VTypography.caption,
                color = VColors.white.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold,
            )
            if (room.isNotBlank()) {
                Text(
                    text = "Room $room",
                    style = VTypography.caption,
                    color = VColors.white.copy(alpha = 0.5f),
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// ALERT STRIP
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun AlertStrip(
    alerts: List<DashboardAlertDto>,
    onOpenPulse: () -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
    ) {
        items(alerts.take(5)) { alert ->
            AlertCard(alert = alert, onClick = onOpenPulse)
        }
    }
}

@Composable
private fun AlertCard(alert: DashboardAlertDto, onClick: () -> Unit) {
    val (bg, fg) = when (alert.type) {
        "CRITICAL" -> VColors.coralSoft to VColors.coral
        "WARNING" -> VColors.goldSoft to VColors.gold
        else -> VColors.violetSoft to VColors.violet
    }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.97f else 1f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(280.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.lg)
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() }
            .padding(12.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alert.title,
                style = VTypography.label,
                color = VColors.ink,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = alert.value,
                style = VTypography.caption,
                color = VColors.ink2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// METRIC ROW — 3 compact stat cards
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun MetricRow(
    attendanceRate: Int,
    fees: FeeData?,
    latestMark: ParentMarkDto?,
    onOpenAcademics: () -> Unit,
    onOpenFees: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MetricCard(
            label = "ATTENDANCE",
            value = if (attendanceRate > 0) "$attendanceRate%" else "—",
            sub = "This term",
            accent = VColors.mint,
            onClick = onOpenAcademics,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "FEES DUE",
            value = fees?.outstandingFees?.takeIf { it.isNotBlank() } ?: "₹0",
            sub = fees?.takeIf { it.overdueCount > 0 }?.let { "${it.overdueCount} overdue" } ?: "All clear",
            accent = if (fees?.overdueCount?.let { it > 0 } == true) VColors.coral else VColors.mint,
            onClick = onOpenFees,
            modifier = Modifier.weight(1f),
        )
        MetricCard(
            label = "LATEST",
            value = latestMark?.let { "${it.marks?.toInt() ?: 0}/${it.maxMarks}" } ?: "—",
            sub = latestMark?.subject ?: "No marks",
            accent = VColors.violet,
            onClick = onOpenAcademics,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    sub: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.97f else 1f

    Column(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(2.dp, VShapes.lg, ambientColor = VColors.ink.copy(alpha = 0.05f), spotColor = VColors.ink.copy(alpha = 0.07f))
            .clip(VShapes.lg)
            .background(VColors.white)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() }
            .padding(12.dp),
    ) {
        Text(
            text = label,
            style = VTypography.caption,
            color = VColors.ink3,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            style = VTypography.h3,
            color = VColors.ink,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = sub,
            style = VTypography.caption,
            color = VColors.ink3,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(3.dp)
                .clip(VShapes.full)
                .background(accent),
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SCHEDULE — horizontal scroll of period cards
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ScheduleSection(
    periods: List<LivePeriod>,
    schoolDayEnded: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (schoolDayEnded) "Today's Classes" else "Schedule",
                style = VTypography.label,
                color = VColors.ink,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${periods.size} periods",
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }
        Spacer(Modifier.height(10.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(periods) { period ->
                PeriodCard(period = period)
            }
        }
    }
}

@Composable
private fun PeriodCard(period: LivePeriod) {
    val (bg, statusText, statusColor) = when (period.relation) {
        -1 -> Triple(VColors.mintSoft, "Done", VColors.success)
        0 -> Triple(VColors.violetSoft, "Now", VColors.violet)
        else -> Triple(VColors.white, "Next", VColors.ink3)
    }

    Column(
        modifier = Modifier
            .width(120.dp)
            .clip(VShapes.md)
            .background(bg)
            .padding(12.dp),
    ) {
        Text(
            text = period.startTime,
            style = VTypography.caption,
            color = VColors.ink2,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = period.subject,
            style = VTypography.bodySmall,
            color = VColors.ink,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (period.teacherName.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = period.teacherName,
                style = VTypography.caption,
                color = VColors.ink3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(VShapes.full)
                .background(statusColor.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(
                text = statusText,
                style = VTypography.caption,
                color = statusColor,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// QUICK ACTIONS — horizontal scroll
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuickActionsSection(
    onOpenAcademics: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenTutor: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenIdCard: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenEvents: () -> Unit,
    unreadCount: Int,
    latestMark: ParentMarkDto?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp),
    ) {
        Text(
            text = "Quick Actions",
            style = VTypography.label,
            color = VColors.ink,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                listOf(
                    ActionTile("Academics", Icons.Filled.School, VColors.violet, VColors.violetSoft,
                        latestMark?.let { "${it.subject.take(3)} ${it.marks?.toInt() ?: 0}/${it.maxMarks}" } ?: "View progress",
                        onOpenAcademics),
                    ActionTile("Messages", Icons.Filled.Notifications, VColors.sky, VColors.skySoft,
                        if (unreadCount > 0) "$unreadCount unread" else "Inbox", onOpenMessages),
                    ActionTile("Transport", Icons.Filled.DirectionsBus, VColors.mint, VColors.mintSoft,
                        "Track bus", onOpenTransport),
                    ActionTile("AI Tutor", Icons.Filled.Insights, VColors.violet, VColors.violetSoft,
                        "Ask AI", onOpenTutor),
                    ActionTile("Scholarships", Icons.Filled.Payment, VColors.gold, VColors.goldSoft,
                        "Apply now", onOpenScholarships),
                    ActionTile("ID Card", Icons.Filled.Badge, VColors.ink2, VColors.surfaceTint,
                        "Digital ID", onOpenIdCard),
                    ActionTile("Library", Icons.Filled.LocalLibrary, VColors.sky, VColors.skySoft,
                        "Books", onOpenLibrary),
                    ActionTile("Events", Icons.Filled.Event, VColors.coral, VColors.coralSoft,
                        "Upcoming", onOpenEvents),
                ),
            ) { tile ->
                ActionTileCard(tile = tile)
            }
        }
    }
}

private data class ActionTile(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val subText: String,
    val onClick: () -> Unit,
)

@Composable
private fun ActionTileCard(tile: ActionTile) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.96f else 1f

    Column(
        modifier = Modifier
            .width(100.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.lg)
            .background(VColors.white)
            .shadow(1.dp, VShapes.lg, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { tile.onClick() }
            .padding(12.dp),
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
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = tile.label,
            style = VTypography.label,
            color = VColors.ink,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
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

// ════════════════════════════════════════════════════════════════════════════
// MESSAGES
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun MessagesSection(
    threads: List<ParentMessageThreadDto>,
    onOpenMessages: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Messages",
                style = VTypography.label,
                color = VColors.ink,
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
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.lg)
                    .background(VColors.white),
            ) {
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

@Composable
private fun MessageRow(
    thread: ParentMessageThreadDto,
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
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
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

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = thread.senderName,
                    style = VTypography.bodySmall,
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

            Column(horizontalAlignment = Alignment.End) {
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
                    .padding(start = 60.dp)
                    .background(VColors.lineSoft),
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SHARED
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ChildAvatar(
    child: DashboardChildSummary,
    size: androidx.compose.ui.unit.Dp,
    dark: Boolean = false,
) {
    val initials = child.name.split(" ").take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
    Box(
        modifier = Modifier
            .size(size)
            .clip(VShapes.full)
            .background(if (dark) VColors.white.copy(alpha = 0.2f) else VColors.violetSoft),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = VTypography.label,
            color = if (dark) VColors.white else VColors.violet,
            fontWeight = FontWeight.Bold,
        )
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(200.dp).clip(VShapes.xl).background(VColors.lineSoft))
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f).height(100.dp).clip(VShapes.lg).background(VColors.lineSoft))
            Box(Modifier.weight(1f).height(100.dp).clip(VShapes.lg).background(VColors.lineSoft))
            Box(Modifier.weight(1f).height(100.dp).clip(VShapes.lg).background(VColors.lineSoft))
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
        modifier = modifier.fillMaxSize().padding(20.dp),
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
        modifier = modifier.fillMaxSize().padding(20.dp),
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
