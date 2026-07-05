package com.littlebridge.enrollplus.ui.v2.tokens

import androidx.compose.ui.graphics.Color

/**
 * M3 Expressive color tokens — lifted verbatim from `preview/parent-portal.html` and
 * `preview/auth-flow.html` `:root` CSS variables.
 *
 * This is a NEW color system separate from the existing `theme/VColors.kt`.
 * See `MIGRATION_NOTES.md` for the mapping between old and new.
 */
object VColors {

    // ── Primary ───────────────────────────────────────────────────────────────
    val Primary = Color(0xFF6750F6)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFE7E0FF)
    val OnPrimaryContainer = Color(0xFF21005D)
    val PrimaryFixed = Color(0xFFE7E0FF)
    val PrimaryFixedDim = Color(0xFFCDBDFF)

    // ── Secondary ─────────────────────────────────────────────────────────────
    val Secondary = Color(0xFF625B71)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFE8DEF8)
    val OnSecondaryContainer = Color(0xFF1E192B)

    // ── Tertiary ──────────────────────────────────────────────────────────────
    val Tertiary = Color(0xFF00BFA0)
    val OnTertiary = Color(0xFF003830)
    val TertiaryContainer = Color(0xFF00F5C4)
    val OnTertiaryContainer = Color(0xFF005048)

    // ── Error ─────────────────────────────────────────────────────────────────
    val Error = Color(0xFFBA1A1A)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnErrorContainer = Color(0xFF410002)

    // ── Surfaces ──────────────────────────────────────────────────────────────
    val Surface = Color(0xFFFEF9FF)
    val SurfaceDim = Color(0xFFDED8E0)
    val SurfaceBright = Color(0xFFFEF9FF)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val SurfaceContainerLow = Color(0xFFF8F4FB)
    val SurfaceContainer = Color(0xFFF2EEF5)
    val SurfaceContainerHigh = Color(0xFFECE8F0)
    val SurfaceContainerHighest = Color(0xFFE6E1E9)

    // ── On-Surface ────────────────────────────────────────────────────────────
    val OnSurface = Color(0xFF1D1B20)
    val OnSurfaceVariant = Color(0xFF49454F)

    // ── Outline ───────────────────────────────────────────────────────────────
    val Outline = Color(0xFF7A757F)
    val OutlineVariant = Color(0xFFCAC4CF)

    // ── Inverse ───────────────────────────────────────────────────────────────
    val InverseSurface = Color(0xFF322F36)
    val InverseOnSurface = Color(0xFFF5EFF7)
    val InversePrimary = Color(0xFFCDBDFF)

    // ── Scrim ─────────────────────────────────────────────────────────────────
    val Scrim = Color(0xFF000000)

    // ── Gradient stops (from inline styles in HTML) ───────────────────────────
    val PrimaryMid = Color(0xFF544AB8)    // hero card gradient mid-stop
    val PrimaryDeep = Color(0xFF3D35A0)   // hero card gradient end-stop
    val TertiaryDeep = Color(0xFF00897B)  // tertiary gradient mid-stop
    val TertiaryDarkest = Color(0xFF00695C) // tertiary gradient end-stop
    val SecondaryDeep = Color(0xFF4A4458) // secondary gradient mid-stop
    val SecondaryDarkest = Color(0xFF2D2A3E) // secondary gradient end-stop

    // ── Accent / warm orange (used for homework, warnings, school cards) ──────
    val WarmOrange = Color(0xFFFF6D00)
    val WarmOrangeDeep = Color(0xFFE65100)
    val WarmOrangeDarkest = Color(0xFFBF360C)
    val WarmOrangeContainer = Color(0xFFFFEEE0)

    // ── Live dot ──────────────────────────────────────────────────────────────
    val LiveCyan = Color(0xFF00F5C4)

    // ── Phone frame ───────────────────────────────────────────────────────────
    val PhoneFrameBgStart = Color(0xFF1A1A24)
    val PhoneFrameBgEnd = Color(0xFF0D0B14)
    val PhoneFrameRing1 = Color(0xFF252330)
    val PhoneFrameRing2 = Color(0xFF1A1A24)
    val PhoneGlow = Color(0x0F6750F6) // rgba(103,80,246,0.06)

    // ── Radial glow colors (from ::before / ::after in HTML) ──────────────────
    val HeroGlowTopRight = Color(0x40CDBDFF)   // rgba(205,189,255,0.25)
    val HeroGlowBottomLeft = Color(0x1F00BFA0)  // rgba(0,191,160,0.12)
    val FeesGlowTopRight = Color(0x33CDBDFF)    // rgba(205,189,255,0.2)
    val LandingGlowPrimary = Color(0x1F6750F6)  // rgba(103,80,246,0.12)
    val LandingGlowTertiary = Color(0x1400BFA0) // rgba(0,191,160,0.08)

    // ── Glassmorphism approximations ──────────────────────────────────────────
    // backdrop-filter: blur(12px) with rgba(255,255,255,0.15) → semi-transparent overlay
    val GlassWhite15 = Color(0x26FFFFFF)  // rgba(255,255,255,0.15)
    val GlassWhite12 = Color(0x1FFFFFFF)  // rgba(255,255,255,0.12)
    val GlassWhite20 = Color(0x33FFFFFF)  // rgba(255,255,255,0.2)
    val GlassWhite25 = Color(0x40FFFFFF)  // rgba(255,255,255,0.25)
    val GlassWhite95 = Color(0xF2FFFFFF)  // rgba(255,255,255,0.95)

    // ── White with opacity (for hero stat backgrounds) ────────────────────────
    val White06 = Color(0x0FFFFFFF)  // rgba(255,255,255,0.06)
    val White08 = Color(0x14FFFFFF)  // rgba(255,255,255,0.08)
    val White14 = Color(0x24FFFFFF)  // rgba(255,255,255,0.14)

    // ── XP bar gradient ───────────────────────────────────────────────────────
    val XpBarStart = Color(0xFF00F5C4)
    val XpBarEnd = Color(0xFF00BFA0)

    // ── Shadow colors ─────────────────────────────────────────────────────────
    val FabShadow = Color(0x5936750F6)    // rgba(103,80,246,0.35) — box-shadow on FAB
    val BrandIconShadow = Color(0x4D6750F6) // rgba(103,80,246,0.3)
    val FabMenuItemShadow1 = Color(0x1F000000) // rgba(0,0,0,0.12)
    val FabMenuItemShadow2 = Color(0x14000000) // rgba(0,0,0,0.08)

    // ── Focus glow ────────────────────────────────────────────────────────────
    val PrimaryFocusGlow = Color(0x1F6750F6) // rgba(103,80,246,0.12) — form-input focus
}
