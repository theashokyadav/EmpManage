package com.empmanage.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.empmanage.domain.model.Department
import com.empmanage.domain.model.EmploymentType
import com.empmanage.domain.model.Gender
import com.empmanage.domain.model.Skill
import com.empmanage.presentation.viewmodel.EmployeeFormViewModel
import com.empmanage.ui.component.DatePickerField
import com.empmanage.ui.component.DropdownField
import com.empmanage.ui.component.ProfileAvatar
import com.empmanage.ui.component.RadioGroupField
import com.empmanage.ui.component.SkillsSelector
import com.empmanage.ui.component.ValidatedTextField
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeFormScreen(
    employeeId: Long? = null,
    onSaved: () -> Unit = {},
    onCancel: () -> Unit = {},
    viewModel: EmployeeFormViewModel = koinViewModel()
) {
    LaunchedEffect(employeeId) {
        if (employeeId != null) viewModel.loadForEdit(employeeId)
    }

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Edit Employee" else "Add Employee") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileAvatar(
                imagePath = state.profileImagePath,
                initials = state.fullName.value.trim().take(2).uppercase().ifBlank { "?" },
                onClick = { /* wired in Phase 4: camera/gallery bottom sheet */ }
            )

            ValidatedTextField(
                value = state.fullName.value,
                onValueChange = viewModel::onFullNameChanged,
                onBlur = viewModel::onFullNameBlur,
                label = "Full Name",
                error = state.fullName.error,
                touched = state.fullName.touched
            )

            ValidatedTextField(
                value = state.email.value,
                onValueChange = viewModel::onEmailChanged,
                onBlur = viewModel::onEmailBlur,
                label = "Email",
                error = state.email.error,
                touched = state.email.touched,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            ValidatedTextField(
                value = state.phone.value,
                onValueChange = viewModel::onPhoneChanged,
                onBlur = viewModel::onPhoneBlur,
                label = "Phone Number",
                error = state.phone.error,
                touched = state.phone.touched,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            ValidatedTextField(
                value = state.address.value,
                onValueChange = viewModel::onAddressChanged,
                onBlur = viewModel::onAddressBlur,
                label = "Address",
                error = state.address.error,
                touched = state.address.touched,
                singleLine = false,
                minLines = 4
            )

            RadioGroupField(
                label = "Gender",
                options = Gender.entries,
                optionLabel = { it.displayName },
                selected = state.gender.value,
                onSelect = viewModel::onGenderSelected,
                error = state.gender.error,
                touched = state.gender.touched
            )

            DropdownField(
                label = "Department",
                options = Department.entries,
                optionLabel = { it.displayName },
                selected = state.department.value,
                onSelect = viewModel::onDepartmentSelected,
                error = state.department.error,
                touched = state.department.touched
            )

            SkillsSelector(
                allSkills = Skill.entries,
                selected = state.skills.value,
                onToggle = viewModel::onSkillToggled,
                error = state.skills.error,
                touched = state.skills.touched
            )

            DropdownField(
                label = "Employment Type",
                options = EmploymentType.entries,
                optionLabel = { it.displayName },
                selected = state.employmentType.value,
                onSelect = viewModel::onEmploymentTypeSelected,
                error = state.employmentType.error,
                touched = state.employmentType.touched
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Active")
                Switch(checked = state.isActive, onCheckedChange = viewModel::onActiveToggled)
            }

            DatePickerField(
                label = "Joining Date",
                selectedMillis = state.joiningDate.value,
                onDateSelected = viewModel::onJoiningDateSelected,
                error = state.joiningDate.error,
                touched = state.joiningDate.touched
            )

            ValidatedTextField(
                value = state.salary.value,
                onValueChange = viewModel::onSalaryChanged,
                onBlur = viewModel::onSalaryBlur,
                label = "Salary",
                error = state.salary.error,
                touched = state.salary.touched,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingText = "$"
            )

            Button(
                onClick = { viewModel.submit(onSaved) },
                enabled = state.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isEditMode) "Update" else "Submit")
            }
        }
    }
}
