package com.littlebridge.enrollplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.tokens.VColors

enum class VStatusDotColor(val color: Color) {
    Success(VColors.success),
    Coral(VColors.coral),
    Gold(VColors.gold),
    Sky(VColors.sky),
    Violet(VColors.violet),
}

@Composable
fun VStatusDot(
    color: VStatusDotColor,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 8.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color.color, CircleShape),
    )
}
