package com.empmanage.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.empmanage.domain.model.Employee
import com.empmanage.presentation.state.SortOption
import com.empmanage.presentation.viewmodel.EmployeeListViewModel
import com.empmanage.ui.component.EmployeeCard
import com.empmanage.ui.component.EmptyState
import com.empmanage.ui.component.FilterBottomSheet
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.compose.viewmodel.koinViewModel

private const val UNDO_WINDOW_MILLIS = 5000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeListScreen(
    onAddEmployee: () -> Unit,
    onEditEmployee: (Long) -> Unit,
    onViewDetails: (Long) -> Unit,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    viewModel: EmployeeListViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var employeeToDelete by remember { mutableStateOf<Employee?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(viewModel) {
        viewModel.undoEvents.collect {
            val result = withTimeoutOrNull(UNDO_WINDOW_MILLIS) {
                snackbarHostState.showSnackbar(
                    message = "Employee deleted",
                    actionLabel = "Undo",
                    duration = SnackbarDuration.Indefinite
                )
            }
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.onUndoDelete()
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            filters = state.filters,
            onDepartmentToggled = viewModel::onDepartmentFilterToggled,
            onEmploymentTypeToggled = viewModel::onEmploymentTypeFilterToggled,
            onStatusSelected = viewModel::onStatusFilterSelected,
            onClearAll = viewModel::onClearAllFilters,
            onDismiss = { showFilterSheet = false }
        )
    }

    employeeToDelete?.let { employee ->
        AlertDialog(
            onDismissRequest = { employeeToDelete = null },
            title = { Text("Delete employee?") },
            text = { Text("This will delete ${employee.fullName}. You can undo this from the snackbar.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDeleteConfirmed(employee)
                    employeeToDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { employeeToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employees") },
                actions = {
                    IconButton(onClick = onToggleDarkTheme) {
                        Icon(
                            if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle dark mode"
                        )
                    }
                    BadgedBox(
                        badge = {
                            if (state.filters.activeCount > 0) {
                                Badge { Text(state.filters.activeCount.toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    }
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    leadingIcon = {
                                        if (option == state.sortOption) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    },
                                    onClick = {
                                        viewModel.onSortOptionSelected(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = onAddEmployee) {
                    Icon(Icons.Default.Add, contentDescription = "Add employee")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All Employees") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Top Earners") }
                )
            }

            if (selectedTab == 1) {
                TopEarnersSection(modifier = Modifier.fillMaxSize())
            } else {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text("Search by name, email, or department") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )

                if (state.isEmpty) {
                    EmptyState(
                        message = if (state.searchQuery.isBlank() && state.filters.activeCount == 0) {
                            "No employees yet. Tap + to add one."
                        } else {
                            "No employees match your search or filters."
                        }
                    )
                } else {
                    val listState = rememberLazyListState()

                    // Pages of 20 (bonus #4): widen the visible window as the user nears the bottom.
                    val shouldLoadMore by remember {
                        derivedStateOf {
                            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            state.hasMore && lastVisible >= state.employees.size - 3
                        }
                    }
                    LaunchedEffect(shouldLoadMore) {
                        if (shouldLoadMore) viewModel.onLoadMore()
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        items(state.employees, key = { it.id }) { employee ->
                            EmployeeCard(
                                employee = employee,
                                searchQuery = state.searchQuery,
                                onEdit = { onEditEmployee(employee.id) },
                                onDelete = { employeeToDelete = employee },
                                onViewDetails = { onViewDetails(employee.id) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        if (state.hasMore) {
                            item(key = "loading-more") {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
