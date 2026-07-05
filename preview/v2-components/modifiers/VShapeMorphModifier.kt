package com.littlebridge.enrollplus.ui.v2.modifiers

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes

/**
 * Shape morph modifier — animates corner radius from [idleRadius] to [pressedRadius]
 * when the associated interaction source reports a press.
 *
 * Reproduces the HTML `:active` border-radius transition:
 *   .hero-card:active { border-radius: var(--shape-xl); }
 *   .btn-primary:active { border-radius: var(--shape-md); }
 *
 * Usage:
 *   val interaction = remember { MutableInteractionSource() }
 *   Box(
 *       Modifier
 *           .shapeMorph(interaction, idleRadius = VShapes.TwoXlDp, pressedRadius = VShapes.XlDp)
 *           .clickable(interactionSource = interaction, indication = null) { }
 *   )
 */
fun Modifier.shapeMorph(
    interactionSource: MutableInteractionSource,
    idleRadius: Dp,
    pressedRadius: Dp,
    durationMs: Int = VMotion.DurLong1,
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val radius by animateDpAsState(
        targetValue = if (pressed) pressedRadius else idleRadius,
        animationSpec = tween(durationMs, easing = VMotion.EaseEmphasized),
        label = "shapeMorph",
    )
    this.clip(RoundedCornerShape(radius))
}
