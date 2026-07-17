package com.littlebridge.enrollplus.ui.v2.screens.parent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto
import com.littlebridge.enrollplus.feature.gamification.domain.model.Reward
import com.littlebridge.enrollplus.feature.gamification.domain.model.RewardRedemption
import com.littlebridge.enrollplus.feature.gamification.domain.model.StudentBadge as GameBadge
import com.littlebridge.enrollplus.feature.gamification.domain.model.StudentQuest
import com.littlebridge.enrollplus.feature.gamification.domain.model.SeasonalEvent
import com.littlebridge.enrollplus.feature.gamification.domain.model.XpHistoryEntry
import com.littlebridge.enrollplus.feature.gamification.domain.model.XpBoost
import com.littlebridge.enrollplus.feature.gamification.domain.model.ClassGoal
import com.littlebridge.enrollplus.feature.gamification.presentation.ParentGamificationState
import com.littlebridge.enrollplus.feature.gamification.presentation.ParentGamificationViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.AchievementBadge
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressState
import com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VPullRefresh
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentProfileCardScreenV2(
    modifier: Modifier = Modifier,
    parentName: String = "",
    children: List<DashboardChildSummary> = emptyList(),
    selectedChild: DashboardChildSummary? = null,
    onSelectChild: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onLinkChild: () -> Unit = {},
    onDiscoverSchools: () -> Unit = {},
    onOpenAccountSettings: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    viewModel: ParentDashboardViewModel = koinViewModel(),
    profileViewModel: ParentProfileViewModel = koinViewModel(),
    academicsViewModel: ParentAcademicsViewModel = koinViewModel(),
    trackViewModel: TrackProgressViewModel = koinViewModel(),
    gamificationViewModel: ParentGamificationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val profile by profileViewModel.state.collectAsStateV2()
    val academics by academicsViewModel.state.collectAsStateV2()
    val track by trackViewModel.state.collectAsStateV2()
    val gamification by gamificationViewModel.state.collectAsStateV2()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(state.refreshEpoch, profile.refreshEpoch) {
        if (state.refreshEpoch > 0 || profile.refreshEpoch > 0) isRefreshing = false
    }

    LaunchedEffect(Unit) {
        if (state.children.isEmpty()) viewModel.load()
    }

    LaunchedEffect(state.selectedChild?.id) {
        state.selectedChild?.id?.let {
            academicsViewModel.selectChild(it)
            gamificationViewModel.load(it)
        }
    }

    VPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.refresh()
            profileViewModel.refresh()
        },
        modifier = modifier.fillMaxSize(),
    ) {
        ProfileContent(
            state = state,
            profile = profile,
            academics = academics,
            track = track,
            gamification = gamification,
            parentName = parentName,
            children = children,
            selectedChild = selectedChild,
            onSelectChild = onSelectChild,
            onRetry = viewModel::load,
            onRetryProfile = profileViewModel::load,
            onRedeemReward = gamificationViewModel::redeemReward,
            onLogout = onLogout,
            onLinkChild = onLinkChild,
            onDiscoverSchools = onDiscoverSchools,
            onOpenAccountSettings = onOpenAccountSettings,
            onOpenNotifications = onOpenNotifications,
            unreadNotificationsCount = unreadNotificationsCount,
        )
    }
}

@Composable
private fun ProfileContent(
    state: ParentDashboardState,
    profile: ParentProfileState,
    academics: com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState,
    track: TrackProgressState,
    gamification: ParentGamificationState,
    parentName: String,
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onSelectChild: (String) -> Unit,
    onRetry: () -> Unit,
    onRetryProfile: () -> Unit,
    onRedeemReward: (String, String) -> Unit,
    onLogout: () -> Unit,
    onLinkChild: () -> Unit,
    onDiscoverSchools: () -> Unit,
    onOpenAccountSettings: () -> Unit,
    onOpenNotifications: () -> Unit,
    unreadNotificationsCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp),
    ) {
        PortalTopHeader(
            parentName = parentName,
            childName = selectedChild?.name?.ifBlank { null } ?: state.selectedChild?.name?.ifBlank { null } ?: "Your Child",
            children = children,
            selectedChild = selectedChild,
            onSelectChild = onSelectChild,
            onOpenNotifications = onOpenNotifications,
            unreadNotificationsCount = unreadNotificationsCount,
            greetingLead = "your",
            greetingAccent = "profile",
        )

        when {
            state.isLoading && state.children.isEmpty() -> ProfileSkeleton()
            state.error != null && state.children.isEmpty() -> ProfileError(message = state.error ?: "", onRetry = onRetry)
            else -> ProfileLoaded(
                state = state,
                profile = profile,
                academics = academics,
                track = track,
                gamification = gamification,
                onRetryProfile = onRetryProfile,
                onRedeemReward = onRedeemReward,
                onLogout = onLogout,
                onLinkChild = onLinkChild,
                onDiscoverSchools = onDiscoverSchools,
                onOpenAccountSettings = onOpenAccountSettings,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SKELETON / ERROR
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ProfileSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(VShapes.xxl)
                .background(VColors.lineSoft),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(VShapes.lg)
                        .background(VColors.lineSoft),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(VShapes.lg)
                .background(VColors.lineSoft),
        )
    }
}

@Composable
private fun ProfileError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(VColors.errorSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.AlertTriangle,
                contentDescription = null,
                tint = VColors.error,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = "Couldn't load profile",
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
        Text(
            text = message,
            style = VTypography.caption,
            color = VColors.ink2,
        )
        Text(
            text = "Retry",
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.violet,
            modifier = Modifier.clickable { onRetry() },
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// LOADED
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ProfileLoaded(
    state: ParentDashboardState,
    profile: ParentProfileState,
    academics: com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState,
    track: TrackProgressState,
    gamification: ParentGamificationState,
    onRetryProfile: () -> Unit,
    onRedeemReward: (String, String) -> Unit,
    onLogout: () -> Unit,
    onLinkChild: () -> Unit,
    onDiscoverSchools: () -> Unit,
    onOpenAccountSettings: () -> Unit,
) {
    val child = state.selectedChild
    val childName = child?.name?.ifBlank { null } ?: "Your Child"
    val attendanceRate = state.attendance?.attendanceRate
    val markPct = state.latestMark?.let { m ->
        if (m.maxMarks > 0) ((m.marks ?: 0.0) / m.maxMarks * 100).roundToInt() else null
    }

    val gameStats = gamification.stats
    val gameLevel = gameStats?.currentLevel ?: track.currentLevel
    val gameXp = gameStats?.totalXp ?: 0
    val gameLevelTitle = gameStats?.levelTitle ?: "Scholar"
    val gameStreak = gameStats?.streakDays ?: 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ProfileHeroCard(
            childName = childName,
            level = gameLevel,
            levelTitle = gameLevelTitle,
            totalXp = gameXp,
            currentXp = gameStats?.currentXp ?: 0,
            streakDays = gameStreak,
        )

        SectionHeader(title = "Stats")
        StatsGrid(
            attendanceRate = attendanceRate,
            markPct = markPct,
            xpPoints = gameXp,
            quizzesDone = academics.quizzes.size,
            streakDays = gameStreak,
            houseName = gamification.house?.name,
            leaderboardRank = gamification.leaderboard?.myRank,
        )

        GamificationCollapsibleSection(
            level = gameLevel,
            levelTitle = gameLevelTitle,
            totalXp = gameXp,
            streakDays = gameStreak,
            houseName = gamification.house?.name,
            leaderboardRank = gamification.leaderboard?.myRank,
            badges = gamification.badges,
            fallbackBadges = track.badges,
            quests = gamification.quests,
            activeBoosts = gamification.activeBoosts,
            events = gamification.events,
            rewards = gamification.rewards,
            currentXp = gameStats?.currentXp ?: 0,
            redemptions = gamification.redemptions,
            xpHistory = gamification.xpHistory,
            classGoals = gamification.classGoals,
            onRedeemReward = { rewardId ->
                state.selectedChild?.id?.let { childId ->
                    onRedeemReward(childId, rewardId)
                }
            },
        )

        SectionHeader(title = "Account")
        AccountCard(
            onOpenAccountSettings = onOpenAccountSettings,
            onLinkChild = onLinkChild,
            onDiscoverSchools = onDiscoverSchools,
            onLogout = onLogout,
        )

        Spacer(Modifier.height(8.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PROFILE HERO
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ProfileHeroCard(
    childName: String,
    level: Int,
    levelTitle: String,
    totalXp: Int,
    currentXp: Int,
    streakDays: Int,
) {
    val xpMax = (level + 1) * 1000
    val xpProgress = (currentXp.toFloat() / xpMax.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xxl)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        VColors.violet,
                        Color(0xFF4A30C4),
                        VColors.violetInk,
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                )
            )
            .padding(24.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(VShapes.xl)
                    .background(VColors.white.copy(alpha = 0.2f))
                    .border(2.dp, VColors.white.copy(alpha = 0.25f), VShapes.xl),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = childName.take(1).uppercase(),
                    style = VTypography.h2.copy(fontSize = 30.sp),
                    color = VColors.white,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    childName,
                    style = VTypography.h3.copy(fontSize = 20.sp),
                    color = VColors.white,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (streakDays > 0) "\uD83D\uDD25 $streakDays day streak" else "Student",
                    style = VTypography.caption.copy(fontSize = 13.sp),
                    color = VColors.white.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(VShapes.full)
                        .background(VColors.white.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = "🏆 Level $level $levelTitle",
                        style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                        color = VColors.white,
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Level $level — $levelTitle",
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                color = VColors.white,
            )
            Text(
                text = "$currentXp / $xpMax XP",
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                color = VColors.white.copy(alpha = 0.8f),
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(VShapes.full)
                .background(VColors.white.copy(alpha = 0.2f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(xpProgress)
                    .height(8.dp)
                    .clip(VShapes.full)
                    .background(VColors.mint),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// STATS GRID
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun StatsGrid(
    attendanceRate: Int?,
    markPct: Int?,
    xpPoints: Int,
    quizzesDone: Int,
    streakDays: Int = 0,
    houseName: String? = null,
    leaderboardRank: Int? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                value = attendanceRate?.let { "$it%" } ?: "—",
                label = "Attendance",
                trend = null,
                trendColor = VColors.mint,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                value = markPct?.let { "$it%" } ?: "—",
                label = "Avg Marks",
                trend = null,
                trendColor = VColors.mint,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                value = formatCompact(xpPoints),
                label = "XP Points",
                trend = null,
                trendColor = VColors.mint,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                value = if (streakDays > 0) "$streakDays" else "—",
                label = "Day Streak",
                trend = null,
                trendColor = VColors.mint,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                value = houseName ?: "—",
                label = "House",
                trend = null,
                trendColor = VColors.mint,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                value = leaderboardRank?.let { "#$it" } ?: "—",
                label = "Rank",
                trend = null,
                trendColor = VColors.mint,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    trend: String? = null,
    trendColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            value,
            style = VTypography.h2.copy(fontSize = 24.sp),
            color = VColors.ink,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            label.uppercase(),
            style = VTypography.caption.copy(fontSize = 10.sp),
            color = VColors.ink3,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
        )
        if (trend != null) {
            Text(
                trend,
                style = VTypography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                color = trendColor,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// BADGES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun BadgesRow(badges: List<AchievementBadge>) {
    if (badges.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(VShapes.xl)
                .background(VColors.surfaceCard)
                .border(1.dp, VColors.line, VShapes.xl)
                .padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No badges yet",
                style = VTypography.caption,
                color = VColors.ink3,
            )
        }
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(badges.size) { idx ->
                val badge = badges[idx]
                BadgeCard(badge = badge)
            }
        }
    }
}

@Composable
private fun BadgeCard(badge: AchievementBadge) {
    Column(
        modifier = Modifier
            .width(152.dp)
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (badge.isLocked) VColors.lineSoft else VColors.violetSoft)
                .border(
                    width = if (badge.isLocked) 0.dp else 3.dp,
                    brush = if (badge.isLocked) Brush.linearGradient(listOf(VColors.lineSoft, VColors.lineSoft)) else Brush.linearGradient(listOf(VColors.violet, VColors.mint, VColors.violet)),
                    shape = CircleShape,
                )
                .padding(14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = pickBadgeIcon(badge.iconName),
                contentDescription = null,
                tint = if (badge.isLocked) VColors.ink3 else VColors.violet,
                modifier = Modifier.size(26.dp),
            )
        }
        Text(
            badge.title,
            style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp),
            color = VColors.ink,
            textAlign = TextAlign.Center,
        )
        Text(
            text = if (badge.isLocked) "Locked" else "Earned",
            style = VTypography.caption.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp),
            color = if (badge.isLocked) VColors.ink3 else VColors.mint,
            letterSpacing = 0.5.sp,
        )
    }
}

private fun pickBadgeIcon(iconName: String): ImageVector = when (iconName.lowercase()) {
    "book", "bookopen", "book-open" -> VIcons.Bookmark
    "zap", "flash" -> VIcons.Sparkles
    "target", "bullseye" -> VIcons.Target
    "check", "checkcircle" -> VIcons.Check
    "clipboard" -> VIcons.ClipboardList
    else -> VIcons.Star
}

// ═══════════════════════════════════════════════════════════════════════════════
// GAMIFICATION — badges from gamification API
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun GameBadgesRow(badges: List<GameBadge>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(badges.size) { idx ->
            val badge = badges[idx]
            GameBadgeCard(badge = badge)
        }
    }
}

@Composable
private fun GameBadgeCard(badge: GameBadge) {
    Column(
        modifier = Modifier
            .width(152.dp)
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(VColors.violetSoft)
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(listOf(VColors.violet, VColors.mint, VColors.violet)),
                    shape = CircleShape,
                )
                .padding(14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = pickBadgeIcon(badge.badgeIcon),
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(26.dp),
            )
        }
        Text(
            badge.badgeName,
            style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp),
            color = VColors.ink,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = badge.badgeRarity,
            style = VTypography.caption.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp),
            color = VColors.mint,
            letterSpacing = 0.5.sp,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GAMIFICATION — quests
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuestsRow(quests: List<StudentQuest>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        quests.forEach { quest ->
            QuestRow(quest = quest)
        }
    }
}

@Composable
private fun QuestRow(quest: StudentQuest) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                quest.questName,
                style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                color = VColors.ink,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (quest.completed) "Done" else "${quest.progress}/${quest.target}",
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                color = if (quest.completed) VColors.mint else VColors.violet,
            )
        }
        val progress = if (quest.target > 0) (quest.progress.toFloat() / quest.target).coerceIn(0f, 1f) else 0f
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(VShapes.full)
                .background(VColors.lineSoft),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .clip(VShapes.full)
                    .background(if (quest.completed) VColors.mint else VColors.violet),
            )
        }
        Text(
            text = "+${quest.xpReward} XP",
            style = VTypography.caption.copy(fontSize = 11.sp),
            color = VColors.ink3,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GAMIFICATION — active boosts
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun BoostsRow(boosts: List<XpBoost>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        boosts.forEach { boost ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(VShapes.md)
                        .background(VColors.violetSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = VIcons.Sparkles,
                        contentDescription = null,
                        tint = VColors.violet,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        boost.boostType.replaceFirstChar { it.uppercase() },
                        style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                        color = VColors.ink,
                    )
                    Text(
                        "${boost.multiplier}x XP multiplier",
                        style = VTypography.caption.copy(fontSize = 12.sp),
                        color = VColors.ink3,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GAMIFICATION — seasonal events
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun EventsRow(events: List<SeasonalEvent>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(events.size) { idx ->
            val event = events[idx]
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .clip(VShapes.xl)
                    .background(
                        Brush.linearGradient(
                            listOf(VColors.violetSoft, VColors.mintSoft),
                        ),
                    )
                    .border(1.dp, VColors.line, VShapes.xl)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    event.name,
                    style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 15.sp),
                    color = VColors.ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${event.startDate} - ${event.endDate}",
                    style = VTypography.caption.copy(fontSize = 11.sp),
                    color = VColors.ink3,
                )
                if (event.isActive) {
                    Box(
                        modifier = Modifier
                            .clip(VShapes.full)
                            .background(VColors.mint)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            "Active",
                            style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = VColors.white,
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GAMIFICATION — rewards shop
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun RewardsRow(
    rewards: List<Reward>,
    currentXp: Int,
    onRedeem: (String) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(rewards.size) { idx ->
            val reward = rewards[idx]
            RewardCard(reward = reward, currentXp = currentXp, onRedeem = onRedeem)
        }
    }
}

@Composable
private fun RewardCard(
    reward: Reward,
    currentXp: Int,
    onRedeem: (String) -> Unit,
) {
    val canAfford = currentXp >= reward.xpCost && reward.isActive
    Column(
        modifier = Modifier
            .width(160.dp)
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(VShapes.md)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.Star,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            reward.name,
            style = VTypography.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp),
            color = VColors.ink,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            reward.description,
            style = VTypography.caption.copy(fontSize = 11.sp),
            color = VColors.ink3,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${reward.xpCost} XP",
                style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                color = if (canAfford) VColors.mint else VColors.ink3,
            )
            val stock = reward.stockRemaining
            if (stock != null && stock <= 0) {
                Text(
                    "Out of stock",
                    style = VTypography.caption.copy(fontSize = 10.sp),
                    color = VColors.error,
                )
            } else if (canAfford) {
                Box(
                    modifier = Modifier
                        .clip(VShapes.full)
                        .background(VColors.violet)
                        .clickable { onRedeem(reward.id) }
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                ) {
                    Text(
                        "Redeem",
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                        color = VColors.white,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GAMIFICATION — redemption history
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun RedemptionsRow(redemptions: List<RewardRedemption>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        redemptions.forEach { redemption ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        redemption.rewardName,
                        style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                        color = VColors.ink,
                    )
                    Text(
                        "-${redemption.xpSpent} XP · ${redemption.createdAt.take(10)}",
                        style = VTypography.caption.copy(fontSize = 11.sp),
                        color = VColors.ink3,
                    )
                }
                val statusColor = when (redemption.status.uppercase()) {
                    "APPROVED", "FULFILLED" -> VColors.mint
                    "REJECTED" -> VColors.error
                    else -> VColors.gold
                }
                Box(
                    modifier = Modifier
                        .clip(VShapes.full)
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        redemption.status,
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        color = statusColor,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GAMIFICATION — XP history
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun XpHistoryRow(history: List<XpHistoryEntry>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        history.take(10).forEach { entry ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        entry.reason,
                        style = VTypography.body.copy(fontWeight = FontWeight.Medium, fontSize = 13.sp),
                        color = VColors.ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (entry.source.isNotBlank()) {
                        Text(
                            entry.source.replace("_", " ").replaceFirstChar { it.uppercase() },
                            style = VTypography.caption.copy(fontSize = 10.sp),
                            color = VColors.ink3,
                        )
                    }
                }
                Text(
                    "+${entry.amount} XP",
                    style = VTypography.body.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                    color = VColors.mint,
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GAMIFICATION — class goals
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ClassGoalsRow(goals: List<ClassGoal>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        goals.forEach { goal ->
            val progress = if (goal.target > 0) (goal.currentProgress.toFloat() / goal.target).coerceIn(0f, 1f) else 0f
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${goal.goalType}${if (goal.className.isNotBlank()) " · ${goal.className}" else ""}",
                        style = VTypography.body.copy(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                        color = VColors.ink,
                    )
                    Text(
                        "${goal.currentProgress}/${goal.target}",
                        style = VTypography.caption.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                        color = VColors.violet,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(VShapes.full)
                        .background(VColors.lineSoft),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(6.dp)
                            .clip(VShapes.full)
                            .background(VColors.violet),
                    )
                }
                if (goal.reward.isNotBlank()) {
                    Text(
                        "Reward: ${goal.reward}",
                        style = VTypography.caption.copy(fontSize = 11.sp),
                        color = VColors.ink3,
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ACCOUNT OPTIONS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun AccountCard(
    onOpenAccountSettings: () -> Unit,
    onLinkChild: () -> Unit,
    onDiscoverSchools: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(vertical = 8.dp),
    ) {
        AccountRow(
            icon = VIcons.User,
            iconBg = VColors.creamDeep,
            label = "Account Settings",
            onClick = onOpenAccountSettings,
        )
        HorizontalDivider(color = VColors.lineSoft, modifier = Modifier.padding(horizontal = 16.dp))
        AccountRow(
            icon = VIcons.Plus,
            iconBg = VColors.violetSoft,
            label = "Link Another Child",
            onClick = onLinkChild,
        )
        HorizontalDivider(color = VColors.lineSoft, modifier = Modifier.padding(horizontal = 16.dp))
        AccountRow(
            icon = VIcons.Search,
            iconBg = VColors.mintSoft,
            label = "Discover Schools",
            onClick = onDiscoverSchools,
        )
        HorizontalDivider(color = VColors.lineSoft, modifier = Modifier.padding(horizontal = 16.dp))
        AccountRow(
            icon = VIcons.LogOut,
            iconBg = VColors.errorSoft,
            label = "Logout",
            onClick = onLogout,
            isDestructive = true,
        )
    }
}

@Composable
private fun AccountRow(
    icon: ImageVector,
    iconBg: Color,
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(VShapes.md)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) VColors.error else VColors.ink,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = label,
            style = VTypography.body.copy(fontWeight = FontWeight.Medium, fontSize = 15.sp),
            color = if (isDestructive) VColors.error else VColors.ink,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = VIcons.ChevronRight,
            contentDescription = null,
            tint = VColors.ink3,
            modifier = Modifier.size(22.dp),
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GAMIFICATION — COLLAPSIBLE SECTION
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun GamificationCollapsibleSection(
    level: Int,
    levelTitle: String,
    totalXp: Int,
    streakDays: Int,
    houseName: String?,
    leaderboardRank: Int?,
    badges: List<GameBadge>,
    fallbackBadges: List<com.littlebridge.enrollplus.feature.parent.presentation.AchievementBadge>,
    quests: List<StudentQuest>,
    activeBoosts: List<XpBoost>,
    events: List<SeasonalEvent>,
    rewards: List<Reward>,
    currentXp: Int,
    redemptions: List<RewardRedemption>,
    xpHistory: List<XpHistoryEntry>,
    classGoals: List<ClassGoal>,
    onRedeemReward: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }
    var pendingRedeemRewardId by remember { mutableStateOf<String?>(null) }

    val activeCount = badges.size + quests.size + activeBoosts.size + events.size +
        rewards.size + redemptions.size + xpHistory.size + classGoals.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xxl)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        VColors.violetSoft,
                        VColors.surfaceCard,
                        VColors.surfaceCard,
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(0f, Float.POSITIVE_INFINITY),
                )
            )
            .border(1.dp, VColors.line, VShapes.xxl),
    ) {
        // ── Header (always visible, click to toggle) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(VShapes.lg)
                        .background(VColors.violet.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = VIcons.Sparkles,
                        contentDescription = null,
                        tint = VColors.violet,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Column {
                    Text(
                        "Gamification",
                        style = VTypography.h3.copy(fontSize = 18.sp),
                        color = VColors.ink,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        buildString {
                            append("Level $level · $levelTitle")
                            if (totalXp > 0) append(" · $totalXp XP")
                            if (streakDays > 0) append(" · $streakDays day streak")
                        },
                        style = VTypography.caption.copy(fontSize = 12.sp),
                        color = VColors.ink3,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (activeCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(VShapes.full)
                            .background(VColors.violet)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            "$activeCount",
                            style = VTypography.caption.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = VColors.white,
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) VIcons.ChevronUp else VIcons.ChevronDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = VColors.ink3,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        // ── Expandable content ──
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Quick stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    GamificationMiniStat(
                        label = "Level",
                        value = "$level",
                        color = VColors.violet,
                        modifier = Modifier.weight(1f),
                    )
                    GamificationMiniStat(
                        label = "XP",
                        value = if (totalXp > 0) formatCompact(totalXp) else "0",
                        color = VColors.mint,
                        modifier = Modifier.weight(1f),
                    )
                    GamificationMiniStat(
                        label = "Streak",
                        value = if (streakDays > 0) "${streakDays}d" else "—",
                        color = VColors.gold,
                        modifier = Modifier.weight(1f),
                    )
                    GamificationMiniStat(
                        label = "Rank",
                        value = leaderboardRank?.let { "#$it" } ?: "—",
                        color = VColors.sky,
                        modifier = Modifier.weight(1f),
                    )
                    GamificationMiniStat(
                        label = "House",
                        value = houseName ?: "—",
                        color = VColors.coral,
                        modifier = Modifier.weight(1f),
                    )
                }

                // Badges
                GamificationSubSection(title = "Badges") {
                    if (badges.isNotEmpty()) {
                        GameBadgesRow(badges = badges)
                    } else {
                        BadgesRow(badges = fallbackBadges)
                    }
                }

                // Quests
                GamificationSubSection(title = "Quests") {
                    if (quests.isNotEmpty()) {
                        QuestsRow(quests = quests)
                    } else {
                        GamificationEmptyState(text = "No active quests. Check back soon!")
                    }
                }

                // Active Boosts
                GamificationSubSection(title = "Active Boosts") {
                    if (activeBoosts.isNotEmpty()) {
                        BoostsRow(boosts = activeBoosts)
                    } else {
                        GamificationEmptyState(text = "No active XP boosts right now.")
                    }
                }

                // Seasonal Events
                GamificationSubSection(title = "Seasonal Events") {
                    if (events.isNotEmpty()) {
                        EventsRow(events = events)
                    } else {
                        GamificationEmptyState(text = "No seasonal events running right now.")
                    }
                }

                // Rewards Shop
                GamificationSubSection(title = "Rewards Shop") {
                    if (rewards.isNotEmpty()) {
                        RewardsRow(
                            rewards = rewards,
                            currentXp = currentXp,
                            onRedeem = { rewardId -> pendingRedeemRewardId = rewardId },
                        )
                    } else {
                        GamificationEmptyState(text = "No rewards available in the shop yet.")
                    }
                }

                // Redemption History
                GamificationSubSection(title = "Redemption History") {
                    if (redemptions.isNotEmpty()) {
                        RedemptionsRow(redemptions = redemptions)
                    } else {
                        GamificationEmptyState(text = "No reward redemptions yet.")
                    }
                }

                // XP History
                GamificationSubSection(title = "XP History") {
                    if (xpHistory.isNotEmpty()) {
                        XpHistoryRow(history = xpHistory)
                    } else {
                        GamificationEmptyState(text = "No XP earned yet. Encourage your child to complete activities!")
                    }
                }

                // Class Goals
                GamificationSubSection(title = "Class Goals") {
                    if (classGoals.isNotEmpty()) {
                        ClassGoalsRow(goals = classGoals)
                    } else {
                        GamificationEmptyState(text = "No class goals set yet.")
                    }
                }
            }
        }
    }

    pendingRedeemRewardId?.let { rewardId ->
        val reward = rewards.find { it.id == rewardId }
        VConfirmDialog(
            visible = true,
            title = "Redeem Reward?",
            message = "Spend ${reward?.xpCost ?: 0} XP on \"${reward?.name ?: "this reward"}\"? This cannot be undone.",
            confirmLabel = "Redeem",
            onConfirm = {
                onRedeemReward(rewardId)
                pendingRedeemRewardId = null
            },
            onDismiss = { pendingRedeemRewardId = null },
        )
    }
}

@Composable
private fun GamificationMiniStat(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(VShapes.md)
            .background(VColors.surfaceTint)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            value,
            style = VTypography.body.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            label,
            style = VTypography.caption.copy(fontSize = 10.sp),
            color = VColors.ink3,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun GamificationSubSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title,
            style = VTypography.caption.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = VColors.ink2,
        )
        content()
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SHARED
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun GamificationEmptyState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.xl)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = VTypography.caption.copy(fontSize = 13.sp),
            color = VColors.ink3,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = VTypography.h3.copy(fontSize = 20.sp),
            color = VColors.ink,
            fontWeight = FontWeight.ExtraBold,
        )
        if (action != null) {
            Text(
                text = action,
                style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                color = VColors.violet,
                modifier = Modifier.clickable(onClick = onAction),
            )
        }
    }
}

private fun formatCompact(value: Int): String {
    return when {
        value >= 1000 -> "${(value / 1000f).roundToInt()}K"
        else -> value.toString()
    }
}

private fun markDisplayGrade(mark: ParentMarkDto?): String {
    if (mark == null) return "—"
    val pct = if (mark.maxMarks > 0) ((mark.marks ?: 0.0) / mark.maxMarks * 100).roundToInt() else 0
    return when {
        pct >= 90 -> "A+"
        pct >= 80 -> "A"
        pct >= 70 -> "B+"
        pct >= 60 -> "B"
        pct >= 50 -> "C"
        else -> "D"
    }
}
