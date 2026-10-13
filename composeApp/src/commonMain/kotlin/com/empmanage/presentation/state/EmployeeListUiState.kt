package com.empmanage.presentation.state

import com.empmanage.domain.model.Employee
import com.empmanage.domain.model.SortOption

data class EmployeeListUiState(
    val searchQuery: String = "",
    val employees: List<Employee> = emptyList(),
    val loadedCount: Int = 0,
    val totalCount: Int = 0,
    val hasMore: Boolean = false,
    val isLoading: Boolean = true,
    val filters: FilterState = FilterState(),
    val sortOption: SortOption = SortOption.NAME_ASC
) {
    val isEmpty: Boolean get() = !isLoading && employees.isEmpty()
}
