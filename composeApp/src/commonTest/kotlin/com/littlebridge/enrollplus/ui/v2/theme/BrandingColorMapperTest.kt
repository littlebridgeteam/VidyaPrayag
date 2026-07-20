package com.littlebridge.enrollplus.ui.v2.theme

import androidx.compose.ui.graphics.Color
import com.littlebridge.enrollplus.feature.branding.domain.model.SchoolBranding
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BrandingColorMapperTest {

    // ── parseHex ───────────────────────────────────────────────────────────

    @Test
    fun parseHex_valid6Digit_returnsColor() {
        val color = BrandingColorMapper.parseHex("#2563EB")
        assertEquals(Color(red = 0x25 / 255f, green = 0x63 / 255f, blue = 0xEB / 255f), color)
    }

    @Test
    fun parseHex_valid6Digit_noHash_returnsColor() {
        val color = BrandingColorMapper.parseHex("2563EB")
        assertEquals(Color(red = 0x25 / 255f, green = 0x63 / 255f, blue = 0xEB / 255f), color)
    }

    @Test
    fun parseHex_valid3Digit_returnsExpandedColor() {
        val color = BrandingColorMapper.parseHex("#F00")
        // F → 15, 15*17 = 255 → full red
        assertEquals(Color(red = 1f, green = 0f, blue = 0f), color)
    }

    @Test
    fun parseHex_valid3Digit_noHash_returnsExpandedColor() {
        val color = BrandingColorMapper.parseHex("0F8")
        assertEquals(Color(red = 0f, green = 1f, blue = 0x88 / 255f), color)
    }

    @Test
    fun parseHex_invalidLength_returnsNull() {
        assertNull(BrandingColorMapper.parseHex("#1234"))
        assertNull(BrandingColorMapper.parseHex("#12345"))
        assertNull(BrandingColorMapper.parseHex("#1234567"))
        assertNull(BrandingColorMapper.parseHex(""))
    }

    @Test
    fun parseHex_invalidChars_returnsNull() {
        assertNull(BrandingColorMapper.parseHex("#GGGGGG"))
        assertNull(BrandingColorMapper.parseHex("#ZZZZZZ"))
    }

    @Test
    fun parseHex_lowercase_returnsColor() {
        val color = BrandingColorMapper.parseHex("#abcdef")
        assertEquals(Color(red = 0xAB / 255f, green = 0xCD / 255f, blue = 0xEF / 255f), color)
    }

    @Test
    fun parseHex_mixedCase_returnsColor() {
        val color = BrandingColorMapper.parseHex("#aBcDeF")
        assertEquals(Color(red = 0xAB / 255f, green = 0xCD / 255f, blue = 0xEF / 255f), color)
    }

    // ── apply ──────────────────────────────────────────────────────────────

    @Test
    fun apply_nullBranding_returnsBaseUnchanged() {
        val base = LightVColors
        val result = BrandingColorMapper.apply(base, null)
        assertSame(base, result)
    }

    @Test
    fun apply_notCustomized_returnsBaseUnchanged() {
        val base = LightVColors
        val branding = SchoolBranding(
            schoolId = "school-1",
            schoolName = "Test School",
            isCustomized = false,
        )
        val result = BrandingColorMapper.apply(base, branding)
        assertSame(base, result)
    }

    @Test
    fun apply_customized_overridesAccentAndTealTokens() {
        val base = LightVColors
        val branding = SchoolBranding(
            schoolId = "school-1",
            schoolName = "Test School",
            primaryColor = "#FF0000",
            secondaryColor = "#00FF00",
            accentColor = "#0000FF",
            isCustomized = true,
        )
        val result = BrandingColorMapper.apply(base, branding)

        assertEquals(Color(red = 1f, green = 0f, blue = 0f), result.accent)
        assertEquals(Color(red = 0f, green = 1f, blue = 0f), result.accentDeep)
        assertEquals(Color(red = 0f, green = 0f, blue = 1f), result.accentSoft)
        assertEquals(Color(red = 1f, green = 0f, blue = 0f), result.teal)
        assertEquals(Color(red = 0f, green = 1f, blue = 0f), result.tealDeep)
    }

    @Test
    fun apply_customized_preservesNonOverriddenTokens() {
        val base = LightVColors
        val branding = SchoolBranding(
            schoolId = "school-1",
            schoolName = "Test School",
            primaryColor = "#FF0000",
            secondaryColor = "#00FF00",
            accentColor = "#0000FF",
            isCustomized = true,
        )
        val result = BrandingColorMapper.apply(base, branding)

        // Non-overridden tokens should stay the same
        assertEquals(base.ink, result.ink)
        assertEquals(base.ink2, result.ink2)
        assertEquals(base.background, result.background)
        assertEquals(base.card, result.card)
        assertEquals(base.success, result.success)
        assertEquals(base.danger, result.danger)
        assertEquals(base.isNight, result.isNight)
    }

    @Test
    fun apply_customized_invalidPrimary_returnsBaseUnchanged() {
        val base = LightVColors
        val branding = SchoolBranding(
            schoolId = "school-1",
            schoolName = "Test School",
            primaryColor = "invalid",
            isCustomized = true,
        )
        val result = BrandingColorMapper.apply(base, branding)
        assertSame(base, result)
    }

    @Test
    fun apply_customized_invalidSecondary_fallsBackToPrimary() {
        val base = LightVColors
        val branding = SchoolBranding(
            schoolId = "school-1",
            schoolName = "Test School",
            primaryColor = "#FF0000",
            secondaryColor = "invalid",
            accentColor = "invalid",
            isCustomized = true,
        )
        val result = BrandingColorMapper.apply(base, branding)

        // secondary and accent should fall back to primary
        assertEquals(result.accent, result.accentDeep)
        assertEquals(result.accent, result.accentSoft)
    }

    @Test
    fun apply_nightMode_derivesNightTint() {
        val base = NightVColors
        val branding = SchoolBranding(
            schoolId = "school-1",
            schoolName = "Test School",
            primaryColor = "#2563EB",
            secondaryColor = "#00FF00",
            accentColor = "#0000FF",
            isCustomized = true,
        )
        val result = BrandingColorMapper.apply(base, branding)

        // Night tint: 12% of primary (#2563EB → r=0.145, g=0.388, b=0.922)
        assertTrue(result.accentTint.red <= 0.145f * 0.12f + 0.01f)
        assertTrue(result.accentTint.green <= 0.388f * 0.12f + 0.01f)
        assertTrue(result.accentTint.blue <= 0.922f * 0.12f + 0.01f)
    }

    @Test
    fun apply_lightMode_derivesLightTint() {
        val base = LightVColors
        val branding = SchoolBranding(
            schoolId = "school-1",
            schoolName = "Test School",
            primaryColor = "#2563EB",
            secondaryColor = "#00FF00",
            accentColor = "#0000FF",
            isCustomized = true,
        )
        val result = BrandingColorMapper.apply(base, branding)

        // Light tint: 8% primary + 92% white → mostly white with slight blue tint
        // #2563EB → r=0.145, g=0.388, b=0.922
        // tint = 0.145*0.08+0.92=0.9316, 0.388*0.08+0.92=0.9510, 0.922*0.08+0.92=0.9938
        assertTrue(result.accentTint.red > 0.9f)
        assertTrue(result.accentTint.red < 1f)
        assertTrue(result.accentTint.green > 0.9f)
        assertTrue(result.accentTint.blue > 0.9f)
    }
}
