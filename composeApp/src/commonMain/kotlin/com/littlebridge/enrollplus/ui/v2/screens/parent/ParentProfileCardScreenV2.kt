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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.domain.model.DashboardChildSummary
import com.littlebridge.enrollplus.feature.parent.domain.model.FeeData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentAttendanceData
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentMarkDto
import com.littlebridge.enrollplus.feature.parent.domain.model.ParentQuizDto
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentAcademicsViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentDashboardViewModel
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileState
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileViewModel
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VShapes
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.ui.v2.components.VAvatar
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VConfirmDialog
import com.littlebridge.enrollplus.ui.v2.components.VIcons
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

/**
 * ParentProfileCardScreenV2 — the rebuilt parent Profile tab.
 *
 * A calm, premium, information-first profile surface: parent account at the
 * top, the selected child's identity card, practical stats, and clear account
 * actions. No house badges, no collectible cards, no gamification rings, no
 * bouncy animations. Cream canvas + white cards + violet accents.
 */
@Composable
fun ParentProfileCardScreenV2(
    onLogout: () -> Unit = {},
    onLinkChild: () -> Unit = {},
    onDiscoverSchools: () -> Unit = {},
    onOpenAccountSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ParentDashboardViewModel = koinViewModel(),
    profileViewModel: ParentProfileViewModel = koinViewModel(),
    academicsViewModel: ParentAcademicsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val profile by profileViewModel.state.collectAsStateV2()
    val academics by academicsViewModel.state.collectAsStateV2()

    val activeChildId = state.selectedChild?.id
    LaunchedEffect(activeChildId) {
        activeChildId?.let { academicsViewModel.loadQuizzes(it) }
    }

    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showThemePicker by remember { mutableStateOf(false) }
    var showLanguagePicker by remember { mutableStateOf(false) }

    ProfileContent(
        state = state,
        profile = profile,
        academics = academics,
        onRetry = viewModel::load,
        onRetryProfile = profileViewModel::load,
        onLogout = { showLogoutConfirm = true },
        onLinkChild = onLinkChild,
        onDiscoverSchools = onDiscoverSchools,
        onOpenAccountSettings = onOpenAccountSettings,
        onThemeTap = { showThemePicker = true },
        onLanguageTap = { showLanguagePicker = true },
        modifier = modifier,
    )

    VConfirmDialog(
        visible = showLogoutConfirm,
        title = "Log out?",
        message = "You'll need to sign in again to follow your child's progress.",
        confirmLabel = "Log out",
        onConfirm = { showLogoutConfirm = false; onLogout() },
        onDismiss = { showLogoutConfirm = false },
        icon = VIcons.AlertTriangle,
    )

    if (showThemePicker) {
        ThemePickerDialog(
            currentMode = profile.profile?.let { "" } ?: "system",
            onSelect = { mode ->
                profileViewModel.setThemeMode(mode)
                showThemePicker = false
            },
            onDismiss = { showThemePicker = false },
        )
    }

    if (showLanguagePicker) {
        LanguagePickerDialog(
            currentLanguage = "English",
            onSelect = { lang ->
                // TODO: wire to locale preference when available
                showLanguagePicker = false
            },
            onDismiss = { showLanguagePicker = false },
        )
    }
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
    onThemeTap: () -> Unit,
    onLanguageTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.cream)
            .padding(bottom = 140.dp),
    ) {
        when {
            state.isLoading && state.selectedChild == null -> ProfileSkeleton()
            state.error != null && state.selectedChild == null -> ProfileError(
                message = state.error ?: "",
                onRetry = onRetry,
            )
            state.children.isEmpty() -> ProfileEmpty(
                onLinkChild = onLinkChild,
                onDiscoverSchools = onDiscoverSchools,
            )
            else -> ProfileLoaded(
                state = state,
                profile = profile,
                academics = academics,
                onRetryProfile = onRetryProfile,
                onLogout = onLogout,
                onLinkChild = onLinkChild,
                onDiscoverSchools = onDiscoverSchools,
                onOpenAccountSettings = onOpenAccountSettings,
                onThemeTap = onThemeTap,
                onLanguageTap = onLanguageTap,
            )
        }
    }
}

@Composable
private fun ProfileSkeleton() {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SkeletonCard(height = 120.dp)
        SkeletonCard(height = 180.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) { SkeletonCard(modifier = Modifier.weight(1f), height = 96.dp) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(2) { SkeletonCard(modifier = Modifier.weight(1f), height = 96.dp) }
        }
        SkeletonCard(height = 240.dp)
    }
}

@Composable
private fun SkeletonCard(height: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(VShapes.lg)
            .background(VColors.lineSoft),
    )
}

@Composable
private fun ProfileError(message: String, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(VColors.errorSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.AlertTriangle,
                contentDescription = null,
                tint = VColors.error,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = appString(StringKeys.COMMON_ERROR_GENERIC),
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            style = VTypography.body,
            color = VColors.ink2,
        )
        Spacer(Modifier.height(20.dp))
        VButton(
            text = appString(StringKeys.COMMON_BUTTON_RETRY),
            onClick = onRetry,
        )
    }
}

@Composable
private fun ProfileEmpty(onLinkChild: () -> Unit, onDiscoverSchools: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(VColors.violetSoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = VIcons.User,
                contentDescription = null,
                tint = VColors.violet,
                modifier = Modifier.size(36.dp),
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = appString(StringKeys.PH_NO_CHILD_LINKED),
            style = VTypography.h3,
            color = VColors.ink,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = appString(StringKeys.PH_NO_CHILD_LINKED_DESC),
            style = VTypography.body,
            color = VColors.ink2,
        )
        Spacer(Modifier.height(24.dp))
        VButton(
            text = "Link a child",
            onClick = onLinkChild,
        )
        Spacer(Modifier.height(10.dp))
        VButton(
            text = "Discover schools",
            onClick = onDiscoverSchools,
            variant = VButtonVariant.Secondary,
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
    onThemeTap: () -> Unit,
    onLanguageTap: () -> Unit,
) {
    val child = state.selectedChild

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ParentAccountHeader(profile = profile, onRetry = onRetryProfile)
        ChildIdentityCard(child = child, className = state.timetable?.className.orEmpty())
        StatsGrid(
            attendance = state.attendance,
            latestMark = state.latestMark,
            fees = state.fees,
            quizzes = academics.quizzes,
        )
        AccountActions(
            onOpenAccountSettings = onOpenAccountSettings,
            onLinkChild = onLinkChild,
            onDiscoverSchools = onDiscoverSchools,
            onLanguageTap = onLanguageTap,
            onThemeTap = onThemeTap,
            onLogout = onLogout,
        )
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun ParentAccountHeader(profile: ParentProfileState, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.lg)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.lg)
            .padding(16.dp),
    ) {
        if (profile.isLoading && profile.profile == null) {
            Box(Modifier.fillMaxWidth().height(56.dp).clip(VShapes.md).background(VColors.lineSoft))
        } else if (profile.error != null && profile.profile == null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = VIcons.AlertTriangle,
                    contentDescription = null,
                    tint = VColors.error,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = profile.error ?: "",
                    style = VTypography.bodySmall,
                    color = VColors.error,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "Retry",
                    style = VTypography.label.copy(color = VColors.violet),
                    modifier = Modifier.clickable(onClick = onRetry),
                )
            }
        } else {
            val p = profile.profile
            Row(verticalAlignment = Alignment.CenterVertically) {
                VAvatar(name = p?.name ?: "Parent", src = p?.photoUrl, size = 52.dp)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = p?.name?.ifBlank { "Parent" } ?: "Parent",
                        style = VTypography.body.copy(fontWeight = FontWeight.Bold),
                        color = VColors.ink,
                    )
                    if (p?.email?.isNotBlank() == true) {
                        Text(
                            text = p.email,
                            style = VTypography.caption,
                            color = VColors.ink2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (p?.phone?.isNotBlank() == true) {
                        Text(
                            text = p.phone,
                            style = VTypography.caption,
                            color = VColors.ink3,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChildIdentityCard(child: DashboardChildSummary?, className: String) {
    val name = child?.name?.ifBlank { "Your child" } ?: "Your child"

    Column(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.xl)
            .background(VColors.violet)
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VAvatar(name = name, src = child?.profilePic, size = 72.dp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = VTypography.h2,
                    color = VColors.white,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = className.ifBlank { "Class not set" },
                    style = VTypography.body,
                    color = VColors.white.copy(alpha = 0.85f),
                )
            }
        }
    }
}

@Composable
private fun StatsGrid(
    attendance: ParentAttendanceData?,
    latestMark: ParentMarkDto?,
    fees: FeeData?,
    quizzes: List<ParentQuizDto>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Child stats",
            style = VTypography.body.copy(fontWeight = FontWeight.Bold),
            color = VColors.ink,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                icon = VIcons.School,
                label = "Attendance",
                value = "${attendance?.attendanceRate ?: 0}%",
                color = VColors.mint,
                softColor = VColors.mintSoft,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                icon = VIcons.Star,
                label = "Latest marks",
                value = markDisplay(latestMark),
                color = VColors.sky,
                softColor = VColors.skySoft,
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile(
                icon = VIcons.Wallet,
                label = "Fees due",
                value = fees?.outstandingFees?.ifBlank { "₹0" } ?: "₹0",
                color = if ((fees?.overdueCount ?: 0) > 0) VColors.coral else VColors.gold,
                softColor = if ((fees?.overdueCount ?: 0) > 0) VColors.coralSoft else VColors.goldSoft,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                icon = VIcons.ListChecks,
                label = "Quizzes",
                value = quizzes.size.toString(),
                color = VColors.violet,
                softColor = VColors.violetSoft,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatTile(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    softColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(VShapes.md)
            .background(VColors.surfaceCard)
            .border(1.dp, VColors.line, VShapes.md)
            .padding(14.dp),
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(VShapes.md)
                .background(softColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = value,
            style = VTypography.h3.copy(fontWeight = FontWeight.ExtraBold),
            color = color,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = VTypography.caption,
            color = VColors.ink2,
        )
    }
}

@Composable
private fun AccountActions(
    onOpenAccountSettings: () -> Unit,
    onLinkChild: () -> Unit,
    onDiscoverSchools: () -> Unit,
    onLanguageTap: () -> Unit,
    onThemeTap: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        Modifier
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
            ActionRow("Language", VIcons.Globe, onLanguageTap),
            ActionRow("Theme", VIcons.Palette, onThemeTap),
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

private data class ActionRow(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false,
)

@Composable
private fun ActionRowItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    val tint = if (isDestructive) VColors.coral else VColors.ink
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            style = VTypography.body.copy(fontWeight = FontWeight.SemiBold),
            color = tint,
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

@Composable
private fun ThemePickerDialog(
    currentMode: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = listOf("light" to "Light", "dark" to "Dark", "system" to "System default")
    VConfirmDialog(
        visible = true,
        title = "Choose theme",
        message = options.joinToString("\n") { (_, label) -> label },
        confirmLabel = "Done",
        onConfirm = onDismiss,
        onDismiss = onDismiss,
    )
}

@Composable
private fun LanguagePickerDialog(
    currentLanguage: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = listOf("English", "Hindi")
    VConfirmDialog(
        visible = true,
        title = "Choose language",
        message = options.joinToString("\n"),
        confirmLabel = "Done",
        onConfirm = onDismiss,
        onDismiss = onDismiss,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun markDisplay(mark: ParentMarkDto?): String {
    if (mark == null) return "—"
    val pct = if (mark.maxMarks > 0) ((mark.marks ?: 0.0) / mark.maxMarks * 100).roundToInt() else 0
    return "$pct%"
}
