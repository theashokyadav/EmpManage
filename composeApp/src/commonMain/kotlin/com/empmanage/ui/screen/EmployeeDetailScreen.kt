package com.empmanage.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.empmanage.presentation.viewmodel.EmployeeDetailViewModel
import com.empmanage.ui.component.ProfileAvatar
import com.empmanage.ui.util.CurrencyFormatter
import com.empmanage.ui.util.DateFormatter
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    employeeId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: EmployeeDetailViewModel = koinViewModel()
) {
    LaunchedEffect(employeeId) { viewModel.load(employeeId) }
    val employee by viewModel.employee.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employee Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(employeeId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { padding ->
        val current = employee
        if (current == null) {
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileAvatar(imagePath = current.profileImagePath, initials = current.initials, onClick = {})

            DetailRow("Full Name", current.fullName)
            DetailRow("Email", current.email)
            DetailRow("Phone", current.phoneNumber)
            DetailRow("Address", current.address)
            DetailRow("Gender", current.gender.displayName)
            DetailRow("Department", current.department.displayName)
            DetailRow("Skills", current.skills.joinToString(", ") { it.displayName })
            DetailRow("Employment Type", current.employmentType.displayName)
            DetailRow("Status", if (current.isActive) "Active" else "Inactive")
            DetailRow("Joining Date", DateFormatter.formatDate(current.joiningDateMillis))
            DetailRow("Salary", CurrencyFormatter.format(current.salary))
            current.resumeMetadata?.let { resume ->
                DetailRow("Resume", resume.fileName)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyLarge)
    }
    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
}
