package com.empmanage.domain.usecase

import com.empmanage.domain.model.Employee
import com.empmanage.domain.util.UndoStack

/**
 * Pops the most recently deleted employee (if any, within the undo
 * window) and re-inserts it via [AddEmployeeUseCase], restoring the
 * duplicate/name indexes as a side effect of that use case.
 */
class UndoDeleteUseCase(
    private val addEmployeeUseCase: AddEmployeeUseCase,
    private val undoStack: UndoStack<Employee>
) {
    suspend operator fun invoke(): Boolean {
        val employee = undoStack.pop() ?: return false
        addEmployeeUseCase(employee)
        return true
    }
}
