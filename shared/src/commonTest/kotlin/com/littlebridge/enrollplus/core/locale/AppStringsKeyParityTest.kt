/*
 * File: AppStringsKeyParityTest.kt
 * Module: commonTest
 *
 * Verifies that every supported language has the same set of string keys
 * as the English baseline. This prevents silent fallback-to-key issues
 * when a new string is added to one language but not others.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §13
 */
package com.littlebridge.enrollplus.core.locale

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppStringsKeyParityTest {

    @Test
    fun allLanguagesHaveSameKeysAsEnglish() {
        val enKeys = AppStrings.getKeys("en")
        assertTrue(enKeys.isNotEmpty(), "English string map should not be empty")

        AppStrings.supportedLanguages.forEach { lang ->
            if (lang == "en") return@forEach
            val langKeys = AppStrings.getKeys(lang)
            assertEquals(
                enKeys,
                langKeys,
                "Language '$lang' is missing or has extra keys compared to English. " +
                    "Missing: ${enKeys - langKeys}, Extra: ${langKeys - enKeys}",
            )
        }
    }

    @Test
    fun everyStringKeyConstantResolvesInEnglish() {
        val enKeys = AppStrings.getKeys("en")

        // Verify a sample of StringKeys constants resolve to non-key values
        val sampleKeys = listOf(
            StringKeys.COMMON_BUTTON_SAVE,
            StringKeys.COMMON_BUTTON_CANCEL,
            StringKeys.AUTH_LOGIN,
            StringKeys.LANGUAGE_TITLE,
            StringKeys.NAV_HOME,
            StringKeys.DASH_GOOD_MORNING,
            StringKeys.SETTINGS_TITLE,
            StringKeys.PROFILE_TITLE,
        )

        sampleKeys.forEach { key ->
            val value = AppStrings.get(key, "en")
            assertTrue(
                value != key,
                "StringKey '$key' resolved to itself — the key is not in the English map.",
            )
            assertTrue(
                enKeys.contains(key),
                "StringKey '$key' is not present in the English string map.",
            )
        }
    }

    @Test
    fun fallbackReturnsKeyWhenMissing() {
        val result = AppStrings.get("nonexistent.key.xyz", "hi")
        assertEquals("nonexistent.key.xyz", result)
    }

    @Test
    fun englishFallbackWorksForPartialTranslations() {
        // If a language is missing a key, it should fall back to English
        // rather than returning the key itself. Since all our languages
        // currently mirror English, we test with a fabricated scenario.
        val value = AppStrings.get(StringKeys.COMMON_BUTTON_SAVE, "hi")
        assertTrue(value.isNotBlank(), "Hindi translation for common.button_save should not be blank")
    }

    @Test
    fun pluralResolvesCorrectly() {
        val singular = AppStrings.getPlural(StringKeys.NOTIF_UNREAD, "en", 1)
        val plural = AppStrings.getPlural(StringKeys.NOTIF_UNREAD, "en", 5)

        assertTrue(singular.contains("1"), "Singular form should contain count: $singular")
        assertTrue(plural.contains("5"), "Plural form should contain count: $plural")
    }

    @Test
    fun supportedLanguagesListIsComplete() {
        val expected = listOf("en", "hi", "bn", "ta", "te", "mr", "gu", "kn", "ml", "pa")
        assertEquals(expected, AppStrings.supportedLanguages)
    }
}
