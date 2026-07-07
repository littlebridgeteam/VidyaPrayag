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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfile
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentProfileCardScreenV2(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    onLinkChild: () -> Unit = {},
    onDiscoverSchools: () -> Unit = {},
    onOpenAccountSettings: () -> Unit = {},
    viewModel: ParentDashboardViewModel = koinViewModel(),
    profileViewModel: ParentProfileViewModel = koinViewModel(),
    academicsViewModel: ParentAcademicsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val profile by profileViewModel.state.collectAsStateV2()
    val academics by academicsViewModel.state.collectAsStateV2()

    ProfileContent(
        state = state,
        profile = profile,
        academics = academics,
        onRetry = viewModel::load,
        onRetryProfile = profileViewModel::load,
        onLogout = onLogout,
        onLinkChild = onLinkChild,
        onDiscoverSchools = onDiscoverSchools,
        onOpenAccountSettings = onOpenAccountSettings,
        modifier = modifier,
    )
}

@Composable
private fun ProfileContent(
    state: ParentDashboardState,
    profile: ParentProfileState,
    academics: ParentAcademicsState,
    onRetry: () -> Unit,
    onRetryProfile: () -> Unit,
    onLogout: () -> Unit,
    onLinkChild: () -> Unit,
    onDiscoverSchools: () -> Unit,
    onOpenAccountSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 110.dp),
    ) {
        ParentPortalHeader(
            label = "Profile",
            children = state.children,
            selectedChild = state.selectedChild,
            onSelectChild = { /* profile tab does not switch child context */ },
            onOpenNotifications = {},
            unreadNotificationsCount = 0,
        )

        when {
            state.isLoading && state.children.isEmpty() -> ProfileSkeleton()
            state.error != null && state.children.isEmpty() -> ProfileError(message = state.error ?: "", onRetry = onRetry)
            else -> ProfileLoaded(
                state = state,
                profile = profile,
                academics = academics,
                onRetryProfile = onRetryProfile,
                onLogout = onLogout,
                onLinkChild = onLinkChild,
                onDiscoverSchools = onDiscoverSchools,
                onOpenAccountSettings = onOpenAccountSettings,
            )
        }
    }
}

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
                .height(120.dp)
                .clip(VShapes.lg)
                .background(VColors.lineSoft),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clip(VShapes.lg)
                        .background(VColors.lineSoft),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
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

@Composable
private fun ProfileLoaded(
    state: ParentDashboardState,
    profile: ParentProfileState,
    academics: ParentAcademicsState,
    onRetryProfile: () -> Unit,
    onLogout: () -> Unit,
    onLinkChild: () -> Unit,
    onDiscoverSchools: () -> Unit,
    onOpenAccountSettings: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ParentAccountCard(profile = profile.profile, onRetry = onRetryProfile)

        if (state.children.isNotEmpty()) {
            ChildStatsSection(state = state, academics = academics)
        }

        AccountActions(
            onOpenAccountSettings = onOpenAccountSettings,
            onLinkChild = onLinkChild,
            onDiscoverSchools = onDiscoverSchools,
            onLogout = onLogout,
        )

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ParentAccountCard(profile: ParentProfile?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (profile == null) {
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(28.dp))
            }
        } else {
            Box(
                modifier = Modifier.size(72.dp).clip(CircleShape).background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = profile.name.take(1).uppercase(),
                    style = VTypography.h2.copy(fontWeight = FontWeight.Bold),
                    color = VColors.violet,
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = profile.name.ifBlank { "Parent" },
                    style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
                    color = VColors.ink,
                )
                if (profile.email.isNotBlank()) {
                    Text(
                        text = profile.email,
                        style = VTypography.caption,
                        color = VColors.ink2,
                    )
                }
                if (profile.phone.isNotBlank()) {
                    Text(
                        text = profile.phone,
                        style = VTypography.caption,
                        color = VColors.ink2,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChildStatsSection(
    state: ParentDashboardState,
    academics: ParentAcademicsState,
) {
    val attendance = state.attendance
    val fees = state.fees
    val latestMark = state.latestMark

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Child stats",
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = VColors.ink,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = "Attendance",
                value = if (attendance != null) "${attendance.attendanceRate}%" else "—",
                icon = VIcons.Calendar,
                iconBg = VColors.violetSoft,
                iconColor = VColors.violet,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "Quizzes",
                value = academics.quizzes.size.toString(),
                icon = VIcons.Academic,
                iconBg = VColors.creamDeep,
                iconColor = VColors.ink,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = "Fees due",
                value = fees?.outstandingFees ?: "₹0",
                icon = VIcons.WalletPremium,
                iconBg = VColors.successSoft,
                iconColor = VColors.success,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "Latest marks",
                value = latestMark?.let { m ->
                    if (m.maxMarks > 0) "${((m.marks ?: 0.0) / m.maxMarks * 100).roundToInt()}%" else "—"
                } ?: "—",
                icon = VIcons.Star,
                iconBg = VColors.goldSoft,
                iconColor = VColors.gold,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = value,
                style = VTypography.h3,
                color = VColors.ink,
            )
            Text(
                text = label,
                style = VTypography.caption,
                color = VColors.ink2,
            )
        }
    }
}

@Composable
private fun AccountActions(
    onOpenAccountSettings: () -> Unit,
    onLinkChild: () -> Unit,
    onDiscoverSchools: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(vertical = 8.dp),
    ) {
        val items = listOf(
            ActionRow("Account settings", VIcons.Settings, onOpenAccountSettings),
            ActionRow("Link another child", VIcons.UserPlus, onLinkChild),
            ActionRow("Discover schools", VIcons.Search, onDiscoverSchools),
            ActionRow("Log out", VIcons.LogOut, onLogout, isDestructive = true),
        )
        items.forEach { (label, icon, onClick, isDestructive) ->
            ActionRowItem(
                label = label,
                icon = icon,
                onClick = onClick,
                isDestructive = isDestructive,
            )
        }
    }
}

@Composable
private fun ActionRowItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    val color = if (isDestructive) VColors.error else VColors.ink
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isDestructive) VColors.errorSoft else VColors.creamDeep),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) VColors.error else VColors.ink,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = label,
            style = VTypography.body.copy(fontWeight = FontWeight.Medium),
            color = color,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = VIcons.ChevronRight,
            contentDescription = null,
            tint = VColors.ink3,
            modifier = Modifier.size(20.dp),
        )
    }
}

private data class ActionRow(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false,
)
