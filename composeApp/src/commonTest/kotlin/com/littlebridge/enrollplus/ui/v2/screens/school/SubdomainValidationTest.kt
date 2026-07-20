package com.littlebridge.enrollplus.ui.v2.screens.school

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubdomainValidationTest {

    // ── Valid subdomains ───────────────────────────────────────────────────

    @Test
    fun isSubdomainValid_valid4CharAlphanumeric_returnsTrue() {
        assertTrue(isSubdomainValid("dps1"))
    }

    @Test
    fun isSubdomainValid_validWithHyphen_returnsTrue() {
        assertTrue(isSubdomainValid("dps-school"))
        assertTrue(isSubdomainValid("my-school123"))
    }

    @Test
    fun isSubdomainValid_valid32Chars_returnsTrue() {
        assertTrue(isSubdomainValid("a1" + "b".repeat(28) + "c"))
    }

    @Test
    fun isSubdomainValid_allLowercase_returnsTrue() {
        assertTrue(isSubdomainValid("delhipublic"))
    }

    @Test
    fun isSubdomainValid_startsAndEndsWithDigit_returnsTrue() {
        assertTrue(isSubdomainValid("1school2"))
    }

    @Test
    fun isSubdomainValid_minimumLength4_returnsTrue() {
        assertTrue(isSubdomainValid("ab12"))
    }

    // ── Invalid subdomains ─────────────────────────────────────────────────

    @Test
    fun isSubdomainValid_tooShort_returnsFalse() {
        assertFalse(isSubdomainValid("ab"))
        assertFalse(isSubdomainValid("abc"))
    }

    @Test
    fun isSubdomainValid_tooLong_returnsFalse() {
        assertFalse(isSubdomainValid("a".repeat(33)))
    }

    @Test
    fun isSubdomainValid_empty_returnsFalse() {
        assertFalse(isSubdomainValid(""))
    }

    @Test
    fun isSubdomainValid_startsOrEndsWithHyphen_returnsFalse() {
        assertFalse(isSubdomainValid("-school"))
        assertFalse(isSubdomainValid("school-"))
        assertFalse(isSubdomainValid("-school-"))
    }

    @Test
    fun isSubdomainValid_uppercase_returnsFalse() {
        assertFalse(isSubdomainValid("DPSSchool"))
        assertFalse(isSubdomainValid("DPS1"))
    }

    @Test
    fun isSubdomainValid_specialChars_returnsFalse() {
        assertFalse(isSubdomainValid("dps_school"))
        assertFalse(isSubdomainValid("dps.school"))
        assertFalse(isSubdomainValid("dps@school"))
    }

    @Test
    fun isSubdomainValid_consecutiveHyphens_returnsTrue() {
        // Regex allows consecutive hyphens in the middle
        assertTrue(isSubdomainValid("a--b"))
    }

    @Test
    fun isSubdomainValid_justDigits_returnsTrue() {
        assertTrue(isSubdomainValid("1234"))
    }

    @Test
    fun isSubdomainValid_singleHyphenInMiddle_returnsTrue() {
        assertTrue(isSubdomainValid("a-b1"))
    }

    @Test
    fun isSubdomainValid_exactly32Chars_returnsTrue() {
        val sub = "a1" + "c".repeat(29) + "d"
        assertEquals(32, sub.length)
        assertTrue(isSubdomainValid(sub))
    }

    @Test
    fun isSubdomainValid_exactly33Chars_returnsFalse() {
        val sub = "a1" + "c".repeat(30) + "d"
        assertEquals(33, sub.length)
        assertFalse(isSubdomainValid(sub))
    }
}
