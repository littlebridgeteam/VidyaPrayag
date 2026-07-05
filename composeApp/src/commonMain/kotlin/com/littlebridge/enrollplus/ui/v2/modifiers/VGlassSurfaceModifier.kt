package com.littlebridge.enrollplus.ui.v2.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes

/**
 * Glass surface modifier — semi-transparent background + subtle border + shadow.
 *
 * Approximates the CSS `backdrop-filter: blur()` glassmorphism effect using
 * a layered approach:
 *   1. A drop shadow for depth
 *   2. A semi-transparent surface background (caller sets the bg color)
 *   3. A subtle frosted-glass sheen overlay
 *
 * Usage:
 *   Box(
 *       Modifier
 *           .glassSurface(VShapes.TwoXl)
 *           .clip(VShapes.TwoXl)
 *   )
 */
fun Modifier.glassSurface(
    shape: Shape = VShapes.Xl,
    elevation: Dp = 8.dp,
    alpha: Float = 0.85f,
): Modifier = composed {
    this
        .shadow(elevation = elevation, shape = shape, clip = false)
        .clip(shape)
        .then(
            Modifier.graphicsLayer {
                this.alpha = alpha
            }
        )
}

/**
 * Frosted glass overlay — draws a subtle vertical white-to-transparent
 * gradient on top of the composable's background to simulate the
 * light-scattering effect of frosted glass.
 *
 * Use this inside a Box that already has a semi-transparent background:
 *   Box(
 *       Modifier
 *           .clip(VShapes.TwoXl)
 *           .background(VColors.GlassWhite12)
 *           .frostedGlassOverlay(VShapes.TwoXl)
 *   )
 */
fun Modifier.frostedGlassOverlay(
    shape: Shape = VShapes.Xl,
): Modifier = composed {
    val topColor = VColors.GlassWhite20
    val midColor = VColors.White08
    val bottomColor = VColors.GlassWhite12
    this.drawBehind {
        val h = this.size.height
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(topColor, midColor, bottomColor),
                startY = 0f,
                endY = h,
            ),
        )
    }
}
