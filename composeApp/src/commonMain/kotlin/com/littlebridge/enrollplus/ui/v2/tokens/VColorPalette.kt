package com.littlebridge.enrollplus.ui.v2.tokens

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Theme-aware color palette for the M3 Expressive premium UI.
 *
 * [VColors] object delegates every property to [LocalVColorPalette.current],
 * so the 30+ component files that reference `VColors.Primary` etc. get the
 * correct light/dark value at runtime without any import changes.
 *
 * Light values are lifted verbatim from `preview/parent-portal.html` `:root`.
 * Dark values follow the M3 dark baseline for primary #6750F6.
 */
data class VColorPalette(
    // ── Primary ───────────────────────────────────────────────────────────
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val primaryFixed: Color,
    val primaryFixedDim: Color,

    // ── Secondary ─────────────────────────────────────────────────────────
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,

    // ── Tertiary ──────────────────────────────────────────────────────────
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,

    // ── Error ─────────────────────────────────────────────────────────────
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,

    // ── Surfaces ──────────────────────────────────────────────────────────
    val surface: Color,
    val surfaceDim: Color,
    val surfaceBright: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,

    // ── On-Surface ────────────────────────────────────────────────────────
    val onSurface: Color,
    val onSurfaceVariant: Color,

    // ── Outline ───────────────────────────────────────────────────────────
    val outline: Color,
    val outlineVariant: Color,

    // ── Inverse ───────────────────────────────────────────────────────────
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,

    // ── Scrim ─────────────────────────────────────────────────────────────
    val scrim: Color,

    // ── Gradient stops (same in light/dark — used on colored bg) ──────────
    val primaryMid: Color,
    val primaryDeep: Color,
    val tertiaryDeep: Color,
    val tertiaryDarkest: Color,
    val secondaryDeep: Color,
    val secondaryDarkest: Color,

    // ── Accent / warm orange ──────────────────────────────────────────────
    val warmOrange: Color,
    val warmOrangeDeep: Color,
    val warmOrangeDarkest: Color,
    val warmOrangeContainer: Color,

    // ── Live dot ──────────────────────────────────────────────────────────
    val liveCyan: Color,

    // ── Phone frame (same in both modes) ──────────────────────────────────
    val phoneFrameBgStart: Color,
    val phoneFrameBgEnd: Color,
    val phoneFrameRing1: Color,
    val phoneFrameRing2: Color,
    val phoneGlow: Color,

    // ── Radial glow colors (same in both modes — on gradient bg) ──────────
    val heroGlowTopRight: Color,
    val heroGlowBottomLeft: Color,
    val feesGlowTopRight: Color,
    val landingGlowPrimary: Color,
    val landingGlowTertiary: Color,

    // ── Glassmorphism (same in both modes — on colored bg) ────────────────
    val glassWhite15: Color,
    val glassWhite12: Color,
    val glassWhite20: Color,
    val glassWhite25: Color,
    val glassWhite95: Color,

    // ── White with opacity (same — on gradient bg) ────────────────────────
    val white06: Color,
    val white08: Color,
    val white14: Color,

    // ── XP bar gradient ───────────────────────────────────────────────────
    val xpBarStart: Color,
    val xpBarEnd: Color,

    // ── Shadow colors (same in both modes) ────────────────────────────────
    val fabShadow: Color,
    val brandIconShadow: Color,
    val fabMenuItemShadow1: Color,
    val fabMenuItemShadow2: Color,

    // ── Focus glow ────────────────────────────────────────────────────────
    val primaryFocusGlow: Color,
)

val LightVColorPalette = VColorPalette(
    primary = Color(0xFF6750F6),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE7E0FF),
    onPrimaryContainer = Color(0xFF21005D),
    primaryFixed = Color(0xFFE7E0FF),
    primaryFixedDim = Color(0xFFCDBDFF),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1E192B),
    tertiary = Color(0xFF00BFA0),
    onTertiary = Color(0xFF003830),
    tertiaryContainer = Color(0xFF00F5C4),
    onTertiaryContainer = Color(0xFF005048),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    surface = Color(0xFFFEF9FF),
    surfaceDim = Color(0xFFDED8E0),
    surfaceBright = Color(0xFFFEF9FF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8F4FB),
    surfaceContainer = Color(0xFFF2EEF5),
    surfaceContainerHigh = Color(0xFFECE8F0),
    surfaceContainerHighest = Color(0xFFE6E1E9),
    onSurface = Color(0xFF1D1B20),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF7A757F),
    outlineVariant = Color(0xFFCAC4CF),
    inverseSurface = Color(0xFF322F36),
    inverseOnSurface = Color(0xFFF5EFF7),
    inversePrimary = Color(0xFFCDBDFF),
    scrim = Color(0xFF000000),
    primaryMid = Color(0xFF544AB8),
    primaryDeep = Color(0xFF3D35A0),
    tertiaryDeep = Color(0xFF00897B),
    tertiaryDarkest = Color(0xFF00695C),
    secondaryDeep = Color(0xFF4A4458),
    secondaryDarkest = Color(0xFF2D2A3E),
    warmOrange = Color(0xFFFF6D00),
    warmOrangeDeep = Color(0xFFE65100),
    warmOrangeDarkest = Color(0xFFBF360C),
    warmOrangeContainer = Color(0xFFFFEEE0),
    liveCyan = Color(0xFF00F5C4),
    phoneFrameBgStart = Color(0xFF1A1A24),
    phoneFrameBgEnd = Color(0xFF0D0B14),
    phoneFrameRing1 = Color(0xFF252330),
    phoneFrameRing2 = Color(0xFF1A1A24),
    phoneGlow = Color(0x0F6750F6),
    heroGlowTopRight = Color(0x40CDBDFF),
    heroGlowBottomLeft = Color(0x1F00BFA0),
    feesGlowTopRight = Color(0x33CDBDFF),
    landingGlowPrimary = Color(0x1F6750F6),
    landingGlowTertiary = Color(0x1400BFA0),
    glassWhite15 = Color(0x26FFFFFF),
    glassWhite12 = Color(0x1FFFFFFF),
    glassWhite20 = Color(0x33FFFFFF),
    glassWhite25 = Color(0x40FFFFFF),
    glassWhite95 = Color(0xF2FFFFFF),
    white06 = Color(0x0FFFFFFF),
    white08 = Color(0x14FFFFFF),
    white14 = Color(0x24FFFFFF),
    xpBarStart = Color(0xFF00F5C4),
    xpBarEnd = Color(0xFF00BFA0),
    fabShadow = Color(0x5936750F6),
    brandIconShadow = Color(0x4D6750F6),
    fabMenuItemShadow1 = Color(0x1F000000),
    fabMenuItemShadow2 = Color(0x14000000),
    primaryFocusGlow = Color(0x1F6750F6),
)

val DarkVColorPalette = VColorPalette(
    primary = Color(0xFFCDBDFF),
    onPrimary = Color(0xFF3B2073),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFE7E0FF),
    primaryFixed = Color(0xFFE7E0FF),
    primaryFixedDim = Color(0xFFCDBDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFF7AD4BB),
    onTertiary = Color(0xFF003830),
    tertiaryContainer = Color(0xFF005048),
    onTertiaryContainer = Color(0xFF9CF2DA),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surface = Color(0xFF141218),
    surfaceDim = Color(0xFF141218),
    surfaceBright = Color(0xFF3B383F),
    surfaceContainerLowest = Color(0xFF0F0D13),
    surfaceContainerLow = Color(0xFF1D1B20),
    surfaceContainer = Color(0xFF211F26),
    surfaceContainerHigh = Color(0xFF2B2930),
    surfaceContainerHighest = Color(0xFF36343B),
    onSurface = Color(0xFFE6E1E9),
    onSurfaceVariant = Color(0xFFCAC4CF),
    outline = Color(0xFF948F99),
    outlineVariant = Color(0xFF49454F),
    inverseSurface = Color(0xFFE6E1E9),
    inverseOnSurface = Color(0xFF141218),
    inversePrimary = Color(0xFF6750F6),
    scrim = Color(0xFF000000),
    primaryMid = Color(0xFF544AB8),
    primaryDeep = Color(0xFF3D35A0),
    tertiaryDeep = Color(0xFF00897B),
    tertiaryDarkest = Color(0xFF00695C),
    secondaryDeep = Color(0xFF4A4458),
    secondaryDarkest = Color(0xFF2D2A3E),
    warmOrange = Color(0xFFFFB77A),
    warmOrangeDeep = Color(0xFFE65100),
    warmOrangeDarkest = Color(0xFFBF360C),
    warmOrangeContainer = Color(0xFF4A2800),
    liveCyan = Color(0xFF00F5C4),
    phoneFrameBgStart = Color(0xFF1A1A24),
    phoneFrameBgEnd = Color(0xFF0D0B14),
    phoneFrameRing1 = Color(0xFF252330),
    phoneFrameRing2 = Color(0xFF1A1A24),
    phoneGlow = Color(0x0F6750F6),
    heroGlowTopRight = Color(0x40CDBDFF),
    heroGlowBottomLeft = Color(0x1F00BFA0),
    feesGlowTopRight = Color(0x33CDBDFF),
    landingGlowPrimary = Color(0x1F6750F6),
    landingGlowTertiary = Color(0x1400BFA0),
    glassWhite15 = Color(0x26FFFFFF),
    glassWhite12 = Color(0x1FFFFFFF),
    glassWhite20 = Color(0x33FFFFFF),
    glassWhite25 = Color(0x40FFFFFF),
    glassWhite95 = Color(0xF2FFFFFF),
    white06 = Color(0x0FFFFFFF),
    white08 = Color(0x14FFFFFF),
    white14 = Color(0x24FFFFFF),
    xpBarStart = Color(0xFF00F5C4),
    xpBarEnd = Color(0xFF00BFA0),
    fabShadow = Color(0x5936750F6),
    brandIconShadow = Color(0x4D6750F6),
    fabMenuItemShadow1 = Color(0x1F000000),
    fabMenuItemShadow2 = Color(0x14000000),
    primaryFocusGlow = Color(0x1F6750F6),
)

val LocalVColorPalette: ProvidableCompositionLocal<VColorPalette> =
    staticCompositionLocalOf { LightVColorPalette }
