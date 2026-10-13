package com.empmanage.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empmanage.domain.model.Department
import com.empmanage.domain.model.Employee
import com.empmanage.domain.model.EmploymentType
import com.empmanage.domain.model.Gender
import com.empmanage.domain.model.ResumeMetadata
import com.empmanage.domain.model.Skill
import com.empmanage.domain.repository.EmployeeRepository
import com.empmanage.domain.usecase.AddEmployeeUseCase
import com.empmanage.domain.usecase.UpdateEmployeeUseCase
import com.empmanage.domain.util.DuplicateIndex
import com.empmanage.domain.util.FormValidators
import com.empmanage.domain.util.NameIndex
import com.empmanage.domain.util.PhoneNormalizer
import com.empmanage.domain.util.ValidationResult
import com.empmanage.platform.PickedDocument
import com.empmanage.platform.currentTimeMillis
import com.empmanage.presentation.state.EmployeeFormUiState
import com.empmanage.presentation.state.FieldState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeFormViewModel(
    private val repository: EmployeeRepository,
    private val duplicateIndex: DuplicateIndex,
    private val nameIndex: NameIndex,
    private val addEmployeeUseCase: AddEmployeeUseCase,
    private val updateEmployeeUseCase: UpdateEmployeeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeFormUiState())
    val uiState: StateFlow<EmployeeFormUiState> = _uiState.asStateFlow()

    private val _snackbarEvents = Channel<String>(Channel.BUFFERED)
    val snackbarEvents: Flow<String> = _snackbarEvents.receiveAsFlow()

    private var originalEmployee: Employee? = null

    fun loadForEdit(employeeId: Long) {
        viewModelScope.launch {
            val employee = repository.getById(employeeId) ?: return@launch
            originalEmployee = employee
            _uiState.value = EmployeeFormUiState(
                employeeId = employee.id,
                isEditMode = true,
                fullName = FieldState(employee.fullName, error = null),
                email = FieldState(employee.email, error = null),
                phone = FieldState(employee.phoneNumber, error = null),
                address = FieldState(employee.address, error = null),
                gender = FieldState(employee.gender, error = null),
                department = FieldState(employee.department, error = null),
                skills = FieldState(employee.skills.toSet(), error = null),
                employmentType = FieldState(employee.employmentType, error = null),
                isActive = employee.isActive,
                joiningDate = FieldState(employee.joiningDateMillis, error = null),
                salary = FieldState(employee.salary.toString(), error = null),
                profileImagePath = employee.profileImagePath,
                resumePath = employee.resumePath,
                resumeMetadata = employee.resumeMetadata
            )
        }
    }

    /**
     * Without Jetpack Navigation's per-destination ViewModelStore, this
     * manual sealed-class navigation reuses the same Activity-scoped
     * EmployeeFormViewModel instance every time the Form screen is shown —
     * confirmed via a real device run: opening "Add", cancelling, then
     * opening "Add" again left the previous attempt's half-filled fields
     * in place. Called from EmployeeFormScreen whenever employeeId is null.
     */
    fun resetForNewEntry() {
        originalEmployee = null
        _uiState.value = EmployeeFormUiState()
    }

    fun onProfileImageSelected(filePath: String) {
        _uiState.update { it.copy(profileImagePath = filePath) }
    }

    fun onProfileImagePickerPermissionDenied() {
        viewModelScope.launch { _snackbarEvents.send("Permission denied — enable it in Settings to use this feature") }
    }

    fun onDocumentPicked(document: PickedDocument) {
        val sizeError = FormValidators.validateResumeSize(document.fileSizeBytes)
        if (sizeError is ValidationResult.Invalid) {
            viewModelScope.launch { _snackbarEvents.send(sizeError.message) }
            return
        }
        _uiState.update {
            it.copy(
                resumePath = document.filePath,
                resumeMetadata = ResumeMetadata(document.fileName, document.fileSizeBytes, document.mimeType)
            )
        }
    }

    fun onResumeRemoved() {
        _uiState.update { it.copy(resumePath = null, resumeMetadata = null) }
    }

    fun onFullNameChanged(value: String) {
        _uiState.update { it.copy(fullName = it.fullName.copy(value = value, error = computeFullNameError(value))) }
    }

    fun onFullNameBlur() {
        _uiState.update { it.copy(fullName = it.fullName.copy(touched = true)) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = it.email.copy(value = value, error = computeEmailError(value))) }
    }

    fun onEmailBlur() {
        _uiState.update { it.copy(email = it.email.copy(touched = true)) }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { it.copy(phone = it.phone.copy(value = value, error = computePhoneError(value))) }
    }

    fun onPhoneBlur() {
        _uiState.update { it.copy(phone = it.phone.copy(touched = true)) }
    }

    fun onAddressChanged(value: String) {
        val error = (FormValidators.validateAddress(value) as? ValidationResult.Invalid)?.message
        _uiState.update { it.copy(address = it.address.copy(value = value, error = error)) }
    }

    fun onAddressBlur() {
        _uiState.update { it.copy(address = it.address.copy(touched = true)) }
    }

    fun onGenderSelected(gender: Gender) {
        _uiState.update { it.copy(gender = FieldState(gender, error = null, touched = true)) }
    }

    fun onDepartmentSelected(department: Department) {
        _uiState.update { it.copy(department = FieldState(department, error = null, touched = true)) }
    }

    fun onSkillToggled(skill: Skill) {
        _uiState.update {
            val newSelection = if (skill in it.skills.value) it.skills.value - skill else it.skills.value + skill
            val error = (FormValidators.validateSkills(newSelection.toList()) as? ValidationResult.Invalid)?.message
            it.copy(skills = FieldState(newSelection, error = error, touched = true))
        }
    }

    fun onEmploymentTypeSelected(employmentType: EmploymentType) {
        _uiState.update { it.copy(employmentType = FieldState(employmentType, error = null, touched = true)) }
    }

    fun onActiveToggled(active: Boolean) {
        _uiState.update { it.copy(isActive = active) }
    }

    fun onJoiningDateSelected(epochMillis: Long) {
        val error = (FormValidators.validateJoiningDate(epochMillis, currentTimeMillis()) as? ValidationResult.Invalid)?.message
        _uiState.update { it.copy(joiningDate = FieldState(epochMillis, error = error, touched = true)) }
    }

    fun onSalaryChanged(value: String) {
        val error = (FormValidators.validateSalary(value) as? ValidationResult.Invalid)?.message
        _uiState.update { it.copy(salary = it.salary.copy(value = value, error = error)) }
    }

    fun onSalaryBlur() {
        _uiState.update { it.copy(salary = it.salary.copy(touched = true)) }
    }

    private fun computeFullNameError(value: String): String? {
        val result = FormValidators.validateFullName(value) { candidate ->
            nameIndex.contains(candidate, excluding = originalEmployee?.fullName)
        }
        return (result as? ValidationResult.Invalid)?.message
    }

    private fun computeEmailError(value: String): String? {
        val formatResult = FormValidators.validateEmail(value)
        if (formatResult is ValidationResult.Invalid) return formatResult.message
        val isDuplicate = duplicateIndex.isEmailDuplicate(value.trim().lowercase(), excludingEmail = originalEmployee?.email)
        return if (isDuplicate) "An employee with this email already exists" else null
    }

    private fun computePhoneError(value: String): String? {
        val formatResult = FormValidators.validatePhone(value)
        if (formatResult is ValidationResult.Invalid) return formatResult.message
        val normalized = PhoneNormalizer.normalize(value)
        val isDuplicate = duplicateIndex.isPhoneDuplicate(normalized, excludingPhone = originalEmployee?.normalizedPhone)
        return if (isDuplicate) "An employee with this phone number already exists" else null
    }

    fun submit(onSaved: () -> Unit) {
        val state = _uiState.value
        if (!state.isSubmitEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val normalizedPhone = PhoneNormalizer.normalize(state.phone.value)
            val now = currentTimeMillis()
            val existing = originalEmployee

            val employee = Employee(
                id = state.employeeId ?: 0,
                fullName = state.fullName.value.trim(),
                email = state.email.value.trim().lowercase(),
                phoneNumber = normalizedPhone,
                normalizedPhone = normalizedPhone,
                address = state.address.value.trim(),
                gender = requireNotNull(state.gender.value),
                department = requireNotNull(state.department.value),
                skills = state.skills.value.toList(),
                employmentType = requireNotNull(state.employmentType.value),
                isActive = state.isActive,
                joiningDateMillis = requireNotNull(state.joiningDate.value),
                salary = state.salary.value.toDouble(),
                profileImagePath = state.profileImagePath,
                resumePath = state.resumePath,
                resumeMetadata = state.resumeMetadata,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )

            if (existing == null) {
                addEmployeeUseCase(employee)
            } else {
                updateEmployeeUseCase(existing, employee)
            }

            _uiState.update { it.copy(isSaving = false, saveCompleted = true) }
            onSaved()
        }
    }
}
