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
// CONTENT — "Command Center" concept
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ContentState(
    state: ParentDashboardState,
    messageThreads: List<ParentMessageThreadDto>,
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
        StatusBar(
            child = state.selectedChild,
            children = state.children,
            selectedChildId = state.selectedChildId,
            unreadCount = unreadCount,
            onSelectChild = onSelectChild,
            onOpenNotifications = onOpenNotifications,
        )

        LiveStatusCard(
            child = state.selectedChild,
            today = state.today,
            periods = state.todayPeriods,
            schoolDayEnded = state.schoolDayEnded,
            onOpenAcademics = onOpenAcademics,
        )

        if (state.alerts.isNotEmpty()) {
            AlertStrip(
                alerts = state.alerts,
                onOpenPulse = onOpenPulse,
            )
        }

        StatTrio(
            attendanceRate = state.attendance?.attendanceRate ?: 0,
            presentDays = state.attendance?.presentDays ?: 0,
            totalDays = state.attendance?.totalDays ?: 0,
            fees = state.fees,
            latestMark = state.latestMark,
            markTrend = state.markTrend,
            onOpenAcademics = onOpenAcademics,
            onOpenFees = onOpenFees,
        )

        if (state.todayPeriods.isNotEmpty()) {
            ScheduleStrip(
                periods = state.todayPeriods,
                schoolDayEnded = state.schoolDayEnded,
            )
        }

        QuickActionsRow(
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

        MessagesPreview(
            threads = messageThreads,
            onOpenMessages = onOpenMessages,
        )

        Spacer(Modifier.height(80.dp))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// 1. STATUS BAR — compact switcher + bell
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun StatusBar(
    child: DashboardChildSummary?,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    unreadCount: Int,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
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
}

@Composable
private fun ChildSwitcher(
    child: DashboardChildSummary?,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    onSelectChild: (String) -> Unit,
) {
    if (child == null) return
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
                .shadow(1.dp, VShapes.md, ambientColor = VColors.ink.copy(alpha = 0.05f), spotColor = VColors.ink.copy(alpha = 0.07f))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) { if (children.size > 1) open = true }
                .padding(start = 6.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
        ) {
            ChildAvatar(child, 28.dp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = child.name,
                    style = VTypography.label,
                    color = VColors.ink,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Grade ${child.currentLevel}",
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }
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

@Composable
private fun NotificationBell(unreadCount: Int, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.92f else 1f

    Box(
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(1.dp, VShapes.full, ambientColor = VColors.ink.copy(alpha = 0.05f), spotColor = VColors.ink.copy(alpha = 0.07f))
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

// ════════════════════════════════════════════════════════════════════════════
// 2. LIVE STATUS CARD — what's happening RIGHT NOW
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun LiveStatusCard(
    child: DashboardChildSummary?,
    today: TodayAttendance,
    periods: List<LivePeriod>,
    schoolDayEnded: Boolean,
    onOpenAcademics: () -> Unit,
) {
    if (child == null) return

    val currentPeriod = periods.firstOrNull { it.relation == 0 }
    val nextPeriod = periods.firstOrNull { it.relation == 1 }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.98f else 1f

    val statusLabel = when {
        schoolDayEnded -> "School ended"
        currentPeriod != null -> "In class now"
        nextPeriod != null -> "On break"
        today.state == AttendanceDayState.Holiday -> "Holiday"
        today.state == AttendanceDayState.Vacation -> "Vacation"
        today.state == AttendanceDayState.Sunday -> "No school"
        else -> "No schedule"
    }

    val statusColor = when {
        currentPeriod != null -> VColors.violet
        schoolDayEnded -> VColors.ink3
        nextPeriod != null -> VColors.gold
        else -> VColors.ink3
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(4.dp, VShapes.xl, ambientColor = VColors.ink.copy(alpha = 0.06f), spotColor = VColors.ink.copy(alpha = 0.10f))
            .clip(VShapes.xl)
            .background(VColors.white)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(VShapes.full)
                            .background(statusColor),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = statusLabel,
                        style = VTypography.caption,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = formatDateDisplay(todayIso()),
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
            }

            Spacer(Modifier.height(14.dp))

            when {
                currentPeriod != null -> {
                    Text(
                        text = currentPeriod.subject,
                        style = VTypography.h3,
                        color = VColors.ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatusDetail(label = "Teacher", value = currentPeriod.teacherName)
                        StatusDetail(label = "Room", value = currentPeriod.room)
                        StatusDetail(label = "Time", value = "${currentPeriod.startTime}–${currentPeriod.endTime}")
                    }
                }
                nextPeriod != null -> {
                    Text(
                        text = "Next: ${nextPeriod.subject}",
                        style = VTypography.h3,
                        color = VColors.ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatusDetail(label = "Starts", value = nextPeriod.startTime)
                        StatusDetail(label = "Room", value = nextPeriod.room)
                        StatusDetail(label = "Teacher", value = nextPeriod.teacherName)
                    }
                }
                schoolDayEnded && periods.isNotEmpty() -> {
                    Text(
                        text = "${periods.size} classes completed",
                        style = VTypography.h3,
                        color = VColors.ink,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Last class ended at ${periods.last().endTime}",
                        style = VTypography.bodySmall,
                        color = VColors.ink3,
                    )
                }
                else -> {
                    Text(
                        text = today.label.ifBlank { "No classes scheduled" },
                        style = VTypography.h3,
                        color = VColors.ink,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${child.name} • Grade ${child.currentLevel}",
                        style = VTypography.bodySmall,
                        color = VColors.ink3,
                    )
                }
            }

            if (currentPeriod != null && periods.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                PeriodProgressDots(periods = periods)
            }
        }
    }
}

@Composable
private fun StatusDetail(label: String, value: String) {
    if (value.isBlank()) return
    Column(modifier = Modifier.width(100.dp)) {
        Text(
            text = label.uppercase(),
            style = VTypography.caption,
            color = VColors.ink3,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = value,
            style = VTypography.bodySmall,
            color = VColors.ink,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun PeriodProgressDots(periods: List<LivePeriod>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        periods.forEach { p ->
            val color = when (p.relation) {
                -1 -> VColors.mint
                0 -> VColors.violet
                else -> VColors.line
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

// ════════════════════════════════════════════════════════════════════════════
// 3. ALERT STRIP — critical alerts as horizontal banners
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
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
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
// 4. STAT TRIO — attendance / fees / latest mark
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun StatTrio(
    attendanceRate: Int,
    presentDays: Int,
    totalDays: Int,
    fees: FeeData?,
    latestMark: ParentMarkDto?,
    markTrend: List<Double>,
    onOpenAcademics: () -> Unit,
    onOpenFees: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatCard(
            label = "ATTENDANCE",
            value = if (attendanceRate > 0) "$attendanceRate%" else "—",
            sub = if (totalDays > 0) "$presentDays/$totalDays days" else "This term",
            accent = VColors.mint,
            onClick = onOpenAcademics,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = "FEES DUE",
            value = fees?.outstandingFees?.takeIf { it.isNotBlank() } ?: "₹0",
            sub = fees?.takeIf { it.overdueCount > 0 }?.let { "${it.overdueCount} overdue" } ?: "All clear",
            accent = if (fees?.overdueCount?.let { it > 0 } == true) VColors.coral else VColors.mint,
            onClick = onOpenFees,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = "LATEST MARK",
            value = latestMark?.let { "${it.marks?.toInt() ?: 0}/${it.maxMarks}" } ?: "—",
            sub = latestMark?.subject ?: "No marks yet",
            accent = VColors.violet,
            onClick = onOpenAcademics,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatCard(
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
            .shadow(2.dp, VShapes.lg, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
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
// 5. SCHEDULE STRIP — horizontal timeline of today's periods
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ScheduleStrip(
    periods: List<LivePeriod>,
    schoolDayEnded: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
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
    val (bg, border, statusText, statusColor) = when (period.relation) {
        -1 -> Quad(VColors.mintSoft, VColors.mint, "Done", VColors.success)
        0 -> Quad(VColors.violetSoft, VColors.violet, "Now", VColors.violet)
        else -> Quad(VColors.white, VColors.line, "Next", VColors.ink3)
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
                .background(border.copy(alpha = 0.15f))
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
// 6. QUICK ACTIONS — horizontal scroll of action tiles
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuickActionsRow(
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
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
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
// 7. MESSAGES PREVIEW
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun MessagesPreview(
    threads: List<ParentMessageThreadDto>,
    onOpenMessages: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
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
            style = VTypography.label,
            color = VColors.violet,
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.weight(1f).height(40.dp).clip(VShapes.md).background(VColors.lineSoft))
            Box(Modifier.size(40.dp).clip(VShapes.full).background(VColors.lineSoft))
        }
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(120.dp).clip(VShapes.xl).background(VColors.lineSoft))
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

// ════════════════════════════════════════════════════════════════════════════
// UTIL
// ════════════════════════════════════════════════════════════════════════════

private data class Quad<A, B, C, D>(
    val a: A, val b: B, val c: C, val d: D,
)

private operator fun <A, B, C, D> Quad<A, B, C, D>.component1() = a
private operator fun <A, B, C, D> Quad<A, B, C, D>.component2() = b
private operator fun <A, B, C, D> Quad<A, B, C, D>.component3() = c
private operator fun <A, B, C, D> Quad<A, B, C, D>.component4() = d
