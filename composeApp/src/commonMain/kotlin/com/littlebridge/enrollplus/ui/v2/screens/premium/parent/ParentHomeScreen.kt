package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.AttendanceDayState
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStat
import com.littlebridge.enrollplus.ui.v2.components.cards.VHeroCard
import com.littlebridge.enrollplus.ui.v2.components.cards.VQuickStatCard
import com.littlebridge.enrollplus.ui.v2.components.cards.VUpdateCard
import com.littlebridge.enrollplus.ui.v2.components.misc.VPullRefreshPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerCardPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
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
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
            Spacer(Modifier.height(12.dp))
        }

        // 2. Hero card
        if (child != null) {
            val initials = child.name.split(" ").take(2).joinToString("") { it.firstOrNull()?.toString() ?: "" }
            val attendanceVal = state.attendance?.attendanceRate?.let { "$it%" } ?: "--"
            val marksVal = state.latestMark?.let { m ->
                m.marks?.let { "${it.toInt()}/${m.maxMarks}" } ?: "--"
            } ?: "--"
            val feesVal = state.fees?.outstandingFees ?: "--"

            VHeroCard(
                studentInitials = initials,
                studentName = child.name,
                studentClass = "Class ${child.currentLevel}",
                stats = listOf(
                    HeroStat(attendanceVal, "ATTENDANCE"),
                    HeroStat(marksVal, "LATEST MARK"),
                    HeroStat(feesVal, "FEES DUE"),
                ),
                onClick = { onSwitchTab(1) },
                liveLabel = if (state.today.state == AttendanceDayState.Present) "PRESENT" else "LIVE",
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        Spacer(Modifier.height(24.dp))

        // 3. Today's schedule
        if (state.todayPeriods.isNotEmpty()) {
            VSectionHeader(title = "Today's Schedule", modifier = Modifier.padding(horizontal = 4.dp))
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

        // 4. Quick actions grid (2-up)
        VSectionHeader(title = "Quick Actions", modifier = Modifier.padding(horizontal = 4.dp))
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val actions = listOf(
                QuickAction("Fees", Icons.Filled.Payments, VColors.PrimaryContainer, VColors.Primary) { onSwitchTab(2) },
                QuickAction("Messages", Icons.AutoMirrored.Filled.Chat, VColors.SecondaryContainer, VColors.OnSecondaryContainer) { onSwitchTab(3) },
                QuickAction("Attendance", Icons.AutoMirrored.Filled.Assignment, VColors.TertiaryContainer, VColors.OnTertiaryContainer) { onSwitchTab(1) },
                QuickAction("Transport", Icons.Filled.DirectionsBus, VColors.PrimaryContainer, VColors.Primary) { onOpenOverlay(ParentOverlay.Transport) },
                QuickAction("Health", Icons.Filled.HealthAndSafety, VColors.ErrorContainer, VColors.OnErrorContainer) { onOpenOverlay(ParentOverlay.Health) },
                QuickAction("Library", Icons.Filled.LocalLibrary, VColors.SecondaryContainer, VColors.OnSecondaryContainer) { onOpenOverlay(ParentOverlay.Library) },
                QuickAction("Scholarships", Icons.Filled.Sports, VColors.TertiaryContainer, VColors.OnTertiaryContainer) { onOpenOverlay(ParentOverlay.Scholarships) },
                QuickAction("ID Card", Icons.Filled.Badge, VColors.PrimaryContainer, VColors.Primary) { onOpenOverlay(ParentOverlay.IDCard) },
                QuickAction("Events", Icons.Filled.Event, VColors.SecondaryContainer, VColors.OnSecondaryContainer) { onOpenOverlay(ParentOverlay.Events) },
                QuickAction("Calendar", Icons.Filled.CalendarMonth, VColors.TertiaryContainer, VColors.OnTertiaryContainer) { onOpenOverlay(ParentOverlay.Calendar) },
            )
            actions.chunked(2).forEach { rowActions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowActions.forEach { action ->
                        VQuickStatCard(
                            value = action.label,
                            label = "",
                            iconBg = action.iconBg,
                            iconColor = action.iconColor,
                            icon = {
                                Icon(action.icon, contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                            onClick = action.onClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (rowActions.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // 5. Recent updates feed (max 5)
        if (state.alerts.isNotEmpty()) {
            VSectionHeader(title = "Recent Updates", modifier = Modifier.padding(horizontal = 4.dp))
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.alerts.take(5).forEach { alert ->
                    VUpdateCard(
                        source = alert.type,
                        timestamp = "",
                        title = alert.title,
                        text = alert.value,
                        avatarIcon = {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = VColors.Primary,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
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
                style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
            )
            Text(
                text = period.teacherName,
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
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
                style = VTypography.ThreadTime.copy(color = statusColor),
            )
        }
    }
}

@Composable
private fun HomeSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VShimmerBoxPremium(height = 180.dp, shape = VShapes.TwoXl)
        repeat(3) {
            VShimmerBoxPremium(height = 60.dp, shape = VShapes.Lg)
        }
        VShimmerCardPremium()
    }
}

private data class QuickAction(
    val label: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconColor: Color,
    val onClick: () -> Unit,
)
