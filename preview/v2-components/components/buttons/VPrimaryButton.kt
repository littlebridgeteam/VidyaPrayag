package com.littlebridge.enrollplus.ui.v2.components.buttons

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Primary button — filled, full-width, shape-morph on press (full → md).
 *
 * HTML: .btn-primary
 *   width: 100%; padding: 16px; border-radius: var(--shape-full);
 *   background: var(--primary); color: var(--on-primary);
 *   font-size: 15px; font-weight: 700;
 *   :active { border-radius: var(--shape-md); transform: scale(0.97); }
 */
@Composable
fun VPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shapeMorph(
                interactionSource = interaction,
                idleRadius = VShapes.FullDp,
                pressedRadius = VShapes.MdDp,
                durationMs = VMotion.DurShort2,
            )
            .pressScale(interaction, pressedScale = 0.97f)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = VTypography.ButtonPrimary.copy(color = VColors.OnPrimary),
            textAlign = TextAlign.Center,
        )
        if (trailing != null) {
            Row(modifier = Modifier.padding(start = 8.dp)) { trailing() }
        }
    }
}
