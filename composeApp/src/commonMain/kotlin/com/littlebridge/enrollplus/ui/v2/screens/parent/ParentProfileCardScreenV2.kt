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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
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
            .padding(bottom = 100.dp),
    ) {
        ParentPortalHeader(
            label = "Profile",
            children = state.children,
            selectedChild = state.selectedChild,
            onSelectChild = { },
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
                .height(140.dp)
                .clip(VShapes.lg)
                .background(VColors.lineSoft),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
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
// LOADED — premium profile layout
// ═══════════════════════════════════════════════════════════════════════════════

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
    val child = state.selectedChild
    val childName = child?.name?.ifBlank { null } ?: "Your Child"
    val attendanceRate = state.attendance?.attendanceRate
    val feesDue = state.fees?.outstandingFees
    val markPct = state.latestMark?.let { m ->
        if (m.maxMarks > 0) ((m.marks ?: 0.0) / m.maxMarks * 100).roundToInt() else null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Parent identity card (matches hero card design) ──
        ParentIdentityCard(profile = profile.profile)

        // ── Child snapshot (matches HomeHeroCard inline stats) ──
        if (child != null) {
            SectionLabel("Child Overview")
            ChildSnapshotCard(
                childName = childName,
                level = child.currentLevel,
                attendanceRate = attendanceRate,
                markPct = markPct,
                feesDue = feesDue,
            )
        }

        // ── Settings list (grouped card with dividers) ──
        SectionLabel("Account")
        SettingsCard(
            onOpenAccountSettings = onOpenAccountSettings,
            onLinkChild = onLinkChild,
            onDiscoverSchools = onDiscoverSchools,
            onLogout = onLogout,
        )

        Spacer(Modifier.height(8.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PARENT IDENTITY CARD
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ParentIdentityCard(profile: ParentProfile?) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(VColors.surfaceCard, VShapes.lg)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(20.dp),
    ) {
        if (profile == null) {
            Box(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = VColors.violet, modifier = Modifier.size(24.dp))
            }
        } else {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(VColors.violetSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = profile.name.take(1).uppercase(),
                        style = VTypography.h3.copy(fontSize = 18.sp),
                        color = VColors.violet,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        profile.name.ifBlank { "Parent" },
                        style = VTypography.h3.copy(fontSize = 16.sp),
                        color = VColors.ink,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Parent Account",
                        style = VTypography.caption.copy(fontSize = 11.sp),
                        color = VColors.ink3,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = VColors.lineSoft)
            Spacer(Modifier.height(14.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ContactItem(
                    icon = VIcons.Phone,
                    value = profile.phone.ifBlank { "—" },
                    label = "Phone",
                    modifier = Modifier.weight(1f),
                )
                ContactItem(
                    icon = VIcons.Mail,
                    value = profile.email.ifBlank { "—" },
                    label = "Email",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ContactItem(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VColors.ink3,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            value,
            style = VTypography.caption.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
            color = VColors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            label,
            style = VTypography.caption.copy(fontSize = 10.sp),
            color = VColors.ink3,
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// CHILD SNAPSHOT CARD — matches HomeHeroCard inline stats
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ChildSnapshotCard(
    childName: String,
    level: Int,
    attendanceRate: Int?,
    markPct: Int?,
    feesDue: String?,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(VColors.surfaceCard, VShapes.lg)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(20.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(VColors.violetSoft),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = childName.take(1).uppercase(),
                    style = VTypography.h3.copy(fontSize = 16.sp),
                    color = VColors.violet,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    childName,
                    style = VTypography.h3.copy(fontSize = 16.sp),
                    color = VColors.ink,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                if (level > 0) {
                    Box(
                        modifier = Modifier
                            .clip(VShapes.sm)
                            .background(VColors.violetSoft)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "Level $level",
                            style = VTypography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                            color = VColors.violet,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = VColors.lineSoft)
        Spacer(Modifier.height(14.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            InlineStat(
                value = attendanceRate?.let { "$it%" } ?: "—",
                label = "Attendance",
                modifier = Modifier.weight(1f),
            )
            InlineStat(
                value = markPct?.let { "$it%" } ?: "—",
                label = "Latest",
                modifier = Modifier.weight(1f),
            )
            InlineStat(
                value = feesDue ?: "—",
                label = "Fees Due",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun InlineStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            value,
            style = VTypography.h3.copy(fontSize = 16.sp),
            color = VColors.ink,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(label, style = VTypography.caption.copy(fontSize = 10.sp), color = VColors.ink3)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SETTINGS CARD — grouped list with dividers (iOS-style)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SettingsCard(
    onOpenAccountSettings: () -> Unit,
    onLinkChild: () -> Unit,
    onDiscoverSchools: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VColors.surfaceCard, VShapes.lg)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(vertical = 4.dp),
    ) {
        SettingsRow(
            icon = VIcons.Settings,
            iconColor = VColors.ink,
            iconBg = VColors.creamDeep,
            label = "Account Settings",
            onClick = onOpenAccountSettings,
        )
        HorizontalDivider(color = VColors.lineSoft, modifier = Modifier.padding(horizontal = 16.dp))
        SettingsRow(
            icon = VIcons.UserPlus,
            iconColor = VColors.violet,
            iconBg = VColors.violetSoft,
            label = "Link Another Child",
            onClick = onLinkChild,
        )
        HorizontalDivider(color = VColors.lineSoft, modifier = Modifier.padding(horizontal = 16.dp))
        SettingsRow(
            icon = VIcons.Search,
            iconColor = VColors.sky,
            iconBg = VColors.skySoft,
            label = "Discover Schools",
            onClick = onDiscoverSchools,
        )
        HorizontalDivider(color = VColors.lineSoft, modifier = Modifier.padding(horizontal = 16.dp))
        SettingsRow(
            icon = VIcons.LogOut,
            iconColor = VColors.error,
            iconBg = VColors.errorSoft,
            label = "Log Out",
            onClick = onLogout,
            isDestructive = true,
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconColor: Color,
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
            modifier = Modifier.size(32.dp).clip(VShapes.sm).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = label,
            style = VTypography.body.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp),
            color = if (isDestructive) VColors.error else VColors.ink,
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

// ═══════════════════════════════════════════════════════════════════════════════
// SHARED
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = VTypography.label,
        color = VColors.ink,
        fontWeight = FontWeight.Bold,
    )
}
