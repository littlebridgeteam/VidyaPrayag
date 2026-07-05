package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStat
import com.littlebridge.enrollplus.ui.v2.components.cards.VHeroCard
import com.littlebridge.enrollplus.ui.v2.components.cards.VQuickStatCard
import com.littlebridge.enrollplus.ui.v2.components.cards.VUpdateCard
import com.littlebridge.enrollplus.ui.v2.components.cards.UpdateAction
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium parent home — matches parent-portal.html Home tab.
 * Hero card, live update banner, filter chips, priority carousel,
 * quick stats, schedule cards, school updates.
 */
@Composable
fun ParentHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ParentDashboardViewModel = koinViewModel(),
    onOpenPulse: () -> Unit = {},
    onOpenTransport: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onSwitchTab: (Int) -> Unit = {},
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState()),
    ) {
        // Loading state
        if (state.isLoading && state.children.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading dashboard...", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
            return@PremiumTheme
        }

        // Error state
        if (state.error != null && state.children.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.error!!, style = VTypography.UpdateText.copy(color = VColors.Error))
            }
            return@PremiumTheme
        }

        // Empty state
        if (state.children.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("No children linked yet", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
            return@PremiumTheme
        }

        val child = state.selectedChild
        val childInitial = child?.name?.firstOrNull()?.toString() ?: "?"

        // ── Greeting ──
        Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)) {
            if (state.greeting.isNotBlank()) {
                Text(state.greeting, style = VTypography.Eyebrow.copy(color = VColors.Primary))
                Spacer(Modifier.height(8.dp))
            }
            Text(
                "Hi ${child?.name?.split(" ")?.firstOrNull() ?: "Parent"},\nhere's ${child?.name?.split(" ")?.lastOrNull()?.let { "${it}'s" } ?: "your"} day",
                style = VTypography.GreetingTitle.copy(color = VColors.OnSurface),
            )
        }

        // ── Hero Card ──
        if (child != null) {
            VHeroCard(
                studentInitials = childInitial,
                studentName = child.name,
                studentClass = "Level ${child.currentLevel} · ${child.attendanceStatus}",
                stats = listOf(
                    HeroStat("${child.overallProgress.toInt()}%", "Progress"),
                    HeroStat("L${child.currentLevel}", "Level"),
                    HeroStat("${state.alerts.size}", "Alerts"),
                ),
                onClick = onOpenPulse,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(20.dp))
        }

        // ── Live Update banner (transport) ──
        val liveInteraction = remember { MutableInteractionSource() }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(VShapes.Lg)
                .background(VColors.TertiaryContainer)
                .pressScale(liveInteraction, pressedScale = 0.98f)
                .shapeMorph(liveInteraction, VShapes.LgDp, VShapes.XlDp, VMotion.DurShort2)
                .clickable(interactionSource = liveInteraction, indication = null, onClick = onOpenTransport)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(VColors.Tertiary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = VColors.OnTertiary, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text("Transport tracking", style = VTypography.UpdateTitle.copy(color = VColors.OnTertiaryContainer, fontWeight = FontWeight.Bold))
                Text("Tap to view live status", style = VTypography.NavLabel.copy(color = VColors.OnTertiaryContainer.copy(alpha = 0.7f)))
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Filter Chips ──
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(listOf("All", "Academics", "Fees", "Attendance", "Transport", "Library")) { label ->
                VFilterChip(label = label, active = label == "All", onClick = { })
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Priority Carousel ──
        VSectionHeader("Priority", linkText = "See all", onLinkClick = { })
        val feeBg = VColors.PrimaryContainer
        val feeFg = VColors.Primary
        val feeText = VColors.OnPrimary
        val attBg = VColors.TertiaryContainer
        val attFg = VColors.Tertiary
        val attText = VColors.OnTertiary
        val alertBg = VColors.WarmOrangeContainer
        val alertFg = VColors.WarmOrange
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                listOf(
                    FeatureCardData("Fee Payment", state.fees?.outstandingFees ?: "—", "Pay Now", feeBg, feeFg, feeText, onSwitchTab = { onSwitchTab(2) }),
                    FeatureCardData("Attendance", "${state.attendance?.attendanceRate ?: 0}%", "On Track", attBg, attFg, attText, onSwitchTab = { onSwitchTab(1) }),
                    FeatureCardData("Alerts", "${state.alerts.size}", "Review", alertBg, alertFg, Color.White, onSwitchTab = { onOpenNotifications() }),
                )
            ) { card ->
                FeatureCard(card)
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Quick Stats ──
        VSectionHeader("Quick Stats")
        Row(
            Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            VQuickStatCard(
                value = state.fees?.outstandingFees ?: "—",
                label = "Fees Due",
                iconBg = VColors.PrimaryContainer,
                iconColor = VColors.Primary,
                icon = { Icon(Icons.Filled.Info, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(18.dp)) },
                onClick = { onSwitchTab(2) },
                modifier = Modifier.weight(1f),
            )
            VQuickStatCard(
                value = "${state.attendance?.attendanceRate ?: 0}%",
                label = "Attendance",
                iconBg = VColors.TertiaryContainer,
                iconColor = VColors.Tertiary,
                icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VColors.Tertiary, modifier = Modifier.size(18.dp)) },
                onClick = { onSwitchTab(1) },
                modifier = Modifier.weight(1f),
            )
            VQuickStatCard(
                value = "${state.alerts.size}",
                label = "Alerts",
                iconBg = VColors.WarmOrangeContainer,
                iconColor = VColors.WarmOrange,
                icon = { Icon(Icons.Filled.Warning, contentDescription = null, tint = VColors.WarmOrange, modifier = Modifier.size(18.dp)) },
                onClick = onOpenNotifications,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Today's Schedule ──
        if (state.todayPeriods.isNotEmpty()) {
            VSectionHeader("Today's Schedule", linkText = "Full timetable", onLinkClick = { })
            Column(Modifier.padding(horizontal = 20.dp)) {
                // Progress bar
                val doneCount = state.todayPeriods.count { it.relation == -1 }
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        Modifier.weight(1f).height(4.dp).clip(VShapes.Full).background(VColors.SurfaceContainerHigh),
                    ) {
                        val progress = if (state.todayPeriods.isNotEmpty()) doneCount.toFloat() / state.todayPeriods.size else 0f
                        Box(
                            Modifier.fillMaxWidth(progress).height(4.dp).clip(VShapes.Full)
                                .background(Brush.linearGradient(listOf(VColors.Primary, VColors.Tertiary))),
                        )
                    }
                    Text("$doneCount of ${state.todayPeriods.size} done", style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
                }
                Spacer(Modifier.height(10.dp))
                state.todayPeriods.take(6).forEach { period ->
                    ScheduleCard(period)
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // ── School Updates (alerts as updates) ──
        if (state.alerts.isNotEmpty()) {
            VSectionHeader("School Updates", linkText = "All", onLinkClick = { })
            Column(Modifier.padding(horizontal = 20.dp)) {
                state.alerts.take(3).forEach { alert ->
                    VUpdateCard(
                        source = "School",
                        timestamp = "",
                        title = alert.title,
                        text = alert.value,
                        avatarIcon = { Icon(Icons.Filled.Info, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(20.dp)) },
                        actions = listOf(
                            UpdateAction("View", isPrimary = true, onClick = { }),
                            UpdateAction("Dismiss", isPrimary = false, onClick = { }),
                        ),
                        onClick = { },
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

private data class FeatureCardData(
    val title: String,
    val amount: String,
    val badge: String,
    val bgColor: Color,
    val badgeBg: Color,
    val badgeFg: Color,
    val onSwitchTab: () -> Unit,
)

@Composable
private fun FeatureCard(data: FeatureCardData) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        Modifier
            .width(280.dp)
            .clip(VShapes.Xl)
            .background(data.bgColor)
            .pressScale(interaction, pressedScale = 0.97f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurMedium2)
            .clickable(interactionSource = interaction, indication = null, onClick = data.onSwitchTab)
            .padding(24.dp),
    ) {
        Box(
            Modifier.size(52.dp).clip(VShapes.Lg).background(data.badgeBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Info, contentDescription = null, tint = data.badgeFg, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(data.title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.ExtraBold))
        Spacer(Modifier.height(4.dp))
        Text(data.badge, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(20.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(data.amount, style = VTypography.HeroStatValue.copy(color = VColors.OnSurface))
            val badgeInteraction = remember { MutableInteractionSource() }
            Text(
                data.badge,
                style = VTypography.UpdateAction.copy(color = data.badgeFg),
                modifier = Modifier
                    .clip(VShapes.Full)
                    .background(data.badgeBg)
                    .pressScale(badgeInteraction, pressedScale = 0.95f)
                    .clickable(interactionSource = badgeInteraction, indication = null, onClick = data.onSwitchTab)
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun ScheduleCard(period: LivePeriod) {
    val isLive = period.relation == 0
    val isPast = period.relation == -1
    val interaction = remember { MutableInteractionSource() }
    val bg = if (isLive) {
        Brush.linearGradient(listOf(VColors.Primary, VColors.PrimaryMid))
    } else {
        Brush.linearGradient(listOf(VColors.SurfaceContainerLow, VColors.SurfaceContainerLow))
    }
    val onColor = if (isLive) VColors.OnPrimary else VColors.OnSurface
    val onColorVariant = if (isLive) VColors.OnPrimary.copy(alpha = 0.65f) else VColors.OnSurfaceVariant

    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(bg)
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurMedium2)
            .clickable(interactionSource = interaction, indication = null) { }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Time
        Column(Modifier.width(64.dp)) {
            Text(period.startTime, style = VTypography.HeroStatValue.copy(color = onColor, fontSize = 22.sp))
            Text("AM", style = VTypography.NavLabel.copy(color = onColorVariant))
        }
        // Divider
        Box(Modifier.width(1.dp).height(40.dp).background(onColor.copy(alpha = 0.12f)))
        // Info
        Column(Modifier.weight(1f)) {
            Text(period.subject, style = VTypography.UpdateTitle.copy(color = onColor, fontWeight = FontWeight.ExtraBold))
            Text("${period.teacherName} · Room ${period.room}", style = VTypography.NavLabel.copy(color = onColorVariant))
        }
        // Status badge
        if (isLive) {
            Row(
                Modifier.clip(VShapes.Full).background(VColors.GlassWhite15).padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(VColors.LiveCyan))
                Text("LIVE", style = VTypography.NavBadge.copy(color = onColor))
            }
        } else if (!isPast) {
            Text("NEXT", style = VTypography.NavBadge.copy(color = VColors.OnPrimaryContainer), modifier = Modifier.clip(VShapes.Full).background(VColors.PrimaryContainer).padding(horizontal = 14.dp, vertical = 6.dp))
        }
    }
}

@Composable
private fun LoadingCard() {
    Box(
        Modifier.fillMaxWidth().height(200.dp).clip(VShapes.Lg).background(VColors.SurfaceContainerLow),
        contentAlignment = Alignment.Center,
    ) {
        Text("Loading dashboard...", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
    }
}
