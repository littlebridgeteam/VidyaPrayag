package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.AttendanceDayState
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
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

    VStateHostPremium(
        loading = state.isLoading && state.children.isEmpty(),
        error = state.error,
        isEmpty = state.children.isEmpty() && !state.isLoading,
        modifier = modifier.fillMaxSize(),
        emptyTitle = "No child linked",
        emptyBody = "Link your child's school to see their attendance, marks, fees, and updates.",
        emptyIcon = Icons.Filled.School,
        onRetry = { viewModel.load() },
        skeleton = { BriefingSkeleton() },
    ) {
        BriefingContent(
            state = state,
            onOpenOverlay = onOpenOverlay,
            onSwitchTab = onSwitchTab,
            onSelectChild = { viewModel.selectChild(it) },
            onLinkChild = onLinkChild,
        )
    }
}

@Composable
private fun BriefingContent(
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

        // 1. Child selector — always visible dropdown + plus icon
        ChildSelector(
            childName = child?.name ?: "Select Child",
            children = state.children.map { it.id to it.name },
            selectedId = state.selectedChildId,
            onSelect = onSelectChild,
            onLinkChild = onLinkChild,
        )

        Spacer(Modifier.height(20.dp))

        // 2. Briefing card — one-line summary, clean
        if (child != null) {
            BriefingCard(
                greeting = state.greeting.ifBlank { "Hello" },
                childName = child.name,
                className = "Class ${child.currentLevel}",
                attendanceState = state.today.state,
                classesLeft = state.todayPeriods.count { it.relation >= 0 },
                feesDue = state.fees?.outstandingFees,
                onClick = { onSwitchTab(1) },
            )
        }

        Spacer(Modifier.height(24.dp))

        // 3. Today's classes — horizontal scroll chips
        if (state.todayPeriods.isNotEmpty()) {
            SectionLabel("Today's Classes")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                state.todayPeriods.forEach { period ->
                    ClassChip(period = period)
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // 4. Needs attention — alert cards as rows
        if (state.alerts.isNotEmpty()) {
            SectionLabel("Needs Attention")
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.alerts.take(4).forEach { alert ->
                    AlertRow(
                        title = alert.title,
                        body = alert.value,
                        type = alert.type,
                        onClick = { onOpenOverlay(ParentOverlay.Notifications) },
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // 5. Quick access — list rows, not grid tiles
        SectionLabel("Quick Access")
        Spacer(Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            QuickAccessRow("Fees & Payments", Icons.Filled.Payments) { onSwitchTab(2) }
            QuickAccessRow("Transport", Icons.Filled.DirectionsBus) { onOpenOverlay(ParentOverlay.Transport) }
            QuickAccessRow("Library", Icons.Filled.LocalLibrary) { onOpenOverlay(ParentOverlay.Library) }
            QuickAccessRow("Health", Icons.Filled.HealthAndSafety) { onOpenOverlay(ParentOverlay.Health) }
            QuickAccessRow("ID Card", Icons.Filled.Badge) { onOpenOverlay(ParentOverlay.IDCard) }
            QuickAccessRow("Calendar", Icons.Filled.CalendarMonth) { onOpenOverlay(ParentOverlay.Calendar) }
        }
    }
}

@Composable
private fun ChildSelector(
    childName: String,
    children: List<Pair<String, String>>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onLinkChild: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.Lg)
                    .background(VColors.SurfaceContainerLow)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { if (children.size > 1) expanded = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = childName,
                    style = VTypography.FeatureTitle.copy(color = VColors.OnSurface),
                    modifier = Modifier.weight(1f),
                )
                if (children.size > 1) {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = VColors.OnSurfaceVariant,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                children.forEach { (id, name) ->
                    DropdownMenuItem(
                        text = { Text(name, style = VTypography.FormInput) },
                        onClick = {
                            onSelect(id)
                            expanded = false
                        },
                    )
                }
            }
        }

        val linkInteraction = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(VShapes.Lg)
                .background(VColors.PrimaryContainer)
                .pressScale(linkInteraction, pressedScale = 0.92f)
                .clickable(
                    interactionSource = linkInteraction,
                    indication = null,
                    onClick = onLinkChild,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Link Child",
                tint = VColors.OnPrimaryContainer,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun BriefingCard(
    greeting: String,
    childName: String,
    className: String,
    attendanceState: AttendanceDayState,
    classesLeft: Int,
    feesDue: String?,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val attendanceText = when (attendanceState) {
        AttendanceDayState.Present -> "Present"
        AttendanceDayState.Absent -> "Absent"
        AttendanceDayState.Late -> "Late"
        AttendanceDayState.Holiday -> "Holiday"
        AttendanceDayState.Sunday -> "Sunday"
        AttendanceDayState.Vacation -> "Vacation"
        AttendanceDayState.NoData -> "—"
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
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainer)
            .pressScale(interaction, pressedScale = 0.98f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(24.dp),
    ) {
        Text(
            text = greeting,
            style = VTypography.HeroSubtitle.copy(color = VColors.OnSurfaceVariant),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "$childName is $attendanceText today",
            style = VTypography.FeatureTitle.copy(color = VColors.OnSurface),
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BriefingMetric(
                value = if (classesLeft > 0) "$classesLeft" else "0",
                label = "classes left",
            )
            if (feesDue != null && feesDue != "0" && feesDue != "₹0") {
                BriefingDivider()
                BriefingMetric(
                    value = feesDue,
                    label = "fees due",
                    valueColor = VColors.Error,
                )
            }
        }
    }
}

@Composable
private fun BriefingMetric(
    value: String,
    label: String,
    valueColor: androidx.compose.ui.graphics.Color = VColors.OnSurface,
) {
    Column {
        Text(
            text = value,
            style = VTypography.QuickStatValue.copy(color = valueColor),
        )
        Text(
            text = label,
            style = VTypography.ThreadTime.copy(color = VColors.OnSurfaceVariant),
        )
    }
}

@Composable
private fun BriefingDivider() {
    Box(
        Modifier
            .width(1.dp)
            .height(28.dp)
            .background(VColors.OutlineVariant),
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = VTypography.ActionCardTitle.copy(color = VColors.OnSurfaceVariant),
        modifier = Modifier.padding(horizontal = 20.dp),
    )
}

@Composable
private fun ClassChip(period: LivePeriod) {
    val isNow = period.relation == 0
    val isDone = period.relation == -1
    val bg = if (isNow) VColors.PrimaryContainer else VColors.SurfaceContainerLow
    val titleColor = if (isNow) VColors.OnPrimaryContainer else VColors.OnSurface
    val subColor = if (isNow) VColors.OnPrimaryContainer.copy(alpha = 0.7f) else VColors.OnSurfaceVariant

    Column(
        modifier = Modifier
            .width(120.dp)
            .clip(VShapes.Lg)
            .background(bg)
            .padding(16.dp),
    ) {
        Text(
            text = "${period.startTime}",
            style = VTypography.ScheduleAmPm.copy(color = subColor),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = period.subject,
            style = VTypography.ScheduleSubject.copy(color = titleColor),
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = period.room,
            style = VTypography.ThreadTime.copy(color = subColor),
        )
        if (isNow) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "NOW",
                style = VTypography.ScheduleStatus.copy(color = VColors.Primary),
            )
        } else if (isDone) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "DONE",
                style = VTypography.ScheduleStatus.copy(color = VColors.OnSurfaceVariant),
            )
        }
    }
}

@Composable
private fun AlertRow(
    title: String,
    body: String,
    type: String,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val dotColor = when (type) {
        "CRITICAL" -> VColors.Error
        "WARNING" -> VColors.WarmOrange
        else -> VColors.Primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .pressScale(interaction, pressedScale = 0.98f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = VTypography.MarkName.copy(color = VColors.OnSurface),
            )
            Text(
                text = body,
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = VColors.OnSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun QuickAccessRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Md)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .pressScale(interaction, pressedScale = 0.98f)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(VShapes.Sm)
                .background(VColors.SurfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(18.dp))
        }
        Text(
            text = label,
            style = VTypography.AccountLabel.copy(color = VColors.OnSurface),
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = VColors.Outline,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun BriefingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VShimmerBoxPremium(height = 48.dp, shape = VShapes.Lg)
        VShimmerBoxPremium(height = 120.dp, shape = VShapes.Xl)
        VShimmerBoxPremium(height = 100.dp, shape = VShapes.Lg)
        VShimmerBoxPremium(height = 60.dp, shape = VShapes.Lg)
        VShimmerBoxPremium(height = 60.dp, shape = VShapes.Lg)
    }
}
