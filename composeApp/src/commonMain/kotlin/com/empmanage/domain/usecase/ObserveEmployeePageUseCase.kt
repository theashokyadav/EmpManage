package com.empmanage.domain.usecase

import com.empmanage.domain.model.Employee
import com.empmanage.domain.model.SortOption
import com.empmanage.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow

/**
 * DB-level pagination (bonus #4): delegates to one of
 * [com.empmanage.data.local.db.EmployeeDao]'s `ORDER BY ... LIMIT :limit`
 * queries, reactively re-running as [limit] grows (EmployeeListViewModel
 * widens it on scroll) or [sort] changes.
 */
class ObserveEmployeePageUseCase(private val repository: EmployeeRepository) {
    operator fun invoke(sort: SortOption, limit: Int): Flow<List<Employee>> =
        repository.observeLimited(sort, limit)
}
