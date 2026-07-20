package com.littlebridge.enrollplus.ui.v2.screens.auth

import kotlin.test.Test
import kotlin.test.assertEquals

class PasswordStrengthTest {

    // ── computePasswordStrength ────────────────────────────────────────────

    @Test
    fun strength_emptyPassword_returns0() {
        assertEquals(0, computePasswordStrength(""))
    }

    @Test
    fun strength_shortPassword_returns3() {
        // Length < 8 → no length point, but case mix + digit + special = 3
        assertEquals(3, computePasswordStrength("Aa1!"))
        assertEquals(3, computePasswordStrength("Ab1!xyz"))
    }

    @Test
    fun strength_onlyLength8_returns1() {
        // 8+ chars but no upper, no lower, no digit, no special
        assertEquals(1, computePasswordStrength("aaaaaaaa"))
    }

    @Test
    fun strength_lengthAndCaseMix_returns2() {
        // 8+ chars + has upper and lower
        assertEquals(2, computePasswordStrength("Aaaaaaaa"))
    }

    @Test
    fun strength_lengthCaseMixAndDigit_returns3() {
        // 8+ chars + upper+lower + digit
        assertEquals(3, computePasswordStrength("Aaaaaaa1"))
    }

    @Test
    fun strength_allFourCriteria_returns4() {
        // 8+ chars + upper+lower + digit + special
        assertEquals(4, computePasswordStrength("Aaaaaaa1!"))
        assertEquals(4, computePasswordStrength("StrongP@ss1"))
    }

    @Test
    fun strength_upperOnlyNoLower_returns1() {
        // 8+ chars + upper but no lower → case mix not satisfied
        assertEquals(1, computePasswordStrength("AAAAAAAA"))
    }

    @Test
    fun strength_lowerOnlyNoUpper_returns1() {
        // 8+ chars + lower but no upper → case mix not satisfied
        assertEquals(1, computePasswordStrength("aaaaaaaa"))
    }

    @Test
    fun strength_lengthAndDigitOnly_returns2() {
        // 8+ chars + digit (no case mix, no special)
        assertEquals(2, computePasswordStrength("12345678"))
    }

    @Test
    fun strength_lengthAndSpecialOnly_returns2() {
        // 8+ chars + special (no case mix, no digit)
        assertEquals(2, computePasswordStrength("@@@@@@@@"))
    }

    @Test
    fun strength_lengthCaseMixAndSpecialNoDigit_returns3() {
        assertEquals(3, computePasswordStrength("Aaaaaaa!"))
    }

    @Test
    fun strength_lengthDigitAndSpecialNoCaseMix_returns3() {
        // 8+ chars + digit + special (no case mix — all same case)
        assertEquals(3, computePasswordStrength("1111111!"))
    }

    @Test
    fun strength_whitespaceNotCountedAsSpecial() {
        // Whitespace is explicitly excluded from special char check
        // "Aa1     " has length 8, upper+lower, digit, but space is not special
        assertEquals(3, computePasswordStrength("Aa1     "))
    }

    @Test
    fun strength_longPasswordAllCriteria_returns4() {
        val pw = "MyVery\$tr0ngP@ssw0rd2024!"
        assertEquals(4, computePasswordStrength(pw))
    }

    @Test
    fun strength_exactly8CharsAllCriteria_returns4() {
        assertEquals(4, computePasswordStrength("Aa1!bbbb"))
    }

    @Test
    fun strength_7CharsAllCriteria_returns3() {
        // Length < 8 → no length point, but case mix + digit + special = 3
        assertEquals(3, computePasswordStrength("Aa1!bcd"))
    }

    // ── passwordStrengthLabel ──────────────────────────────────────────────

    @Test
    fun label_score0_returnsEmpty() {
        assertEquals("", passwordStrengthLabel(0))
    }

    @Test
    fun label_score1_returnsWeak() {
        assertEquals("Weak", passwordStrengthLabel(1))
    }

    @Test
    fun label_score2_returnsFair() {
        assertEquals("Fair", passwordStrengthLabel(2))
    }

    @Test
    fun label_score3_returnsGood() {
        assertEquals("Good", passwordStrengthLabel(3))
    }

    @Test
    fun label_score4_returnsStrong() {
        assertEquals("Strong", passwordStrengthLabel(4))
    }

    @Test
    fun label_negativeScore_returnsEmpty() {
        assertEquals("", passwordStrengthLabel(-1))
    }

    @Test
    fun label_scoreAbove4_returnsEmpty() {
        assertEquals("", passwordStrengthLabel(5))
    }

    // ── Round-trip: strength → label ───────────────────────────────────────

    @Test
    fun roundTrip_emptyPassword_labelIsEmpty() {
        assertEquals("", passwordStrengthLabel(computePasswordStrength("")))
    }

    @Test
    fun roundTrip_weakPassword_labelIsWeak() {
        assertEquals("Weak", passwordStrengthLabel(computePasswordStrength("aaaaaaaa")))
    }

    @Test
    fun roundTrip_strongPassword_labelIsStrong() {
        assertEquals("Strong", passwordStrengthLabel(computePasswordStrength("Aa1!bbbb")))
    }
}
