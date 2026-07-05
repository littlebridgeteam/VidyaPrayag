package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Premium theme picker — Light / Dark / System selection cards.
 * Uses M3 Expressive tokens. Works with the PremiumTheme system's isDark flag.
 */
@Composable
fun VThemePickerPremium(
    currentMode: String,
    onSelect: (mode: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            "Appearance",
            style = VTypography.SectionHeader.copy(color = VColors.OnSurface),
        )
        Spacer(Modifier.height(12.dp))

        ThemePickerRowPremium(
            icon = Icons.Filled.Settings,
            label = "System",
            caption = "Follow your device setting",
            active = currentMode == "system",
            onClick = { onSelect("system") },
        )
        Spacer(Modifier.height(8.dp))
        ThemePickerRowPremium(
            icon = Icons.Filled.Settings,
            label = "Light",
            caption = "Crisp & bright",
            active = currentMode == "light",
            onClick = { onSelect("light") },
        )
        Spacer(Modifier.height(8.dp))
        ThemePickerRowPremium(
            icon = Icons.Filled.Settings,
            label = "Dark",
            caption = "Easy on the eyes",
            active = currentMode == "dark",
            onClick = { onSelect("dark") },
        )
    }
}

@Composable
private fun ThemePickerRowPremium(
    icon: ImageVector,
    label: String,
    caption: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(if (active) VColors.PrimaryContainer else VColors.SurfaceContainerLow)
            .border(
                1.dp,
                if (active) VColors.Primary.copy(alpha = 0.35f) else VColors.OutlineVariant,
                VShapes.Lg,
            )
            .pressScale(interaction, pressedScale = 0.98f)
            .clickable(interactionSource = interaction, indication = null) { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (active) VColors.Primary.copy(alpha = 0.15f) else VColors.SurfaceContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (active) VColors.Primary else VColors.OnSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                label,
                style = VTypography.UpdateTitle.copy(
                    color = if (active) VColors.OnPrimaryContainer else VColors.OnSurface,
                ),
            )
            Text(
                caption,
                style = VTypography.UpdateTime.copy(color = VColors.OnSurfaceVariant),
            )
        }
        if (active) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(VColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = VColors.OnPrimary,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}
