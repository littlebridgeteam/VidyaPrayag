package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerCardPremium
import com.littlebridge.enrollplus.ui.v2.components.buttons.VPrimaryButton
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// SkeletonCard — premium loading placeholder with shimmer
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Skeleton card — a premium loading placeholder that mimics the shape of
 * real content using [VShimmerBoxPremium]. Use while data is loading
 * to prevent layout shift.
 *
 * @param variant Controls the skeleton layout:
 *   - "card"   → full card with title, subtitle, and two stat boxes
 *   - "list"   → row with avatar circle + two text lines
 *   - "hero"   → large hero-sized block
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    variant: String = "card",
) {
    when (variant) {
        "hero" -> Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(VShapes.TwoXl)
                .background(VColors.SurfaceContainerLowest)
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VShimmerBoxPremium(width = 64.dp, height = 64.dp, shape = VShapes.Xl)
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    VShimmerBoxPremium(height = 22.dp, shape = VShapes.Sm)
                    VShimmerBoxPremium(height = 14.dp, shape = VShapes.Sm)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) {
                    VShimmerBoxPremium(
                        modifier = Modifier.weight(1f),
                        height = 56.dp,
                        shape = VShapes.Lg,
                    )
                }
            }
        }
        "list" -> Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VShimmerBoxPremium(width = 48.dp, height = 48.dp, shape = VShapes.Full)
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                VShimmerBoxPremium(height = 16.dp, shape = VShapes.Sm)
                VShimmerBoxPremium(height = 12.dp, shape = VShapes.Sm)
            }
        }
        else -> VShimmerCardPremium(modifier = modifier)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ErrorStateCard — premium error state with shake animation + retry
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Error state card — displays an error icon, message, and optional retry
 * button with a subtle shake entrance animation.
 */
@Composable
fun ErrorStateCard(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.ErrorOutline,
) {
    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(100)
        shakeAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        )
    }
    val shakeOffset = (shakeAnim.value * 8f).let {
        (it * kotlin.math.sin(shakeAnim.value * 20f))
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .offset(x = shakeOffset.dp)
            .clip(VShapes.Xl)
            .background(VColors.ErrorContainer)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(56.dp).clip(VShapes.Full).background(VColors.Error.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.Error, modifier = Modifier.size(28.dp))
        }
        Text(
            "Something went wrong",
            style = VTypography.SectionHeader.copy(color = VColors.OnErrorContainer),
            textAlign = TextAlign.Center,
        )
        Text(
            message,
            style = VTypography.BodyMedium.copy(color = VColors.OnErrorContainer.copy(alpha = 0.8f)),
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            Spacer(Modifier.height(4.dp))
            VPrimaryButton(text = "Retry", onClick = onRetry)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// EmptyStateCard — premium empty state with entrance animation
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Empty state card — displays an illustration icon, title, optional body,
 * and optional action button with a fade+scale entrance animation.
 */
@Composable
fun EmptyStateCard(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Inbox,
    body: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(VMotion.SlideUpDuration, easing = VMotion.EaseEmphasized)) +
            scaleIn(
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
                initialScale = 0.85f,
            ) +
            slideInVertically(
                tween(VMotion.SlideUpDuration, easing = VMotion.EaseEmphasized),
                initialOffsetY = { it / 4 },
            ),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(72.dp).clip(VShapes.Full).background(VColors.PrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(32.dp))
            }
            Text(
                title,
                style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
                textAlign = TextAlign.Center,
            )
            if (body != null) {
                Text(
                    body,
                    style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
                    textAlign = TextAlign.Center,
                )
            }
            if (actionText != null && onAction != null) {
                Spacer(Modifier.height(4.dp))
                VPrimaryButton(text = actionText, onClick = onAction)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FeatureGridCard — icon + label + metric + tap (for the 10-card 2-up grid)
// ─────────────────────────────────────────────────────────────────────────────

data class FeatureGridData(
    val icon: ImageVector,
    val label: String,
    val metric: String,
    val iconBg: Color,
    val iconTint: Color,
    val onClick: () -> Unit,
)

/**
 * Feature grid card — a compact card with an icon, label, and metric value
 * designed for the 10-card 2-up grid on the Home tab.
 *
 * Includes pressScale + shapeMorph tactile feedback per the design system.
 */
@Composable
fun FeatureGridCard(
    data: FeatureGridData,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .pressScale(interaction, pressedScale = 0.96f)
            .shapeMorph(interaction, VShapes.LgDp, VShapes.XlDp, VMotion.DurShort2)
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .clickable(interactionSource = interaction, indication = null, onClick = data.onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(36.dp).clip(VShapes.Md).background(data.iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(data.icon, contentDescription = null, tint = data.iconTint, modifier = Modifier.size(20.dp))
        }
        Text(
            data.metric,
            style = VTypography.QuickStatValue.copy(color = VColors.OnSurface),
        )
        Text(
            data.label,
            style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ChildSwitcherDropdown — compact avatar + name + expandable child picker
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Child switcher dropdown — a compact pill showing the active child's
 * avatar + name. Tapping expands a dropdown list to switch children.
 *
 * Uses AnimatedVisibility for the dropdown panel and rotates the chevron
 * 180° when expanded.
 */
@Composable
fun ChildSwitcherDropdown(
    children: List<DashboardChildSummary>,
    selectedChildId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = children.firstOrNull { it.id == selectedChildId } ?: children.firstOrNull()
    if (selected == null) return

    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(220, easing = VMotion.EaseEmphasized),
        label = "chevron-rotate",
    )

    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        val interaction = remember { MutableInteractionSource() }
        Row(
            modifier = Modifier
                .clip(VShapes.Full)
                .background(VColors.SurfaceContainerLow)
                .pressScale(interaction, pressedScale = 0.95f)
                .clickable(interactionSource = interaction, indication = null) { expanded = !expanded }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier.size(32.dp).clip(VShapes.Full).background(VColors.PrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    selected.name.firstOrNull()?.toString() ?: "?",
                    style = VTypography.ThreadName.copy(
                        color = VColors.OnPrimaryContainer,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
            }
            Text(
                selected.name,
                style = VTypography.Chip.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold),
            )
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = VColors.OnSurfaceVariant,
                modifier = Modifier.size(20.dp).rotate(chevronRotation),
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(180)) + slideInVertically(tween(180), initialOffsetY = { -it / 4 }),
            exit = fadeOut(tween(150)),
        ) {
            Column(
                Modifier
                    .width(220.dp)
                    .padding(top = 48.dp)
                    .clip(VShapes.Lg)
                    .background(VColors.SurfaceContainerLowest)
                    .border(1.dp, VColors.Outline.copy(alpha = 0.15f), VShapes.Lg)
                    .padding(vertical = 4.dp),
            ) {
                children.forEach { child ->
                    val isSelected = child.id == selected.id
                    val itemInteraction = remember { MutableInteractionSource() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(VShapes.Md)
                            .background(if (isSelected) VColors.PrimaryContainer.copy(alpha = 0.5f) else Color.Transparent)
                            .pressScale(itemInteraction, pressedScale = 0.97f)
                            .clickable(interactionSource = itemInteraction, indication = null) {
                                onSelect(child.id)
                                expanded = false
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            Modifier.size(28.dp).clip(VShapes.Full).background(VColors.PrimaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                child.name.firstOrNull()?.toString() ?: "?",
                                style = VTypography.NavLabel.copy(
                                    color = VColors.OnPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                ),
                            )
                        }
                        Text(
                            child.name,
                            style = VTypography.Chip.copy(
                                color = if (isSelected) VColors.OnPrimaryContainer else VColors.OnSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            ),
                            modifier = Modifier.weight(1f),
                        )
                        if (isSelected) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AttendanceCalendar — monthly grid with color-coded attendance
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Attendance day status — determines the cell color in the calendar.
 */
enum class AttendanceCellStatus {
    Present, Absent, Late, Holiday, Sunday, NoData, Future
}

data class AttendanceCalendarDay(
    val dayOfMonth: Int,
    val status: AttendanceCellStatus,
    val isToday: Boolean = false,
)

/**
 * Attendance calendar — a monthly grid (7 columns × weeks) with color-coded
 * day cells. Each cell is a small rounded square tinted by attendance status.
 *
 * @param monthName Display name of the month (e.g. "March 2025")
 * @param days List of [AttendanceCalendarDay] for the month (1-indexed)
 * @param onPrevMonth Callback when the previous-month arrow is tapped
 * @param onNextMonth Callback when the next-month arrow is tapped
 */
@Composable
fun AttendanceCalendar(
    monthName: String,
    days: List<AttendanceCalendarDay>,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(monthName, style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CalendarArrowButton(text = "‹", onClick = onPrevMonth)
                CalendarArrowButton(text = "›", onClick = onNextMonth)
            }
        }

        val weekdays = listOf("S", "M", "T", "W", "T", "F", "S")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            weekdays.forEach { day ->
                Text(
                    day,
                    style = VTypography.NavLabel.copy(
                        color = VColors.OnSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
        }

        val totalCells = ((days.size + 6) / 7) * 7
        val rows = (totalCells / 7)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(rows) { rowIdx ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    repeat(7) { colIdx ->
                        val dayIdx = rowIdx * 7 + colIdx
                        val day = days.getOrNull(dayIdx)
                        if (day != null) {
                            AttendanceCalendarCell(day, Modifier.weight(1f))
                        } else {
                            Box(Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }

        AttendanceCalendarLegend()
    }
}

@Composable
private fun AttendanceCalendarCell(day: AttendanceCalendarDay, modifier: Modifier = Modifier) {
    val (bg, fg) = when (day.status) {
        AttendanceCellStatus.Present -> VColors.Tertiary to VColors.OnTertiary
        AttendanceCellStatus.Absent -> VColors.Error to VColors.OnError
        AttendanceCellStatus.Late -> VColors.WarmOrange to VColors.GlassWhite95
        AttendanceCellStatus.Holiday -> VColors.PrimaryContainer to VColors.OnPrimaryContainer
        AttendanceCellStatus.Sunday -> VColors.SurfaceContainerHigh to VColors.OnSurfaceVariant
        AttendanceCellStatus.NoData -> VColors.SurfaceContainerLow to VColors.OnSurfaceVariant
        AttendanceCellStatus.Future -> VColors.SurfaceContainerLow to VColors.Outline
    }
    val border = if (day.isToday) VColors.Primary else null
    Box(
        modifier
            .aspectRatio(1f)
            .clip(VShapes.Md)
            .background(bg)
            .then(
                if (border != null) Modifier.border(2.dp, border, VShapes.Md) else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            day.dayOfMonth.toString(),
            style = VTypography.NavLabel.copy(
                color = fg,
                fontWeight = if (day.isToday) FontWeight.ExtraBold else FontWeight.SemiBold,
            ),
        )
    }
}

@Composable
private fun CalendarArrowButton(text: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        Modifier.size(32.dp).clip(VShapes.Md).background(VColors.SurfaceContainerLow)
            .pressScale(interaction, pressedScale = 0.9f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = VTypography.SectionHeader.copy(color = VColors.OnSurface))
    }
}

@Composable
private fun AttendanceCalendarLegend() {
    val legendItems = listOf(
        "Present" to VColors.Tertiary,
        "Absent" to VColors.Error,
        "Late" to VColors.WarmOrange,
        "Holiday" to VColors.PrimaryContainer,
    )
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        legendItems.forEach { (label, color) ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.size(10.dp).clip(VShapes.Xs).background(color))
                Text(label, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TypingIndicator — 3 animated dots for chat typing state
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Typing indicator — three dots that bounce in staggered sequence
 * to indicate the other party is typing.
 */
@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier,
    dotColor: Color = VColors.OnSurfaceVariant,
) {
    val transition = rememberInfiniteTransition(label = "typing")
    val phases = (0..2).map { index ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, delayMillis = index * 150, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "typing-dot-$index",
        )
    }
    Row(
        modifier = modifier
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        phases.forEach { phase ->
            val scale = 0.6f + phase.value * 0.4f
            val offsetY = (1f - phase.value) * 4f
            Box(
                Modifier
                    .offset(y = offsetY.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor.copy(alpha = 0.4f + phase.value * 0.6f))
                    .then(Modifier.graphicsLayer(scaleX = scale, scaleY = scale)),
            )
        }
    }
}
