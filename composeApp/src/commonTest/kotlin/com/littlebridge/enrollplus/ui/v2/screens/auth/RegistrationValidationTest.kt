package com.littlebridge.enrollplus.ui.v2.screens.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RegistrationValidationTest {

    // ════════════════════════════════════════════════════════════════════════
    // validateName
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun validateName_blank_returnsRequiredError() {
        assertEquals("Name is required", validateName(""))
        assertEquals("Name is required", validateName("   "))
    }

    @Test
    fun validateName_singleChar_returnsMinLengthError() {
        assertEquals("Name must be at least 2 characters", validateName("A"))
        assertEquals("Name must be at least 2 characters", validateName(" x "))
    }

    @Test
    fun validateName_twoChars_returnsNull() {
        assertNull(validateName("Ab"))
        assertNull(validateName("  Ab  "))
    }

    @Test
    fun validateName_withSpaces_returnsNull() {
        assertNull(validateName("Rajesh Sharma"))
        assertNull(validateName("Dr. Rajesh Sharma"))
    }

    @Test
    fun validateName_withDotAndHyphen_returnsNull() {
        assertNull(validateName("Dr. Rajesh-Sharma"))
    }

    @Test
    fun validateName_withDigits_returnsError() {
        assertEquals("Name must contain only letters", validateName("Rajesh123"))
    }

    @Test
    fun validateName_withSpecialChars_returnsError() {
        assertEquals("Name must contain only letters", validateName("Rajesh@"))
        assertEquals("Name must contain only letters", validateName("Raj#"))
    }

    @Test
    fun validateName_longName_returnsNull() {
        assertNull(validateName("Venkata Subramanya Iyer Krishnamurthy"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validateEmail
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun validateEmail_blank_returnsRequiredError() {
        assertEquals("Email is required", validateEmail(""))
        assertEquals("Email is required", validateEmail("   "))
    }

    @Test
    fun validateEmail_validSimple_returnsNull() {
        assertNull(validateEmail("user@example.com"))
        assertNull(validateEmail("principal@dps.edu.in"))
        assertNull(validateEmail("test.user@domain.co.uk"))
    }

    @Test
    fun validateEmail_noAtSign_returnsError() {
        assertEquals("Enter a valid email address", validateEmail("userexample.com"))
    }

    @Test
    fun validateEmail_doubleAt_returnsError() {
        assertEquals("Enter a valid email address", validateEmail("user@@example.com"))
    }

    @Test
    fun validateEmail_localPartStartsWithDot_returnsError() {
        assertEquals("Enter a valid email address", validateEmail(".user@example.com"))
    }

    @Test
    fun validateEmail_localPartEndsWithDot_returnsError() {
        assertEquals("Enter a valid email address", validateEmail("user.@example.com"))
    }

    @Test
    fun validateEmail_consecutiveDots_returnsError() {
        assertEquals("Enter a valid email address", validateEmail("user..name@example.com"))
    }

    @Test
    fun validateEmail_tooLong_returnsError() {
        val longLocal = "a".repeat(65)
        assertEquals("Enter a valid email address", validateEmail("$longLocal@example.com"))
    }

    @Test
    fun validateEmail_localPartMax64_returnsNull() {
        val local = "a".repeat(64)
        assertNull(validateEmail("$local@example.com"))
    }

    @Test
    fun validateEmail_plusAddressing_returnsNull() {
        assertNull(validateEmail("user+tag@example.com"))
    }

    @Test
    fun validateEmail_withSubdomain_returnsNull() {
        assertNull(validateEmail("user@mail.sub.example.com"))
    }

    @Test
    fun validateEmail_noDomain_returnsError() {
        assertEquals("Enter a valid email address", validateEmail("user@"))
    }

    @Test
    fun validateEmail_shortDomain_returnsError() {
        assertEquals("Enter a valid email address", validateEmail("user@x"))
    }

    @Test
    fun validateEmail_uppercase_returnsNull() {
        assertNull(validateEmail("USER@EXAMPLE.COM"))
        assertNull(validateEmail("User@Example.Com"))
    }

    @Test
    fun validateEmail_specialCharsInLocal_returnsNull() {
        assertNull(validateEmail("user!#$%&'*+/=?^_`{|}~-@example.com"))
    }

    @Test
    fun validateEmail_totalLengthOver254_returnsError() {
        val local = "a".repeat(50)
        val domain = "b".repeat(200) + ".com"
        assertEquals("Enter a valid email address", validateEmail("$local@$domain"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validatePhone
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun validatePhone_blank_returnsRequiredError() {
        assertEquals("Phone number is required", validatePhone(""))
    }

    @Test
    fun validatePhone_valid10Digits_returnsNull() {
        assertNull(validatePhone("9876543210"))
        assertNull(validatePhone("6123456789"))
        assertNull(validatePhone("7999999999"))
        assertNull(validatePhone("8000000000"))
        assertNull(validatePhone("9000000000"))
    }

    @Test
    fun validatePhone_tooShort_returnsError() {
        assertEquals("Phone must be exactly 10 digits", validatePhone("987654321"))
    }

    @Test
    fun validatePhone_tooLong_returnsError() {
        assertEquals("Phone must be exactly 10 digits", validatePhone("98765432101"))
    }

    @Test
    fun validatePhone_withLetters_returnsError() {
        assertEquals("Phone must contain only digits", validatePhone("98765432ab"))
    }

    @Test
    fun validatePhone_startsBelow6_returnsError() {
        assertEquals("Enter a valid Indian mobile number", validatePhone("5123456789"))
        assertEquals("Enter a valid Indian mobile number", validatePhone("0123456789"))
        assertEquals("Enter a valid Indian mobile number", validatePhone("1234567890"))
    }

    @Test
    fun validatePhone_startsWith6_returnsNull() {
        assertNull(validatePhone("6123456789"))
    }

    @Test
    fun validatePhone_startsWith9_returnsNull() {
        assertNull(validatePhone("9123456789"))
    }

    @Test
    fun validatePhone_withSpaces_returnsError() {
        // 10 chars with space → length check passes, digit check fails
        assertEquals("Phone must contain only digits", validatePhone("9876 43210"))
    }

    @Test
    fun validatePhone_withCountryCode_returnsError() {
        assertEquals("Phone must be exactly 10 digits", validatePhone("919876543210"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validatePassword
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun validatePassword_blank_returnsRequiredError() {
        assertEquals("Password is required", validatePassword(""))
    }

    @Test
    fun validatePassword_tooShort_returnsError() {
        assertEquals("Must be at least 8 characters", validatePassword("Aa1!"))
        assertEquals("Must be at least 8 characters", validatePassword("Ab1!xyz"))
    }

    @Test
    fun validatePassword_exactly8CharsValid_returnsNull() {
        assertNull(validatePassword("Aa1!bbbb"))
    }

    @Test
    fun validatePassword_noUppercase_returnsError() {
        assertEquals("Must contain an uppercase letter", validatePassword("aa1!bbbb"))
    }

    @Test
    fun validatePassword_noLowercase_returnsError() {
        assertEquals("Must contain a lowercase letter", validatePassword("AA1!BBBB"))
    }

    @Test
    fun validatePassword_noDigit_returnsError() {
        assertEquals("Must contain a number", validatePassword("Aa!bbbbbb"))
    }

    @Test
    fun validatePassword_noSpecialChar_returnsError() {
        assertEquals("Must contain a special character", validatePassword("Aa1bbbbb"))
    }

    @Test
    fun validatePassword_allRequirementsMet_returnsNull() {
        assertNull(validatePassword("StrongP@ss1"))
        assertNull(validatePassword("Abcdef1!"))
    }

    @Test
    fun validatePassword_over128Chars_returnsError() {
        val pw = "Aa1!" + "b".repeat(125)
        assertEquals("Must be 128 characters or fewer", validatePassword(pw))
    }

    @Test
    fun validatePassword_exactly128CharsValid_returnsNull() {
        val pw = "Aa1!" + "b".repeat(124)
        assertEquals(128, pw.length)
        assertNull(validatePassword(pw))
    }

    @Test
    fun validatePassword_withWhitespaceAsSpecial_returnsError() {
        // Whitespace is explicitly excluded from special char check
        assertEquals("Must contain a special character", validatePassword("Aa1bbbb b"))
    }

    @Test
    fun validatePassword_commonSpecialChars_returnNull() {
        assertNull(validatePassword("Aa1!@#$%^"))
        assertNull(validatePassword("Aa1&*()_+"))
        assertNull(validatePassword("Aa1<>?/+"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validateConfirmPassword
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun validateConfirmPassword_blank_returnsError() {
        assertEquals("Please confirm your password", validateConfirmPassword("Aa1!bbbb", ""))
    }

    @Test
    fun validateConfirmPassword_matching_returnsNull() {
        assertNull(validateConfirmPassword("Aa1!bbbb", "Aa1!bbbb"))
    }

    @Test
    fun validateConfirmPassword_notMatching_returnsError() {
        assertEquals("Passwords do not match", validateConfirmPassword("Aa1!bbbb", "Aa1!bbbc"))
    }

    @Test
    fun validateConfirmPassword_caseSensitive_returnsError() {
        assertEquals("Passwords do not match", validateConfirmPassword("Aa1!bbbb", "aa1!bbbb"))
    }

    @Test
    fun validateConfirmPassword_bothEmpty_returnsError() {
        assertEquals("Please confirm your password", validateConfirmPassword("", ""))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validateSchoolName
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun validateSchoolName_blank_returnsRequiredError() {
        assertEquals("School name is required", validateSchoolName(""))
    }

    @Test
    fun validateSchoolName_tooShort_returnsError() {
        assertEquals("School name must be at least 3 characters", validateSchoolName("AB"))
        assertEquals("School name must be at least 3 characters", validateSchoolName("  A  "))
    }

    @Test
    fun validateSchoolName_validName_returnsNull() {
        assertNull(validateSchoolName("Delhi Public School"))
        assertNull(validateSchoolName("St. Mary's High School"))
        assertNull(validateSchoolName("DPS-School"))
    }

    @Test
    fun validateSchoolName_withDigits_returnsError() {
        assertEquals("School name must contain only alphabetic characters", validateSchoolName("DPS 123"))
    }

    @Test
    fun validateSchoolName_withSpecialChars_returnsError() {
        assertEquals("School name must contain only alphabetic characters", validateSchoolName("DPS@School"))
        assertEquals("School name must contain only alphabetic characters", validateSchoolName("DPS#1"))
    }

    @Test
    fun validateSchoolName_withApostrophe_returnsNull() {
        assertNull(validateSchoolName("St. Mary's"))
    }

    @Test
    fun validateSchoolName_longName_returnsNull() {
        assertNull(validateSchoolName("Delhi Public School Society of Higher Education"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validateShortName
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun validateShortName_blank_returnsRequiredError() {
        assertEquals("Short name is required", validateShortName(""))
    }

    @Test
    fun validateShortName_singleChar_returnsError() {
        assertEquals("Short name must be at least 2 characters", validateShortName("D"))
    }

    @Test
    fun validateShortName_twoChars_returnsNull() {
        assertNull(validateShortName("DP"))
    }

    @Test
    fun validateShortName_withSpace_returnsNull() {
        assertNull(validateShortName("DPS"))
        assertNull(validateShortName("D PS"))
    }

    @Test
    fun validateShortName_withDigits_returnsError() {
        assertEquals("Short name must contain only alphabetic characters", validateShortName("DP1"))
    }

    @Test
    fun validateShortName_withSpecialChars_returnsError() {
        assertEquals("Short name must contain only alphabetic characters", validateShortName("D-S"))
        assertEquals("Short name must contain only alphabetic characters", validateShortName("D.S"))
        assertEquals("Short name must contain only alphabetic characters", validateShortName("D'S"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validateAffiliationNumber
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun validateAffiliationNumber_blank_returnsNull() {
        assertNull(validateAffiliationNumber(""))
        assertNull(validateAffiliationNumber("   "))
    }

    @Test
    fun validateAffiliationNumber_validAlphanumeric_returnsNull() {
        assertNull(validateAffiliationNumber("1234567"))
        assertNull(validateAffiliationNumber("ABC123"))
        assertNull(validateAffiliationNumber("aff123"))
    }

    @Test
    fun validateAffiliationNumber_tooShort_returnsError() {
        assertEquals("Affiliation number must be at least 3 characters", validateAffiliationNumber("AB"))
    }

    @Test
    fun validateAffiliationNumber_exactly3Chars_returnsNull() {
        assertNull(validateAffiliationNumber("ABC"))
    }

    @Test
    fun validateAffiliationNumber_tooLong_returnsError() {
        assertEquals("Affiliation number must be 30 characters or fewer", validateAffiliationNumber("a".repeat(31)))
    }

    @Test
    fun validateAffiliationNumber_exactly30Chars_returnsNull() {
        assertNull(validateAffiliationNumber("a".repeat(30)))
    }

    @Test
    fun validateAffiliationNumber_withSpecialChars_returnsError() {
        assertEquals("Affiliation number must contain only letters and digits", validateAffiliationNumber("ABC-123"))
        assertEquals("Affiliation number must contain only letters and digits", validateAffiliationNumber("ABC@123"))
    }

    @Test
    fun validateAffiliationNumber_withSpaces_returnsError() {
        assertEquals("Affiliation number must contain only letters and digits", validateAffiliationNumber("ABC 123"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validatePrincipalName
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun validatePrincipalName_blank_returnsRequiredError() {
        assertEquals("Principal name is required", validatePrincipalName(""))
    }

    @Test
    fun validatePrincipalName_validName_returnsNull() {
        assertNull(validatePrincipalName("Dr. Rajesh Sharma"))
        assertNull(validatePrincipalName("Sister Mary"))
        assertNull(validatePrincipalName("Prof. Iyer-Kumar"))
    }

    @Test
    fun validatePrincipalName_singleChar_returnsNull() {
        // No min length check for principal name
        assertNull(validatePrincipalName("A"))
    }

    @Test
    fun validatePrincipalName_withDigits_returnsError() {
        assertEquals("Name must contain only letters", validatePrincipalName("Rajesh123"))
    }

    @Test
    fun validatePrincipalName_withSpecialChars_returnsError() {
        assertEquals("Name must contain only letters", validatePrincipalName("Rajesh@"))
        assertEquals("Name must contain only letters", validatePrincipalName("Raj#"))
    }

    @Test
    fun validatePrincipalName_withApostrophe_returnsError() {
        // Apostrophe is NOT allowed in principal name (unlike school name)
        assertEquals("Name must contain only letters", validatePrincipalName("O'Connor"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // validatePrincipalPhone
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun validatePrincipalPhone_blank_returnsRequiredError() {
        assertEquals("Principal phone is required", validatePrincipalPhone(""))
    }

    @Test
    fun validatePrincipalPhone_valid10Digits_returnsNull() {
        assertNull(validatePrincipalPhone("9876543210"))
        assertNull(validatePrincipalPhone("6123456789"))
    }

    @Test
    fun validatePrincipalPhone_wrongLength_returnsError() {
        assertEquals("Phone must be exactly 10 digits", validatePrincipalPhone("987654321"))
        assertEquals("Phone must be exactly 10 digits", validatePrincipalPhone("98765432101"))
    }

    @Test
    fun validatePrincipalPhone_withLetters_returnsError() {
        assertEquals("Phone must contain only digits", validatePrincipalPhone("98765432ab"))
    }

    @Test
    fun validatePrincipalPhone_startsBelow6_returnsError() {
        assertEquals("Enter a valid Indian mobile number", validatePrincipalPhone("5123456789"))
    }

    // ════════════════════════════════════════════════════════════════════════
    // emailPattern regex
    // ════════════════════════════════════════════════════════════════════════

    @Test
    fun emailPattern_matchesStandardEmail() {
        assert(emailPattern.matches("user@example.com"))
        assert(emailPattern.matches("a@b.co"))
    }

    @Test
    fun emailPattern_doesNotMatchInvalidEmail() {
        assert(!emailPattern.matches("notanemail"))
        assert(!emailPattern.matches("@example.com"))
        assert(!emailPattern.matches("user@"))
    }
}
