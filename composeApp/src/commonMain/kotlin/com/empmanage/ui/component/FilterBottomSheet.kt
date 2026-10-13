package com.empmanage.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.empmanage.domain.model.Department
import com.empmanage.domain.model.EmploymentType
import com.empmanage.presentation.state.ActiveStatusFilter
import com.empmanage.presentation.state.FilterState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filters: FilterState,
    onDepartmentToggled: (Department) -> Unit,
    onEmploymentTypeToggled: (EmploymentType) -> Unit,
    onStatusSelected: (ActiveStatusFilter) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Filters", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onClearAll) { Text("Clear All") }
            }

            Text("Status", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 16.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                ActiveStatusFilter.entries.forEach { status ->
                    FilterChip(
                        selected = filters.status == status,
                        onClick = { onStatusSelected(status) },
                        label = { Text(status.label) }
                    )
                }
            }

            Text("Department", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 16.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Department.entries.forEach { department ->
                    FilterChip(
                        selected = department in filters.departments,
                        onClick = { onDepartmentToggled(department) },
                        label = { Text(department.displayName) }
                    )
                }
            }

            Text("Employment Type", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 16.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            ) {
                EmploymentType.entries.forEach { employmentType ->
                    FilterChip(
                        selected = employmentType in filters.employmentTypes,
                        onClick = { onEmploymentTypeToggled(employmentType) },
                        label = { Text(employmentType.displayName) }
                    )
                }
            }
        }
    }
}
