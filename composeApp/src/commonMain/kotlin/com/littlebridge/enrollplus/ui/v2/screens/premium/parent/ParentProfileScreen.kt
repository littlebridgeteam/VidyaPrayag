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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfile
import com.littlebridge.enrollplus.feature.parent.presentation.ParentProfileViewModel
import com.littlebridge.enrollplus.ui.v2.components.misc.VShimmerBoxPremium
import com.littlebridge.enrollplus.ui.v2.components.misc.VStateHostPremium
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParentProfileScreen(
    onLogout: () -> Unit,
    onOpenOverlay: (ParentOverlay) -> Unit,
    onSwitchTab: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ParentProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()

    VStateHostPremium(
        loading = state.isLoading,
        error = state.error,
        isEmpty = false,
        modifier = modifier.fillMaxSize(),
        onRetry = { viewModel.load() },
        skeleton = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VShimmerBoxPremium(height = 120.dp, shape = VShapes.TwoXl)
                VShimmerBoxPremium(height = 200.dp, shape = VShapes.Xl)
                repeat(4) { VShimmerBoxPremium(height = 56.dp, shape = VShapes.Lg) }
            }
        },
    ) {
        ProfileContent(
            profile = state.profile,
            onLogout = onLogout,
            onOpenOverlay = onOpenOverlay,
        )
    }
}

@Composable
private fun ProfileContent(
    profile: ParentProfile?,
    onLogout: () -> Unit,
    onOpenOverlay: (ParentOverlay) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 140.dp),
    ) {
        // 1. Identity card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(VShapes.TwoXl)
                .background(VColors.SurfaceContainerLowest)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(VColors.PrimaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = profile?.name?.take(1)?.uppercase() ?: "?",
                    style = VTypography.HeroStatValue.copy(color = VColors.OnPrimaryContainer),
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = profile?.name ?: "Parent",
                style = VTypography.HeroName.copy(color = VColors.OnSurface),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = profile?.phone ?: "",
                style = VTypography.BodyMedium.copy(color = VColors.OnSurfaceVariant),
            )
            if (!profile?.email.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = profile!!.email!!,
                    style = VTypography.ThreadPreview.copy(color = VColors.OnSurfaceVariant),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // 2. Account actions
        ActionRow(
            icon = Icons.Filled.Settings,
            label = "Account Settings",
            onClick = { onOpenOverlay(ParentOverlay.AccountSettings) },
        )
        Spacer(Modifier.height(8.dp))
        ActionRow(
            icon = Icons.Filled.Add,
            label = "Link Another Child",
            onClick = { onOpenOverlay(ParentOverlay.LinkChild) },
        )
        Spacer(Modifier.height(8.dp))
        ActionRow(
            icon = Icons.Filled.Search,
            label = "Discover Schools",
            onClick = { onOpenOverlay(ParentOverlay.LinkChild) },
        )
        Spacer(Modifier.height(8.dp))
        ActionRow(
            icon = Icons.Filled.Language,
            label = "Language",
            onClick = { /* TODO: language picker */ },
        )
        Spacer(Modifier.height(8.dp))
        ActionRow(
            icon = Icons.Filled.Palette,
            label = "Theme",
            onClick = { /* TODO: theme picker */ },
        )
        Spacer(Modifier.height(8.dp))
        ActionRow(
            icon = Icons.AutoMirrored.Filled.Logout,
            label = "Logout",
            onClick = onLogout,
            isDestructive = true,
        )
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    val labelColor = if (isDestructive) VColors.Error else VColors.OnSurface
    val iconBg = if (isDestructive) VColors.ErrorContainer else VColors.SurfaceContainerHigh
    val iconFg = if (isDestructive) VColors.Error else VColors.OnSurfaceVariant

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
                .clip(VShapes.Md)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconFg, modifier = Modifier.size(18.dp))
        }
        Text(
            text = label,
            style = VTypography.BodyLarge.copy(color = labelColor),
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = VColors.OnSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}
