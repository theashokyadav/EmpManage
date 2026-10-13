package com.empmanage.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.empmanage.presentation.viewmodel.TopEarnersViewModel
import com.empmanage.ui.component.EmptyState
import com.empmanage.ui.component.TopEarnerCard
import org.koin.compose.viewmodel.koinViewModel

/**
 * Top Earners tab: shows the top N highest-paid employees via the
 * min-heap-backed [com.empmanage.domain.usecase.TopNSalaryUseCase],
 * with a stepper (1-10) to change N reactively.
 */
@Composable
fun TopEarnersSection(
    modifier: Modifier = Modifier,
    viewModel: TopEarnersViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Top ${state.n} Earners", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.onNChanged(state.n - 1) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease N")
                }
                Text(state.n.toString(), style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { viewModel.onNChanged(state.n + 1) }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase N")
                }
            }
        }

        if (state.topEmployees.isEmpty()) {
            EmptyState(message = "No employees yet.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                itemsIndexed(state.topEmployees) { index, employee ->
                    TopEarnerCard(
                        rank = index + 1,
                        employee = employee,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
