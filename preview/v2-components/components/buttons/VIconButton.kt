package com.littlebridge.enrollplus.ui.v2.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
 * Icon button — circular 44dp, transparent bg, scale 0.9 on press.
 *
 * HTML: .icon-btn
 *   width: 44px; height: 44px; border-radius: var(--shape-full);
 *   border: none; background: transparent;
 *   :active { background: var(--surface-container-highest); transform: scale(0.9); }
 *   svg { width: 24px; height: 24px; color: var(--on-surface-variant); }
 *
 * Also supports an optional badge dot (red, top-right).
 *
 * HTML: .icon-badge
 *   position: absolute; top: 8px; right: 8px;
 *   width: 10px; height: 10px; border-radius: var(--shape-full);
 *   background: var(--error); border: 2px solid var(--surface);
 */
@Composable
fun VIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    iconColor: Color = VColors.OnSurfaceVariant,
    iconSize: Int = 24,
    showBadge: Boolean = false,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .pressScale(interaction, pressedScale = 0.9f)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            icon()
        }
        if (showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(VColors.Error),
            )
        }
    }
}
