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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.cards.VProfileHeroCard
import com.littlebridge.enrollplus.ui.v2.components.carousel.VStaggeredItem
import com.littlebridge.enrollplus.ui.v2.components.misc.VPullRefreshPremium
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

private data class BadgeData(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val earned: Boolean,
    val earnedDate: String? = null,
    val progress: Float = 0f,
    val progressText: String? = null,
)

private data class StatData(
    val value: String,
    val label: String,
    val trend: String,
)

/**
 * Premium parent profile — rebuilt with premium loading/error/empty states,
 * pull-to-refresh, VStaggeredItem entrances, and 140dp bottom padding.
 */
@Composable
fun ParentProfileScreen(
    onLogout: () -> Unit = {},
    onLinkChild: () -> Unit = {},
    onDiscoverSchools: () -> Unit = {},
    onAccountSettings: () -> Unit = {},
    childName: String? = null,
    childLevel: Int? = null,
    childAttendanceRate: Int? = null,
    childAvgMarks: Int? = null,
    childClassName: String? = null,
    modifier: Modifier = Modifier,
    viewModel: ParentProfileViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    VPullRefreshPremium(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.load() },
        modifier = modifier.fillMaxSize().background(VColors.Surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 140.dp),
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Loading state ──
            if (state.isLoading) {
                VStaggeredItem(delayMs = 0) {
                    SkeletonCard(variant = "hero", modifier = Modifier.padding(horizontal = 20.dp))
                }
                VStaggeredItem(delayMs = 60) {
                    SkeletonCard(variant = "card", modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))
                }
                VStaggeredItem(delayMs = 120) {
                    SkeletonCard(variant = "card", modifier = Modifier.padding(horizontal = 20.dp))
                }
                return@Column
            }

            // ── Error state ──
            if (state.error != null) {
                ErrorStateCard(
                    message = state.error ?: "Unknown error",
                    onRetry = { viewModel.load() },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 48.dp),
                )
                return@Column
            }

            val profile = state.profile
            if (profile == null) {
                EmptyStateCard(
                    title = "No Profile Data",
                    body = "Your profile will appear here once loaded.",
                    icon = Icons.Filled.Settings,
                    modifier = Modifier.padding(vertical = 48.dp),
                )
                return@Column
            }

            // Use child data if available, fall back to parent profile
            val displayName = childName ?: profile.name
            val displayInitial = displayName.firstOrNull()?.toString() ?: "?"
            val displayClass = childClassName ?: profile.role.replaceFirstChar { it.uppercase() }
            val displayLevel = childLevel ?: 1

            // ── Profile Hero Card ──
            VStaggeredItem(delayMs = 0) {
                VProfileHeroCard(
                    initials = displayInitial,
                    name = displayName,
                    className = displayClass,
                    levelText = "Level $displayLevel — Scholar",
                    xpText = "${displayLevel * 300} / ${(displayLevel + 1) * 500} XP",
                    xpProgress = (displayLevel * 300f) / ((displayLevel + 1) * 500f),
                    badge = "Level $displayLevel",
                    onClick = { /* TODO: open player card detail */ },
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Stats — 2×2 grid ──
            VStaggeredItem(delayMs = 60) {
                VSectionHeader("Stats")
            }
            val stats = listOf(
                StatData("${childAttendanceRate ?: 0}%", "Attendance", "↑ 2% this month"),
                StatData("${childAvgMarks ?: 0}%", "Avg Marks", "↑ 5% this term"),
                StatData("${(displayLevel * 300)}", "XP Points", "↑ 420 this week"),
                StatData("18", "Quizzes Done", "↑ 3 this week"),
            )
            VStaggeredItem(delayMs = 100) {
                Column(Modifier.padding(horizontal = 20.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(stats[0], Modifier.weight(1f))
                        StatCard(stats[1], Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(stats[2], Modifier.weight(1f))
                        StatCard(stats[3], Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Badges — horizontal scroll ──
            VStaggeredItem(delayMs = 150) {
                VSectionHeader("Badges", linkText = "All", onLinkClick = { /* TODO: view all badges */ })
            }
            val badges = listOf(
                BadgeData("Math Champ", "Score 90%+ in 5 consecutive math tests", Icons.Filled.CheckCircle, earned = true, earnedDate = "Feb 12"),
                BadgeData("Bookworm", "Read and reviewed 10 library books", Icons.AutoMirrored.Filled.MenuBook, earned = true, earnedDate = "Jan 28"),
                BadgeData("Quick Solver", "Complete 20 quizzes under time limit", Icons.Filled.Verified, earned = true, earnedDate = "Feb 20"),
                BadgeData("Perfect Score", "Achieve 100% on any test", Icons.Filled.School, earned = true, earnedDate = "Mar 2"),
                BadgeData("Science Whiz", "Score 90%+ in 5 science tests", Icons.Filled.School, earned = false, progress = 0.6f, progressText = "3 of 5 completed"),
                BadgeData("100 Days", "100 consecutive days of attendance", Icons.Filled.CheckCircle, earned = false, progress = 0.94f, progressText = "94 of 100 days"),
                BadgeData("Art Master", "Submit 10 creative art projects", Icons.Filled.School, earned = false, progress = 0.4f, progressText = "4 of 10 submitted"),
            )
            VStaggeredItem(delayMs = 200) {
                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(badges) { badge ->
                        BadgeCard(badge)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Account ──
            VStaggeredItem(delayMs = 250) {
                VSectionHeader("Account")
            }
            VStaggeredItem(delayMs = 300) {
                Column(Modifier.padding(horizontal = 20.dp)) {
                    AccountRow(
                        icon = Icons.Filled.Settings,
                        label = "Account Settings",
                        onClick = onAccountSettings,
                    )
                    Spacer(Modifier.height(10.dp))
                    AccountRow(
                        icon = Icons.Filled.Add,
                        label = "Link Another Child",
                        onClick = onLinkChild,
                    )
                    Spacer(Modifier.height(10.dp))
                    AccountRow(
                        icon = Icons.Filled.Explore,
                        label = "Discover Schools",
                        onClick = onDiscoverSchools,
                    )
                    Spacer(Modifier.height(10.dp))
                    AccountRow(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        label = "Logout",
                        labelColor = VColors.Error,
                        onClick = onLogout,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(stat: StatData, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.96f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null) { /* TODO: view stat detail */ }
            .padding(20.dp),
    ) {
        Text(stat.value, style = VTypography.StatValue.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(4.dp))
        Text(stat.label, style = VTypography.StatLabel.copy(color = VColors.OnSurfaceVariant))
        Spacer(Modifier.height(8.dp))
        Text(stat.trend, style = VTypography.NavLabel.copy(color = VColors.Tertiary, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun BadgeCard(badge: BadgeData) {
    val interaction = remember { MutableInteractionSource() }
    val bg = if (badge.earned) VColors.SurfaceContainerLowest else VColors.SurfaceContainerLow
    val iconRingBg = if (badge.earned) {
        Brush.linearGradient(listOf(VColors.Primary, VColors.Tertiary, VColors.Primary))
    } else {
        null
    }
    val iconColor = if (badge.earned) VColors.Primary else VColors.OnSurfaceVariant
    val iconAlpha = if (badge.earned) 1f else 0.4f

    Column(
        Modifier
            .width(168.dp)
            .clip(VShapes.Xl)
            .background(bg)
            .pressScale(interaction, pressedScale = 0.96f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null) { /* TODO: view badge detail */ },
    ) {
        // Badge top — gradient bg for earned
        Box(
            Modifier
                .fillMaxWidth()
                .then(
                    if (badge.earned) Modifier.background(
                        Brush.linearGradient(
                            colors = listOf(VColors.PrimaryContainer, VColors.SurfaceContainerLowest),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(10000f, 10000f),
                        ),
                    ) else Modifier.background(VColors.SurfaceContainerLow)
                )
                .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .then(
                            if (badge.earned) Modifier.background(iconRingBg!!) else Modifier.background(VColors.SurfaceContainerHigh)
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        badge.icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp).then(if (!badge.earned) Modifier.alpha(iconAlpha) else Modifier),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(badge.name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.ExtraBold))
            }
        }
        // Badge body
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                badge.description,
                style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            if (badge.earned && badge.earnedDate != null) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VColors.Tertiary, modifier = Modifier.size(12.dp))
                    Text(
                        "Earned · ${badge.earnedDate}",
                        style = VTypography.NavLabel.copy(color = VColors.Tertiary, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp),
                    )
                }
            } else if (badge.progressText != null) {
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier.fillMaxWidth().height(5.dp).clip(VShapes.Full).background(VColors.SurfaceContainerHigh),
                ) {
                    Box(
                        Modifier.fillMaxWidth(badge.progress).height(5.dp).clip(VShapes.Full).background(VColors.Tertiary),
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(badge.progressText, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 10.sp))
            }
        }
    }
}

@Composable
private fun AccountRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    labelColor: Color = VColors.OnSurface,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.98f)
            .shapeMorph(interaction, VShapes.XlDp, VShapes.TwoXlDp, VMotion.DurShort2)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier.size(40.dp).clip(VShapes.Md).background(VColors.SurfaceContainerLow),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(20.dp))
        }
        Text(label, style = VTypography.UpdateTitle.copy(color = labelColor, fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = VColors.Outline, modifier = Modifier.size(20.dp))
    }
}
