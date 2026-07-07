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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardAlertDto
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.FeeData
import com.littlebridge.enrollplus.feature.parent.presentation.AttendanceDayState
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.TodayAttendance
import com.littlebridge.enrollplus.feature.parent.presentation.CoveredUnit
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
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
                    onDiscoverSchools = onDiscoverSchools,
                    onSelectChild = { dashboardViewModel.selectChild(it) },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun ContentState(
    state: ParentDashboardState,
    messageThreads: List<com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto>,
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
    onDiscoverSchools: () -> Unit,
    onSelectChild: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        GreetingHeader(
            greeting = state.greeting,
            childName = state.selectedChild?.name ?: "",
            onOpenNotifications = onOpenNotifications,
        )

        ChildStatusHero(
            child = state.selectedChild,
            today = state.today,
            attendanceRate = state.attendance?.attendanceRate ?: 0,
            latestMark = state.latestMark,
            coveredCount = state.coveredToday.size,
            children = state.children,
            selectedChildId = state.selectedChildId,
            onSelectChild = onSelectChild,
            onOpenAcademics = onOpenAcademics,
        )

        if (state.alerts.isNotEmpty()) {
            AttentionSection(alerts = state.alerts, fees = state.fees, onOpenFees = onOpenFees)
        }

        TodayAtSchoolSection(
            periods = state.todayPeriods,
            coveredToday = state.coveredToday,
            schoolDayEnded = state.schoolDayEnded,
        )

        MessagesSection(
            threads = messageThreads,
            onOpenMessages = onOpenMessages,
        )

        QuickActionsRow(
            onOpenFees = onOpenFees,
            onOpenAcademics = onOpenAcademics,
            onOpenEvents = onOpenEvents,
            onOpenScholarships = onOpenScholarships,
            onOpenTransport = onOpenTransport,
            onOpenTutor = onOpenTutor,
            onOpenPulse = onOpenPulse,
            onOpenLibrary = onOpenLibrary,
            onOpenIdCard = onOpenIdCard,
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun GreetingHeader(
    greeting: String,
    childName: String,
    onOpenNotifications: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 12.dp, top = 24.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting.ifBlank { "Hello" },
                style = VTypography.body,
                color = VColors.ink2,
            )
            Text(
                text = childName.ifBlank { "Parent" },
                style = VTypography.h2,
                color = VColors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = remember { formatDateToday() },
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }
        NotificationBell(onClick = onOpenNotifications)
    }
}

@Composable
private fun NotificationBell(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.95f else 1f

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(VShapes.full)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.full)
            .graphicsLayer { scaleX = scale; scaleY = scale }
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
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun ChildStatusHero(
    child: DashboardChildSummary?,
    today: TodayAttendance,
    attendanceRate: Int,
    latestMark: com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto?,
    coveredCount: Int,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    onSelectChild: (String) -> Unit,
    onOpenAcademics: () -> Unit,
) {
    if (child == null) return

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.99f else 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(VShapes.xl)
                .background(VColors.surfaceCard)
                .border(1.dp, VColors.line, VShapes.xl)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) { onOpenAcademics() }
                .padding(20.dp),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ChildAvatar(child, size = 48.dp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = child.name,
                                style = VTypography.h3,
                                color = VColors.ink,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "Grade ${child.currentLevel}",
                                style = VTypography.bodySmall,
                                color = VColors.ink2,
                            )
                        }
                    }
                    AttendancePill(today)
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    MiniStat(
                        value = if (attendanceRate > 0) "$attendanceRate%" else "--",
                        label = "Attendance",
                    )
                    VerticalDivider()
                    MiniStat(
                        value = latestMark?.let {
                            val pct = if (it.maxMarks > 0) (it.marks?.toInt() ?: 0) * 100 / it.maxMarks else 0
                            "$pct%"
                        } ?: "--",
                        label = "Last Test",
                    )
                    VerticalDivider()
                    MiniStat(
                        value = if (coveredCount > 0) "$coveredCount" else "--",
                        label = "Covered Today",
                    )
                }
            }
        }

        if (children.size > 1) {
            Spacer(Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = children,
                    key = { it.id },
                ) { c ->
                    ChildChip(
                        name = c.name,
                        isSelected = c.id == selectedChildId,
                        onClick = { onSelectChild(c.id) },
                    )
                }
            }
        }
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
            style = VTypography.label,
            color = VColors.violet,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AttendancePill(today: TodayAttendance) {
    val (bg, fg, text) = when (today.state) {
        AttendanceDayState.Present -> Triple(VColors.mintSoft, VColors.success, "Present")
        AttendanceDayState.Late -> Triple(VColors.goldSoft, VColors.ink, "Late")
        AttendanceDayState.Absent -> Triple(VColors.errorSoft, VColors.error, "Absent")
        AttendanceDayState.Holiday -> Triple(VColors.skySoft, VColors.ink, "Holiday")
        AttendanceDayState.Vacation -> Triple(VColors.skySoft, VColors.ink, today.label.ifBlank { "Vacation" })
        AttendanceDayState.Sunday -> Triple(VColors.surfaceTint, VColors.ink2, "Sunday")
        AttendanceDayState.NoData -> Triple(VColors.surfaceTint, VColors.ink2, "Not Marked")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(VShapes.full)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        if (today.state == AttendanceDayState.Present) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = text,
            style = VTypography.label,
            color = fg,
        )
    }
}

@Composable
private fun MiniStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = VTypography.h3,
            color = VColors.ink,
        )
        Text(
            text = label,
            style = VTypography.caption,
            color = VColors.ink3,
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(VColors.line),
    )
}

@Composable
private fun ChildChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.97f else 1f

    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.full)
            .background(if (isSelected) VColors.violet else VColors.surfaceCard)
            .border(1.dp, if (isSelected) VColors.violet else VColors.line, VShapes.full)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = name,
            style = VTypography.label,
            color = if (isSelected) VColors.white else VColors.ink2,
        )
    }
}

@Composable
private fun AttentionSection(
    alerts: List<DashboardAlertDto>,
    fees: FeeData?,
    onOpenFees: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.creamDeep)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "Needs your attention",
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(12.dp))

        alerts.forEach { alert ->
            AlertCard(alert, onOpenFees)
            Spacer(Modifier.height(8.dp))
        }

        if (fees != null && fees.overdueCount > 0) {
            FeeAlertCard(fees, onOpenFees)
        }
    }
}

@Composable
private fun AlertCard(alert: DashboardAlertDto, onOpenFees: () -> Unit) {
    val (borderColor, bg) = when (alert.type.uppercase()) {
        "CRITICAL" -> VColors.coral to VColors.coralSoft
        "WARNING" -> VColors.gold to VColors.goldSoft
        else -> VColors.sky to VColors.skySoft
    }

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.99f else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onOpenFees() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(VShapes.sm)
                .background(borderColor),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alert.title,
                style = VTypography.body,
                color = VColors.ink,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = alert.value,
                style = VTypography.bodySmall,
                color = VColors.ink2,
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = VColors.ink3,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun FeeAlertCard(fees: FeeData, onOpenFees: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.99f else 1f

    val payInteractionSource = remember { MutableInteractionSource() }
    val payPressed by payInteractionSource.collectIsPressedAsState()
    val payScale = if (payPressed) 0.95f else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onOpenFees() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(VShapes.sm)
                .background(VColors.coral),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Fee Due",
                style = VTypography.body,
                color = VColors.ink,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = fees.outstandingFees,
                style = VTypography.bodySmall,
                color = VColors.coral,
            )
        }
        Box(
            modifier = Modifier
                .graphicsLayer { scaleX = payScale; scaleY = payScale }
                .clip(VShapes.md)
                .background(VColors.violet)
                .clickable(
                    interactionSource = payInteractionSource,
                    indication = null,
                ) { onOpenFees() }
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = "Pay Now",
                style = VTypography.label,
                color = VColors.white,
            )
        }
    }
}

@Composable
private fun TodayAtSchoolSection(
    periods: List<LivePeriod>,
    coveredToday: List<CoveredUnit>,
    schoolDayEnded: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = if (schoolDayEnded) "Today's Summary" else "Today",
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(12.dp))

        if (periods.isEmpty() && coveredToday.isEmpty()) {
            Text(
                text = "Nothing scheduled today",
                style = VTypography.bodySmall,
                color = VColors.ink3,
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(
                    items = periods,
                    key = { "${it.startTime}-${it.subject}" },
                ) { period ->
                    TodayCard(
                        title = period.subject,
                        subtitle = "${period.startTime} - ${period.endTime}",
                        isActive = period.relation == 0,
                    )
                }
                items(
                    items = coveredToday,
                    key = { "${it.subject}-${it.title}" },
                ) { unit ->
                    TodayCard(
                        title = unit.subject,
                        subtitle = unit.title,
                        isCovered = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayCard(
    title: String,
    subtitle: String,
    isActive: Boolean = false,
    isCovered: Boolean = false,
) {
    val bg = when {
        isActive -> VColors.violetSoft
        isCovered -> VColors.mintSoft
        else -> VColors.surfaceCard
    }
    val titleColor = when {
        isActive -> VColors.violet
        isCovered -> VColors.success
        else -> VColors.ink
    }
    val badge = when {
        isActive -> "Now"
        isCovered -> "Covered"
        else -> null
    }

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(88.dp)
            .clip(VShapes.lg)
            .background(bg)
            .border(1.dp, VColors.lineSoft, VShapes.lg)
            .padding(12.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = title,
                    style = VTypography.label,
                    color = titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(VShapes.full)
                            .background(if (isActive) VColors.violet else VColors.success)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = badge,
                            style = VTypography.caption,
                            color = VColors.white,
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = VTypography.caption,
                color = VColors.ink3,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MessagesSection(
    threads: List<com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto>,
    onOpenMessages: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Messages",
                style = VTypography.h3,
                color = VColors.ink,
            )
            Text(
                text = "View all",
                style = VTypography.label,
                color = VColors.violet,
                modifier = Modifier
                    .clip(VShapes.sm)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onOpenMessages() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }

        if (threads.isEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "No messages yet",
                style = VTypography.bodySmall,
                color = VColors.ink3,
            )
        } else {
            Spacer(Modifier.height(8.dp))
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

@Composable
private fun MessageRow(
    thread: com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto,
    onClick: () -> Unit,
    isLast: Boolean,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.99f else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (thread.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(VShapes.full)
                    .background(VColors.violet),
            )
            Spacer(Modifier.width(12.dp))
        } else {
            Spacer(Modifier.width(20.dp))
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(VShapes.full)
                .background(VColors.surfaceTint),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = thread.senderName.firstOrNull()?.uppercase() ?: "",
                style = VTypography.label,
                color = VColors.ink2,
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = thread.senderName,
                style = VTypography.body,
                color = VColors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = thread.lastMessage,
                style = VTypography.bodySmall,
                color = VColors.ink2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = thread.time,
            style = VTypography.caption,
            color = VColors.ink3,
        )
    }

    if (!isLast) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(VColors.lineSoft),
        )
    }
}

@Composable
private fun QuickActionsRow(
    onOpenFees: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenTutor: () -> Unit,
    onOpenPulse: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenIdCard: () -> Unit,
) {
    val actions = listOf(
        QuickAction("Fees", Icons.Filled.Payment, onOpenFees),
        QuickAction("Academics", Icons.Filled.School, onOpenAcademics),
        QuickAction("Events", Icons.Filled.Event, onOpenEvents),
        QuickAction("Pulse", Icons.Filled.Insights, onOpenPulse),
        QuickAction("Scholarships", Icons.Filled.ReceiptLong, onOpenScholarships),
        QuickAction("Transport", Icons.Filled.Send, onOpenTransport),
        QuickAction("Tutor", Icons.Filled.MenuBook, onOpenTutor),
        QuickAction("Library", Icons.Filled.LocalLibrary, onOpenLibrary),
        QuickAction("ID Card", Icons.Filled.Badge, onOpenIdCard),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = "Quick Actions",
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(
                items = actions,
                key = { it.label },
            ) { action ->
                ActionPill(action)
            }
        }
    }
}

private data class QuickAction(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit,
)

@Composable
private fun ActionPill(action: QuickAction) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.97f else 1f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .height(48.dp)
            .clip(VShapes.full)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.full)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { action.onClick() }
            .padding(horizontal = 16.dp),
    ) {
        Icon(
            imageVector = action.icon,
            contentDescription = null,
            tint = VColors.violet,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = action.label,
            style = VTypography.label,
            color = VColors.ink,
        )
    }
}

private fun formatDateToday(): String {
    val iso = todayIso()
    val (y, m, d) = com.littlebridge.enrollplus.util.parseIsoDate(iso) ?: return ""
    val monthName = when (m) {
        1 -> "January"; 2 -> "February"; 3 -> "March"; 4 -> "April"
        5 -> "May"; 6 -> "June"; 7 -> "July"; 8 -> "August"
        9 -> "September"; 10 -> "October"; 11 -> "November"; 12 -> "December"
        else -> ""
    }
    val dow = com.littlebridge.enrollplus.util.dayOfWeek(y, m, d)
    val dayName = when (dow) {
        0 -> "Sunday"; 1 -> "Monday"; 2 -> "Tuesday"; 3 -> "Wednesday"
        4 -> "Thursday"; 5 -> "Friday"; 6 -> "Saturday"; else -> ""
    }
    return "$dayName, $d $monthName"
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Box(Modifier.fillMaxWidth(0.4f).height(16.dp).clip(VShapes.sm).background(VColors.lineSoft))
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth(0.5f).height(20.dp).clip(VShapes.sm).background(VColors.lineSoft))
                Spacer(Modifier.height(4.dp))
                Box(Modifier.fillMaxWidth(0.3f).height(12.dp).clip(VShapes.sm).background(VColors.lineSoft))
            }
            Box(Modifier.size(44.dp).clip(VShapes.full).background(VColors.lineSoft))
        }
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(VShapes.xl)
                .background(VColors.lineSoft),
        )
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(56.dp).clip(VShapes.lg).background(VColors.lineSoft))
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth(0.7f).height(56.dp).clip(VShapes.lg).background(VColors.lineSoft))
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
            text = "Couldn't load",
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = VTypography.bodySmall,
            color = VColors.ink2,
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .clip(VShapes.md)
                .background(VColors.violet)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onRetry() }
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Text(
                text = "Retry",
                style = VTypography.label,
                color = VColors.white,
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
        Icon(
            imageVector = Icons.Filled.School,
            contentDescription = null,
            tint = VColors.ink3,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No child linked yet",
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Find your child's school to get started",
            style = VTypography.bodySmall,
            color = VColors.ink2,
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .clip(VShapes.md)
                .background(VColors.violet)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onDiscoverSchools() }
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Text(
                text = "Find School",
                style = VTypography.label,
                color = VColors.white,
            )
        }
    }
}
