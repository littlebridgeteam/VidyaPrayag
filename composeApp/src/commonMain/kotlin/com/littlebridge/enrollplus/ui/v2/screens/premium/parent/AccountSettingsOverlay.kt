package com.littlebridge.enrollplus.ui.v2.screens.premium.parent

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileViewModel
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountSettingsOverlay(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParentProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    val themeMode by viewModel.themeMode.collectAsStateV2()

    ParentOverlayScaffold(
        title = "Account Settings",
        onBack = onBack,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Profile summary
            state.profile?.let { profile ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(VShapes.Xl)
                        .background(VColors.SurfaceContainerLowest)
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(VColors.PrimaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = profile.name.take(1).uppercase(),
                            style = VTypography.QuickStatValue.copy(color = VColors.OnPrimaryContainer),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile.name,
                            style = VTypography.UpdateTitle.copy(color = VColors.OnSurface),
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = profile.email,
                            style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Settings rows
            SettingsRow(
                icon = Icons.Filled.Palette,
                title = "Theme",
                subtitle = "Current: ${themeMode.replaceFirstChar { it.uppercase() }}",
                onClick = { /* TODO: theme picker */ },
            )
            SettingsRow(
                icon = Icons.Filled.Language,
                title = "Language",
                subtitle = "App language",
                onClick = { /* TODO: language picker */ },
            )
            SettingsRow(
                icon = Icons.Filled.Notifications,
                title = "Notifications",
                subtitle = "Push notification preferences",
                onClick = { /* TODO: notification settings */ },
            )
            SettingsRow(
                icon = Icons.Filled.Lock,
                title = "Change Password",
                subtitle = "Update your password",
                onClick = { /* TODO: password change */ },
            )

            Spacer(Modifier.height(16.dp))

            // Logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(VShapes.Lg)
                    .background(VColors.ErrorContainer.copy(alpha = 0.3f))
                    .clickable { onLogout() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = VColors.Error, modifier = Modifier.size(20.dp))
                Text(
                    text = "Log Out",
                    style = VTypography.BodyLarge.copy(color = VColors.Error),
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(VColors.SurfaceContainerHigh),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = VTypography.BodyLarge.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
            )
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = VColors.Outline, modifier = Modifier.size(20.dp))
    }
}
