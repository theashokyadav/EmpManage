package com.empmanage.domain.usecase

import com.empmanage.domain.model.Employee
import com.empmanage.domain.repository.EmployeeRepository
import com.empmanage.domain.util.DuplicateIndex
import com.empmanage.domain.util.NameIndex

/**
 * Persists a new employee and keeps the in-memory [DuplicateIndex] and
 * [NameIndex] in sync so the very next form submission sees this record
 * without re-querying the database.
 */
class AddEmployeeUseCase(
    private val repository: EmployeeRepository,
    private val duplicateIndex: DuplicateIndex,
    private val nameIndex: NameIndex
) {
    suspend operator fun invoke(employee: Employee): Long {
        val id = repository.add(employee)
        duplicateIndex.add(employee.email, employee.normalizedPhone)
        nameIndex.add(employee.fullName)
        return id
    }
}
