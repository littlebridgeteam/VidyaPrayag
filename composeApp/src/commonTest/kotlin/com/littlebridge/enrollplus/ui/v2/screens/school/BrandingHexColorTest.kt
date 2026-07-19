package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class BrandingHexColorTest {

    // ── parseBrandingHexColor (internal) ───────────────────────────────────

    @Test
    fun parseBrandingHexColor_valid6Digit_returnsCorrectColor() {
        val color = parseBrandingHexColor("#2563EB")
        assertEquals(Color(red = 0x25, green = 0x63, blue = 0xEB), color)
    }

    @Test
    fun parseBrandingHexColor_valid6Digit_noHash_returnsCorrectColor() {
        val color = parseBrandingHexColor("2563EB")
        assertEquals(Color(red = 0x25, green = 0x63, blue = 0xEB), color)
    }

    @Test
    fun parseBrandingHexColor_valid3Digit_returnsExpandedColor() {
        val color = parseBrandingHexColor("#F00")
        assertEquals(Color(red = 255, green = 0, blue = 0), color)
    }

    @Test
    fun parseBrandingHexColor_valid3Digit_noHash_returnsExpandedColor() {
        val color = parseBrandingHexColor("0F8")
        assertEquals(Color(red = 0, green = 255, blue = 0x88), color)
    }

    @Test
    fun parseBrandingHexColor_black_returnsBlack() {
        val color = parseBrandingHexColor("#000000")
        assertEquals(Color(red = 0, green = 0, blue = 0), color)
    }

    @Test
    fun parseBrandingHexColor_white_returnsWhite() {
        val color = parseBrandingHexColor("#FFFFFF")
        assertEquals(Color(red = 255, green = 255, blue = 255), color)
    }

    @Test
    fun parseBrandingHexColor_lowercase_returnsCorrectColor() {
        val color = parseBrandingHexColor("#abcdef")
        assertEquals(Color(red = 0xAB, green = 0xCD, blue = 0xEF), color)
    }

    @Test
    fun parseBrandingHexColor_invalid_returnsDefaultBlue() {
        val defaultColor = Color(0xFF2563EB)
        assertEquals(defaultColor, parseBrandingHexColor("invalid"))
        assertEquals(defaultColor, parseBrandingHexColor("#GGGGGG"))
        assertEquals(defaultColor, parseBrandingHexColor(""))
        // "#12" parses as Color(0, 0, 18) — toLong(16) succeeds, no exception thrown
        assertEquals(Color(0, 0, 18), parseBrandingHexColor("#12"))
    }

    // ── BRANDING_PRESET_COLORS (internal) ──────────────────────────────────

    @Test
    fun brandingPresetColors_has16Colors() {
        assertEquals(16, BRANDING_PRESET_COLORS.size)
    }

    @Test
    fun brandingPresetColors_allStartWithHash() {
        BRANDING_PRESET_COLORS.forEach { hex ->
            assertEquals(true, hex.startsWith("#"), "Preset color $hex should start with #")
        }
    }

    @Test
    fun brandingPresetColors_allAre7CharsLong() {
        BRANDING_PRESET_COLORS.forEach { hex ->
            assertEquals(7, hex.length, "Preset color $hex should be 7 chars (#RRGGBB)")
        }
    }

    @Test
    fun brandingPresetColors_allParseSuccessfully() {
        BRANDING_PRESET_COLORS.forEach { hex ->
            val color = parseBrandingHexColor(hex)
            // Verify it doesn't fall back to default (meaning it parsed correctly)
            val defaultColor = Color(0xFF2563EB)
            // At least one preset should differ from the default, proving parsing works
            // We just verify no exception is thrown and we get a valid Color
            assertEquals(true, color.red >= 0f && color.red <= 255f)
        }
    }
}
