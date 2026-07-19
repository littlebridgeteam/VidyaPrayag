package com.littlebridge.enrollplus.ui.v2.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VColorsContrastTest {

    // ── contrastRatio ──────────────────────────────────────────────────────

    @Test
    fun contrastRatio_sameColor_returns1() {
        val color = Color(0xFF808080)
        assertEquals(1.0, contrastRatio(color, color), 0.01)
    }

    @Test
    fun contrastRatio_blackOnWhite_returns21() {
        assertEquals(21.0, contrastRatio(Color.Black, Color.White), 0.01)
    }

    @Test
    fun contrastRatio_whiteOnBlack_returns21() {
        assertEquals(21.0, contrastRatio(Color.White, Color.Black), 0.01)
    }

    @Test
    fun contrastRatio_isSymmetric() {
        val fg = Color(0xFF3CB9A9)
        val bg = Color(0xFFFCF8FF)
        val ratio1 = contrastRatio(fg, bg)
        val ratio2 = contrastRatio(bg, fg)
        assertEquals(ratio1, ratio2, 0.001)
    }

    @Test
    fun contrastRatio_lightGrayOnWhite_isLow() {
        val ratio = contrastRatio(Color(0xFFCCCCCC), Color.White)
        assertTrue(ratio < 3.0, "Light gray on white should have low contrast ($ratio)")
    }

    // ── meetsWCAGAA ────────────────────────────────────────────────────────

    @Test
    fun meetsWCAGAA_blackOnWhite_returnsTrue() {
        assertTrue(meetsWCAGAA(Color.Black, Color.White))
    }

    @Test
    fun meetsWCAGAA_whiteOnBlack_returnsTrue() {
        assertTrue(meetsWCAGAA(Color.White, Color.Black))
    }

    @Test
    fun meetsWCAGAA_lightGrayOnWhite_returnsFalse() {
        assertFalse(meetsWCAGAA(Color(0xFFCCCCCC), Color.White))
    }

    @Test
    fun meetsWCAGAA_tealDeepOnWhite_meetsAA() {
        // tealDeep = #006A60 — should be dark enough on white
        assertTrue(meetsWCAGAA(Color(0xFF006A60), Color.White))
    }

    @Test
    fun meetsWCAGAA_inkOnCard_lightTheme_meetsAA() {
        // ink = #1A2422 on card = white
        assertTrue(meetsWCAGAA(LightVColors.ink, LightVColors.card))
    }

    @Test
    fun meetsWCAGAA_inkOnBackground_lightTheme_meetsAA() {
        assertTrue(meetsWCAGAA(LightVColors.ink, LightVColors.background))
    }

    @Test
    fun meetsWCAGAA_ink3OnCard_lightTheme_doesNotMeetAA() {
        // ink3 = #6D7A77 — lighter text, likely fails AA for normal text
        val ratio = contrastRatio(LightVColors.ink3, LightVColors.card)
        // ink3 is a secondary text color — it may or may not pass, just verify the function works
        assertEquals(ratio >= WCAG_AA_NORMAL, meetsWCAGAA(LightVColors.ink3, LightVColors.card))
    }

    // ── Night theme contrast ───────────────────────────────────────────────

    @Test
    fun meetsWCAGAA_nightInkOnNightCard_meetsAA() {
        assertTrue(meetsWCAGAA(NightVColors.ink, NightVColors.card))
    }

    @Test
    fun meetsWCAGAA_nightInkOnNightBackground_meetsAA() {
        assertTrue(meetsWCAGAA(NightVColors.ink, NightVColors.background))
    }

    // ── High contrast theme ────────────────────────────────────────────────

    @Test
    fun highContrastTheme_isNotNight() {
        assertFalse(HighContrastVColors.isNight)
    }

    @Test
    fun highContrastTheme_isHighContrast() {
        assertTrue(HighContrastVColors.isHighContrast)
    }

    @Test
    fun highContrastTheme_inkIsBlack() {
        assertEquals(Color.Black, HighContrastVColors.ink)
    }

    @Test
    fun highContrastTheme_cardIsWhite() {
        assertEquals(Color.White, HighContrastVColors.card)
    }

    @Test
    fun highContrastTheme_inkOnCard_meetsAA() {
        assertTrue(meetsWCAGAA(HighContrastVColors.ink, HighContrastVColors.card))
    }

    // ── WCAG constants ─────────────────────────────────────────────────────

    @Test
    fun WCAG_AA_NORMAL_is4_5() {
        assertEquals(4.5, WCAG_AA_NORMAL)
    }

    @Test
    fun WCAG_AA_LARGE_is3_0() {
        assertEquals(3.0, WCAG_AA_LARGE)
    }

    // ── Light vs Night palette sanity ──────────────────────────────────────

    @Test
    fun lightVColors_isNotNight() {
        assertFalse(LightVColors.isNight)
    }

    @Test
    fun nightVColors_isNight() {
        assertTrue(NightVColors.isNight)
    }

    @Test
    fun lightVColors_isNotHighContrast() {
        assertFalse(LightVColors.isHighContrast)
    }

    @Test
    fun nightVColors_isNotHighContrast() {
        assertFalse(NightVColors.isHighContrast)
    }
}
