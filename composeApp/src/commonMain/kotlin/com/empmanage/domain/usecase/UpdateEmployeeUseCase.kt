package com.empmanage.domain.usecase

import com.empmanage.domain.model.Employee
import com.empmanage.domain.repository.EmployeeRepository
import com.empmanage.domain.util.DuplicateIndex
import com.empmanage.domain.util.NameIndex

/**
 * Persists changes to an existing employee and re-syncs the in-memory
 * indexes: the [previous] record's email/phone/name are removed before
 * the [updated] record's are added, so edits don't leak stale entries.
 */
class UpdateEmployeeUseCase(
    private val repository: EmployeeRepository,
    private val duplicateIndex: DuplicateIndex,
    private val nameIndex: NameIndex
) {
    suspend operator fun invoke(previous: Employee, updated: Employee) {
        repository.update(updated)
        duplicateIndex.remove(previous.email, previous.normalizedPhone)
        duplicateIndex.add(updated.email, updated.normalizedPhone)
        nameIndex.remove(previous.fullName)
        nameIndex.add(updated.fullName)
    }
}
