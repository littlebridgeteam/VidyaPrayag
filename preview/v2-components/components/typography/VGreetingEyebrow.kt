package com.littlebridge.enrollplus.ui.v2.components.typography

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.progress.VPulseDot
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Greeting eyebrow — primary color, 13sp, weight 600, with optional pulse dot.
 *
 * HTML: .greeting-eyebrow / .landing-eyebrow
 *   font-size: 13px; font-weight: 600; color: var(--primary); letter-spacing: 0.01em;
 *   .landing-eyebrow-dot { animation: livePulse 2s infinite; }
 */
@Composable
fun VGreetingEyebrow(
    text: String,
    modifier: Modifier = Modifier,
    showPulseDot: Boolean = false,
) {
    Row(
        modifier = modifier.padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showPulseDot) {
            VPulseDot(color = VColors.Tertiary)
        }
        Text(
            text = text,
            style = VTypography.Eyebrow.copy(color = VColors.Primary),
        )
    }
}
