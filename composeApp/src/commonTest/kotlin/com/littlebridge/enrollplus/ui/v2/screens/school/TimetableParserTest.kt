package com.littlebridge.enrollplus.ui.v2.screens.school

import com.littlebridge.enrollplus.feature.school.domain.model.SchoolDaySlotDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimetableParserTest {

    // ── normalizeTime ──────────────────────────────────────────────────────

    @Test
    fun normalizeTime_singleDigitHour_padsTo2() {
        assertEquals("08:00", normalizeTime("8:00"))
        assertEquals("09:45", normalizeTime("9:45"))
    }

    @Test
    fun normalizeTime_doubleDigitHour_unchanged() {
        assertEquals("10:00", normalizeTime("10:00"))
        assertEquals("12:30", normalizeTime("12:30"))
    }

    @Test
    fun normalizeTime_singleDigitMinute_padsTo2() {
        assertEquals("08:05", normalizeTime("8:5"))
        assertEquals("10:03", normalizeTime("10:3"))
    }

    @Test
    fun normalizeTime_alreadyPadded_unchanged() {
        assertEquals("08:00", normalizeTime("08:00"))
        assertEquals("14:30", normalizeTime("14:30"))
    }

    @Test
    fun normalizeTime_noColon_returnsAsIs() {
        assertEquals("0800", normalizeTime("0800"))
    }

    @Test
    fun normalizeTime_threeParts_returnsAsIs() {
        assertEquals("08:00:00", normalizeTime("08:00:00"))
    }

    // ── parseTimetableText ─────────────────────────────────────────────────

    @Test
    fun parseTimetableText_emptyString_returnsEmptySlotsAndName() {
        val (slots, name) = parseTimetableText("")
        assertTrue(slots.isEmpty())
        assertEquals("", name)
    }

    @Test
    fun parseTimetableText_singleSlot_dashFormat() {
        val (slots, name) = parseTimetableText("08:00-08:45 Maths")
        assertEquals(1, slots.size)
        assertEquals("Maths", slots[0].label)
        assertEquals("08:00", slots[0].startTime)
        assertEquals("08:45", slots[0].endTime)
        assertEquals("TEACHING", slots[0].slotType)
        assertEquals("", name)
    }

    @Test
    fun parseTimetableText_multipleSlots() {
        val text = """
            Monday Schedule
            08:00-08:45 Maths
            08:45-09:30 English
            09:30-10:15 Science
        """.trimIndent()
        val (slots, name) = parseTimetableText(text)
        assertEquals(3, slots.size)
        assertEquals("Maths", slots[0].label)
        assertEquals("English", slots[1].label)
        assertEquals("Science", slots[2].label)
        assertEquals("Monday Schedule", name)
    }

    @Test
    fun parseTimetableText_enDashFormat() {
        val (slots, _) = parseTimetableText("08:00–08:45 Maths")
        assertEquals(1, slots.size)
        assertEquals("08:00", slots[0].startTime)
        assertEquals("08:45", slots[0].endTime)
    }

    @Test
    fun parseTimetableText_dotSeparatorInTime() {
        val (slots, _) = parseTimetableText("08.00-08.45 Maths")
        assertEquals(1, slots.size)
        assertEquals("08:00", slots[0].startTime)
        assertEquals("08:45", slots[0].endTime)
    }

    @Test
    fun parseTimetableText_labelBeforeTime() {
        val (slots, _) = parseTimetableText("Period 1 08:00-08:45")
        assertEquals(1, slots.size)
        assertEquals("Period 1", slots[0].label)
    }

    @Test
    fun parseTimetableText_labelAfterTime() {
        val (slots, _) = parseTimetableText("08:00-08:45 Maths")
        assertEquals(1, slots.size)
        assertEquals("Maths", slots[0].label)
    }

    @Test
    fun parseTimetableText_breakSlot_typeIsBreak() {
        val (slots, _) = parseTimetableText("10:15-10:30 Break")
        assertEquals(1, slots.size)
        assertEquals("BREAK", slots[0].slotType)
    }

    @Test
    fun parseTimetableText_recessSlot_typeIsBreak() {
        val (slots, _) = parseTimetableText("10:15-10:30 Recess")
        assertEquals(1, slots.size)
        assertEquals("BREAK", slots[0].slotType)
    }

    @Test
    fun parseTimetableText_lunchSlot_typeIsBreak() {
        val (slots, _) = parseTimetableText("12:00-12:45 Lunch Break")
        assertEquals(1, slots.size)
        assertEquals("BREAK", slots[0].slotType)
    }

    @Test
    fun parseTimetableText_intervalSlot_typeIsBreak() {
        val (slots, _) = parseTimetableText("10:15-10:30 Interval")
        assertEquals(1, slots.size)
        assertEquals("BREAK", slots[0].slotType)
    }

    @Test
    fun parseTimetableText_assemblySlot_typeIsAssembly() {
        val (slots, _) = parseTimetableText("08:00-08:15 Assembly")
        assertEquals(1, slots.size)
        assertEquals("ASSEMBLY", slots[0].slotType)
    }

    @Test
    fun parseTimetableText_prayerSlot_typeIsAssembly() {
        val (slots, _) = parseTimetableText("08:00-08:15 Prayer")
        assertEquals(1, slots.size)
        assertEquals("ASSEMBLY", slots[0].slotType)
    }

    @Test
    fun parseTimetableText_labSlot_typeIsLab() {
        val (slots, _) = parseTimetableText("09:00-10:00 Computer Lab")
        assertEquals(1, slots.size)
        assertEquals("LAB", slots[0].slotType)
    }

    @Test
    fun parseTimetableText_teachingSlot_typeIsTeaching() {
        val (slots, _) = parseTimetableText("08:00-08:45 Mathematics")
        assertEquals(1, slots.size)
        assertEquals("TEACHING", slots[0].slotType)
    }

    @Test
    fun parseTimetableText_noLabel_usesSlotIndex() {
        val (slots, _) = parseTimetableText("08:00-08:45")
        assertEquals(1, slots.size)
        assertEquals("Slot 1", slots[0].label)
    }

    @Test
    fun parseTimetableText_mixedSlots() {
        val text = """
            Daily Schedule
            08:00-08:15 Assembly
            08:15-09:00 Maths
            09:00-09:15 Break
            09:15-10:00 English
            10:00-11:00 Chemistry Lab
        """.trimIndent()
        val (slots, name) = parseTimetableText(text)
        assertEquals(5, slots.size)
        assertEquals("ASSEMBLY", slots[0].slotType)
        assertEquals("TEACHING", slots[1].slotType)
        assertEquals("BREAK", slots[2].slotType)
        assertEquals("TEACHING", slots[3].slotType)
        assertEquals("LAB", slots[4].slotType)
        assertEquals("Daily Schedule", name)
    }

    @Test
    fun parseTimetableText_slotIndexIsSequential() {
        val text = """
            08:00-08:45 Maths
            08:45-09:30 English
            09:30-10:15 Science
        """.trimIndent()
        val (slots, _) = parseTimetableText(text)
        assertEquals(0, slots[0].slotIndex)
        assertEquals(1, slots[1].slotIndex)
        assertEquals(2, slots[2].slotIndex)
    }

    @Test
    fun parseTimetableText_nameFromFirstNonTimeLine() {
        val text = """
            Class 8-A Timetable
            08:00-08:45 Maths
        """.trimIndent()
        val (_, name) = parseTimetableText(text)
        assertEquals("Class 8-A Timetable", name)
    }

    @Test
    fun parseTimetableText_nameTooLong_ignored() {
        val text = """
            This is a very long line that exceeds 40 characters limit
            08:00-08:45 Maths
        """.trimIndent()
        val (_, name) = parseTimetableText(text)
        assertEquals("", name)
    }

    @Test
    fun parseTimetableText_nameTooShort_ignored() {
        val text = """
            AB
            08:00-08:45 Maths
        """.trimIndent()
        val (_, name) = parseTimetableText(text)
        assertEquals("", name)
    }

    @Test
    fun parseTimetableText_onlyBlankLines_returnsEmpty() {
        val (slots, name) = parseTimetableText("   \n\n   \n")
        assertTrue(slots.isEmpty())
        assertEquals("", name)
    }

    @Test
    fun parseTimetableText_toKeywordAsSeparator() {
        val (slots, _) = parseTimetableText("08:00 to 08:45 Maths")
        assertEquals(1, slots.size)
        assertEquals("08:00", slots[0].startTime)
        assertEquals("08:45", slots[0].endTime)
    }

    @Test
    fun parseTimetableText_lunchBreakAssembledAsBreak() {
        val (slots, _) = parseTimetableText("12:00-12:45 Lunch")
        assertEquals(1, slots.size)
        assertEquals("BREAK", slots[0].slotType)
        assertEquals("Lunch", slots[0].label)
    }

    @Test
    fun parseTimetableText_caseInsensitiveBreakKeywords() {
        val (slots, _) = parseTimetableText("10:00-10:15 BREAK")
        assertEquals("BREAK", slots[0].slotType)

        val (slots2, _) = parseTimetableText("10:00-10:15 LUNCH")
        assertEquals("BREAK", slots2[0].slotType)

        val (slots3, _) = parseTimetableText("10:00-10:15 Assembly")
        assertEquals("ASSEMBLY", slots3[0].slotType)
    }
}
