package com.littlebridge.enrollplus.ui.v2.theme

import kotlin.test.Test
import kotlin.test.assertEquals

class VThemeModeTest {

    @Test
    fun fromString_system_returnsSystem() {
        assertEquals(VThemeMode.SYSTEM, VThemeMode.fromString("system"))
    }

    @Test
    fun fromString_light_returnsLight() {
        assertEquals(VThemeMode.LIGHT, VThemeMode.fromString("light"))
    }

    @Test
    fun fromString_dark_returnsDark() {
        assertEquals(VThemeMode.DARK, VThemeMode.fromString("dark"))
    }

    @Test
    fun fromString_custom_returnsCustom() {
        assertEquals(VThemeMode.CUSTOM, VThemeMode.fromString("custom"))
    }

    @Test
    fun fromString_null_returnsSystem() {
        assertEquals(VThemeMode.SYSTEM, VThemeMode.fromString(null))
    }

    @Test
    fun fromString_emptyString_returnsSystem() {
        assertEquals(VThemeMode.SYSTEM, VThemeMode.fromString(""))
    }

    @Test
    fun fromString_unknownString_returnsSystem() {
        assertEquals(VThemeMode.SYSTEM, VThemeMode.fromString("unknown"))
        assertEquals(VThemeMode.SYSTEM, VThemeMode.fromString("auto"))
        assertEquals(VThemeMode.SYSTEM, VThemeMode.fromString("midnight"))
    }

    @Test
    fun fromString_caseSensitive_unknownReturnsSystem() {
        // "System" with capital S is not a valid storage value
        assertEquals(VThemeMode.SYSTEM, VThemeMode.fromString("System"))
        assertEquals(VThemeMode.SYSTEM, VThemeMode.fromString("Light"))
        assertEquals(VThemeMode.SYSTEM, VThemeMode.fromString("Dark"))
    }

    // ── storageValue ───────────────────────────────────────────────────────

    @Test
    fun storageValue_system_isSystem() {
        assertEquals("system", VThemeMode.SYSTEM.storageValue)
    }

    @Test
    fun storageValue_light_isLight() {
        assertEquals("light", VThemeMode.LIGHT.storageValue)
    }

    @Test
    fun storageValue_dark_isDark() {
        assertEquals("dark", VThemeMode.DARK.storageValue)
    }

    @Test
    fun storageValue_custom_isCustom() {
        assertEquals("custom", VThemeMode.CUSTOM.storageValue)
    }

    // ── Round-trip ─────────────────────────────────────────────────────────

    @Test
    fun roundTrip_allModes() {
        VThemeMode.entries.forEach { mode ->
            val parsed = VThemeMode.fromString(mode.storageValue)
            assertEquals(mode, parsed, "Round-trip failed for $mode")
        }
    }
}
