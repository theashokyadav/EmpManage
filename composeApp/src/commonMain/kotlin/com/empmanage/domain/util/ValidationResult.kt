package com.empmanage.domain.util

/**
 * Outcome of validating a single form field. Carries the user-facing error
 * message so the Composable can render it directly beneath the field.
 */
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()

    val isValid: Boolean get() = this is Valid
}
