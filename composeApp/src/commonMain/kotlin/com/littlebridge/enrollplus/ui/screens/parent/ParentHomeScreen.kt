package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Whatsapp
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardAlertDto
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.FeeData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMessageThreadDto
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
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
            state.isLoading && state.children.isEmpty() -> LoadingState(Modifier.padding(padding))
            state.error != null && state.children.isEmpty() -> ErrorState(
                message = state.error!!,
                onRetry = { dashboardViewModel.load() },
                modifier = Modifier.padding(padding),
            )
            state.children.isEmpty() && !state.isLoading -> EmptyState(
                onDiscoverSchools = onDiscoverSchools,
                modifier = Modifier.padding(padding),
            )
            else -> ContentState(
                state = state,
                messageThreads = messageState.threads.take(3),
                unreadCount = unreadCount,
                unreadNotificationsCount = unreadNotificationsCount,
                onOpenNotifications = onOpenNotifications,
                onOpenFees = onOpenFees,
                onOpenAcademics = onOpenAcademics,
                onOpenMessages = onOpenMessages,
                onOpenPulse = onOpenPulse,
                onOpenEvents = onOpenEvents,
                onOpenScholarships = onOpenScholarships,
                onOpenTransport = onOpenTransport,
                onOpenTutor = onOpenTutor,
                onOpenLibrary = onOpenLibrary,
                onOpenIdCard = onOpenIdCard,
                onOpenProfile = onOpenProfile,
                onSelectChild = { dashboardViewModel.selectChild(it) },
                modifier = Modifier.padding(padding),
            )
        }
    }
}

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
    onOpenPulse: () -> Unit,
    onOpenEvents: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenTutor: () -> Unit,
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
        TopBar(
            child = state.selectedChild,
            children = state.children,
            selectedChildId = state.selectedChildId,
            unreadCount = unreadNotificationsCount,
            onSelectChild = onSelectChild,
            onOpenNotifications = onOpenNotifications,
            onOpenMessages = onOpenMessages,
            onOpenProfile = onOpenProfile,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = state.greeting.ifBlank { "Good morning" },
            style = VTypography.body,
            color = VColors.ink3,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(16.dp))

        state.selectedChild?.let { child ->
            ChildCard(
                child = child,
                attendanceRate = state.attendance?.attendanceRate ?: 0,
                onOpenAcademics = onOpenAcademics,
            )
        }

        if (state.alerts.isNotEmpty()) {
            AlertStrip(alerts = state.alerts, onOpenPulse = onOpenPulse)
        }

        if (state.todayPeriods.isNotEmpty()) {
            ScheduleCard(
                periods = state.todayPeriods,
                schoolDayEnded = state.schoolDayEnded,
                onOpenAcademics = onOpenAcademics,
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

        MessagesSection(
            threads = messageThreads,
            onOpenMessages = onOpenMessages,
        )

        Spacer(Modifier.height(96.dp))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TOP BAR
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TopBar(
    child: DashboardChildSummary?,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    unreadCount: Int,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChildSwitcher(
            child = child,
            children = children,
            selectedChildId = selectedChildId,
            onSelectChild = onSelectChild,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IconButton(
                icon = Icons.Filled.Whatsapp,
                badge = unreadCount,
                onClick = onOpenMessages,
            )
            IconButton(
                icon = Icons.Filled.Notifications,
                badge = unreadCount,
                onClick = onOpenNotifications,
            )
        }
    }
}

@Composable
private fun ChildSwitcher(
    child: DashboardChildSummary?,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    onSelectChild: (String) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    val canSwitch = children.size > 1
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.97f else 1f

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(VShapes.full)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = canSwitch,
                ) { if (canSwitch) open = true }
                .padding(vertical = 6.dp),
        ) {
            Text(
                text = child?.name?.ifBlank { "Your child" } ?: "Your child",
                style = VTypography.body,
                color = VColors.ink,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (canSwitch) {
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = "Switch child",
                    tint = VColors.ink3,
                    modifier = Modifier.size(20.dp),
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
                            Avatar(name = c.name, size = 32.dp)
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
private fun IconButton(
    icon: ImageVector,
    badge: Int,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.9f else 1f

    Box(
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.full)
            .background(VColors.white)
            .shadow(2.dp, VShapes.full, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VColors.ink,
            modifier = Modifier.size(20.dp),
        )
        if (badge > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(16.dp)
                    .clip(VShapes.full)
                    .background(VColors.coral),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (badge > 9) "9+" else badge.toString(),
                    style = VTypography.caption,
                    color = VColors.white,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CHILD CARD
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ChildCard(
    child: DashboardChildSummary,
    attendanceRate: Int,
    onOpenAcademics: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.98f else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.xl)
            .background(VColors.white)
            .shadow(3.dp, VShapes.xl, ambientColor = VColors.ink.copy(alpha = 0.05f), spotColor = VColors.ink.copy(alpha = 0.07f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onOpenAcademics() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(name = child.name, size = 56.dp)
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = child.name,
                style = VTypography.h3,
                color = VColors.ink,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Grade ${child.currentLevel}",
                style = VTypography.bodySmall,
                color = VColors.ink3,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge(
                    text = child.attendanceStatus.replaceFirstChar { it.uppercase() },
                    bg = VColors.coralSoft,
                    textColor = VColors.coral,
                )
                Badge(
                    text = if (attendanceRate > 0) "$attendanceRate% Attendance" else "Attendance N/A",
                    bg = VColors.mintSoft,
                    textColor = VColors.mint,
                )
            }
        }
    }
}

@Composable
private fun Badge(text: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(VShapes.full)
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = VTypography.caption,
            color = textColor,
            fontWeight = FontWeight.Bold,
        )
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
        modifier = Modifier
            .width(260.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.lg)
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(4.dp))
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
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(VShapes.full)
                .background(fg),
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SCHEDULE CARD
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ScheduleCard(
    periods: List<LivePeriod>,
    schoolDayEnded: Boolean,
    onOpenAcademics: () -> Unit,
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
                text = "Today's Learning",
                style = VTypography.label,
                color = VColors.ink,
                fontWeight = FontWeight.Bold,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(VShapes.sm)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onOpenAcademics() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "Swipe to expand",
                    style = VTypography.caption,
                    color = VColors.ink3,
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = VColors.ink3,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(VShapes.xl)
                .background(VColors.white)
                .shadow(3.dp, VShapes.xl, ambientColor = VColors.ink.copy(alpha = 0.05f), spotColor = VColors.ink.copy(alpha = 0.07f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            periods.take(4).forEachIndexed { index, period ->
                PeriodRow(
                    period = period,
                    isLast = index == periods.take(4).lastIndex,
                )
            }
            if (periods.size > 4) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(VShapes.sm)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onOpenAcademics() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Show all ${periods.size} classes",
                        style = VTypography.caption,
                        color = VColors.violet,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PeriodRow(
    period: LivePeriod,
    isLast: Boolean,
) {
    val dotColor = when (period.relation) {
        -1 -> VColors.mint
        0 -> VColors.violet
        else -> VColors.line
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(8.dp)
                    .clip(VShapes.full)
                    .background(dotColor),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = period.subject,
                        style = VTypography.body,
                        color = VColors.ink,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${period.startTime} — ${period.endTime}",
                        style = VTypography.caption,
                        color = VColors.ink3,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (period.teacherName.isNotBlank() || period.room.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = buildString {
                            if (period.teacherName.isNotBlank()) append(period.teacherName)
                            if (period.teacherName.isNotBlank() && period.room.isNotBlank()) append(" · ")
                            if (period.room.isNotBlank()) append("Room ${period.room}")
                        },
                        style = VTypography.caption,
                        color = VColors.ink2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val tags = listOfNotNull(
                        period.room.takeIf { it.isNotBlank() },
                        when (period.relation) {
                            -1 -> "Completed"
                            0 -> "Happening now"
                            else -> "Upcoming"
                        },
                    )
                    tags.forEach { tag ->
                        PeriodTag(tag)
                    }
                }
            }
        }
        if (!isLast) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(start = 20.dp)
                    .background(VColors.lineSoft),
            )
        }
    }
}

@Composable
private fun PeriodTag(text: String) {
    Box(
        modifier = Modifier
            .clip(VShapes.sm)
            .background(VColors.surfaceTint)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            style = VTypography.caption,
            color = VColors.ink2,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// QUICK ACTIONS
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
            .padding(horizontal = 20.dp),
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(
                listOf(
                    ActionTile("Academics", Icons.Filled.School, VColors.violet, VColors.violetSoft,
                        latestMark?.let { "${it.marks?.toInt() ?: 0}/${it.maxMarks} ${it.subject}" } ?: "View progress",
                        onOpenAcademics),
                    ActionTile("Messages", Icons.Filled.Whatsapp, VColors.sky, VColors.skySoft,
                        if (unreadCount > 0) "$unreadCount unread" else "Inbox", onOpenMessages),
                    ActionTile("Transport", Icons.Filled.DirectionsBus, VColors.mint, VColors.mintSoft,
                        "Track bus", onOpenTransport),
                    ActionTile("AI Tutor", Icons.Filled.Payment, VColors.violet, VColors.violetSoft,
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
    val icon: ImageVector,
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
            .width(96.dp)
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
            .padding(horizontal = 20.dp, vertical = 16.dp),
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
                    .clip(VShapes.xl)
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
                    .clip(VShapes.xl)
                    .background(VColors.white)
                    .shadow(2.dp, VShapes.xl, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(name = thread.senderName, size = 38.dp)
            Spacer(Modifier.width(12.dp))
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
                    .padding(start = 50.dp)
                    .background(VColors.lineSoft),
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SHARED AVATAR
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun Avatar(name: String, size: androidx.compose.ui.unit.Dp) {
    val initials = name.split(" ").take(2).joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
        .ifBlank { "?" }
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
        Box(Modifier.fillMaxWidth().height(120.dp).clip(VShapes.xl).background(VColors.lineSoft))
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(180.dp).clip(VShapes.xl).background(VColors.lineSoft))
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

