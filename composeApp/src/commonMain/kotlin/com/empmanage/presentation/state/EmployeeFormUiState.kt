package com.empmanage.presentation.state

import com.empmanage.domain.model.Department
import com.empmanage.domain.model.EmploymentType
import com.empmanage.domain.model.Gender
import com.empmanage.domain.model.Skill
import com.empmanage.domain.util.FormValidators
import com.empmanage.domain.util.ValidationResult
import com.empmanage.platform.currentTimeMillis

private fun errorMessageOf(result: ValidationResult): String? = (result as? ValidationResult.Invalid)?.message

data class EmployeeFormUiState(
    val employeeId: Long? = null,
    val isEditMode: Boolean = false,
    val fullName: FieldState<String> = FieldState("", error = errorMessageOf(FormValidators.validateFullName("") { false } )),
    val email: FieldState<String> = FieldState("", error = errorMessageOf(FormValidators.validateEmail(""))),
    val phone: FieldState<String> = FieldState("", error = errorMessageOf(FormValidators.validatePhone(""))),
    val address: FieldState<String> = FieldState("", error = errorMessageOf(FormValidators.validateAddress(""))),
    val gender: FieldState<Gender?> = FieldState(null, error = errorMessageOf(FormValidators.validateGender(null))),
    val department: FieldState<Department?> = FieldState(null, error = errorMessageOf(FormValidators.validateDepartment(null))),
    val skills: FieldState<Set<Skill>> = FieldState(emptySet(), error = errorMessageOf(FormValidators.validateSkills(emptyList()))),
    val employmentType: FieldState<EmploymentType?> = FieldState(null, error = errorMessageOf(FormValidators.validateEmploymentType(null))),
    val isActive: Boolean = true,
    val joiningDate: FieldState<Long?> = FieldState(null, error = errorMessageOf(FormValidators.validateJoiningDate(null, currentTimeMillis()))),
    val salary: FieldState<String> = FieldState("", error = errorMessageOf(FormValidators.validateSalary(""))),
    val profileImagePath: String? = null,
    val resumePath: String? = null,
    val isSaving: Boolean = false,
    val saveCompleted: Boolean = false
) {
    /**
     * Recomputed from each field's live [FieldState.error] rather than
     * [FieldState.touched], so the Submit button enables the instant the
     * whole form becomes valid — even for fields the user hasn't blurred.
     */
    val isSubmitEnabled: Boolean
        get() = !isSaving &&
            fullName.error == null &&
            email.error == null &&
            phone.error == null &&
            address.error == null &&
            gender.error == null &&
            department.error == null &&
            skills.error == null &&
            employmentType.error == null &&
            joiningDate.error == null &&
            salary.error == null
}
