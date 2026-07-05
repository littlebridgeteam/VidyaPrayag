package com.littlebridge.enrollplus.ui.v2.tokens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

/**
 * PremiumTheme — the M3 Expressive theme provider for the premium UI layer.
 *
 * Wraps content with [LocalVColorPalette] (light or dark) plus a Material 3
 * [ColorScheme] bridge so Material components also honour the active palette.
 *
 * Usage:
 *   PremiumTheme(isDark = false) { /* premium screens */ }
 *
 * Access colors anywhere via the [VColors] accessor object:
 *   VColors.Primary, VColors.Surface, etc.
 */
@Composable
fun PremiumTheme(
    isDark: Boolean = false,
    content: @Composable () -> Unit,
) {
    val palette = if (isDark) DarkVColorPalette else LightVColorPalette
    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = palette.onPrimary,
            primaryContainer = palette.primaryContainer,
            onPrimaryContainer = palette.onPrimaryContainer,
            secondary = palette.secondary,
            onSecondary = palette.onSecondary,
            secondaryContainer = palette.secondaryContainer,
            onSecondaryContainer = palette.onSecondaryContainer,
            tertiary = palette.tertiary,
            onTertiary = palette.onTertiary,
            tertiaryContainer = palette.tertiaryContainer,
            onTertiaryContainer = palette.onTertiaryContainer,
            error = palette.error,
            onError = palette.onError,
            errorContainer = palette.errorContainer,
            onErrorContainer = palette.onErrorContainer,
            background = palette.surface,
            onBackground = palette.onSurface,
            surface = palette.surface,
            onSurface = palette.onSurface,
            surfaceVariant = palette.surfaceContainer,
            onSurfaceVariant = palette.onSurfaceVariant,
            outline = palette.outline,
            outlineVariant = palette.outlineVariant,
            inverseSurface = palette.inverseSurface,
            inverseOnSurface = palette.inverseOnSurface,
            inversePrimary = palette.inversePrimary,
            scrim = palette.scrim,
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = palette.onPrimary,
            primaryContainer = palette.primaryContainer,
            onPrimaryContainer = palette.onPrimaryContainer,
            secondary = palette.secondary,
            onSecondary = palette.onSecondary,
            secondaryContainer = palette.secondaryContainer,
            onSecondaryContainer = palette.onSecondaryContainer,
            tertiary = palette.tertiary,
            onTertiary = palette.onTertiary,
            tertiaryContainer = palette.tertiaryContainer,
            onTertiaryContainer = palette.onTertiaryContainer,
            error = palette.error,
            onError = palette.onError,
            errorContainer = palette.errorContainer,
            onErrorContainer = palette.onErrorContainer,
            background = palette.surface,
            onBackground = palette.onSurface,
            surface = palette.surface,
            onSurface = palette.onSurface,
            surfaceVariant = palette.surfaceContainer,
            onSurfaceVariant = palette.onSurfaceVariant,
            outline = palette.outline,
            outlineVariant = palette.outlineVariant,
            inverseSurface = palette.inverseSurface,
            inverseOnSurface = palette.inverseOnSurface,
            inversePrimary = palette.inversePrimary,
            scrim = palette.scrim,
        )
    }
    MaterialTheme(colorScheme = colorScheme) {
        CompositionLocalProvider(
            LocalVColorPalette provides palette,
            content = content,
        )
    }
}
