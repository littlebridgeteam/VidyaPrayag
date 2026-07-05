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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.cards.VProfileHeroCard
import com.littlebridge.enrollplus.ui.v2.components.typography.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium parent profile — matches parent-portal.html Profile tab.
 * VProfileHeroCard with XP bar, stat tiles, settings sections
 * (Account, Preferences, Support), linked children, logout.
 */
@Composable
fun ParentProfileScreen(
    onLogout: () -> Unit = {},
    onLinkChild: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ParentProfileViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            Box(
                Modifier.fillMaxWidth().height(200.dp).clip(VShapes.Lg).background(VColors.SurfaceContainerLow),
                contentAlignment = Alignment.Center,
            ) {
                Text("Loading profile...", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
            return@PremiumTheme
        }

        if (state.error != null) {
            Box(
                Modifier.fillMaxWidth().height(200.dp).clip(VShapes.Lg).background(VColors.ErrorContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(state.error!!, style = VTypography.UpdateText.copy(color = VColors.OnErrorContainer))
            }
            return@PremiumTheme
        }

        val profile = state.profile
        if (profile == null) {
            Box(
                Modifier.fillMaxWidth().height(200.dp).clip(VShapes.Lg).background(VColors.SurfaceContainerLow),
                contentAlignment = Alignment.Center,
            ) {
                Text("No profile data", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
            return@PremiumTheme
        }

        val initials = profile.name.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString("")

        // ── Profile Hero Card ──
        VProfileHeroCard(
            initials = initials,
            name = profile.name,
            className = profile.role.replaceFirstChar { it.uppercase() },
            levelText = "Level 5 · Engaged Parent",
            xpText = "1,240 / 2,000 XP",
            xpProgress = 0.62f,
            badge = "Premium",
            onClick = { },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(24.dp))

        // ── Stat Tiles ──
        Row(
            Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatTile("3", "Children", Modifier.weight(1f))
            StatTile("85%", "Attendance", Modifier.weight(1f))
            StatTile("12", "Badges", Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        // ── Linked Children ──
        VSectionHeader("Linked Children", linkText = "Add", onLinkClick = onLinkChild)
        Column(Modifier.padding(horizontal = 20.dp)) {
            LinkedChildRow("Aarav Sharma", "Grade 5 · Section A")
            Spacer(Modifier.height(10.dp))
            LinkedChildRow("Diya Sharma", "Grade 3 · Section B")
            Spacer(Modifier.height(10.dp))
            val addInteraction = remember { MutableInteractionSource() }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(VShapes.Xl)
                    .background(VColors.PrimaryContainer.copy(alpha = 0.3f))
                    .pressScale(addInteraction, pressedScale = 0.97f)
                    .clickable(interactionSource = addInteraction, indication = null, onClick = onLinkChild)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(VColors.PrimaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = VColors.Primary, modifier = Modifier.size(20.dp))
                }
                Text("Link another child", style = VTypography.UpdateTitle.copy(color = VColors.Primary, fontWeight = FontWeight.SemiBold))
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Account Settings ──
        VSectionHeader("Account")
        Column(Modifier.padding(horizontal = 20.dp)) {
            SettingsRow(Icons.Filled.AccountCircle, "Personal Information", "Name, email, phone", VColors.Primary) { }
            Spacer(Modifier.height(10.dp))
            SettingsRow(Icons.Filled.Security, "Security", "Password, 2FA", VColors.Tertiary) { }
            Spacer(Modifier.height(10.dp))
            SettingsRow(Icons.Filled.PrivacyTip, "Privacy", "Data and permissions", VColors.WarmOrange) { }
        }

        Spacer(Modifier.height(24.dp))

        // ── Preferences ──
        VSectionHeader("Preferences")
        Column(Modifier.padding(horizontal = 20.dp)) {
            SettingsRow(Icons.Filled.Notifications, "Notifications", "Push, email, SMS", VColors.Primary) { }
            Spacer(Modifier.height(10.dp))
            SettingsRow(Icons.Filled.Palette, "Theme", "Light, dark, system", VColors.Tertiary) { }
            Spacer(Modifier.height(10.dp))
            SettingsRow(Icons.Filled.Language, "Language", "English", VColors.WarmOrange) { }
        }

        Spacer(Modifier.height(24.dp))

        // ── Support ──
        VSectionHeader("Support")
        Column(Modifier.padding(horizontal = 20.dp)) {
            SettingsRow(Icons.Filled.Info, "Help Center", "FAQs and guides", VColors.Primary) { }
            Spacer(Modifier.height(10.dp))
            SettingsRow(Icons.Filled.Description, "Terms & Policies", "Legal information", VColors.OnSurfaceVariant) { }
        }

        Spacer(Modifier.height(24.dp))

        // ── Logout ──
        val logoutInteraction = remember { MutableInteractionSource() }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(VShapes.Xl)
                .background(VColors.ErrorContainer)
                .pressScale(logoutInteraction, pressedScale = 0.97f)
                .clickable(interactionSource = logoutInteraction, indication = null, onClick = onLogout)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(Icons.Filled.Logout, contentDescription = null, tint = VColors.Error, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text("Log Out", style = VTypography.UpdateTitle.copy(color = VColors.Error, fontWeight = FontWeight.Bold))
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = VTypography.StatValue.copy(color = VColors.OnSurface))
        Spacer(Modifier.height(4.dp))
        Text(label, style = VTypography.StatLabel.copy(color = VColors.OnSurfaceVariant))
    }
}

@Composable
private fun LinkedChildRow(name: String, info: String) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.97f)
            .clickable(interactionSource = interaction, indication = null) { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(VColors.PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(name.firstOrNull()?.toString() ?: "?", style = VTypography.HeroStatValue.copy(color = VColors.Primary))
        }
        Column(Modifier.weight(1f)) {
            Text(name, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(info, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.97f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier.size(40.dp).clip(VShapes.Md).background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(subtitle, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(24.dp))
    }
}
