package com.littlebridge.enrollplus.ui.v2.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Text button — no bg, primary color text, opacity on press.
 *
 * HTML: .btn-text
 *   background: none; border: none; font-size: 14px; font-weight: 600;
 *   color: var(--primary);
 *   :active { opacity: 0.6; }
 */
@Composable
fun VTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    Text(
        text = text,
        style = VTypography.ButtonText.copy(color = VColors.Primary),
        modifier = modifier
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
    )
}
