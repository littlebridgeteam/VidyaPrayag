package com.littlebridge.enrollplus.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors

@Composable
fun VProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    barHeight: Int = 3,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(400),
        label = "progress",
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight.dp)
            .background(VColors.line, RoundedCornerShape(50)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(VColors.violet, RoundedCornerShape(50)),
        )
    }
}

@Composable
fun VProgressBarSegments(
    total: Int,
    current: Int,
    modifier: Modifier = Modifier,
    barHeight: Int = 2,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
    ) {
        repeat(total) { index ->
            val isCompleted = index <= current
            val color = if (isCompleted) VColors.violet else VColors.line
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(barHeight.dp)
                    .background(color, RoundedCornerShape(50)),
            )
        }
    }
}
