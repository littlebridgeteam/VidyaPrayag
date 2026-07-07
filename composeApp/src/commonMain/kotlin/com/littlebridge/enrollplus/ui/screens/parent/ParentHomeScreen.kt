package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
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
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentAttendanceData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto
import com.littlebridge.enrollplus.feature.parent.presentation.CoveredUnit
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentMessageViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
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
        PortalHeader(
            child = state.selectedChild,
            children = state.children,
            selectedChildId = state.selectedChildId,
            unreadNotifications = unreadNotificationsCount,
            onSelectChild = onSelectChild,
            onOpenNotifications = onOpenNotifications,
        )
        Text(
            text = state.greeting.ifBlank { "Good morning" },
            style = VTypography.caption,
            color = VColors.ink3,
            modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 2.dp),
        )
        state.selectedChild?.let { child ->
            ChildHeroCard(
                child = child,
                attendanceRate = state.attendance?.attendanceRate ?: 0,
                onClick = onOpenProfile,
            )
        }
        if (state.todayPeriods.isNotEmpty()) {
            TodaysLearning(
                periods = state.todayPeriods,
                coveredToday = state.coveredToday,
                onOpenAcademics = onOpenAcademics,
            )
        }
        QuickInsights(
            attendance = state.attendance,
            fees = state.fees,
            onOpenAcademics = onOpenAcademics,
            onOpenFees = onOpenFees,
        )
        if (state.todayPeriods.isNotEmpty()) {
            TodaysSchedule(periods = state.todayPeriods)
        }
        QuickAccessGrid(
            onOpenAcademics = onOpenAcademics,
            onOpenMessages = onOpenMessages,
            onOpenPulse = onOpenPulse,
            onOpenTransport = onOpenTransport,
            onOpenTutor = onOpenTutor,
            onOpenScholarships = onOpenScholarships,
            onOpenIdCard = onOpenIdCard,
            onOpenLibrary = onOpenLibrary,
            onOpenEvents = onOpenEvents,
            unreadCount = unreadCount,
            latestMark = state.latestMark,
        )
        if (state.alerts.isNotEmpty()) {
            Announcements(alerts = state.alerts)
        }
        Spacer(Modifier.height(96.dp))
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PORTAL HEADER — child selector + bell icon
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun PortalHeader(
    child: DashboardChildSummary?,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    unreadNotifications: Int,
    onSelectChild: (String) -> Unit,
    onOpenNotifications: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChildSwitcher(
            child = child,
            children = children,
            selectedChildId = selectedChildId,
            onSelectChild = onSelectChild,
        )
        HeaderBellButton(
            badge = unreadNotifications,
            onClick = onOpenNotifications,
        )
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
                .padding(vertical = 4.dp),
        ) {
            Text(
                text = child?.name?.ifBlank { "Your child" } ?: "Your child",
                style = VTypography.wordmark,
                color = VColors.ink,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (canSwitch) {
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = "Switch child",
                    tint = VColors.ink3,
                    modifier = Modifier.size(14.dp),
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
private fun HeaderBellButton(
    badge: Int,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.9f else 1f

    Box(
        modifier = Modifier
            .size(38.dp)
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
            imageVector = Icons.Filled.Notifications,
            contentDescription = null,
            tint = VColors.ink2,
            modifier = Modifier.size(18.dp),
        )
        if (badge > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-3).dp, y = (-3).dp)
                    .size(17.dp)
                    .clip(VShapes.full)
                    .background(VColors.coral)
                    .border(2.dp, VColors.white, VShapes.full),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (badge > 9) "9+" else badge.toString(),
                    style = VTypography.caption,
                    color = VColors.white,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.graphicsLayer { scaleX = 0.8f; scaleY = 0.8f },
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CHILD HERO CARD — white card with decorative blobs, avatar, name+arrow, tags
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ChildHeroCard(
    child: DashboardChildSummary,
    attendanceRate: Int,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.98f else 1f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.lg)
            .background(VColors.white)
            .shadow(2.dp, VShapes.lg, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-40).dp)
                .size(120.dp)
                .clip(VShapes.full)
                .background(VColors.violetSoft.copy(alpha = 0.4f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = 30.dp)
                .size(70.dp)
                .clip(VShapes.full)
                .background(VColors.coralSoft.copy(alpha = 0.3f)),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(VShapes.full)
                    .background(VColors.white)
                    .padding(3.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(VShapes.full)
                        .background(VColors.violetSoft)
                        .border(2.dp, VColors.violetSoft, VShapes.full),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = child.name.split(" ").take(2).joinToString("") {
                            it.firstOrNull()?.uppercase() ?: ""
                        }.ifBlank { "?" },
                        style = VTypography.body,
                        color = VColors.violet,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = child.name,
                        style = VTypography.wordmark,
                        color = VColors.ink,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = VColors.ink3.copy(alpha = 0.6f),
                        modifier = Modifier.size(13.dp),
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Grade ${child.currentLevel}",
                    style = VTypography.caption,
                    color = VColors.ink3,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    HeroTag(
                        text = child.attendanceStatus.replaceFirstChar { it.uppercase() },
                        bg = VColors.coralSoft,
                        textColor = VColors.coral,
                    )
                    if (attendanceRate > 0) {
                        HeroTag(
                            text = "$attendanceRate% Attendance",
                            bg = VColors.surfaceTint,
                            textColor = VColors.ink2,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroTag(text: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(VShapes.full)
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            style = VTypography.caption,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.graphicsLayer { scaleX = 0.85f; scaleY = 0.85f },
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TODAY'S LEARNING — collapsible card with lesson descriptions + topic tags
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TodaysLearning(
    periods: List<LivePeriod>,
    coveredToday: List<CoveredUnit>,
    onOpenAcademics: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val visibleCount = if (expanded) periods.size else minOf(3, periods.size)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(VShapes.md)
                .background(VColors.white)
                .shadow(1.dp, VShapes.md, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Today's Learning",
                    style = VTypography.label,
                    color = VColors.ink,
                    fontWeight = FontWeight.ExtraBold,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (expanded) "Show less" else "Swipe to expand",
                        style = VTypography.caption,
                        color = VColors.ink3,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = VColors.ink3,
                        modifier = Modifier
                            .size(11.dp)
                            .graphicsLayer { rotationZ = if (expanded) 180f else 0f },
                    )
                }
            }
            periods.take(visibleCount).forEachIndexed { index, period ->
                LearnItemRow(
                    period = period,
                    coveredUnit = coveredToday.firstOrNull { it.subject.equals(period.subject, ignoreCase = true) },
                    showDivider = index > 0,
                )
            }
            if (periods.size > 3) {
                val interactionSource = remember { MutableInteractionSource() }
                val pressed by interactionSource.collectIsPressedAsState()
                val scale = if (pressed) 0.97f else 1f
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                        ) { expanded = !expanded }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = VColors.violet,
                        modifier = Modifier
                            .size(12.dp)
                            .graphicsLayer { rotationZ = if (expanded) 180f else 0f },
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (expanded) "Show less" else "Show all ${periods.size} classes",
                        style = VTypography.caption,
                        color = VColors.violet,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun LearnItemRow(
    period: LivePeriod,
    coveredUnit: CoveredUnit?,
    showDivider: Boolean,
) {
    val dotColor = when (period.relation) {
        -1 -> VColors.mint
        0 -> VColors.violet
        else -> VColors.line
    }
    val dotBorder = if (period.relation > 0) VColors.ink3 else Color.Transparent

    Column {
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(VColors.lineSoft),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(VShapes.full)
                        .background(dotColor)
                        .border(2.dp, dotBorder, VShapes.full),
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = period.subject,
                    style = VTypography.label,
                    color = VColors.ink,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${period.startTime} — ${period.endTime}",
                    style = VTypography.caption,
                    color = VColors.ink3,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            if (coveredUnit != null && coveredUnit.title.isNotBlank()) {
                Text(
                    text = coveredUnit.title,
                    style = VTypography.caption,
                    color = VColors.ink2,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 18.dp, top = 6.dp),
                )
            }
            if (period.teacherName.isNotBlank() || period.room.isNotBlank()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(start = 18.dp, top = 6.dp),
                ) {
                    if (period.teacherName.isNotBlank()) TopicTag(period.teacherName)
                    if (period.room.isNotBlank()) TopicTag("Room ${period.room}")
                }
            }
        }
    }
}

@Composable
private fun TopicTag(text: String) {
    Box(
        modifier = Modifier
            .clip(VShapes.full)
            .background(VColors.surfaceTint)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            style = VTypography.caption,
            color = VColors.ink2,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.graphicsLayer { scaleX = 0.85f; scaleY = 0.85f },
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// QUICK INSIGHTS — two side-by-side cards (Attendance + Fees)
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuickInsights(
    attendance: ParentAttendanceData?,
    fees: FeeData?,
    onOpenAcademics: () -> Unit,
    onOpenFees: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        InsightCard(
            icon = Icons.Filled.Check,
            iconBg = VColors.mintSoft,
            iconTint = VColors.success,
            bigNumber = if (attendance != null) "${attendance.attendanceRate}%" else "—",
            label = "Attendance",
            subText = if (attendance != null) "This term · ${attendance.presentDays}/${attendance.totalDays} days" else "No data",
            accentColor = VColors.mint,
            onClick = onOpenAcademics,
            modifier = Modifier.weight(1f),
        )
        InsightCard(
            icon = Icons.Filled.School,
            iconBg = VColors.coralSoft,
            iconTint = VColors.coral,
            bigNumber = fees?.outstandingFees?.ifBlank { "₹0" } ?: "₹0",
            label = "Fees Due",
            subText = if (fees != null && fees.overdueCount > 0) "${fees.overdueCount} overdue" else "All clear",
            accentColor = VColors.coral,
            onClick = onOpenFees,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun InsightCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    bigNumber: String,
    label: String,
    subText: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.98f else 1f

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.md)
            .background(VColors.white)
            .shadow(1.dp, VShapes.md, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { onClick() },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(3.dp)
                .background(accentColor.copy(alpha = 0.6f)),
        )
        Column(modifier = Modifier.padding(16.dp)) {
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
                    modifier = Modifier.size(15.dp),
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = bigNumber,
                style = VTypography.h3,
                color = VColors.ink,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = label.uppercase(),
                style = VTypography.caption,
                color = VColors.ink3,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.graphicsLayer { scaleX = 0.85f; scaleY = 0.85f },
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subText,
                style = VTypography.caption,
                color = VColors.ink3,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TODAY'S SCHEDULE — timeline with dots, time, subject, teacher, status
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TodaysSchedule(
    periods: List<LivePeriod>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "TODAY'S SCHEDULE",
                style = VTypography.caption,
                color = VColors.ink3,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.graphicsLayer { scaleX = 0.85f; scaleY = 0.85f },
            )
            Text(
                text = todayShortDate(),
                style = VTypography.caption,
                color = VColors.ink3,
                fontWeight = FontWeight.Medium,
            )
        }
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(VShapes.md)
                .background(VColors.white)
                .shadow(1.dp, VShapes.md, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            periods.forEachIndexed { index, period ->
                SchedulePeriodRow(
                    period = period,
                    isLast = index == periods.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun SchedulePeriodRow(
    period: LivePeriod,
    isLast: Boolean,
) {
    val dotColor = when (period.relation) {
        -1 -> VColors.mint
        0 -> VColors.violet
        else -> VColors.white
    }
    val dotBorder = when (period.relation) {
        -1 -> VColors.mint
        0 -> VColors.violet
        else -> VColors.ink3
    }
    val statusText = when (period.relation) {
        -1 -> "Done"
        0 -> "Now"
        else -> if (isLast) period.startTime else "Next"
    }
    val statusBg = when (period.relation) {
        -1 -> VColors.mintSoft
        0 -> VColors.violetSoft
        else -> VColors.surfaceTint
    }
    val statusFg = when (period.relation) {
        -1 -> VColors.success
        0 -> VColors.violet
        else -> VColors.ink3
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(VShapes.full)
                .background(dotColor)
                .border(2.dp, dotBorder, VShapes.full),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = period.startTime,
            style = VTypography.caption,
            color = VColors.ink3,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(48.dp),
        )
        Spacer(Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = period.subject,
                style = VTypography.label,
                color = VColors.ink,
                fontWeight = FontWeight.Bold,
            )
            if (period.teacherName.isNotBlank()) {
                Text(
                    text = period.teacherName,
                    style = VTypography.caption,
                    color = VColors.ink3,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(VShapes.full)
                .background(statusBg)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(
                text = statusText,
                style = VTypography.caption,
                color = statusFg,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.graphicsLayer { scaleX = 0.85f; scaleY = 0.85f },
            )
        }
    }
}

private fun todayShortDate(): String {
    val iso = com.littlebridge.enrollplus.util.todayIso()
    val parsed = com.littlebridge.enrollplus.util.parseIsoDate(iso)
    if (parsed == null) return ""
    val (y, m, d) = parsed
    val dow = com.littlebridge.enrollplus.util.dayOfWeek(y, m, d)
    val dayName = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")[dow]
    val monName = com.littlebridge.enrollplus.util.MONTH_SHORT.getOrNull(m - 1) ?: ""
    return "$dayName, $monName $d"
}

// ════════════════════════════════════════════════════════════════════════════
// QUICK ACCESS — 2-column grid of feature tiles
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuickAccessGrid(
    onOpenAcademics: () -> Unit,
    onOpenMessages: () -> Unit,
    onOpenPulse: () -> Unit,
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
            .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Text(
            text = "QUICK ACCESS",
            style = VTypography.caption,
            color = VColors.ink3,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .graphicsLayer { scaleX = 0.85f; scaleY = 0.85f }
                .padding(bottom = 10.dp),
        )
        val tiles = listOf(
            QuickTile("Academics", Icons.Filled.School, VColors.violet, VColors.violetSoft,
                latestMark?.let { "${it.marks?.toInt() ?: 0}/${it.maxMarks} ${it.subject}" } ?: "View progress",
                if (latestMark != null) "A+" else null, onOpenAcademics),
            QuickTile("Messages", Icons.Filled.School, VColors.sky, VColors.skySoft,
                if (unreadCount > 0) "$unreadCount unread" else "Inbox",
                if (unreadCount > 0) unreadCount.toString() else null, onOpenMessages),
            QuickTile("Health", Icons.Filled.Favorite, VColors.coral, VColors.coralSoft,
                "No alerts", null, onOpenPulse),
            QuickTile("Transport", Icons.Filled.DirectionsBus, VColors.mint, VColors.mintSoft,
                "Track bus", "On route", onOpenTransport),
            QuickTile("AI Tutor", Icons.Filled.RocketLaunch, VColors.violet, VColors.violetSoft,
                "Ask AI", null, onOpenTutor),
            QuickTile("Scholarships", Icons.Filled.WorkspacePremium, VColors.gold, VColors.goldSoft,
                "Apply now", "3", onOpenScholarships),
            QuickTile("Digital ID", Icons.Filled.Badge, VColors.ink2, VColors.surfaceTint,
                "View QR", null, onOpenIdCard),
            QuickTile("Library", Icons.Filled.LocalLibrary, VColors.sky, VColors.skySoft,
                "Books", "2", onOpenLibrary),
            QuickTile("Events", Icons.Filled.Event, VColors.violet, VColors.violetSoft,
                "Upcoming", "1", onOpenEvents),
        )
        // 2-column grid using chunked rows
        tiles.chunked(2).forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowTiles.forEach { tile ->
                    QuickTileCard(tile = tile, modifier = Modifier.weight(1f))
                }
                if (rowTiles.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private data class QuickTile(
    val label: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val subText: String,
    val badge: String?,
    val onClick: () -> Unit,
)

@Composable
private fun QuickTileCard(
    tile: QuickTile,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = if (pressed) 0.97f else 1f

    Column(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(VShapes.md)
            .background(VColors.white)
            .shadow(1.dp, VShapes.md, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { tile.onClick() }
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
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
                val badgeBg = when (tile.iconTint) {
                    VColors.coral -> VColors.coralSoft
                    VColors.gold -> VColors.goldSoft
                    VColors.mint -> VColors.mintSoft
                    VColors.violet -> VColors.violetSoft
                    else -> VColors.surfaceTint
                }
                Box(
                    modifier = Modifier
                        .clip(VShapes.full)
                        .background(badgeBg)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = tile.badge,
                        style = VTypography.caption,
                        color = tile.iconTint,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.graphicsLayer { scaleX = 0.85f; scaleY = 0.85f },
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = tile.label,
            style = VTypography.label,
            color = VColors.ink,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = tile.subText,
            style = VTypography.caption,
            color = VColors.ink3,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// ANNOUNCEMENTS — alert cards with left accent bar
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun Announcements(
    alerts: List<DashboardAlertDto>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "SCHOOL ANNOUNCEMENTS",
                style = VTypography.caption,
                color = VColors.ink3,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.graphicsLayer { scaleX = 0.85f; scaleY = 0.85f },
            )
            Text(
                text = "View all",
                style = VTypography.caption,
                color = VColors.violet,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(10.dp))
        alerts.take(4).forEach { alert ->
            AnnouncementCard(alert = alert)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AnnouncementCard(alert: DashboardAlertDto) {
    val accentColor = when (alert.type) {
        "CRITICAL" -> VColors.coral
        "WARNING" -> VColors.gold
        else -> VColors.sky
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.md)
            .background(VColors.white)
            .shadow(1.dp, VShapes.md, ambientColor = VColors.ink.copy(alpha = 0.04f), spotColor = VColors.ink.copy(alpha = 0.06f))
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(40.dp)
                .clip(VShapes.sm)
                .background(accentColor),
        )
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(VShapes.sm)
                .background(VColors.skySoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = VColors.sky,
                modifier = Modifier.size(15.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alert.title,
                style = VTypography.label,
                color = VColors.ink,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = alert.value,
                style = VTypography.caption,
                color = VColors.ink2,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
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
        Box(Modifier.fillMaxWidth().height(120.dp).clip(VShapes.lg).background(VColors.lineSoft))
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(180.dp).clip(VShapes.lg).background(VColors.lineSoft))
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
