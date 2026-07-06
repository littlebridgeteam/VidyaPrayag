package com.littlebridge.enrollplus.ui.screens.parent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LocalLibrary
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.SportsScore
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.domain.util.UiState
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentAnnouncementDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentDailyLogEntryDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentPeriodDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentTimetableDayDto
import com.littlebridge.enrollplus.presentation.ParentViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.util.formatDateShort
import com.littlebridge.enrollplus.util.todayIso
import com.littlebridge.enrollplus.util.todayWeekday

@Composable
fun ParentHomeTab(
    viewModel: ParentViewModel,
    onOverlayOpen: (ParentOverlay) -> Unit,
    onTabSwitch: (ParentTab) -> Unit,
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val attendanceState by viewModel.attendanceState.collectAsState()
    val feesState by viewModel.feesState.collectAsState()
    val timetableState by viewModel.timetableState.collectAsState()
    val dailySummaryState by viewModel.dailySummaryState.collectAsState()
    val announcementsState by viewModel.announcementsState.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val children by viewModel.children.collectAsState()
    val selectedChildId by viewModel.selectedChildId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.cream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Portal Header ──
        PortalHeader(
            dashboardState = dashboardState,
            children = children,
            selectedChildId = selectedChildId,
            onChildSelect = { viewModel.selectChild(it) },
            onNotificationsClick = { onOverlayOpen(ParentOverlay.Notifications) },
            unreadNotifications = when (val s = announcementsState) {
                is UiState.Success -> s.data.announcements.size
                else -> 0
            },
        )

        when (dashboardState) {
            is UiState.Loading -> HomeLoadingState()
            is UiState.Error -> HomeErrorState(
                message = (dashboardState as UiState.Error).message,
                onRetry = { viewModel.loadDashboard() },
            )
            is UiState.Success -> {
                val data = (dashboardState as UiState.Success).data
                val child = data.children.firstOrNull { it.id == selectedChildId }
                    ?: data.children.firstOrNull()
                    ?: data.childSummary

                if (child != null) {
                    // Greeting
                    Text(
                        text = data.greeting,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.2.sp,
                        ),
                        color = VColors.ink3,
                        modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 2.dp),
                    )

                    // Child Hero Card
                    ChildHeroCard(
                        child = child,
                        attendanceRate = when (val s = attendanceState) {
                            is UiState.Success -> s.data.attendanceRate
                            else -> 0
                        },
                        onClick = { onTabSwitch(ParentTab.Academics) },
                    )

                    // Today's Learning Summary
                    TodayLearningSection(
                        dailySummaryState = dailySummaryState,
                    )

                    // Quick Insights (Attendance + Fees)
                    QuickInsightsRow(
                        attendanceRate = when (val s = attendanceState) {
                            is UiState.Success -> s.data.attendanceRate
                            else -> 0
                        },
                        presentDays = when (val s = attendanceState) {
                            is UiState.Success -> s.data.presentDays
                            else -> 0
                        },
                        totalDays = when (val s = attendanceState) {
                            is UiState.Success -> s.data.totalDays
                            else -> 0
                        },
                        outstandingFees = when (val s = feesState) {
                            is UiState.Success -> s.data.outstandingFees
                            else -> "—"
                        },
                        overdueCount = when (val s = feesState) {
                            is UiState.Success -> s.data.overdueCount
                            else -> 0
                        },
                        onAttendanceClick = { onTabSwitch(ParentTab.Academics) },
                        onFeesClick = { onTabSwitch(ParentTab.Fees) },
                    )

                    // Today's Schedule Timeline
                    TodayScheduleSection(
                        timetableState = timetableState,
                    )

                    // Quick Access Grid
                    QuickAccessSection(
                        unreadMessages = unreadCount,
                        onOverlayOpen = onOverlayOpen,
                        onTabSwitch = onTabSwitch,
                    )

                    // Announcements
                    AnnouncementsSection(
                        announcementsState = announcementsState,
                        onViewAll = { onTabSwitch(ParentTab.Conversations) },
                    )
                } else {
                    // No child linked — should be caught by UnlinkedParentGate in shell
                    HomeLoadingState()
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

// ════════════════════════════════════════════════════════════════
// Portal Header
// ════════════════════════════════════════════════════════════════

@Composable
private fun PortalHeader(
    dashboardState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentDashboardData>,
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    onChildSelect: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    unreadNotifications: Int,
) {
    var showChildPicker by remember { mutableStateOf(false) }

    val childName = when (val s = dashboardState) {
        is UiState.Success -> {
            val child = s.data.children.firstOrNull { it.id == selectedChildId }
                ?: s.data.children.firstOrNull()
                ?: s.data.childSummary
            child?.name ?: "—"
        }
        else -> "—"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Enroll+ Parent",
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                ),
                color = VColors.ink3,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    if (children.size > 1) showChildPicker = !showChildPicker
                },
            ) {
                Text(
                    text = childName,
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.4).sp,
                    ),
                    color = VColors.ink,
                )
                if (children.size > 1) {
                    Icon(
                        imageVector = if (showChildPicker) Icons.AutoMirrored.Rounded.TrendingUp else Icons.AutoMirrored.Rounded.TrendingDown,
                        contentDescription = null,
                        tint = VColors.ink3,
                        modifier = Modifier.size(14.dp).padding(start = 4.dp),
                    )
                }
            }
        }

        Box {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(VColors.white, VShapes.full)
                    .shadow(1.dp, VShapes.full)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onNotificationsClick() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = "Notifications",
                    tint = VColors.ink2,
                    modifier = Modifier.size(18.dp),
                )
            }
            if (unreadNotifications > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(17.dp)
                        .background(VColors.coral, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = unreadNotifications.coerceAtMost(9).toString(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VColors.white,
                    )
                }
            }
        }
    }

    // Child picker dropdown
    AnimatedVisibility(visible = showChildPicker && children.size > 1) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .background(VColors.white, VShapes.md)
                .shadow(2.dp, VShapes.md)
                .padding(vertical = 4.dp),
        ) {
            children.forEach { child ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            onChildSelect(child.id)
                            showChildPicker = false
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(VColors.violetSoft, VShapes.full),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = child.name.take(2).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = VColors.violet,
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = child.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (child.id == selectedChildId) VColors.violet else VColors.ink,
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// Child Hero Card
// ════════════════════════════════════════════════════════════════

@Composable
private fun ChildHeroCard(
    child: DashboardChildSummary,
    attendanceRate: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .background(VColors.white, VShapes.lg)
            .shadow(1.dp, VShapes.lg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(20.dp),
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(120.dp)
                .background(VColors.violetSoft.copy(alpha = 0.4f), CircleShape),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(VColors.violetSoft, VShapes.full),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = child.name.take(2).uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VColors.violet,
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = child.name,
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.4).sp,
                        ),
                        color = VColors.ink,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                        contentDescription = null,
                        tint = VColors.ink3,
                        modifier = Modifier.size(13.dp).padding(start = 6.dp),
                    )
                }
                Text(
                    text = "Level ${child.currentLevel} · ${if (attendanceRate > 0) "$attendanceRate% Attendance" else child.attendanceStatus}",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = VColors.ink3,
                    modifier = Modifier.padding(top = 2.dp),
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    HeroTag(
                        text = "${(child.overallProgress * 100).toInt()}% Progress",
                        bg = VColors.violetSoft,
                        color = VColors.violet,
                    )
                    if (attendanceRate > 0) {
                        HeroTag(
                            text = "$attendanceRate% Present",
                            bg = VColors.mintSoft,
                            color = VColors.success,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroTag(text: String, bg: Color, color: Color) {
    Box(
        modifier = Modifier
            .background(bg, VShapes.full)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 0.3.sp,
        )
    }
}

// ════════════════════════════════════════════════════════════════
// Today's Learning Section
// ════════════════════════════════════════════════════════════════

@Composable
private fun TodayLearningSection(
    dailySummaryState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentDailySummaryData>,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Today's Learning",
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            ),
            color = VColors.ink3,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        when (val s = dailySummaryState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VColors.white, VShapes.md)
                        .shadow(1.dp, VShapes.md)
                        .padding(16.dp),
                ) {
                    Text(
                        text = "Loading today's learning...",
                        style = VTypography.body,
                        color = VColors.ink3,
                    )
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VColors.white, VShapes.md)
                        .shadow(1.dp, VShapes.md)
                        .padding(16.dp),
                ) {
                    Text(
                        text = "Could not load learning summary",
                        style = VTypography.body,
                        color = VColors.ink3,
                    )
                }
            }
            is UiState.Success -> {
                val entries = s.data.entries
                if (entries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VColors.white, VShapes.md)
                            .shadow(1.dp, VShapes.md)
                            .padding(16.dp),
                    ) {
                        Text(
                            text = "No classes logged today yet",
                            style = VTypography.body,
                            color = VColors.ink3,
                        )
                    }
                } else {
                    val visibleEntries = if (expanded) entries else entries.take(3)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VColors.white, VShapes.md)
                            .shadow(1.dp, VShapes.md),
                    ) {
                        Column {
                            visibleEntries.forEach { entry ->
                                LearningItem(entry = entry)
                            }
                            if (entries.size > 3) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                        ) { expanded = !expanded }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = if (expanded) "Show less" else "Show all ${entries.size} classes",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = VColors.violet,
                                        letterSpacing = 0.3.sp,
                                    )
                                    Icon(
                                        imageVector = if (expanded) Icons.AutoMirrored.Rounded.TrendingUp else Icons.AutoMirrored.Rounded.TrendingDown,
                                        contentDescription = null,
                                        tint = VColors.violet,
                                        modifier = Modifier.size(12.dp).padding(start = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LearningItem(entry: ParentDailyLogEntryDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(VColors.violet, CircleShape),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = entry.subject,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = VColors.ink,
                modifier = Modifier.weight(1f),
                letterSpacing = (-0.2).sp,
            )
            Text(
                text = "${entry.coveragePct}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = VColors.ink3,
            )
        }
        if (entry.summaryText.isNotBlank()) {
            Text(
                text = entry.summaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink2,
                lineHeight = 18.sp,
                modifier = Modifier.padding(start = 18.dp, top = 6.dp),
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// Quick Insights Row
// ════════════════════════════════════════════════════════════════

@Composable
private fun QuickInsightsRow(
    attendanceRate: Int,
    presentDays: Int,
    totalDays: Int,
    outstandingFees: String,
    overdueCount: Int,
    onAttendanceClick: () -> Unit,
    onFeesClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        InsightCard(
            icon = Icons.Rounded.CheckCircle,
            iconBg = VColors.mintSoft,
            iconTint = VColors.success,
            value = if (attendanceRate > 0) "$attendanceRate%" else "—",
            label = "Attendance",
            sub = if (totalDays > 0) "This term · $presentDays/$totalDays days" else "Loading...",
            accentBar = VColors.mint,
            onClick = onAttendanceClick,
            modifier = Modifier.weight(1f),
        )
        InsightCard(
            icon = Icons.Rounded.CurrencyRupee,
            iconBg = VColors.coralSoft,
            iconTint = VColors.coral,
            value = outstandingFees,
            label = "Fees Due",
            sub = if (overdueCount > 0) "$overdueCount overdue" else "No overdue",
            accentBar = VColors.coral,
            onClick = onFeesClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun InsightCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    value: String,
    label: String,
    sub: String,
    accentBar: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(iconBg, VShapes.sm),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VColors.ink,
                letterSpacing = (-0.3).sp,
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 5.dp),
                letterSpacing = 0.5.sp,
            )
            Text(
                text = sub,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        // Accent bar at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(3.dp)
                .background(accentBar.copy(alpha = 0.6f), VShapes.md),
        )
    }
}

// ════════════════════════════════════════════════════════════════
// Today's Schedule Timeline
// ════════════════════════════════════════════════════════════════

@Composable
private fun TodayScheduleSection(
    timetableState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentTimetableData>,
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Today's Schedule",
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                ),
                color = VColors.ink3,
            )
            Text(
                text = formatDateShort(todayIso()),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
            )
        }
        Spacer(Modifier.height(10.dp))

        when (val s = timetableState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VColors.white, VShapes.md)
                        .shadow(1.dp, VShapes.md)
                        .padding(16.dp),
                ) {
                    Text("Loading schedule...", style = VTypography.body, color = VColors.ink3)
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VColors.white, VShapes.md)
                        .shadow(1.dp, VShapes.md)
                        .padding(16.dp),
                ) {
                    Text("Could not load schedule", style = VTypography.body, color = VColors.ink3)
                }
            }
            is UiState.Success -> {
                val todayIdx = todayWeekday() // 1=Mon … 7=Sun
                val today: ParentTimetableDayDto? = s.data.weekdays.firstOrNull { it.weekday == todayIdx }
                if (today == null || today.periods.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VColors.white, VShapes.md)
                            .shadow(1.dp, VShapes.md)
                            .padding(16.dp),
                    ) {
                        Text("No classes scheduled today", style = VTypography.body, color = VColors.ink3)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VColors.white, VShapes.md)
                            .shadow(1.dp, VShapes.md)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                    ) {
                        Column {
                            val nowIdx = today.nowIndex
                            today.periods.forEachIndexed { index, period ->
                                TimelinePeriod(
                                    period = period,
                                    isDone = nowIdx != null && index < nowIdx,
                                    isNow = nowIdx == index,
                                    isUpcoming = nowIdx == null || index > nowIdx,
                                )
                                if (index < today.periods.lastIndex) {
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelinePeriod(
    period: ParentPeriodDto,
    isDone: Boolean,
    isNow: Boolean,
    isUpcoming: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Dot
        val dotColor = when {
            isDone -> VColors.mint
            isNow -> VColors.violet
            else -> VColors.line
        }
        val dotBorder = when {
            isDone -> VColors.mint
            isNow -> VColors.violet
            else -> VColors.ink3
        }
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(dotColor, CircleShape),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = period.startTime,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = VColors.ink3,
            modifier = Modifier.width(48.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = period.subject,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = VColors.ink,
                letterSpacing = (-0.2).sp,
            )
            if (period.teacherName.isNotBlank()) {
                Text(
                    text = period.teacherName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = VColors.ink3,
                    modifier = Modifier.padding(top = 1.dp),
                )
            }
        }
        // Status badge
        val (statusText, statusBg, statusColor) = when {
            isDone -> Triple("Done", VColors.mintSoft, VColors.success)
            isNow -> Triple("Now", VColors.violetSoft, VColors.violet)
            else -> Triple("Next", VColors.surfaceTint, VColors.ink3)
        }
        Box(
            modifier = Modifier
                .background(statusBg, VShapes.full)
                .padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(
                text = statusText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                letterSpacing = 0.3.sp,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// Quick Access Grid
// ════════════════════════════════════════════════════════════════

private data class QuickTile(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val label: String,
    val sub: String,
    val badge: String? = null,
    val badgeBg: Color = VColors.surfaceTint,
    val badgeColor: Color = VColors.ink3,
    val onClick: () -> Unit,
)

@Composable
private fun QuickAccessSection(
    unreadMessages: Int,
    onOverlayOpen: (ParentOverlay) -> Unit,
    onTabSwitch: (ParentTab) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(
            text = "Quick Access",
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            ),
            color = VColors.ink3,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        val tiles = listOf(
            QuickTile(
                icon = Icons.AutoMirrored.Rounded.MenuBook,
                iconBg = VColors.violetSoft,
                iconTint = VColors.violet,
                label = "Academics",
                sub = "View marks & attendance",
                onClick = { onTabSwitch(ParentTab.Academics) },
            ),
            QuickTile(
                icon = Icons.AutoMirrored.Rounded.Chat,
                iconBg = VColors.skySoft,
                iconTint = VColors.sky,
                label = "Messages",
                sub = if (unreadMessages > 0) "$unreadMessages unread" else "No new messages",
                badge = if (unreadMessages > 0) unreadMessages.toString() else null,
                badgeBg = VColors.coralSoft,
                badgeColor = VColors.coral,
                onClick = { onTabSwitch(ParentTab.Conversations) },
            ),
            QuickTile(
                icon = Icons.Rounded.Favorite,
                iconBg = VColors.coralSoft,
                iconTint = VColors.coral,
                label = "Health",
                sub = "Health records & pulse",
                onClick = { onOverlayOpen(ParentOverlay.Health) },
            ),
            QuickTile(
                icon = Icons.Rounded.DirectionsBus,
                iconBg = VColors.mintSoft,
                iconTint = VColors.success,
                label = "Transport",
                sub = "Track bus route",
                onClick = { onOverlayOpen(ParentOverlay.Transport) },
            ),
            QuickTile(
                icon = Icons.Rounded.RocketLaunch,
                iconBg = VColors.violetSoft,
                iconTint = VColors.violet,
                label = "AI Tutor",
                sub = "Practice with AI",
                onClick = { onOverlayOpen(ParentOverlay.TutorChat) },
            ),
            QuickTile(
                icon = Icons.Rounded.SportsScore,
                iconBg = VColors.goldSoft,
                iconTint = VColors.gold,
                label = "Scholarships",
                sub = "Available scholarships",
                onClick = { onOverlayOpen(ParentOverlay.Scholarships) },
            ),
            QuickTile(
                icon = Icons.Rounded.AccountBox,
                iconBg = VColors.surfaceTint,
                iconTint = VColors.ink2,
                label = "Digital ID",
                sub = "View ID card",
                onClick = { onOverlayOpen(ParentOverlay.DigitalIdCard) },
            ),
            QuickTile(
                icon = Icons.Rounded.LocalLibrary,
                iconBg = VColors.skySoft,
                iconTint = VColors.sky,
                label = "Library",
                sub = "Borrowed books",
                onClick = { onOverlayOpen(ParentOverlay.Library) },
            ),
            QuickTile(
                icon = Icons.Rounded.CalendarMonth,
                iconBg = VColors.violetSoft,
                iconTint = VColors.violet,
                label = "Events",
                sub = "School events",
                onClick = { onOverlayOpen(ParentOverlay.Events) },
            ),
        )

        // 2-column grid using simple Rows
        val rows = tiles.chunked(2)
        rows.forEach { rowTiles ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowTiles.forEach { tile ->
                    QuickTileCard(tile = tile, modifier = Modifier.weight(1f))
                }
                if (rowTiles.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickTileCard(tile: QuickTile, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
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
                        .size(34.dp)
                        .background(tile.iconBg, VShapes.sm),
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
                            .background(tile.badgeBg, VShapes.full)
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = tile.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tile.badgeColor,
                            letterSpacing = 0.3.sp,
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = tile.label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = VColors.ink,
                letterSpacing = (-0.2).sp,
            )
            Text(
                text = tile.sub,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// Announcements Section
// ════════════════════════════════════════════════════════════════

@Composable
private fun AnnouncementsSection(
    announcementsState: UiState<com.littlebridge.enrollplus.feature.parent.domain.model.ParentAnnouncementsData>,
    onViewAll: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "School Announcements",
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                ),
                color = VColors.ink3,
            )
            Text(
                text = "View all",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = VColors.violet,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onViewAll() },
            )
        }
        Spacer(Modifier.height(10.dp))

        when (val s = announcementsState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VColors.white, VShapes.md)
                        .shadow(1.dp, VShapes.md)
                        .padding(16.dp),
                ) {
                    Text("Loading announcements...", style = VTypography.body, color = VColors.ink3)
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VColors.white, VShapes.md)
                        .shadow(1.dp, VShapes.md)
                        .padding(16.dp),
                ) {
                    Text("Could not load announcements", style = VTypography.body, color = VColors.ink3)
                }
            }
            is UiState.Success -> {
                val anns = s.data.announcements.take(3)
                if (anns.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VColors.white, VShapes.md)
                            .shadow(1.dp, VShapes.md)
                            .padding(16.dp),
                    ) {
                        Text("No announcements", style = VTypography.body, color = VColors.ink3)
                    }
                } else {
                    anns.forEach { ann ->
                        AnnouncementCard(ann = ann)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnouncementCard(ann: ParentAnnouncementDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.white, VShapes.md)
            .shadow(1.dp, VShapes.md)
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(VColors.skySoft, VShapes.sm),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Event,
                contentDescription = null,
                tint = VColors.sky,
                modifier = Modifier.size(15.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ann.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = VColors.ink,
                letterSpacing = (-0.2).sp,
            )
            Text(
                text = ann.description,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = VColors.ink2,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
            Text(
                text = ann.date,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = VColors.ink3,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// Loading / Error States
// ════════════════════════════════════════════════════════════════

@Composable
private fun HomeLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("Loading dashboard...", style = VTypography.body, color = VColors.ink3)
    }
}

@Composable
private fun HomeErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Could not load dashboard",
            style = VTypography.h3,
            color = VColors.ink,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Text(
            text = message,
            style = VTypography.body,
            color = VColors.ink3,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Text(
            text = "Retry",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = VColors.violet,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onRetry() },
        )
    }
}
