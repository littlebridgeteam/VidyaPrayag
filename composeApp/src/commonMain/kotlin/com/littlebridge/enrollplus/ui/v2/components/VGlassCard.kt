package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

/**
 * Glassmorphic surface with theme-aware translucency.
 *
 * KMP-safe: uses a soft gradient scrim + hairline border instead of platform blur
 * so it works on Android, iOS, desktop and web. The gradient adapts automatically
 * to light / dark / midnight themes via [VTheme.colors].
 */
@Composable
fun VGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    padding: Dp = 16.dp,
    borderColor: Color = VTheme.colors.border1.copy(alpha = 0.5f),
    backgroundBrush: Brush = Brush.verticalGradient(
        colors = listOf(
            VTheme.colors.card.copy(alpha = 0.85f),
            VTheme.colors.accentTint.copy(alpha = 0.40f),
        )
    ),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundBrush)
            .border(
                width = 0.5.dp,
                color = borderColor,
                shape = shape,
            )
            .then(clickableModifier)
    ) {
        Column(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}
