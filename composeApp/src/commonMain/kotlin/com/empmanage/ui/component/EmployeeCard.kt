package com.empmanage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.empmanage.domain.model.Employee
import com.empmanage.ui.util.CurrencyFormatter
import com.empmanage.ui.util.DateFormatter

@OptIn(ExperimentalComposeUiApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun EmployeeCard(
    employee: Employee,
    searchQuery: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onViewDetails,
                onLongClick = { showMenu = true }
            )
    ) {
        Box {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(employee.initials, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    HighlightedText(
                        text = employee.fullName,
                        query = searchQuery,
                        modifier = Modifier,
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssistChip(onClick = {}, label = { Text(employee.department.displayName) })
                        SuggestionChip(onClick = {}, label = { Text(employee.employmentType.displayName) })
                    }
                    Row(
                        modifier = Modifier.padding(top = 6.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            CurrencyFormatter.format(employee.salary),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            DateFormatter.formatDate(employee.joiningDateMillis),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (employee.isActive) Color(0xFF4CAF50) else Color(0xFF9E9E9E))
                    )
                    Text(
                        text = if (employee.isActive) "Active" else "Inactive",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text("Edit") }, onClick = { showMenu = false; onEdit() })
                DropdownMenuItem(text = { Text("Delete") }, onClick = { showMenu = false; onDelete() })
                DropdownMenuItem(text = { Text("View Details") }, onClick = { showMenu = false; onViewDetails() })
            }
        }
    }
}
