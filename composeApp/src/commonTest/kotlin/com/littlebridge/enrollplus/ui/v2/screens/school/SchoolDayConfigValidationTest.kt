package com.littlebridge.enrollplus.ui.v2.screens.school

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SchoolDayConfigValidationTest {

    // ── isValidDays ────────────────────────────────────────────────────────

    @Test
    fun isValidDays_singleDay_returnsTrue() {
        assertTrue(isValidDays("1"))
        assertTrue(isValidDays("7"))
    }

    @Test
    fun isValidDays_multipleDaysCommaSeparated_returnsTrue() {
        assertTrue(isValidDays("1,2,3,4,5"))
        assertTrue(isValidDays("1, 2, 3, 4, 5"))
    }

    @Test
    fun isValidDays_allDays1to7_returnsTrue() {
        assertTrue(isValidDays("1,2,3,4,5,6,7"))
    }

    @Test
    fun isValidDays_day0_returnsFalse() {
        assertFalse(isValidDays("0"))
        assertFalse(isValidDays("0,1,2"))
    }

    @Test
    fun isValidDays_day8_returnsFalse() {
        assertFalse(isValidDays("8"))
        assertFalse(isValidDays("1,2,8"))
    }

    @Test
    fun isValidDays_empty_returnsFalse() {
        assertFalse(isValidDays(""))
    }

    @Test
    fun isValidDays_withLetters_returnsFalse() {
        assertFalse(isValidDays("Mon,Tue,Wed"))
        assertFalse(isValidDays("1,a,3"))
    }

    @Test
    fun isValidDays_withSpaces_returnsTrue() {
        assertTrue(isValidDays(" 1, 2 , 3 "))
    }

    @Test
    fun isValidDays_trailingComma_returnsFalse() {
        assertFalse(isValidDays("1,2,"))
    }

    @Test
    fun isValidDays_doubleComma_returnsFalse() {
        assertFalse(isValidDays("1,,2"))
    }

    @Test
    fun isValidDays_negativeNumber_returnsFalse() {
        assertFalse(isValidDays("-1"))
        assertFalse(isValidDays("1,-2,3"))
    }

    @Test
    fun isValidDays_decimalNumber_returnsFalse() {
        assertFalse(isValidDays("1.5"))
        assertFalse(isValidDays("1,2.0,3"))
    }

    @Test
    fun isValidDays_allSameDay_returnsTrue() {
        assertTrue(isValidDays("1,1,1"))
    }

    @Test
    fun isValidDays_singleDayWithSpaces_returnsTrue() {
        assertTrue(isValidDays("  3  "))
    }
}
