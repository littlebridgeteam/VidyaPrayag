package com.littlebridge.enrollplus.ui.v2.components.buttons

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.modifiers.shapeMorph
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Secondary button — outlined, surface-container-low bg, shape-morph on press.
 *
 * HTML: .btn-secondary
 *   width: 100%; padding: 16px; border-radius: var(--shape-full);
 *   background: var(--surface-container-low); color: var(--on-surface);
 *   border: 1.5px solid var(--outline-variant);
 *   font-size: 15px; font-weight: 700;
 *   :active { border-radius: var(--shape-md); transform: scale(0.97); }
 */
@Composable
fun VSecondaryButton(
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
            .border(BorderStroke(1.5.dp, VColors.OutlineVariant), VShapes.Full)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = VTypography.ButtonPrimary.copy(color = VColors.OnSurface),
            textAlign = TextAlign.Center,
        )
        if (trailing != null) {
            Row(modifier = Modifier.padding(start = 10.dp)) { trailing() }
        }
    }
}
