package com.littlebridge.enrollplus.ui.v2.components.buttons

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.modifiers.pressScale
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Landing primary button — white bg, primary text. For dark gradient hero sections.
 * Also includes a glass variant (translucent bg + border) for fees pay button.
 *
 * HTML: .fees-pay-btn (glass variant)
 *   background: rgba(255,255,255,0.15); backdrop-filter: blur(12px);
 *   border: 1px solid rgba(255,255,255,0.2); color: var(--on-primary);
 *   font-size: 15px; font-weight: 700;
 *   :active { transform: scale(0.97); border-radius: var(--shape-lg); }
 */
@Composable
fun VLandingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glass: Boolean = false,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    val bgColor = if (glass) VColors.GlassWhite15 else Color.White
    val fgColor = if (glass) VColors.OnPrimary else VColors.Primary
    val borderColor = if (glass) VColors.GlassWhite20 else Color.Transparent

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(if (glass) VShapes.Full else VShapes.Full)
            .background(bgColor)
            .border(
                width = if (glass) 1.dp else 0.dp,
                color = borderColor,
                shape = VShapes.Full,
            )
            .pressScale(interaction, pressedScale = 0.97f)
            .clickable(interactionSource = interaction, indication = null, enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = VTypography.ButtonPrimary.copy(color = fgColor),
            textAlign = TextAlign.Center,
        )
    }
}
