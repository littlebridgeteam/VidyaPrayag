package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes

@Composable
fun VShimmerBoxPremium(
    modifier: Modifier = Modifier,
    width: Dp = Dp.Unspecified,
    height: Dp = 20.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(6.dp),
) {
    val transition = rememberInfiniteTransition(label = "shimmer-premium")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer-phase-premium",
    )
    val baseColor = VColors.SurfaceContainerHigh
    val highlightColor = VColors.SurfaceContainerHighest
    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(x = phase * 1000f - 500f, y = 0f),
        end = Offset(x = phase * 1000f + 500f, y = 0f),
    )
    Box(
        modifier
            .then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .clip(shape)
            .background(brush),
    )
}

@Composable
fun VShimmerCardPremium(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        VShimmerBoxPremium(height = 24.dp, shape = VShapes.Sm)
        VShimmerBoxPremium(height = 16.dp, shape = VShapes.Sm)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            VShimmerBoxPremium(modifier = Modifier.weight(1f), height = 80.dp, shape = VShapes.Md)
            VShimmerBoxPremium(modifier = Modifier.weight(1f), height = 80.dp, shape = VShapes.Md)
        }
    }
}

@Composable
fun VShimmerListPremium(
    modifier: Modifier = Modifier,
    itemCount: Int = 5,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(itemCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                VShimmerBoxPremium(width = 48.dp, height = 48.dp, shape = VShapes.Full)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    VShimmerBoxPremium(height = 16.dp, shape = VShapes.Sm)
                    VShimmerBoxPremium(height = 12.dp, shape = VShapes.Sm)
                }
            }
        }
    }
}
