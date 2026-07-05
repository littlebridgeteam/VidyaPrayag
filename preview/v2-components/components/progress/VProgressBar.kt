package com.littlebridge.enrollplus.ui.v2.components.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes

/**
 * Progress bar — rounded track, gradient fill (primary→tertiary), 4dp height.
 *
 * HTML: .sp-track / .sp-fill
 *   height: 4px; border-radius: var(--shape-full);
 *   background: var(--surface-container-high);
 *   .sp-fill { background: linear-gradient(90deg, var(--primary), var(--tertiary)); }
 */
@Composable
fun VProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 4.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(VShapes.Full)
            .background(VColors.SurfaceContainerHigh),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(VShapes.Full)
                .background(
                    Brush.linearGradient(
                        colors = listOf(VColors.Primary, VColors.Tertiary),
                    ),
                ),
        )
    }
}
