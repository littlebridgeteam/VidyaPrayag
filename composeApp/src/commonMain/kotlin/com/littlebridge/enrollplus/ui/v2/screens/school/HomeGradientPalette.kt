package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.littlebridge.enrollplus.ui.v2.theme.VTheme

/**
 * Static theme-aware page background gradient for the redesigned admin home.
 * No animation (per design choice), but updates automatically with the active
 * theme so light / dark / midnight all look intentional.
 */
@Composable
fun homeBackgroundGradient(): Brush {
    val colors = when {
        VTheme.colors.isNight -> listOf(
            VTheme.colors.background,
            VTheme.colors.accentTint.copy(alpha = 0.6f),
            VTheme.colors.background,
        )
        else -> listOf(
            VTheme.colors.background,
            VTheme.colors.accentTint.copy(alpha = 0.55f),
            VTheme.colors.accentSoft.copy(alpha = 0.18f),
        )
    }
    return Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )
}

@Composable
fun heroGradient(): Brush {
    val colors = when {
        VTheme.colors.isNight -> listOf(
            VTheme.colors.card.copy(alpha = 0.80f),
            VTheme.colors.accentDeep.copy(alpha = 0.22f),
        )
        else -> listOf(
            VTheme.colors.card.copy(alpha = 0.92f),
            VTheme.colors.accentSoft.copy(alpha = 0.22f),
        )
    }
    return Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )
}

/**
 * Purple gradient for the Today's Overview hero card.
 * Deep indigo → violet, matching the reference design's hero section.
 */
@Composable
fun overviewGradient(): Brush {
    val colors = when {
        VTheme.colors.isNight -> listOf(
            Color(0xFF2D1B69),
            Color(0xFF1A1145),
        )
        else -> listOf(
            Color(0xFF5B3FD9),
            Color(0xFF3B28CC),
        )
    }
    return Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )
}

/**
 * Teal-to-green gradient for the school health status bar.
 */
@Composable
fun healthBarGradient(): Brush {
    val colors = when {
        VTheme.colors.isNight -> listOf(
            Color(0xFF0D3D32),
            Color(0xFF0A2E26),
        )
        else -> listOf(
            Color(0xFFE8F8F0),
            Color(0xFFD4F1E4),
        )
    }
    return Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f),
    )
}
