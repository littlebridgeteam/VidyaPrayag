package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.LivePeriod
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.ui.v2.components.cards.HeroStat
import com.littlebridge.enrollplus.ui.v2.components.cards.VUpdateCard
import com.littlebridge.enrollplus.ui.v2.components.cards.UpdateAction
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.components.misc.VPullRefreshPremium
import com.littlebridge.enrollplus.ui.v2.components.navigation.VFilterChip
import com.littlebridge.enrollplus.ui.v2.components.typography.VGreetingEyebrow
import com.littlebridge.enrollplus.ui.v2.components.typography.VGreetingTitle
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.radialGlow
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.tokens.rememberLiveBlink
import com.littlebridge.enrollplus.ui.v2.tokens.rememberLivePulse
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium parent home — rebuilt with 10-card feature grid, child switcher,
 * premium loading/error/empty states, pull-to-refresh, and 140dp bottom padding.
 */
@Composable
fun ParentHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ParentDashboardViewModel = koinViewModel(),
    onOpenPulse: () -> Unit = {},
    onOpenTransport: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenCalendar: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    onOpenTutorChat: () -> Unit = {},
    onOpenHealth: () -> Unit = {},
    onOpenLeave: () -> Unit = {},
    onOpenScholarships: () -> Unit = {},
    onOpenDigitalId: () -> Unit = {},
    onOpenEvents: () -> Unit = {},
    onLinkChild: () -> Unit = {},
    onSwitchTab: (Int) -> Unit = {},
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()
    var selectedFilter by remember { mutableStateOf("All") }

    VPullRefreshPremium(
        isRefreshing = state.isLoading && state.children.isNotEmpty(),
        onRefresh = { viewModel.load() },
        modifier = modifier.fillMaxSize().background(VColors.Surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 140.dp),
        ) {
            // ── Loading state (initial) ──
            if (state.isLoading && state.children.isEmpty()) {
                VStaggeredItem(delayMs = 0) {
                    SkeletonCard(variant = "hero", modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp))
                }
                VStaggeredItem(delayMs = 60) {
                    SkeletonCard(variant = "card", modifier = Modifier.padding(horizontal = 20.dp))
                }
                VStaggeredItem(delayMs = 120) {
                    SkeletonCard(variant = "card", modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))
                }
                VStaggeredItem(delayMs = 180) {
                    SkeletonCard(variant = "list", modifier = Modifier.padding(vertical = 8.dp))
                }
                VStaggeredItem(delayMs = 240) {
                    SkeletonCard(variant = "list", modifier = Modifier.padding(vertical = 8.dp))
                }
                return@Column
            }

            // ── Error state ──
            if (state.error != null && state.children.isEmpty()) {
                ErrorStateCard(
                    message = state.error ?: "Unknown error",
                    onRetry = { viewModel.load() },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 48.dp),
                )
                return@Column
            }

            // ── Empty state (no children linked) ──
            if (state.children.isEmpty()) {
                EmptyStateCard(
                    title = "No Children Linked",
                    body = "Link your child to start tracking their academic progress, attendance, and more.",
                    icon = Icons.Filled.School,
                    actionText = "Link a Child",
                    onAction = onLinkChild,
                    modifier = Modifier.padding(vertical = 48.dp),
                )
                return@Column
            }

            val child = state.selectedChild
            val childInitial = child?.name?.firstOrNull()?.toString() ?: "?"

            // ── Greeting + Child Switcher ──
            VStaggeredItem(delayMs = 0) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 24.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(Modifier.weight(1f)) {
                        if (state.greeting.isNotBlank()) {
                            VGreetingEyebrow(text = state.greeting)
                        }
                        val firstName = child?.name?.split(" ")?.firstOrNull() ?: "Parent"
                        val dayAccent = child?.name?.split(" ")?.lastOrNull()?.let { "${it}'s day" } ?: "your day"
                        VGreetingTitle(
                            plainText = "Hi $firstName,\nhere's ",
                            accentText = dayAccent,
                        )
                    }
                    if (state.children.size > 1) {
                        ChildSwitcherDropdown(
                            children = state.children,
                            selectedChildId = state.selectedChildId,
                            onSelect = { viewModel.selectChild(it) },
                        )
                    }
                }
            }

            // ── Hero Card ──
            VStaggeredItem(delayMs = 30) {
                if (child != null) {
                    ParentHeroCard(
                        studentInitials = childInitial,
                        studentName = child.name,
                        studentClass = state.timetable?.className?.ifBlank { null } ?: "Level ${child.currentLevel}",
                        overallProgress = child.overallProgress,
                        currentLevel = child.currentLevel,
                        stats = listOf(
                            HeroStat("${state.attendance?.attendanceRate ?: 0}%", "Attendance"),
                            HeroStat(child.attendanceStatus.ifBlank { "—" }, "Status"),
                            HeroStat("${state.alerts.size}", "Pending"),
                        ),
                        onClick = onOpenPulse,
                        onIconClick = onOpenPulse,
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }

            // ── Live Update banner (transport) ──
            VStaggeredItem(delayMs = 60) {
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
                        Text("Live tracking available", style = VTypography.UpdateTitle.copy(color = VColors.OnTertiaryContainer, fontWeight = FontWeight.Bold))
                        Text("Tap to view real-time transport status", style = VTypography.NavLabel.copy(color = VColors.OnTertiaryContainer.copy(alpha = 0.7f)))
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = VColors.OnTertiaryContainer.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Filter Chips ──
            VStaggeredItem(delayMs = 100, useSpringIn = true) {
                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(listOf("All", "Academics", "Fees", "School Life", "Transport")) { label ->
                        VFilterChip(
                            label = label,
                            active = label == selectedFilter,
                            onClick = { selectedFilter = label },
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── 10-Card Feature Grid (2-up, filtered) ──
            VStaggeredItem(delayMs = 150) {
                VSectionHeader("Quick Access")
                val allItems = buildFeatureGrid(state, onSwitchTab, onOpenPulse, onOpenTransport, onOpenCalendar, onOpenLibrary, onOpenTutorChat, onOpenScholarships, onOpenDigitalId, onOpenEvents)
                val gridItems = if (selectedFilter == "All") allItems else allItems.filter { it.category == selectedFilter }
                Column(Modifier.padding(horizontal = 20.dp)) {
                    gridItems.chunked(2).forEach { rowItems ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            rowItems.forEach { item ->
                                FeatureGridCard(
                                    data = item.data,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Today's Schedule ──
            VStaggeredItem(delayMs = 250, useSpringIn = true) {
                if (state.todayPeriods.isNotEmpty()) {
                    VSectionHeader("Today's Schedule", linkText = "Full timetable", onLinkClick = onOpenCalendar)
                    Column(Modifier.padding(horizontal = 20.dp)) {
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
                        val nextIndex = state.todayPeriods.indexOfFirst { it.relation == 1 }
                        state.todayPeriods.take(6).forEachIndexed { idx, period ->
                            ScheduleCard(period, isNext = idx == nextIndex)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // ── School Updates (alerts as updates) ──
            VStaggeredItem(delayMs = 300) {
                if (state.alerts.isNotEmpty()) {
                    VSectionHeader("School Updates", linkText = "All", onLinkClick = onOpenNotifications)
                    Column(Modifier.padding(horizontal = 20.dp)) {
                        state.alerts.take(3).forEach { alert ->
                            val alertIcon: ImageVector = when (alert.type) {
                                "CRITICAL" -> Icons.Filled.Warning
                                "WARNING" -> Icons.Filled.Warning
                                else -> Icons.Filled.Info
                            }
                            val alertIconColor = when (alert.type) {
                                "CRITICAL" -> VColors.Error
                                "WARNING" -> VColors.WarmOrange
                                else -> VColors.Primary
                            }
                            VUpdateCard(
                                source = "School",
                                timestamp = "",
                                title = alert.title,
                                text = alert.value,
                                avatarIcon = { Icon(alertIcon, contentDescription = null, tint = alertIconColor, modifier = Modifier.size(20.dp)) },
                                actions = listOf(
                                    UpdateAction("View", isPrimary = true, onClick = onOpenNotifications),
                                    UpdateAction("Dismiss", isPrimary = false, onClick = {}),
                                ),
                                onClick = onOpenNotifications,
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private data class CategorizedFeature(
    val category: String,
    val data: FeatureGridData,
)

@Composable
private fun buildFeatureGrid(
    state: com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState,
    onSwitchTab: (Int) -> Unit,
    onOpenPulse: () -> Unit,
    onOpenTransport: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenTutorChat: () -> Unit,
    onOpenScholarships: () -> Unit,
    onOpenDigitalId: () -> Unit,
    onOpenEvents: () -> Unit,
): List<CategorizedFeature> {
    val feeIconBg = VColors.PrimaryContainer
    val feeIconFg = VColors.Primary
    val acdIconBg = VColors.TertiaryContainer
    val acdIconFg = VColors.Tertiary
    val msgIconBg = VColors.SecondaryContainer
    val msgIconFg = VColors.Secondary
    val pulseIconBg = VColors.ErrorContainer
    val pulseIconFg = VColors.Error
    val transportIconBg = VColors.WarmOrangeContainer
    val transportIconFg = VColors.WarmOrange
    val tutorIconBg = VColors.PrimaryContainer
    val tutorIconFg = VColors.Primary
    val schIconBg = VColors.PrimaryContainer
    val schIconFg = VColors.Primary
    val idIconBg = VColors.TertiaryContainer
    val idIconFg = VColors.Tertiary
    val libIconBg = VColors.TertiaryContainer
    val libIconFg = VColors.Tertiary
    val eventsIconBg = VColors.WarmOrangeContainer
    val eventsIconFg = VColors.WarmOrange

    val latestMarkPct = state.latestMark?.let { mark ->
        val score = mark.marks
        val max = mark.maxMarks.toDouble()
        if (max > 0 && score != null) (score / max * 100).toInt() else null
    }

    return listOf(
        CategorizedFeature("Fees", FeatureGridData(Icons.Filled.Payments, "Fees", state.fees?.outstandingFees ?: "—", feeIconBg, feeIconFg, onClick = { onSwitchTab(2) })),
        CategorizedFeature("Academics", FeatureGridData(Icons.Filled.School, "Academics", latestMarkPct?.let { "$it%" } ?: "View", acdIconBg, acdIconFg, onClick = { onSwitchTab(1) })),
        CategorizedFeature("School Life", FeatureGridData(Icons.AutoMirrored.Filled.Message, "Messages", "${state.alerts.size}", msgIconBg, msgIconFg, onClick = { onSwitchTab(3) })),
        CategorizedFeature("Academics", FeatureGridData(Icons.Filled.Favorite, "Pulse", "${state.attendance?.attendanceRate ?: 0}%", pulseIconBg, pulseIconFg, onClick = onOpenPulse)),
        CategorizedFeature("Transport", FeatureGridData(Icons.Filled.DirectionsBus, "Transport", "Track", transportIconBg, transportIconFg, onClick = onOpenTransport)),
        CategorizedFeature("Academics", FeatureGridData(Icons.Filled.Quiz, "Tutor", "Ask AI", tutorIconBg, tutorIconFg, onClick = onOpenTutorChat)),
        CategorizedFeature("Fees", FeatureGridData(Icons.Filled.CreditCard, "Scholarships", "Apply", schIconBg, schIconFg, onClick = onOpenScholarships)),
        CategorizedFeature("School Life", FeatureGridData(Icons.Filled.AccountCircle, "ID Card", "View", idIconBg, idIconFg, onClick = onOpenDigitalId)),
        CategorizedFeature("School Life", FeatureGridData(Icons.AutoMirrored.Filled.MenuBook, "Library", "Browse", libIconBg, libIconFg, onClick = onOpenLibrary)),
        CategorizedFeature("School Life", FeatureGridData(Icons.Filled.CalendarToday, "Events", "View", eventsIconBg, eventsIconFg, onClick = onOpenEvents)),
    )
}

@Composable
private fun ScheduleCard(period: LivePeriod, isNext: Boolean = false) {
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
    val pastModifier = if (isPast) Modifier.graphicsLayer { alpha = 0.5f } else Modifier

    Row(
        pastModifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(bg)
            .then(
                if (isLive) Modifier.radialGlow(offsetX = 120.dp, offsetY = (-40).dp, radius = 140.dp, color = VColors.LiveCyan.copy(alpha = 0.15f))
                else Modifier
            )
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurMedium2)
            .clickable(interactionSource = interaction, indication = null) { }
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Time
        val amPm = remember(period.startTime) {
            val hour = period.startTime.substringBefore(":").toIntOrNull() ?: 0
            if (hour < 12) "AM" else "PM"
        }
        Column(Modifier.width(64.dp)) {
            Text(period.startTime, style = VTypography.ScheduleHour.copy(color = onColor))
            Text(amPm, style = VTypography.ScheduleAmPm.copy(color = onColorVariant))
        }
        // Divider
        Box(Modifier.width(1.dp).height(40.dp).background(onColor.copy(alpha = 0.12f)))
        // Info
        Column(Modifier.weight(1f)) {
            Text(period.subject, style = VTypography.ScheduleSubject.copy(color = onColor))
            Text("${period.teacherName} · Room ${period.room}", style = VTypography.ScheduleTeacher.copy(color = onColorVariant))
        }
        // Status badge
        if (isLive) {
            val blinkAlpha = rememberLiveBlink()
            val liveColor = VColors.LiveCyan
            Row(
                Modifier.clip(VShapes.Full).background(VColors.GlassWhite20).padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(liveColor.copy(alpha = blinkAlpha)))
                Text("LIVE", style = VTypography.ScheduleStatus.copy(color = onColor))
            }
        } else if (isPast) {
            Text("DONE", style = VTypography.ScheduleStatus.copy(color = VColors.OnSurfaceVariant), modifier = Modifier.clip(VShapes.Full).background(VColors.SurfaceContainerHigh).padding(horizontal = 14.dp, vertical = 6.dp))
        } else if (isNext) {
            Text("NEXT", style = VTypography.ScheduleStatus.copy(color = VColors.OnPrimaryContainer), modifier = Modifier.clip(VShapes.Full).background(VColors.PrimaryContainer).padding(horizontal = 14.dp, vertical = 6.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ParentHeroCard — hero card with journey progress ring + level
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Parent hero card — gradient bg, radial glows, live pill, avatar, journey
 * progress ring (level + overall progress), and stats grid.
 *
 * Enhances VHeroCard with a circular progress ring showing the child's
 * overall academic journey progress and current level.
 */
@Composable
private fun ParentHeroCard(
    studentInitials: String,
    studentName: String,
    studentClass: String,
    overallProgress: Double,
    currentLevel: Int,
    stats: List<HeroStat>,
    onClick: () -> Unit,
    onIconClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val (ringScale, ringAlpha) = rememberLivePulse()
    val progressTarget = (overallProgress.toFloat() / 100f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(VMotion.DurLong1, easing = VMotion.EaseEmphasized),
        label = "hero-progress",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shapeMorph(interaction, VShapes.TwoXlDp, VShapes.XlDp, VMotion.DurLong1)
            .pressScale(interaction, pressedScale = 0.98f)
            .background(
                Brush.linearGradient(
                    colors = listOf(VColors.Primary, VColors.PrimaryMid, VColors.PrimaryDeep),
                ),
            )
            .radialGlow(offsetX = 280.dp, offsetY = (-100).dp, radius = 280.dp, color = VColors.HeroGlowTopRight)
            .radialGlow(offsetX = (-80).dp, offsetY = 600.dp, radius = 240.dp, color = VColors.HeroGlowBottomLeft)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .clip(VShapes.Full)
                        .background(VColors.GlassWhite15)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size((ringScale * 2).dp)
                                .clip(CircleShape)
                                .background(VColors.LiveCyan.copy(alpha = ringAlpha)),
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(VColors.LiveCyan),
                        )
                    }
                    Text(
                        text = "LIVE",
                        style = VTypography.LivePill.copy(color = VColors.OnPrimary),
                    )
                }
                val iconInteraction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(VColors.GlassWhite12)
                        .pressScale(iconInteraction, pressedScale = 0.9f)
                        .clickable(interactionSource = iconInteraction, indication = null, onClick = onIconClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Open Pulse",
                        tint = VColors.OnPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(VShapes.Xl)
                        .background(VColors.GlassWhite20),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = studentInitials,
                        style = VTypography.HeroName.copy(color = VColors.OnPrimary),
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        text = studentName,
                        style = VTypography.HeroName.copy(color = VColors.OnPrimary),
                    )
                    Text(
                        text = studentClass,
                        style = VTypography.HeroSubtitle.copy(color = VColors.OnPrimary.copy(alpha = 0.7f)),
                    )
                }
                JourneyProgressRing(
                    progress = animatedProgress,
                    level = currentLevel,
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.Lg)
                    .background(VColors.White08),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                stats.forEach { stat ->
                    val statInteraction = remember { MutableInteractionSource() }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(VShapes.Lg)
                            .background(VColors.White06)
                            .pressScale(statInteraction, pressedScale = 0.95f)
                            .clickable(interactionSource = statInteraction, indication = null) {}
                            .padding(vertical = 18.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stat.value,
                            style = VTypography.HeroStatValue.copy(color = VColors.OnPrimary),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stat.label,
                            style = VTypography.HeroStatLabel.copy(color = VColors.OnPrimary.copy(alpha = 0.55f)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JourneyProgressRing(
    progress: Float,
    level: Int,
    modifier: Modifier = Modifier,
) {
    val ringColor = VColors.LiveCyan
    val trackColor = VColors.GlassWhite20
    val onPrimaryColor = VColors.OnPrimary

    Box(
        modifier = modifier.size(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(56.dp)) {
            val strokeWidth = 4.dp.toPx()
            val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = stroke,
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = level.toString(),
                style = VTypography.HeroStatValue.copy(
                    color = onPrimaryColor,
                    fontSize = 18.sp,
                ),
            )
            Text(
                text = "LVL",
                style = VTypography.HeroStatLabel.copy(
                    color = onPrimaryColor.copy(alpha = 0.6f),
                ),
            )
        }
    }
}
