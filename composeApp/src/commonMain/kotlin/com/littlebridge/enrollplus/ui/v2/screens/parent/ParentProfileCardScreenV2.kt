package com.littlebridge.enrollplus.ui.v2.screens.parent

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.littlebridge.enrollplus.feature.parent.presentation.AchievementBadge
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressState
import com.littlebridge.enrollplus.feature.parent.presentation.TrackProgressViewModel
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel
import com.littlebridge.enrollplus.ui.v2.theme.VTheme
import androidx.compose.foundation.shape.RoundedCornerShape

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
) {
    val state by viewModel.state.collectAsStateV2()
    val profile by profileViewModel.state.collectAsStateV2()
    val academics by academicsViewModel.state.collectAsStateV2()
    val track by trackViewModel.state.collectAsStateV2()

    LaunchedEffect(Unit) {
        if (state.children.isEmpty()) viewModel.load()
    }

    LaunchedEffect(state.selectedChild?.id) {
        state.selectedChild?.id?.let {
            academicsViewModel.selectChild(it)
        }
    }

    ProfileContent(
        state = state,
        profile = profile,
        academics = academics,
        track = track,
        parentName = parentName,
        children = children,
        selectedChild = selectedChild,
        onSelectChild = onSelectChild,
        onRetry = viewModel::load,
        onRetryProfile = profileViewModel::load,
        onLogout = onLogout,
        onLinkChild = onLinkChild,
        onDiscoverSchools = onDiscoverSchools,
        onOpenAccountSettings = onOpenAccountSettings,
        onOpenNotifications = onOpenNotifications,
        unreadNotificationsCount = unreadNotificationsCount,
        modifier = modifier,
    )
}

@Composable
private fun ProfileContent(
    state: ParentDashboardState,
    profile: ParentProfileState,
    academics: com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState,
    track: TrackProgressState,
    parentName: String,
    children: List<DashboardChildSummary>,
    selectedChild: DashboardChildSummary?,
    onSelectChild: (String) -> Unit,
    onRetry: () -> Unit,
    onRetryProfile: () -> Unit,
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
            .background(VTheme.colors.cream)
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
        )

        when {
            state.isLoading && state.children.isEmpty() -> ProfileSkeleton()
            state.error != null && state.children.isEmpty() -> ProfileError(message = state.error ?: "", onRetry = onRetry)
            else -> ProfileLoaded(
                state = state,
                profile = profile,
                academics = academics,
                track = track,
                onRetryProfile = onRetryProfile,
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
                .clip(RoundedCornerShape(32.dp))
                .background(VTheme.colors.lineSoft),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(VTheme.colors.lineSoft),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(VTheme.colors.lineSoft),
        )
    }
}

@Composable
private fun ProfileError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(VTheme.colors.errorSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.AlertTriangle,
                contentDescription = "",
                tint = VTheme.colors.error,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = "Couldn't load profile",
            style = VTheme.type.body.copy(fontWeight = FontWeight.SemiBold),
            color = VTheme.colors.ink,
        )
        Text(
            text = message,
            style = VTheme.type.caption,
            color = VTheme.colors.ink2,
        )
        Text(
            text = "Retry",
            style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold),
            color = VTheme.colors.violet,
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
    onRetryProfile: () -> Unit,
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ProfileHeroCard(
            childName = childName,
            level = child?.currentLevel ?: track.currentLevel,
            overallProgress = track.overallProgress,
        )

        SectionHeader(title = "Stats")
        StatsGrid(
            attendanceRate = attendanceRate,
            markPct = markPct,
            xpPoints = (track.overallProgress * 5000).roundToInt(),
            quizzesDone = academics.quizzes.size,
        )

        SectionHeader(title = "Badges", action = "All", onAction = {})
        BadgesRow(badges = track.badges)

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
    overallProgress: Float,
) {
    val xp = (overallProgress * 5000).roundToInt()
    val xpMax = 5000

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        VTheme.colors.violet,
                        Color(0xFF4A30C4),
                        VTheme.colors.violetInk,
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
                    .clip(RoundedCornerShape(24.dp))
                    .background(VTheme.colors.white.copy(alpha = 0.2f))
                    .border(2.dp, VTheme.colors.white.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = childName.take(1).uppercase(),
                    style = VTheme.type.h2.copy(fontSize = 30.sp),
                    color = VTheme.colors.white,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    childName,
                    style = VTheme.type.h3.copy(fontSize = 20.sp),
                    color = VTheme.colors.white,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Student",
                    style = VTheme.type.caption.copy(fontSize = 13.sp),
                    color = VTheme.colors.white.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(VTheme.colors.white.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = "🏆 Level $level Scholar",
                        style = VTheme.type.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                        color = VTheme.colors.white,
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
                text = "Level $level — Scholar",
                style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                color = VTheme.colors.white,
            )
            Text(
                text = "$xp / $xpMax XP",
                style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                color = VTheme.colors.white.copy(alpha = 0.8f),
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(VTheme.colors.white.copy(alpha = 0.2f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((overallProgress).coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(VTheme.colors.mint),
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
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                value = attendanceRate?.let { "$it%" } ?: "—",
                label = "Attendance",
                trend = "↑ 2% this month",
                trendColor = VTheme.colors.mint,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                value = markPct?.let { "$it%" } ?: "—",
                label = "Avg Marks",
                trend = "↑ 5% this term",
                trendColor = VTheme.colors.mint,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                value = formatCompact(xpPoints),
                label = "XP Points",
                trend = "↑ ${formatCompact((xpPoints * 0.12f).roundToInt())} this week",
                trendColor = VTheme.colors.mint,
                modifier = Modifier.weight(1f),
            )
            StatCard(
                value = quizzesDone.toString(),
                label = "Quizzes Done",
                trend = "↑ 3 this week",
                trendColor = VTheme.colors.mint,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    trend: String,
    trendColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(18.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            value,
            style = VTheme.type.h2.copy(fontSize = 24.sp),
            color = VTheme.colors.ink,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            label.uppercase(),
            style = VTheme.type.caption.copy(fontSize = 10.sp),
            color = VTheme.colors.ink3,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
        )
        Text(
            trend,
            style = VTheme.type.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
            color = trendColor,
        )
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
                .clip(RoundedCornerShape(24.dp))
                .background(VTheme.colors.surfaceCard)
                .border(1.dp, VTheme.colors.line, RoundedCornerShape(24.dp))
                .padding(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No badges yet",
                style = VTheme.type.caption,
                color = VTheme.colors.ink3,
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
            .clip(RoundedCornerShape(24.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (badge.isLocked) VTheme.colors.lineSoft else VTheme.colors.violetSoft)
                .border(
                    width = if (badge.isLocked) 0.dp else 3.dp,
                    brush = if (badge.isLocked) Brush.linearGradient(listOf(VTheme.colors.lineSoft, VTheme.colors.lineSoft)) else Brush.linearGradient(listOf(VTheme.colors.violet, VTheme.colors.mint, VTheme.colors.violet)),
                    shape = CircleShape,
                )
                .padding(14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = pickBadgeIcon(badge.iconName),
                contentDescription = "",
                tint = if (badge.isLocked) VTheme.colors.ink3 else VTheme.colors.violet,
                modifier = Modifier.size(26.dp),
            )
        }
        Text(
            badge.title,
            style = VTheme.type.body.copy(fontWeight = FontWeight.ExtraBold, fontSize = 14.sp),
            color = VTheme.colors.ink,
            textAlign = TextAlign.Center,
        )
        Text(
            text = if (badge.isLocked) "Locked" else "Earned",
            style = VTheme.type.caption.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp),
            color = if (badge.isLocked) VTheme.colors.ink3 else VTheme.colors.mint,
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
            .clip(RoundedCornerShape(24.dp))
            .background(VTheme.colors.surfaceCard)
            .border(1.dp, VTheme.colors.line, RoundedCornerShape(24.dp))
            .padding(vertical = 8.dp),
    ) {
        AccountRow(
            icon = VIcons.User,
            iconBg = VTheme.colors.creamDeep,
            label = "Account Settings",
            onClick = onOpenAccountSettings,
        )
        HorizontalDivider(color = VTheme.colors.lineSoft, modifier = Modifier.padding(horizontal = 16.dp))
        AccountRow(
            icon = VIcons.Plus,
            iconBg = VTheme.colors.violetSoft,
            label = "Link Another Child",
            onClick = onLinkChild,
        )
        HorizontalDivider(color = VTheme.colors.lineSoft, modifier = Modifier.padding(horizontal = 16.dp))
        AccountRow(
            icon = VIcons.Search,
            iconBg = VTheme.colors.mintSoft,
            label = "Discover Schools",
            onClick = onDiscoverSchools,
        )
        HorizontalDivider(color = VTheme.colors.lineSoft, modifier = Modifier.padding(horizontal = 16.dp))
        AccountRow(
            icon = VIcons.LogOut,
            iconBg = VTheme.colors.errorSoft,
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
                .clip(RoundedCornerShape(14.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = if (isDestructive) VTheme.colors.error else VTheme.colors.ink,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = label,
            style = VTheme.type.body.copy(fontWeight = FontWeight.Medium, fontSize = 15.sp),
            color = if (isDestructive) VTheme.colors.error else VTheme.colors.ink,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = VIcons.ChevronRight,
            contentDescription = "",
            tint = VTheme.colors.ink3,
            modifier = Modifier.size(22.dp),
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SHARED
// ═══════════════════════════════════════════════════════════════════════════════

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
            style = VTheme.type.h3.copy(fontSize = 20.sp),
            color = VTheme.colors.ink,
            fontWeight = FontWeight.ExtraBold,
        )
        if (action != null) {
            Text(
                text = action,
                style = VTheme.type.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
                color = VTheme.colors.violet,
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
