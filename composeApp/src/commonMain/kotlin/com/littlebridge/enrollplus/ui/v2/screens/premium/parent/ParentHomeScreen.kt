package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.AttendanceDayState
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VPullRefreshPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentHomeScreen(
    onOpenOverlay: (ParentOverlay) -> Unit,
    onSwitchTab: (Int) -> Unit = {},
    onLinkChild: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ParentDashboardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    VPullRefreshPremium(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.load() },
        modifier = modifier.fillMaxSize(),
    ) {
        VStateHostPremium(
            loading = state.isLoading && state.children.isEmpty(),
            error = state.error,
            isEmpty = state.children.isEmpty() && !state.isLoading,
            modifier = Modifier.fillMaxSize(),
            emptyTitle = "No child linked",
            emptyBody = "Link your child's school to see their attendance, marks, fees, and updates.",
            emptyIcon = Icons.Filled.School,
            onRetry = { viewModel.load() },
            skeleton = { HomeSkeleton() },
        ) {
            HomeContent(
                state = state,
                onOpenOverlay = onOpenOverlay,
                onSwitchTab = onSwitchTab,
                onSelectChild = { viewModel.selectChild(it) },
                onLinkChild = onLinkChild,
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState,
    onOpenOverlay: (ParentOverlay) -> Unit,
    onSwitchTab: (Int) -> Unit,
    onSelectChild: (String) -> Unit,
    onLinkChild: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 140.dp),
    ) {
        val child = state.selectedChild

        // 1. Child switcher (only if multiple children)
        if (state.children.size > 1) {
            ChildSwitcher(
                children = state.children.map { it.id to it.name },
                selectedId = state.selectedChildId,
                onSelect = onSelectChild,
            )
            Spacer(Modifier.height(16.dp))
        }

        // 2. Summary card — tonal surface, no gradient, no pulsing dot
        if (child != null) {
            ChildSummaryCard(
                name = child.name,
                className = "Class ${child.currentLevel}",
                attendance = state.attendance?.attendanceRate?.let { "$it%" } ?: "--",
                latestMark = state.latestMark?.let { m ->
                    m.marks?.let { "${it.toInt()}/${m.maxMarks}" } ?: "--"
                } ?: "--",
                feesDue = state.fees?.outstandingFees ?: "--",
                attendanceState = state.today.state,
                onClick = { onSwitchTab(1) },
            )
        }

        Spacer(Modifier.height(24.dp))

        // 3. Today's schedule
        if (state.todayPeriods.isNotEmpty()) {
            VSectionHeader(title = "Today's Schedule", modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.todayPeriods.forEach { period ->
                    PeriodCard(period = period)
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // 4. Quick actions — 6 key actions in 2 rows, not 10
        VSectionHeader(title = "Quick Actions", modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val actions = listOf(
                QuickAction("Fees", Icons.Filled.Payments, VColors.PrimaryContainer, VColors.OnPrimaryContainer) { onSwitchTab(2) },
                QuickAction("Messages", Icons.AutoMirrored.Filled.Chat, VColors.SecondaryContainer, VColors.OnSecondaryContainer) { onSwitchTab(3) },
                QuickAction("Transport", Icons.Filled.DirectionsBus, VColors.TertiaryContainer, VColors.OnTertiaryContainer) { onOpenOverlay(ParentOverlay.Transport) },
                QuickAction("Health", Icons.Filled.HealthAndSafety, VColors.ErrorContainer, VColors.OnErrorContainer) { onOpenOverlay(ParentOverlay.Health) },
                QuickAction("Library", Icons.Filled.LocalLibrary, VColors.SecondaryContainer, VColors.OnSecondaryContainer) { onOpenOverlay(ParentOverlay.Library) },
                QuickAction("ID Card", Icons.Filled.Badge, VColors.PrimaryContainer, VColors.OnPrimaryContainer) { onOpenOverlay(ParentOverlay.IDCard) },
            )
            actions.chunked(3).forEach { rowActions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowActions.forEach { action ->
                        ActionTile(
                            label = action.label,
                            icon = action.icon,
                            iconBg = action.iconBg,
                            iconColor = action.iconColor,
                            onClick = action.onClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (rowActions.size < 3) {
                        Spacer(Modifier.weight((3 - rowActions.size).toFloat()))
                    }
                }
            }
        }

        // 4b. Secondary actions — compact row of overlay links
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SecondaryAction("Calendar", Icons.Filled.CalendarMonth) { onOpenOverlay(ParentOverlay.Calendar) }
            SecondaryAction("Events", Icons.Filled.School) { onOpenOverlay(ParentOverlay.Events) }
            SecondaryAction("Scholarships", Icons.Filled.School) { onOpenOverlay(ParentOverlay.Scholarships) }
            SecondaryAction("Attendance", Icons.AutoMirrored.Filled.Assignment) { onSwitchTab(1) }
        }

        Spacer(Modifier.height(24.dp))

        // 5. Recent updates feed (max 5)
        if (state.alerts.isNotEmpty()) {
            VSectionHeader(title = "Recent Updates", modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.alerts.take(5).forEach { alert ->
                    UpdateCard(
                        title = alert.title,
                        body = alert.value,
                        type = alert.type,
                        onClick = {
                            when (alert.type) {
                                "CRITICAL" -> onOpenOverlay(ParentOverlay.Notifications)
                                "WARNING" -> onOpenOverlay(ParentOverlay.Notifications)
                                else -> onOpenOverlay(ParentOverlay.Notifications)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ChildSummaryCard(
    name: String,
    className: String,
    attendance: String,
    latestMark: String,
    feesDue: String,
    attendanceState: AttendanceDayState,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val initials = remember(name) {
        name.split(" ").take(2).joinToString("") { it.firstOrNull()?.toString() ?: "" }
    }
    val attendanceLabel = when (attendanceState) {
        AttendanceDayState.Present -> "Present"
        AttendanceDayState.Absent -> "Absent"
        AttendanceDayState.Late -> "Late"
        AttendanceDayState.Holiday -> "Holiday"
        AttendanceDayState.Sunday -> "Sunday"
        AttendanceDayState.Vacation -> "Vacation"
        AttendanceDayState.NoData -> "--"
    }
    val attendanceColor = when (attendanceState) {
        AttendanceDayState.Present -> VColors.Primary
        AttendanceDayState.Absent -> VColors.Error
        AttendanceDayState.Late -> VColors.WarmOrange
        else -> VColors.OnSurfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurMedium2)
            .pressScale(interaction, pressedScale = 0.98f)
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLow)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(20.dp),
    ) {
        // Top: avatar + name + class + attendance badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(VShapes.Md)
                    .background(VColors.PrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    style = VTypography.HeroName.copy(color = VColors.OnPrimaryContainer, fontSize = 18.sp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
                )
                Text(
                    text = className,
                    style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                )
            }
            // Attendance badge
            Row(
                modifier = Modifier
                    .clip(VShapes.Full)
                    .background(attendanceColor.copy(alpha = 0.12f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(attendanceColor),
                )
                Text(
                    text = attendanceLabel,
                    style = VTypography.ScheduleStatus.copy(color = attendanceColor),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Stats row — 3 inline stats, no glassmorphism
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StatItem(value = attendance, label = "ATTENDANCE")
            StatDivider()
            StatItem(value = latestMark, label = "LATEST MARK")
            StatDivider()
            StatItem(value = feesDue, label = "FEES DUE")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = VTypography.QuickStatLabel.copy(color = VColors.OnSurfaceVariant),
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        Modifier
            .width(1.dp)
            .height(32.dp)
            .background(VColors.OutlineVariant),
    )
}

@Composable
private fun ActionTile(
    label: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .pressScale(interaction, pressedScale = 0.96f)
            .shapeMorph(interaction, VShapes.LgDp, VShapes.XlDp, VMotion.DurShort2)
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(VShapes.Md)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Text(
            text = label,
            style = VTypography.ActionCardTitle.copy(color = VColors.OnSurface),
        )
    }
}

@Composable
private fun SecondaryAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .pressScale(interaction, pressedScale = 0.95f)
            .clip(VShapes.Full)
            .background(VColors.SurfaceContainerHigh)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(16.dp))
        Text(
            text = label,
            style = VTypography.SubTab.copy(color = VColors.OnSurfaceVariant),
        )
    }
}

@Composable
private fun UpdateCard(
    title: String,
    body: String,
    type: String,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val typeColor = when (type) {
        "CRITICAL" -> VColors.Error
        "WARNING" -> VColors.WarmOrange
        else -> VColors.Primary
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurMedium2)
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(typeColor),
            )
            Text(
                text = type,
                style = VTypography.UpdateSource.copy(color = typeColor),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = title,
            style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = body,
            style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant),
        )
    }
}

@Composable
private fun ChildSwitcher(
    children: List<Pair<String, String>>,
    selectedId: String?,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        children.forEach { (id, name) ->
            VFilterChip(
                label = name,
                active = id == selectedId,
                onClick = { onSelect(id) },
            )
        }
    }
}

@Composable
private fun PeriodCard(period: LivePeriod) {
    val statusColor = when (period.relation) {
        -1 -> VColors.OnSurfaceVariant
        0 -> VColors.Primary
        else -> VColors.Outline
    }
    val statusText = when (period.relation) {
        -1 -> "Finished"
        0 -> "Now"
        else -> "Upcoming"
    }
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.LgDp, VShapes.XlDp, VMotion.DurShort2)
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .clickable(interactionSource = interaction, indication = null, onClick = {})
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.width(56.dp),
        ) {
            Text(
                text = period.startTime,
                style = VTypography.BodyMedium.copy(color = VColors.OnSurface),
            )
            Text(
                text = period.endTime,
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = period.subject,
                style = VTypography.ScheduleSubject.copy(color = VColors.OnSurface),
            )
            Text(
                text = period.teacherName,
                style = VTypography.ScheduleTeacher.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor),
            )
            Text(
                text = statusText,
                style = VTypography.ScheduleStatus.copy(color = statusColor),
            )
        }
    }
}

@Composable
private fun HomeSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VShimmerBoxPremium(height = 140.dp, shape = VShapes.Xl)
        repeat(3) {
            VShimmerBoxPremium(height = 60.dp, shape = VShapes.Lg)
        }
        VShimmerBoxPremium(height = 80.dp, shape = VShapes.Lg)
    }
}

private data class QuickAction(
    val label: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconColor: Color,
    val onClick: () -> Unit,
)


