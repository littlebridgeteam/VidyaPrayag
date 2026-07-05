package com.littlebridge.enrollplus.ui.v2.components.progress

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VMotion
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes

/**
 * Shimmer loading — 4 bars, scaleY bounce, staggered 100ms delays.
 *
 * HTML: .loading-indicator / .li-bar
 *   width: 5px; height: 28px; border-radius: var(--shape-full);
 *   background: var(--primary);
 *   animation: liBounce 1s infinite var(--ease-emphasized);
 *   @keyframes liBounce { 0%,100% { transform: scaleY(0.3); opacity: 0.3; } 50% { transform: scaleY(1); opacity: 1; } }
 *   nth-child delays: 0, 100ms, 200ms, 300ms
 */
@Composable
fun VShimmer(
    modifier: Modifier = Modifier,
    barCount: Int = 4,
) {
    val transition = rememberInfiniteTransition(label = "shimmer")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(barCount) { index ->
            val scaleY by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    tween(VMotion.LiBounceDuration / 2, delayMillis = index * VMotion.LiBarDelayStep, easing = VMotion.EaseEmphasized),
                    RepeatMode.Reverse,
                ),
                label = "shimmer$index",
            )
            val alpha by transition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    tween(VMotion.LiBounceDuration / 2, delayMillis = index * VMotion.LiBarDelayStep, easing = VMotion.EaseEmphasized),
                    RepeatMode.Reverse,
                ),
                label = "shimmerAlpha$index",
            )
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(28.dp)
                    .scale(scaleY = scaleY, scaleX = 1f)
                    .clip(VShapes.Full)
                    .background(VColors.Primary.copy(alpha = alpha)),
            )
        }
    }
}
