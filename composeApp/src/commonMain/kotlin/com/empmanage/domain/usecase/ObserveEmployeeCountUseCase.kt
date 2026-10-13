package com.empmanage.domain.usecase

import com.empmanage.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow

/** Total employee count, used to tell whether a page still [hasMore] beyond what's loaded. */
class ObserveEmployeeCountUseCase(private val repository: EmployeeRepository) {
    operator fun invoke(): Flow<Int> = repository.observeCount()
}
