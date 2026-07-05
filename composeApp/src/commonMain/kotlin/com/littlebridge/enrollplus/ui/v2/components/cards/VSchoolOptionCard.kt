package com.littlebridge.enrollplus.ui.v2.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * School option card — selectable, logo, name, meta, checkmark, border highlight.
 *
 * HTML: .school-option
 *   padding: 16px 20px; border-radius: var(--shape-xl);
 *   background: var(--surface-container-lowest); border: 2px solid transparent;
 *   .selected { border-color: var(--primary); background: var(--primary-container); }
 *   :active { transform: scale(0.98); }
 */
@Composable
fun VSchoolOptionCard(
    name: String,
    meta: String,
    logoText: String,
    logoColor: androidx.compose.ui.graphics.Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val bgColor = if (selected) VColors.PrimaryContainer else VColors.SurfaceContainerLowest
    val borderColor = if (selected) VColors.Primary else androidx.compose.ui.graphics.Color.Transparent

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressScale(interaction, pressedScale = 0.98f)
            .clip(VShapes.Xl)
            .background(bgColor)
            .border(width = 2.dp, color = borderColor, shape = VShapes.Xl)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(VShapes.Md)
                .background(logoColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = logoText,
                style = VTypography.SchoolOptionLogo.copy(color = androidx.compose.ui.graphics.Color.White),
            )
        }
        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = VTypography.SchoolOptionName.copy(color = VColors.OnSurface),
            )
            Text(
                text = meta,
                style = VTypography.SchoolOptionMeta.copy(color = VColors.OnSurfaceVariant),
            )
        }
        // Checkmark (visible when selected)
        if (selected) {
            Text(
                text = "✓",
                style = VTypography.SchoolOptionName.copy(color = VColors.Primary),
            )
        }
    }
}
