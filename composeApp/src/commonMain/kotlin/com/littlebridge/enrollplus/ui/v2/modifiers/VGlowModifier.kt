package com.littlebridge.enrollplus.ui.v2.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Radial glow modifier — draws a radial gradient circle at a specified position
 * relative to the composable bounds. Reproduces HTML `::before` / `::after` radial
 * gradient pseudo-elements.
 *
 * Example HTML:
 *   .hero-card::before {
 *     content: ''; position: absolute; top: -100px; right: -100px;
 *     width: 280px; height: 280px;
 *     background: radial-gradient(circle, rgba(205,189,255,0.25) 0%, transparent 50%);
 *     border-radius: 50%; pointer-events: none;
 *   }
 *
 * Usage:
 *   Box(
 *       Modifier
 *           .radialGlow(
 *               offsetX = 280.dp, offsetY = (-100).dp,
 *               radius = 280.dp,
 *               color = VColors.HeroGlowTopRight,
 *           )
 *   )
 *
 * @param offsetX  X offset of the glow center from the top-left corner (can be negative)
 * @param offsetY  Y offset of the glow center from the top-left corner (can be negative)
 * @param radius   Radius of the radial gradient
 * @param color    The glow color (pre-alpha-applied)
 */
fun Modifier.radialGlow(
    offsetX: Dp,
    offsetY: Dp,
    radius: Dp,
    color: Color,
): Modifier = composed {
    this.drawBehind {
        val rPx = radius.toPx()
        val cxPx = offsetX.toPx()
        val cyPx = offsetY.toPx()
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color, Color.Transparent),
                center = Offset(cxPx, cyPx),
                radius = rPx,
            ),
        )
    }
}

/**
 * Radial glow with float-based positioning for precise control.
 */
fun Modifier.radialGlowPx(
    offsetX: Float,
    offsetY: Float,
    radius: Float,
    color: Color,
): Modifier = composed {
    this.drawBehind {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color, Color.Transparent),
                center = Offset(offsetX, offsetY),
                radius = radius,
            ),
        )
    }
}
