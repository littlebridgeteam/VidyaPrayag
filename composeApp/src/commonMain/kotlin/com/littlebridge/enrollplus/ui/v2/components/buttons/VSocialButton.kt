package com.littlebridge.enrollplus.ui.v2.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Social button — icon + label, outlined, surface-container-low bg.
 *
 * HTML: .social-btn
 *   flex: 1; padding: 14px; border-radius: var(--shape-lg);
 *   border: 1.5px solid var(--outline-variant);
 *   background: var(--surface-container-low);
 *   font-size: 14px; font-weight: 600; color: var(--on-surface);
 *   :active { transform: scale(0.96); border-radius: var(--shape-md); }
 */
@Composable
fun VSocialButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(VShapes.Lg)
            .background(VColors.SurfaceContainerLow)
            .border(BorderStroke(1.5.dp, VColors.OutlineVariant), VShapes.Lg)
            .pressScale(interaction, pressedScale = 0.96f)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            icon()
        }
        Text(
            text = text,
            style = VTypography.SocialButton.copy(color = VColors.OnSurface),
            modifier = Modifier.padding(start = if (icon != null) 8.dp else 0.dp),
        )
    }
}
