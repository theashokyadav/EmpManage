package com.empmanage.domain.util

import com.empmanage.domain.model.Department
import com.empmanage.domain.model.EmploymentType
import com.empmanage.domain.model.Gender
import com.empmanage.domain.model.Skill
import com.empmanage.domain.util.ValidationResult.Invalid
import com.empmanage.domain.util.ValidationResult.Valid

/**
 * Pure, framework-free validation rules for every Employee Form field.
 * Each function runs in O(length of input) time and O(1) extra space
 * (aside from the regex match itself), so it's cheap to call on every
 * blur event as the spec requires.
 */
object FormValidators {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    /**
     * [isDuplicate] is supplied by the caller (typically backed by a
     * [NameIndex]) so this validator stays decoupled from any particular
     * collection/index implementation.
     */
    fun validateFullName(name: String, isDuplicate: (trimmedName: String) -> Boolean): ValidationResult {
        val trimmed = name.trim()
        if (trimmed.length < 3) return Invalid("Full name must be at least 3 characters")
        if (isDuplicate(trimmed)) return Invalid("An employee with this name already exists")
        return Valid
    }

    fun validateEmail(email: String): ValidationResult {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) return Invalid("Email is required")
        if (!EMAIL_REGEX.matches(trimmed)) return Invalid("Enter a valid email address")
        return Valid
    }

    fun validatePhone(rawPhone: String): ValidationResult {
        if (rawPhone.isBlank()) return Invalid("Phone number is required")
        val normalized = PhoneNormalizer.normalize(rawPhone)
        if (!normalized.all { it.isDigit() }) return Invalid("Phone number must contain digits only")
        if (normalized.length != 10) return Invalid("Phone number must be 10 digits")
        return Valid
    }

    fun validateAddress(address: String): ValidationResult {
        val trimmed = address.trim()
        if (trimmed.isEmpty()) return Invalid("Address is required")
        if (trimmed.length < 6) return Invalid("Address must be at least 6 characters")
        return Valid
    }

    fun validateSkills(skills: List<Skill>): ValidationResult {
        if (skills.isEmpty()) return Invalid("Select at least one skill")
        return Valid
    }

    fun validateGender(gender: Gender?): ValidationResult =
        if (gender == null) Invalid("Select a gender") else Valid

    fun validateDepartment(department: Department?): ValidationResult =
        if (department == null) Invalid("Select a department") else Valid

    fun validateEmploymentType(employmentType: EmploymentType?): ValidationResult =
        if (employmentType == null) Invalid("Select an employment type") else Valid

    fun validateSalary(rawSalary: String): ValidationResult {
        if (rawSalary.isBlank()) return Invalid("Salary is required")
        val value = rawSalary.toDoubleOrNull() ?: return Invalid("Enter a valid number")
        if (value <= 0) return Invalid("Salary must be a positive number")
        return Valid
    }

    fun validateJoiningDate(selectedEpochMillis: Long?, nowEpochMillis: Long): ValidationResult {
        if (selectedEpochMillis == null) return Invalid("Joining date is required")
        if (selectedEpochMillis > nowEpochMillis) return Invalid("Joining date cannot be in the future")
        return Valid
    }
}
