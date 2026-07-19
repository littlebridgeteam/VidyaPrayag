package com.littlebridge.enrollplus.ui.v2.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class VThemeRegistryTest {

    // ── Registered themes ──────────────────────────────────────────────────

    @Test
    fun themes_containsLight() {
        assertTrue(VThemeRegistry.themes.any { it.id == "light" })
    }

    @Test
    fun themes_containsDark() {
        assertTrue(VThemeRegistry.themes.any { it.id == "dark" })
    }

    @Test
    fun themes_containsMidnight() {
        assertTrue(VThemeRegistry.themes.any { it.id == "midnight" })
    }

    @Test
    fun themes_containsWarm() {
        assertTrue(VThemeRegistry.themes.any { it.id == "warm" })
    }

    @Test
    fun themes_containsHighContrast() {
        assertTrue(VThemeRegistry.themes.any { it.id == "high_contrast" })
    }

    @Test
    fun themes_hasAtLeast5Themes() {
        assertTrue(VThemeRegistry.themes.size >= 5)
    }

    // ── Theme IDs are unique ───────────────────────────────────────────────

    @Test
    fun themeIds_areUnique() {
        val ids = VThemeRegistry.themes.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "Theme IDs must be unique")
    }

    // ── byId lookup ────────────────────────────────────────────────────────

    @Test
    fun byId_light_returnsLightTheme() {
        val theme = VThemeRegistry.byId["light"]
        assertNotNull(theme)
        assertEquals("light", theme.id)
        assertEquals("Light", theme.displayName)
        assertFalse(theme.isDark)
    }

    @Test
    fun byId_dark_returnsDarkTheme() {
        val theme = VThemeRegistry.byId["dark"]
        assertNotNull(theme)
        assertEquals("dark", theme.id)
        assertEquals("Dark", theme.displayName)
        assertTrue(theme.isDark)
    }

    @Test
    fun byId_midnight_returnsMidnightTheme() {
        val theme = VThemeRegistry.byId["midnight"]
        assertNotNull(theme)
        assertEquals("midnight", theme.id)
        assertTrue(theme.isDark)
    }

    @Test
    fun byId_highContrast_returnsHighContrastTheme() {
        val theme = VThemeRegistry.byId["high_contrast"]
        assertNotNull(theme)
        assertEquals("high_contrast", theme.id)
        assertFalse(theme.isDark)
        assertTrue(theme.colors.isHighContrast)
    }

    // ── resolve ────────────────────────────────────────────────────────────

    @Test
    fun resolve_light_returnsLightTheme() {
        val theme = VThemeRegistry.resolve("light")
        assertEquals("light", theme.id)
    }

    @Test
    fun resolve_dark_returnsDarkTheme() {
        val theme = VThemeRegistry.resolve("dark")
        assertEquals("dark", theme.id)
    }

    @Test
    fun resolve_unknownId_returnsDefault() {
        val theme = VThemeRegistry.resolve("nonexistent")
        assertEquals(VThemeRegistry.defaultTheme.id, theme.id)
    }

    @Test
    fun resolve_emptyString_returnsDefault() {
        val theme = VThemeRegistry.resolve("")
        assertEquals(VThemeRegistry.defaultTheme.id, theme.id)
    }

    // ── defaultTheme ───────────────────────────────────────────────────────

    @Test
    fun defaultTheme_isLight() {
        assertEquals("light", VThemeRegistry.defaultTheme.id)
    }

    @Test
    fun defaultTheme_isNotDark() {
        assertFalse(VThemeRegistry.defaultTheme.isDark)
    }

    // ── defaultDarkTheme ───────────────────────────────────────────────────

    @Test
    fun defaultDarkTheme_isDark() {
        assertTrue(VThemeRegistry.defaultDarkTheme.isDark)
    }

    @Test
    fun defaultDarkTheme_isDarkId() {
        assertEquals("dark", VThemeRegistry.defaultDarkTheme.id)
    }

    // ── resolveSystem ──────────────────────────────────────────────────────

    @Test
    fun resolveSystem_darkTrue_returnsDarkTheme() {
        val theme = VThemeRegistry.resolveSystem(isSystemDark = true)
        assertTrue(theme.isDark)
        assertEquals("dark", theme.id)
    }

    @Test
    fun resolveSystem_darkFalse_returnsLightTheme() {
        val theme = VThemeRegistry.resolveSystem(isSystemDark = false)
        assertFalse(theme.isDark)
        assertEquals("light", theme.id)
    }

    // ── resolveInclusive ───────────────────────────────────────────────────

    @Test
    fun resolveInclusive_knownId_returnsTheme() {
        val theme = VThemeRegistry.resolveInclusive("midnight")
        assertEquals("midnight", theme.id)
    }

    @Test
    fun resolveInclusive_unknownId_returnsDefault() {
        val theme = VThemeRegistry.resolveInclusive("nonexistent")
        assertEquals(VThemeRegistry.defaultTheme.id, theme.id)
    }

    @Test
    fun resolveInclusive_nullId_returnsDefault() {
        val theme = VThemeRegistry.resolveInclusive("null")
        assertEquals(VThemeRegistry.defaultTheme.id, theme.id)
    }

    // ── Dynamic registration ───────────────────────────────────────────────

    @Test
    fun allThemes_includesStaticThemes() {
        val allIds = VThemeRegistry.allThemes.map { it.id }
        assertTrue(allIds.contains("light"))
        assertTrue(allIds.contains("dark"))
    }

    @Test
    fun registerDynamic_addsTheme() {
        val customTheme = VThemeDef(
            id = "test-custom-theme",
            displayName = "Test Custom",
            description = "Test",
            colors = LightVColors,
            isDark = false,
            icon = VThemeRegistry.defaultTheme.icon,
        )
        VThemeRegistry.registerDynamic(customTheme)

        val resolved = VThemeRegistry.resolveInclusive("test-custom-theme")
        assertEquals("test-custom-theme", resolved.id)
    }

    @Test
    fun registerDynamic_duplicateId_doesNotAddAgain() {
        val customTheme1 = VThemeDef(
            id = "test-dup-theme",
            displayName = "Test Dup 1",
            description = "Test",
            colors = LightVColors,
            isDark = false,
            icon = VThemeRegistry.defaultTheme.icon,
        )
        val customTheme2 = VThemeDef(
            id = "test-dup-theme",
            displayName = "Test Dup 2",
            description = "Test",
            colors = NightVColors,
            isDark = true,
            icon = VThemeRegistry.defaultTheme.icon,
        )
        VThemeRegistry.registerDynamic(customTheme1)
        VThemeRegistry.registerDynamic(customTheme2)

        val resolved = VThemeRegistry.resolveInclusive("test-dup-theme")
        assertEquals("Test Dup 1", resolved.displayName)
    }

    @Test
    fun registerDynamic_staticThemeId_doesNotAddDuplicate() {
        // Try to register a theme with id "light" (already exists)
        val duplicateLight = VThemeDef(
            id = "light",
            displayName = "Fake Light",
            description = "Should not override",
            colors = NightVColors,
            isDark = true,
            icon = VThemeRegistry.defaultTheme.icon,
        )
        VThemeRegistry.registerDynamic(duplicateLight)

        // The original "light" should still be returned
        val resolved = VThemeRegistry.resolve("light")
        assertEquals("Light", resolved.displayName)
        assertFalse(resolved.isDark)
    }
}
