package com.empmanage.domain.model

/**
 * Document metadata stored alongside a resume file path.
 */
data class ResumeMetadata(
    val fileName: String,
    val fileSizeBytes: Long,
    val mimeType: String
)

/**
 * Domain representation of an employee record. `normalizedPhone` is the
 * cleaned (digits-only, country/trunk-prefix-stripped) form of [phoneNumber]
 * used for O(1) duplicate lookups via [com.empmanage.domain.util.DuplicateIndex].
 */
data class Employee(
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val normalizedPhone: String,
    val address: String,
    val gender: Gender,
    val department: Department,
    val skills: List<Skill>,
    val employmentType: EmploymentType,
    val isActive: Boolean = true,
    val joiningDateMillis: Long,
    val salary: Double,
    val profileImagePath: String? = null,
    val resumePath: String? = null,
    val resumeMetadata: ResumeMetadata? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
) {
    val initials: String
        get() = fullName.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString(separator = "")
}
