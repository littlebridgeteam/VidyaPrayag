package com.littlebridge.enrollplus.ui.v2.components.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SweepGradient
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

/**
 * Progress ring — conic-gradient ring, centered label, 80dp or 120dp.
 *
 * HTML: .pc-ring
 *   width: 80px; height: 80px; border-radius: 50%;
 *   border: 6px solid var(--tertiary-container);
 *   ::before { border: 6px solid transparent; border-top-color: var(--tertiary); border-right-color: var(--tertiary); transform: rotate(45deg); }
 *
 * Approximated with SweepGradient drawn behind a clipped circle.
 */
@Composable
fun VProgressRing(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    ringWidth: Dp = 6.dp,
) {
    val trackColor = VColors.TertiaryContainer
    val progressColor = VColors.Tertiary

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        // Track ring
        Box(
            modifier = Modifier
                .size(size)
                .clip(VShapes.Full)
                .background(trackColor),
        )
        // Progress arc (approximated with sweep gradient)
        if (progress > 0f) {
            Box(
                modifier = Modifier
                    .size(size)
                    .drawBehind {
                        val sweepAngle = 360f * progress
                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            strokeWidth = ringWidth.toPx(),
                        )
                    },
            )
        }
        // Inner circle (to create ring effect)
        Box(
            modifier = Modifier
                .size(size - ringWidth * 2)
                .clip(VShapes.Full)
                .background(VColors.SurfaceContainerLowest),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = VTypography.ProgressRingValue.copy(color = progressColor),
            )
        }
    }
}
