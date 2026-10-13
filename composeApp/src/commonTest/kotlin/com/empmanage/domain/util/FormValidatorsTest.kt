package com.empmanage.domain.util

import kotlin.test.Test
import kotlin.test.assertTrue

class FormValidatorsTest {

    @Test
    fun fullNameShorterThanThreeCharsIsInvalid() {
        assertTrue(FormValidators.validateFullName("Al", emptySet()) is ValidationResult.Invalid)
    }

    @Test
    fun fullNameIsTrimmedBeforeLengthCheck() {
        assertTrue(FormValidators.validateFullName("  Al  ", emptySet()) is ValidationResult.Invalid)
    }

    @Test
    fun duplicateFullNameIsRejected() {
        val existing = setOf("john doe")
        assertTrue(FormValidators.validateFullName("John Doe", existing) is ValidationResult.Invalid)
    }

    @Test
    fun editingSameEmployeeNameIsNotTreatedAsDuplicate() {
        val existing = setOf("john doe")
        val result = FormValidators.validateFullName("John Doe", existing, excluding = "John Doe")
        assertTrue(result.isValid)
    }

    @Test
    fun validEmailPasses() {
        assertTrue(FormValidators.validateEmail("jane@example.com").isValid)
    }

    @Test
    fun malformedEmailFails() {
        assertTrue(FormValidators.validateEmail("jane@@example") is ValidationResult.Invalid)
    }

    @Test
    fun phoneWithNonDigitsAfterNormalizationFails() {
        assertTrue(FormValidators.validatePhone("98765abcde").let { it is ValidationResult.Invalid })
    }

    @Test
    fun phoneWithWrongDigitCountFails() {
        assertTrue(FormValidators.validatePhone("12345").let { it is ValidationResult.Invalid })
    }

    @Test
    fun tenDigitPhonePasses() {
        assertTrue(FormValidators.validatePhone("+91 98765-43210").isValid)
    }

    @Test
    fun addressShorterThanSixCharsFails() {
        assertTrue(FormValidators.validateAddress("Flat").let { it is ValidationResult.Invalid })
    }

    @Test
    fun skillsListMustHaveAtLeastOneEntry() {
        assertTrue(FormValidators.validateSkills(emptyList()).let { it is ValidationResult.Invalid })
    }

    @Test
    fun negativeSalaryFails() {
        assertTrue(FormValidators.validateSalary("-500").let { it is ValidationResult.Invalid })
    }

    @Test
    fun nonNumericSalaryFails() {
        assertTrue(FormValidators.validateSalary("abc").let { it is ValidationResult.Invalid })
    }

    @Test
    fun futureJoiningDateFails() {
        val now = 1_000_000L
        val future = now + 86_400_000L
        assertTrue(FormValidators.validateJoiningDate(future, now).let { it is ValidationResult.Invalid })
    }

    @Test
    fun pastOrTodayJoiningDatePasses() {
        val now = 1_000_000L
        assertTrue(FormValidators.validateJoiningDate(now, now).isValid)
    }
}
