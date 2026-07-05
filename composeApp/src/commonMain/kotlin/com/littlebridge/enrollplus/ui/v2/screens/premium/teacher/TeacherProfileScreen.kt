package com.littlebridge.enrollplus.ui.v2.screens.premium.teacher

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.teacher.presentation.TeacherProfileViewModel
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.PremiumTheme
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherProfileScreen(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TeacherProfileViewModel = koinViewModel(),
) = PremiumTheme(isDark = false) {
    val state by viewModel.state.collectAsStateV2()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VColors.Surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(24.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("Loading profile...", style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
            }
            return@Column
        }

        if (state.error != null) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text(state.error!!, style = VTypography.UpdateText.copy(color = VColors.Error))
            }
            return@Column
        }

        val profile = state.profile

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box(
                Modifier.size(80.dp).clip(CircleShape).background(VColors.PrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    profile?.name?.firstOrNull()?.toString() ?: "T",
                    style = VTypography.LandingStatValue.copy(color = VColors.OnPrimaryContainer),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            profile?.name ?: "Teacher",
            style = VTypography.GreetingTitle.copy(color = VColors.OnSurface),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            profile?.email ?: "",
            style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(32.dp))

        if (profile != null) {
            InfoRow("School", profile.schoolName)
            Spacer(Modifier.height(8.dp))
            InfoRow("Phone", profile.phone)
            Spacer(Modifier.height(8.dp))
            InfoRow("Subjects", profile.subjects.joinToString(", "))
            Spacer(Modifier.height(8.dp))
            InfoRow("Classes", profile.classes.joinToString(", "))
            Spacer(Modifier.height(24.dp))
        }

        SettingsRow(Icons.Filled.Mail, "Messages", "View conversations", onClick = { })
        SettingsRow(Icons.Filled.Password, "Change Password", "Update your password", onClick = { })
        SettingsRow(Icons.Filled.School, "Leave Requests", "Apply for leave", onClick = { })

        Spacer(Modifier.height(24.dp))

        val logoutInteraction = remember { MutableInteractionSource() }
        Row(
            Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.ErrorContainer)
                .pressScale(logoutInteraction, pressedScale = 0.97f)
                .clickable(interactionSource = logoutInteraction, indication = null, onClick = onLogout)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = VColors.OnErrorContainer, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(8.dp))
            Text("Log Out", style = VTypography.ButtonText.copy(color = VColors.OnErrorContainer, fontWeight = FontWeight.SemiBold))
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = VTypography.UpdateText.copy(color = VColors.OnSurfaceVariant))
        Text(value, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier.fillMaxWidth().clip(VShapes.Xl).background(VColors.SurfaceContainerLowest)
            .pressScale(interaction, pressedScale = 0.97f)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(40.dp).clip(VShapes.Md).background(VColors.SurfaceContainerHigh), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = VColors.OnSurfaceVariant, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = VTypography.UpdateTitle.copy(color = VColors.OnSurface, fontWeight = FontWeight.SemiBold))
            Text(subtitle, style = VTypography.NavLabel.copy(color = VColors.OnSurfaceVariant))
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = VColors.Outline, modifier = Modifier.size(18.dp))
    }
}
