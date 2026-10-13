package com.empmanage.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empmanage.domain.model.Department
import com.empmanage.domain.model.Employee
import com.empmanage.domain.model.EmploymentType
import com.empmanage.domain.usecase.DeleteEmployeeUseCase
import com.empmanage.domain.usecase.GetEmployeesUseCase
import com.empmanage.domain.usecase.UndoDeleteUseCase
import com.empmanage.domain.util.DuplicateIndex
import com.empmanage.domain.util.NameIndex
import com.empmanage.presentation.state.ActiveStatusFilter
import com.empmanage.presentation.state.EmployeeListUiState
import com.empmanage.presentation.state.FilterState
import com.empmanage.presentation.state.SortOption
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

/**
 * Owns every list transformation (search, filter, sort) so the Composable
 * only ever renders [uiState] as-is. [combine] reactively recomputes the
 * visible list whenever the employees Flow, the debounced search query,
 * the filter selection, or the sort option changes.
 */
class EmployeeListViewModel(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val undoDeleteUseCase: UndoDeleteUseCase,
    private val duplicateIndex: DuplicateIndex,
    private val nameIndex: NameIndex
) : ViewModel() {

    init {
        // Populate the HashSet-backed indexes from the DB once on app start, per spec 6.1 —
        // this is the first screen the app shows, so it's the natural place to seed them.
        viewModelScope.launch {
            val employees = getEmployeesUseCase().first()
            duplicateIndex.populate(employees.map { it.email }, employees.map { it.normalizedPhone })
            nameIndex.populate(employees.map { it.fullName })
        }
    }

    private val searchQueryState = MutableStateFlow("")
    private val filterState = MutableStateFlow(FilterState())
    private val sortOptionState = MutableStateFlow(SortOption.NAME_ASC)
    private val visibleCountState = MutableStateFlow(PAGE_SIZE)

    private val _undoEvents = Channel<Unit>(Channel.BUFFERED)
    val undoEvents: Flow<Unit> = _undoEvents.receiveAsFlow()

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    private val debouncedQuery = searchQueryState.debounce(300).distinctUntilChanged()

    init {
        // A fresh search/filter/sort makes the previous page count meaningless —
        // start back at one page so pagination doesn't show a stale "has more".
        viewModelScope.launch {
            combine(debouncedQuery, filterState, sortOptionState) { _, _, _ -> Unit }
                .collect { visibleCountState.value = PAGE_SIZE }
        }
    }

    private val filteredAndSorted: Flow<List<Employee>> = combine(
        getEmployeesUseCase(),
        debouncedQuery,
        filterState,
        sortOptionState
    ) { employees, query, filters, sort ->
        applySort(employees.filter { matchesSearch(it, query) && matchesFilters(it, filters) }, sort)
    }

    val uiState: StateFlow<EmployeeListUiState> = combine(
        filteredAndSorted,
        searchQueryState,
        filterState,
        sortOptionState,
        visibleCountState
    ) { employees, rawQuery, filters, sort, visibleCount ->
        EmployeeListUiState(
            searchQuery = rawQuery,
            employees = employees.take(visibleCount),
            totalMatchCount = employees.size,
            hasMore = employees.size > visibleCount,
            isLoading = false,
            filters = filters,
            sortOption = sort
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EmployeeListUiState())

    /** Loads the next page of 20 (bonus #4) by widening the visible window over the already filtered/sorted list. */
    fun onLoadMore() {
        visibleCountState.update { it + PAGE_SIZE }
    }

    fun onSearchQueryChanged(query: String) {
        searchQueryState.value = query
    }

    fun onDepartmentFilterToggled(department: Department) {
        filterState.update {
            val updated = if (department in it.departments) it.departments - department else it.departments + department
            it.copy(departments = updated)
        }
    }

    fun onEmploymentTypeFilterToggled(employmentType: EmploymentType) {
        filterState.update {
            val updated = if (employmentType in it.employmentTypes) {
                it.employmentTypes - employmentType
            } else {
                it.employmentTypes + employmentType
            }
            it.copy(employmentTypes = updated)
        }
    }

    fun onStatusFilterSelected(status: ActiveStatusFilter) {
        filterState.update { it.copy(status = status) }
    }

    fun onClearAllFilters() {
        filterState.value = FilterState()
    }

    fun onSortOptionSelected(sort: SortOption) {
        sortOptionState.value = sort
    }

    fun onDeleteConfirmed(employee: Employee) {
        viewModelScope.launch {
            deleteEmployeeUseCase(employee)
            _undoEvents.send(Unit)
        }
    }

    fun onUndoDelete() {
        viewModelScope.launch { undoDeleteUseCase() }
    }

    private fun matchesSearch(employee: Employee, query: String): Boolean {
        if (query.isBlank()) return true
        val needle = query.trim().lowercase()
        return employee.fullName.lowercase().contains(needle) ||
            employee.email.lowercase().contains(needle) ||
            employee.department.displayName.lowercase().contains(needle)
    }

    private fun matchesFilters(employee: Employee, filters: FilterState): Boolean {
        val departmentMatch = filters.departments.isEmpty() || employee.department in filters.departments
        val statusMatch = when (filters.status) {
            ActiveStatusFilter.ALL -> true
            ActiveStatusFilter.ACTIVE -> employee.isActive
            ActiveStatusFilter.INACTIVE -> !employee.isActive
        }
        val employmentTypeMatch = filters.employmentTypes.isEmpty() || employee.employmentType in filters.employmentTypes
        return departmentMatch && statusMatch && employmentTypeMatch
    }

    private fun applySort(employees: List<Employee>, sort: SortOption): List<Employee> = when (sort) {
        SortOption.NAME_ASC -> employees.sortedBy { it.fullName.lowercase() }
        SortOption.NAME_DESC -> employees.sortedByDescending { it.fullName.lowercase() }
        SortOption.JOINING_NEWEST -> employees.sortedByDescending { it.joiningDateMillis }
        SortOption.JOINING_OLDEST -> employees.sortedBy { it.joiningDateMillis }
        SortOption.SALARY_HIGH -> employees.sortedByDescending { it.salary }
        SortOption.SALARY_LOW -> employees.sortedBy { it.salary }
    }
}
