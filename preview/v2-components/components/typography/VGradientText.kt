package com.littlebridge.enrollplus.ui.v2.components.typography

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.littlebridge.enrollplus.ui.v2.tokens.VColors

/**
 * Gradient text — reusable gradient-clipped text (linear-gradient primary→tertiary).
 *
 * HTML pattern (used in greeting titles, landing headlines):
 *   background: linear-gradient(135deg, var(--primary), var(--tertiary));
 *   -webkit-background-clip: text; -webkit-text-fill-color: transparent;
 *
 * Uses Compose's TextStyle.brush API (available since Compose 1.4+).
 */
@Composable
fun VGradientText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    colors: List<androidx.compose.ui.graphics.Color> = listOf(VColors.Primary, VColors.Tertiary),
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        style = style.copy(
            brush = Brush.linearGradient(colors),
        ),
        modifier = modifier,
        textAlign = textAlign,
    )
}
