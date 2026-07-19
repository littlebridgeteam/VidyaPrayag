package com.littlebridge.enrollplus.ui.v2.screens.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AcademicYearOptionsTest {

    @Test
    fun options_year2025_returns2025_26And2026_27() {
        val options = academicYearOptionsTestable(2025)
        assertEquals(2, options.size)
        assertEquals("2025-26", options[0])
        assertEquals("2026-27", options[1])
    }

    @Test
    fun options_year2024_returns2024_25And2025_26() {
        val options = academicYearOptionsTestable(2024)
        assertEquals(2, options.size)
        assertEquals("2024-25", options[0])
        assertEquals("2025-26", options[1])
    }

    @Test
    fun options_year2000_returns2000_01And2001_02() {
        val options = academicYearOptionsTestable(2000)
        assertEquals("2000-01", options[0])
        assertEquals("2001-02", options[1])
    }

    @Test
    fun options_year2099_returns2099_00And2100_01() {
        val options = academicYearOptionsTestable(2099)
        assertEquals("2099-00", options[0])
        assertEquals("2100-01", options[1])
    }

    @Test
    fun options_year1999_returns1999_00And2000_01() {
        val options = academicYearOptionsTestable(1999)
        assertEquals("1999-00", options[0])
        assertEquals("2000-01", options[1])
    }

    @Test
    fun options_alwaysReturnsTwoOptions() {
        for (year in 2000..2100) {
            assertEquals(2, academicYearOptionsTestable(year).size)
        }
    }

    @Test
    fun options_secondYearIsFirstPlusOne() {
        for (year in 2000..2050) {
            val options = academicYearOptionsTestable(year)
            val firstStart = options[0].substringBefore("-").toInt()
            val secondStart = options[1].substringBefore("-").toInt()
            assertEquals(firstStart + 1, secondStart)
        }
    }

    @Test
    fun options_endPartIsZeroPadded() {
        val options = academicYearOptionsTestable(2025)
        // 2025 → end = (2026 % 100) = 26 → "26" (already 2 digits)
        assertEquals("26", options[0].substringAfter("-"))
        // 2026 → end = (2027 % 100) = 27 → "27"
        assertEquals("27", options[1].substringAfter("-"))
    }

    @Test
    fun options_centuryBoundary_zeroPadded() {
        val options = academicYearOptionsTestable(2099)
        // 2099 → end = (2100 % 100) = 0 → "00" (padStart(2, '0'))
        assertEquals("00", options[0].substringAfter("-"))
    }

    @Test
    fun options_year2100_returns2100_01And2101_02() {
        val options = academicYearOptionsTestable(2100)
        assertEquals("2100-01", options[0])
        assertEquals("2101-02", options[1])
    }
}
