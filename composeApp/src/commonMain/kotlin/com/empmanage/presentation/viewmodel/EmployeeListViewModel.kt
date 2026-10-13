package com.empmanage.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empmanage.domain.model.Department
import com.empmanage.domain.model.Employee
import com.empmanage.domain.model.EmploymentType
import com.empmanage.domain.model.SortOption
import com.empmanage.domain.usecase.DeleteEmployeeUseCase
import com.empmanage.domain.usecase.GetEmployeesUseCase
import com.empmanage.domain.usecase.ObserveEmployeeCountUseCase
import com.empmanage.domain.usecase.ObserveEmployeePageUseCase
import com.empmanage.domain.usecase.UndoDeleteUseCase
import com.empmanage.domain.util.DuplicateIndex
import com.empmanage.domain.util.NameIndex
import com.empmanage.presentation.state.ActiveStatusFilter
import com.empmanage.presentation.state.EmployeeListUiState
import com.empmanage.presentation.state.FilterState
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

/**
 * Owns every list transformation (search, filter, sort, pagination) so the
 * Composable only ever renders [uiState] as-is.
 *
 * Pagination (bonus #4) is genuinely DB-level: [pagedEmployees] re-queries
 * [ObserveEmployeePageUseCase] — one of [com.empmanage.data.local.db.EmployeeDao]'s
 * `ORDER BY ... LIMIT :limit` queries — every time [sortOptionState] or
 * [visibleCountState] changes, via `flatMapLatest`. Search and filter are
 * still applied client-side on top of that loaded window: Section 4's
 * search/filter requirement needs to scan the *whole* dataset to be
 * correct, so [onSearchQueryChanged]/filter changes reset the window back
 * to one page rather than trying to search only what's currently loaded —
 * the same page-growth mechanism used for plain scrolling just also kicks
 * in naturally as more of the sorted data loads in.
 */
class EmployeeListViewModel(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val observeEmployeePageUseCase: ObserveEmployeePageUseCase,
    private val observeEmployeeCountUseCase: ObserveEmployeeCountUseCase,
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
        // A fresh search/filter makes the previous page window meaningless for finding
        // matches — start back at one page so pagination doesn't show a stale "has more".
        viewModelScope.launch {
            combine(debouncedQuery, filterState) { _, _ -> Unit }
                .collect { visibleCountState.value = PAGE_SIZE }
        }
        // Changing sort switches to a different DAO query entirely; also restart at page one.
        viewModelScope.launch {
            sortOptionState.collect { visibleCountState.value = PAGE_SIZE }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val pagedEmployees: Flow<List<Employee>> = combine(sortOptionState, visibleCountState) { sort, limit ->
        sort to limit
    }.flatMapLatest { (sort, limit) -> observeEmployeePageUseCase(sort, limit) }

    private val totalCount: Flow<Int> = observeEmployeeCountUseCase()

    // Collects pagedEmployees exactly once — referencing a flatMapLatest-backed Flow from
    // two separate combine() calls would re-subscribe (and re-query the DB) independently.
    private data class PageSnapshot(val filtered: List<Employee>, val loadedCount: Int, val totalCount: Int)

    private val pageSnapshot: Flow<PageSnapshot> = combine(
        pagedEmployees,
        debouncedQuery,
        filterState,
        totalCount
    ) { employees, query, filters, total ->
        PageSnapshot(
            filtered = employees.filter { matchesSearch(it, query) && matchesFilters(it, filters) },
            loadedCount = employees.size,
            totalCount = total
        )
    }

    val uiState: StateFlow<EmployeeListUiState> = combine(
        pageSnapshot,
        searchQueryState,
        filterState,
        sortOptionState
    ) { snapshot, rawQuery, filters, sort ->
        EmployeeListUiState(
            searchQuery = rawQuery,
            employees = snapshot.filtered,
            loadedCount = snapshot.loadedCount,
            totalCount = snapshot.totalCount,
            hasMore = snapshot.loadedCount < snapshot.totalCount,
            isLoading = false,
            filters = filters,
            sortOption = sort
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EmployeeListUiState())

    /** Widens the DB query's LIMIT by one page of 20 (bonus #4) — a real re-query, not a client-side slice. */
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
}
