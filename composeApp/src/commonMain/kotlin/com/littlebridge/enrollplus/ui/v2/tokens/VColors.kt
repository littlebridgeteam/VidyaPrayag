package com.littlebridge.enrollplus.ui.v2.tokens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * M3 Expressive color tokens — lifted verbatim from `preview/parent-portal.html` and
 * `preview/auth-flow.html` `:root` CSS variables.
 *
 * Every property delegates to [LocalVColorPalette.current] so the 30+ component
 * files that reference `VColors.Primary` etc. get the correct light/dark value
 * at runtime without any import or call-site changes.
 *
 * Light values: `preview/parent-portal.html` `:root` CSS variables.
 * Dark values: M3 dark baseline for primary #6750F6.
 */
object VColors {

    // ── Primary ───────────────────────────────────────────────────────────────
    val Primary: Color @Composable get() = LocalVColorPalette.current.primary
    val OnPrimary: Color @Composable get() = LocalVColorPalette.current.onPrimary
    val PrimaryContainer: Color @Composable get() = LocalVColorPalette.current.primaryContainer
    val OnPrimaryContainer: Color @Composable get() = LocalVColorPalette.current.onPrimaryContainer
    val PrimaryFixed: Color @Composable get() = LocalVColorPalette.current.primaryFixed
    val PrimaryFixedDim: Color @Composable get() = LocalVColorPalette.current.primaryFixedDim

    // ── Secondary ─────────────────────────────────────────────────────────────
    val Secondary: Color @Composable get() = LocalVColorPalette.current.secondary
    val OnSecondary: Color @Composable get() = LocalVColorPalette.current.onSecondary
    val SecondaryContainer: Color @Composable get() = LocalVColorPalette.current.secondaryContainer
    val OnSecondaryContainer: Color @Composable get() = LocalVColorPalette.current.onSecondaryContainer

    // ── Tertiary ──────────────────────────────────────────────────────────────
    val Tertiary: Color @Composable get() = LocalVColorPalette.current.tertiary
    val OnTertiary: Color @Composable get() = LocalVColorPalette.current.onTertiary
    val TertiaryContainer: Color @Composable get() = LocalVColorPalette.current.tertiaryContainer
    val OnTertiaryContainer: Color @Composable get() = LocalVColorPalette.current.onTertiaryContainer

    // ── Error ─────────────────────────────────────────────────────────────────
    val Error: Color @Composable get() = LocalVColorPalette.current.error
    val OnError: Color @Composable get() = LocalVColorPalette.current.onError
    val ErrorContainer: Color @Composable get() = LocalVColorPalette.current.errorContainer
    val OnErrorContainer: Color @Composable get() = LocalVColorPalette.current.onErrorContainer

    // ── Surfaces ──────────────────────────────────────────────────────────────
    val Surface: Color @Composable get() = LocalVColorPalette.current.surface
    val SurfaceDim: Color @Composable get() = LocalVColorPalette.current.surfaceDim
    val SurfaceBright: Color @Composable get() = LocalVColorPalette.current.surfaceBright
    val SurfaceContainerLowest: Color @Composable get() = LocalVColorPalette.current.surfaceContainerLowest
    val SurfaceContainerLow: Color @Composable get() = LocalVColorPalette.current.surfaceContainerLow
    val SurfaceContainer: Color @Composable get() = LocalVColorPalette.current.surfaceContainer
    val SurfaceContainerHigh: Color @Composable get() = LocalVColorPalette.current.surfaceContainerHigh
    val SurfaceContainerHighest: Color @Composable get() = LocalVColorPalette.current.surfaceContainerHighest

    // ── On-Surface ────────────────────────────────────────────────────────────
    val OnSurface: Color @Composable get() = LocalVColorPalette.current.onSurface
    val OnSurfaceVariant: Color @Composable get() = LocalVColorPalette.current.onSurfaceVariant

    // ── Outline ───────────────────────────────────────────────────────────────
    val Outline: Color @Composable get() = LocalVColorPalette.current.outline
    val OutlineVariant: Color @Composable get() = LocalVColorPalette.current.outlineVariant

    // ── Inverse ───────────────────────────────────────────────────────────────
    val InverseSurface: Color @Composable get() = LocalVColorPalette.current.inverseSurface
    val InverseOnSurface: Color @Composable get() = LocalVColorPalette.current.inverseOnSurface
    val InversePrimary: Color @Composable get() = LocalVColorPalette.current.inversePrimary

    // ── Scrim ─────────────────────────────────────────────────────────────────
    val Scrim: Color @Composable get() = LocalVColorPalette.current.scrim

    // ── Gradient stops ────────────────────────────────────────────────────────
    val PrimaryMid: Color @Composable get() = LocalVColorPalette.current.primaryMid
    val PrimaryDeep: Color @Composable get() = LocalVColorPalette.current.primaryDeep
    val TertiaryDeep: Color @Composable get() = LocalVColorPalette.current.tertiaryDeep
    val TertiaryDarkest: Color @Composable get() = LocalVColorPalette.current.tertiaryDarkest
    val SecondaryDeep: Color @Composable get() = LocalVColorPalette.current.secondaryDeep
    val SecondaryDarkest: Color @Composable get() = LocalVColorPalette.current.secondaryDarkest

    // ── Accent / warm orange ──────────────────────────────────────────────────
    val WarmOrange: Color @Composable get() = LocalVColorPalette.current.warmOrange
    val WarmOrangeDeep: Color @Composable get() = LocalVColorPalette.current.warmOrangeDeep
    val WarmOrangeDarkest: Color @Composable get() = LocalVColorPalette.current.warmOrangeDarkest
    val WarmOrangeContainer: Color @Composable get() = LocalVColorPalette.current.warmOrangeContainer

    // ── Live dot ──────────────────────────────────────────────────────────────
    val LiveCyan: Color @Composable get() = LocalVColorPalette.current.liveCyan

    // ── Phone frame ───────────────────────────────────────────────────────────
    val PhoneFrameBgStart: Color @Composable get() = LocalVColorPalette.current.phoneFrameBgStart
    val PhoneFrameBgEnd: Color @Composable get() = LocalVColorPalette.current.phoneFrameBgEnd
    val PhoneFrameRing1: Color @Composable get() = LocalVColorPalette.current.phoneFrameRing1
    val PhoneFrameRing2: Color @Composable get() = LocalVColorPalette.current.phoneFrameRing2
    val PhoneGlow: Color @Composable get() = LocalVColorPalette.current.phoneGlow

    // ── Radial glow colors ────────────────────────────────────────────────────
    val HeroGlowTopRight: Color @Composable get() = LocalVColorPalette.current.heroGlowTopRight
    val HeroGlowBottomLeft: Color @Composable get() = LocalVColorPalette.current.heroGlowBottomLeft
    val FeesGlowTopRight: Color @Composable get() = LocalVColorPalette.current.feesGlowTopRight
    val LandingGlowPrimary: Color @Composable get() = LocalVColorPalette.current.landingGlowPrimary
    val LandingGlowTertiary: Color @Composable get() = LocalVColorPalette.current.landingGlowTertiary

    // ── Glassmorphism approximations ──────────────────────────────────────────
    val GlassWhite15: Color @Composable get() = LocalVColorPalette.current.glassWhite15
    val GlassWhite12: Color @Composable get() = LocalVColorPalette.current.glassWhite12
    val GlassWhite20: Color @Composable get() = LocalVColorPalette.current.glassWhite20
    val GlassWhite25: Color @Composable get() = LocalVColorPalette.current.glassWhite25
    val GlassWhite95: Color @Composable get() = LocalVColorPalette.current.glassWhite95

    // ── White with opacity ────────────────────────────────────────────────────
    val White06: Color @Composable get() = LocalVColorPalette.current.white06
    val White08: Color @Composable get() = LocalVColorPalette.current.white08
    val White14: Color @Composable get() = LocalVColorPalette.current.white14

    // ── XP bar gradient ───────────────────────────────────────────────────────
    val XpBarStart: Color @Composable get() = LocalVColorPalette.current.xpBarStart
    val XpBarEnd: Color @Composable get() = LocalVColorPalette.current.xpBarEnd

    // ── Shadow colors ─────────────────────────────────────────────────────────
    val FabShadow: Color @Composable get() = LocalVColorPalette.current.fabShadow
    val BrandIconShadow: Color @Composable get() = LocalVColorPalette.current.brandIconShadow
    val FabMenuItemShadow1: Color @Composable get() = LocalVColorPalette.current.fabMenuItemShadow1
    val FabMenuItemShadow2: Color @Composable get() = LocalVColorPalette.current.fabMenuItemShadow2

    // ── Focus glow ────────────────────────────────────────────────────────────
    val PrimaryFocusGlow: Color @Composable get() = LocalVColorPalette.current.primaryFocusGlow
}
