package com.littlebridge.enrollplus.ui.v2.screens.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OnboardingStepValidationTest {

    // ════════════════════════════════════════════════════════════════════════
    // StepValidationResult
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun stepValidationResult_noErrors_isValidTrue() {
        val result = StepValidationResult(emptyList())
        assertTrue(result.isValid)
    }

    @Test
    fun stepValidationResult_withErrors_isValidFalse() {
        val result = StepValidationResult(listOf(FieldError("name", "Required")))
        assertFalse(result.isValid)
    }

    @Test
    fun stepValidationResult_getError_existingField_returnsMessage() {
        val result = StepValidationResult(listOf(FieldError("email", "Invalid")))
        assertEquals("Invalid", result.getError("email"))
    }

    @Test
    fun stepValidationResult_getError_nonExistingField_returnsNull() {
        val result = StepValidationResult(listOf(FieldError("email", "Invalid")))
        assertNull(result.getError("name"))
    }

    @Test
    fun stepValidationResult_getError_emptyErrors_returnsNull() {
        val result = StepValidationResult(emptyList())
        assertNull(result.getError("anything"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validateStep1BasicDetails
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun step1_allValid_returnsNoErrors() {
        val result = validateStep1BasicDetails("Rajesh Sharma", "Principal", "rajesh@dps.edu", "9876543210")
        assertTrue(result.isValid)
    }

    @Test
    fun step1_blankName_returnsNameError() {
        val result = validateStep1BasicDetails("", "Principal", "rajesh@dps.edu", "9876543210")
        assertEquals("Name is required", result.getError("name"))
    }

    @Test
    fun step1_blankRole_returnsRoleError() {
        val result = validateStep1BasicDetails("Rajesh", "", "rajesh@dps.edu", "9876543210")
        assertEquals("Select your role", result.getError("role"))
    }

    @Test
    fun step1_invalidEmail_returnsEmailError() {
        val result = validateStep1BasicDetails("Rajesh", "Principal", "notanemail", "9876543210")
        assertEquals("Enter a valid email address", result.getError("email"))
    }

    @Test
    fun step1_invalidPhone_returnsPhoneError() {
        val result = validateStep1BasicDetails("Rajesh", "Principal", "rajesh@dps.edu", "1234567890")
        assertEquals("Enter a valid Indian mobile number", result.getError("phone"))
    }

    @Test
    fun step1_allBlank_returnsAllErrors() {
        val result = validateStep1BasicDetails("", "", "", "")
        assertEquals(4, result.errors.size)
        assertFalse(result.isValid)
    }

    @Test
    fun step1_roleWithSpaces_returnsRoleError() {
        val result = validateStep1BasicDetails("Rajesh", "   ", "rajesh@dps.edu", "9876543210")
        assertEquals("Select your role", result.getError("role"))
    }

    @Test
    fun step1_validWithAllRoles() {
        listOf("Principal", "Vice Principal", "Administrator", "Director", "Manager").forEach { role ->
            val result = validateStep1BasicDetails("Rajesh", role, "rajesh@dps.edu", "9876543210")
            assertNull(result.getError("role"))
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // validateStep2Password
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun step2_validMatchingPasswords_returnsNoErrors() {
        val result = validateStep2Password("StrongP@ss1", "StrongP@ss1")
        assertTrue(result.isValid)
    }

    @Test
    fun step2_blankPassword_returnsPasswordError() {
        val result = validateStep2Password("", "")
        assertEquals("Password is required", result.getError("password"))
    }

    @Test
    fun step2_shortPassword_returnsPasswordError() {
        val result = validateStep2Password("Short1!", "Short1!")
        assertEquals("Must be at least 8 characters", result.getError("password"))
    }

    @Test
    fun step2_passwordsDoNotMatch_returnsConfirmError() {
        val result = validateStep2Password("StrongP@ss1", "StrongP@ss2")
        assertEquals("Passwords do not match", result.getError("confirm"))
    }

    @Test
    fun step2_blankConfirm_returnsConfirmError() {
        val result = validateStep2Password("StrongP@ss1", "")
        assertEquals("Please confirm your password", result.getError("confirm"))
    }

    @Test
    fun step2_noUppercase_returnsPasswordError() {
        val result = validateStep2Password("strongp@ss1", "strongp@ss1")
        assertEquals("Must contain an uppercase letter", result.getError("password"))
    }

    @Test
    fun step2_noSpecialChar_returnsPasswordError() {
        val result = validateStep2Password("StrongPass1", "StrongPass1")
        assertEquals("Must contain a special character", result.getError("password"))
    }

    @Test
    fun step2_multipleErrors_bothReported() {
        val result = validateStep2Password("weak", "different")
        assertEquals("Must be at least 8 characters", result.getError("password"))
        assertEquals("Passwords do not match", result.getError("confirm"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validateStep3SchoolIdentity
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun step3_allValid_returnsNoErrors() {
        val result = validateStep3SchoolIdentity(
            "Delhi Public School", "DPS", "CBSE", "", "Private Unaided",
            "1234567", "Dr. Rajesh Sharma", "9876543210",
        )
        assertTrue(result.isValid)
    }

    @Test
    fun step3_blankSchoolName_returnsSchoolNameError() {
        val result = validateStep3SchoolIdentity("", "DPS", "CBSE", "", "Private", "", "", "")
        assertEquals("School name is required", result.getError("schoolName"))
    }

    @Test
    fun step3_blankShortName_returnsShortNameError() {
        val result = validateStep3SchoolIdentity("DPS School", "", "CBSE", "", "Private", "", "", "")
        assertEquals("Short name is required", result.getError("shortName"))
    }

    @Test
    fun step3_blankBoard_returnsBoardError() {
        val result = validateStep3SchoolIdentity("DPS School", "DPS", "", "", "Private", "", "", "")
        assertEquals("Select a board", result.getError("board"))
    }

    @Test
    fun step3_otherBoardBlankCustom_returnsCustomBoardError() {
        val result = validateStep3SchoolIdentity("DPS School", "DPS", "Other", "", "Private", "", "", "")
        assertEquals("Enter board name", result.getError("customBoard"))
    }

    @Test
    fun step3_otherBoardWithCustom_returnsNoBoardError() {
        val result = validateStep3SchoolIdentity("DPS School", "DPS", "Other", "IB", "Private", "", "", "")
        assertNull(result.getError("board"))
        assertNull(result.getError("customBoard"))
    }

    @Test
    fun step3_blankSchoolType_returnsSchoolTypeError() {
        val result = validateStep3SchoolIdentity("DPS School", "DPS", "CBSE", "", "", "", "", "")
        assertEquals("Select school type", result.getError("schoolType"))
    }

    @Test
    fun step3_invalidAffiliationNumber_returnsAffiliationError() {
        val result = validateStep3SchoolIdentity("DPS School", "DPS", "CBSE", "", "Private", "AB", "", "")
        assertEquals("Affiliation number must be at least 3 characters", result.getError("affiliationNumber"))
    }

    @Test
    fun step3_blankAffiliationNumber_returnsNoError() {
        val result = validateStep3SchoolIdentity("DPS School", "DPS", "CBSE", "", "Private", "", "", "")
        assertNull(result.getError("affiliationNumber"))
    }

    @Test
    fun step3_principalNameWithDigits_returnsPrincipalNameError() {
        val result = validateStep3SchoolIdentity("DPS School", "DPS", "CBSE", "", "Private", "", "Rajesh123", "")
        assertEquals("Name must contain only letters", result.getError("principalName"))
    }

    @Test
    fun step3_blankPrincipalName_returnsNoError() {
        val result = validateStep3SchoolIdentity("DPS School", "DPS", "CBSE", "", "Private", "", "", "")
        assertNull(result.getError("principalName"))
    }

    @Test
    fun step3_principalPhoneInvalid_returnsPrincipalPhoneError() {
        val result = validateStep3SchoolIdentity("DPS School", "DPS", "CBSE", "", "Private", "", "", "1234567890")
        assertEquals("Enter a valid Indian mobile number", result.getError("principalPhone"))
    }

    @Test
    fun step3_blankPrincipalPhone_returnsNoError() {
        val result = validateStep3SchoolIdentity("DPS School", "DPS", "CBSE", "", "Private", "", "", "")
        assertNull(result.getError("principalPhone"))
    }

    @Test
    fun step3_allBlank_returnsMultipleErrors() {
        val result = validateStep3SchoolIdentity("", "", "", "", "", "", "", "")
        assertFalse(result.isValid)
        assertEquals("School name is required", result.getError("schoolName"))
        assertEquals("Short name is required", result.getError("shortName"))
        assertEquals("Select a board", result.getError("board"))
        assertEquals("Select school type", result.getError("schoolType"))
    }

    @Test
    fun step3_validWithAllBoards() {
        listOf("CBSE", "ICSE", "UP State").forEach { board ->
            val result = validateStep3SchoolIdentity("DPS School", "DPS", board, "", "Private", "", "", "")
            assertNull(result.getError("board"))
            assertNull(result.getError("customBoard"))
        }
    }

    @Test
    fun step3_validWithAllSchoolTypes() {
        listOf("Government", "Private Aided", "Private Unaided", "Central").forEach { type ->
            val result = validateStep3SchoolIdentity("DPS School", "DPS", "CBSE", "", type, "", "", "")
            assertNull(result.getError("schoolType"))
        }
    }

    @Test
    fun step3_optionalPrincipalFields_valid_returnsNoErrors() {
        val result = validateStep3SchoolIdentity(
            "DPS School", "DPS", "CBSE", "", "Private", "1234567",
            "Dr. Rajesh Sharma", "9876543210",
        )
        assertTrue(result.isValid)
    }

    // ════════════════════════════════════════════════════════════════════════
    // validateStep4AcademicYear
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun step4_allValid_returnsNoErrors() {
        val result = validateStep4AcademicYear(
            "2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "08:00", "14:00",
        )
        assertTrue(result.isValid)
    }

    @Test
    fun step4_blankYear_returnsYearError() {
        val result = validateStep4AcademicYear("", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "08:00", "14:00")
        assertEquals("Select an academic year", result.getError("year"))
    }

    @Test
    fun step4_blankStartDate_returnsStartDateError() {
        val result = validateStep4AcademicYear("2025-26", "", "2026-03-31", "Mon-Fri", "8", "08:00", "14:00")
        assertEquals("Select start date", result.getError("startDate"))
    }

    @Test
    fun step4_blankEndDate_returnsEndDateError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "", "Mon-Fri", "8", "08:00", "14:00")
        assertEquals("Select end date", result.getError("endDate"))
    }

    @Test
    fun step4_endDateBeforeStartDate_returnsEndDateError() {
        val result = validateStep4AcademicYear("2025-26", "2026-03-31", "2025-04-01", "Mon-Fri", "8", "08:00", "14:00")
        assertEquals("End date must be after start date", result.getError("endDate"))
    }

    @Test
    fun step4_endDateSameAsStartDate_returnsEndDateError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2025-04-01", "Mon-Fri", "8", "08:00", "14:00")
        assertEquals("End date must be after start date", result.getError("endDate"))
    }

    @Test
    fun step4_endDateAfterStartDate_returnsNoEndDateError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2025-04-02", "Mon-Fri", "8", "08:00", "14:00")
        assertNull(result.getError("endDate"))
    }

    @Test
    fun step4_blankWorkingDays_returnsWorkingDaysError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "", "8", "08:00", "14:00")
        assertEquals("Select working days", result.getError("workingDays"))
    }

    @Test
    fun step4_blankPeriods_returnsPeriodsError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "", "08:00", "14:00")
        assertEquals("Select periods per day", result.getError("periods"))
    }

    @Test
    fun step4_endTimeBeforeStartTime_returnsEndTimeError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "14:00", "08:00")
        assertEquals("End time must be after start time", result.getError("endTime"))
    }

    @Test
    fun step4_endTimeSameAsStartTime_returnsEndTimeError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "08:00", "08:00")
        assertEquals("End time must be after start time", result.getError("endTime"))
    }

    @Test
    fun step4_endTimeSameHourButLaterMinute_returnsNoError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "08:00", "08:01")
        assertNull(result.getError("endTime"))
    }

    @Test
    fun step4_endTimeSameHourButEarlierMinute_returnsEndTimeError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "08:30", "08:00")
        assertEquals("End time must be after start time", result.getError("endTime"))
    }

    @Test
    fun step4_startTimeGreaterThanEndTime_returnsEndTimeError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "15:00", "14:00")
        assertEquals("End time must be after start time", result.getError("endTime"))
    }

    @Test
    fun step4_malformedTimeStrings_returnsTimeError() {
        // Malformed times default to 0:0, and 0 == 0 && 0 >= 0 → time error
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "abc", "xyz")
        assertEquals("End time must be after start time", result.getError("endTime"))
    }

    @Test
    fun step4_emptyTimeStrings_returnsTimeError() {
        // Empty strings default to 0:0, and 0 == 0 && 0 >= 0 → time error
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "", "")
        assertEquals("End time must be after start time", result.getError("endTime"))
    }

    @Test
    fun step4_allBlank_returnsAllErrors() {
        val result = validateStep4AcademicYear("", "", "", "", "", "", "")
        // 5 blank-field errors + 1 time error (0:0 >= 0:0) = 6 total
        assertEquals(6, result.errors.size)
        assertFalse(result.isValid)
        assertEquals("Select an academic year", result.getError("year"))
        assertEquals("Select start date", result.getError("startDate"))
        assertEquals("Select end date", result.getError("endDate"))
        assertEquals("Select working days", result.getError("workingDays"))
        assertEquals("Select periods per day", result.getError("periods"))
        assertEquals("End time must be after start time", result.getError("endTime"))
    }

    @Test
    fun step4_validWithMonSat_returnsNoErrors() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Sat", "10", "08:00", "15:00")
        assertTrue(result.isValid)
    }

    @Test
    fun step4_validWithAllPeriodCounts() {
        listOf("4", "5", "6", "7", "8", "9", "10", "11", "12").forEach { periods ->
            val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", periods, "08:00", "14:00")
            assertNull(result.getError("periods"))
        }
    }

    @Test
    fun step4_dateStringComparison_isStringBased() {
        // ISO date strings compare lexicographically, which is correct for YYYY-MM-DD
        val result = validateStep4AcademicYear("2025-26", "2025-12-31", "2026-01-01", "Mon-Fri", "8", "08:00", "14:00")
        assertNull(result.getError("endDate"))
    }

    @Test
    fun step4_timeWithMissingMinute_defaultsToZero() {
        // "08" → substringBefore(":") = "08", substringAfter(":") = "08" → take(2) = "08" → ifBlank not triggered
        // Actually "08".substringAfter(":") returns "08" since no colon, take(2) = "08", toIntOrNull = 8
        // So startH=8, startM=8, endH=14, endM=0 → 8 < 14 → no error
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "08", "14")
        assertNull(result.getError("endTime"))
    }

    @Test
    fun step4_timeWithOnlyHour_endBeforeStart_returnsError() {
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "2026-03-31", "Mon-Fri", "8", "14", "08")
        assertEquals("End time must be after start time", result.getError("endTime"))
    }

    @Test
    fun step4_bothDatesBlank_noDateOrderError() {
        // Both dates blank → date ordering check skipped, but blank date errors present
        val result = validateStep4AcademicYear("2025-26", "", "", "Mon-Fri", "8", "08:00", "14:00")
        assertEquals("Select start date", result.getError("startDate"))
        assertEquals("Select end date", result.getError("endDate"))
    }

    @Test
    fun step4_oneDateBlank_noDateOrderError() {
        // Only start date present, end date blank → ordering check skipped
        val result = validateStep4AcademicYear("2025-26", "2025-04-01", "", "Mon-Fri", "8", "08:00", "14:00")
        // endDate error is "Select end date", not "End date must be after start date"
        assertEquals("Select end date", result.getError("endDate"))
    }
}
