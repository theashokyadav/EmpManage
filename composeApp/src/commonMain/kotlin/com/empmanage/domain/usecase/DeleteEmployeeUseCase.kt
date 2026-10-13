package com.empmanage.domain.usecase

import com.empmanage.domain.model.Employee
import com.empmanage.domain.repository.EmployeeRepository
import com.empmanage.domain.util.DuplicateIndex
import com.empmanage.domain.util.NameIndex
import com.empmanage.domain.util.UndoStack

/**
 * Deletes an employee, re-syncs the in-memory indexes, and pushes the
 * deleted record onto [undoStack] so [UndoDeleteUseCase] can restore it
 * while the undo snackbar is still showing.
 */
class DeleteEmployeeUseCase(
    private val repository: EmployeeRepository,
    private val duplicateIndex: DuplicateIndex,
    private val nameIndex: NameIndex,
    private val undoStack: UndoStack<Employee>
) {
    suspend operator fun invoke(employee: Employee) {
        repository.delete(employee)
        duplicateIndex.remove(employee.email, employee.normalizedPhone)
        nameIndex.remove(employee.fullName)
        undoStack.push(employee)
    }
}
