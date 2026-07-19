package com.littlebridge.enrollplus.ui.v2.screens.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TeacherFirstLoginValidationTest {

    private val tooShort = "Password must be at least 8 characters"
    private val noMatch = "Passwords do not match"

    // ── validate (TeacherFirstLoginScreenV2) ───────────────────────────────

    @Test
    fun validate_passwordTooShort_returnsTooShortError() {
        assertEquals(tooShort, validate("oldPw", "short", "short", tooShort, noMatch))
    }

    @Test
    fun validate_passwordExactly7Chars_returnsTooShortError() {
        assertEquals(tooShort, validate("oldPw", "1234567", "1234567", tooShort, noMatch))
    }

    @Test
    fun validate_passwordExactly8Chars_returnsNull() {
        assertNull(validate("oldPw", "12345678", "12345678", tooShort, noMatch))
    }

    @Test
    fun validate_passwordsDoNotMatch_returnsNoMatchError() {
        assertEquals(noMatch, validate("oldPw", "longpass1", "longpass2", tooShort, noMatch))
    }

    @Test
    fun validate_passwordsMatch_returnsNull() {
        assertNull(validate("oldPw", "longpass1", "longpass1", tooShort, noMatch))
    }

    @Test
    fun validate_bothEmpty_returnsTooShortError() {
        assertEquals(tooShort, validate("", "", "", tooShort, noMatch))
    }

    @Test
    fun validate_newPasswordEmpty_confirmEmpty_returnsTooShortError() {
        // Length 0 < 8, so tooShort takes precedence
        assertEquals(tooShort, validate("oldPw", "", "", tooShort, noMatch))
    }

    @Test
    fun validate_longPasswordMatching_returnsNull() {
        val pw = "A".repeat(100)
        assertNull(validate("oldPw", pw, pw, tooShort, noMatch))
    }

    @Test
    fun validate_shortButMatching_returnsTooShortError() {
        // Even though they match, length check comes first
        assertEquals(tooShort, validate("oldPw", "same123", "same123", tooShort, noMatch))
    }

    @Test
    fun validate_shortAndNotMatching_returnsTooShortError() {
        // Length check takes precedence over match check
        assertEquals(tooShort, validate("oldPw", "short1", "short2", tooShort, noMatch))
    }

    @Test
    fun validate_longButNotMatching_returnsNoMatchError() {
        assertEquals(noMatch, validate("oldPw", "longpass1", "longpassX", tooShort, noMatch))
    }

    @Test
    fun validate_caseSensitiveMatching_returnsNoMatchError() {
        assertEquals(noMatch, validate("oldPw", "LongPass1", "longpass1", tooShort, noMatch))
    }
}
