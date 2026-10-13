package com.empmanage.domain.usecase

import com.empmanage.domain.model.Employee
import com.empmanage.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow

class GetEmployeesUseCase(private val repository: EmployeeRepository) {
    operator fun invoke(): Flow<List<Employee>> = repository.observeAll()
}
