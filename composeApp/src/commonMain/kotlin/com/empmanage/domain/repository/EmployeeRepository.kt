package com.empmanage.domain.repository

import com.empmanage.domain.model.Employee
import com.empmanage.domain.model.SortOption
import kotlinx.coroutines.flow.Flow

/**
 * Domain-facing contract for employee persistence. The implementation
 * (in `data/repository`) is the only place that knows about Room.
 */
interface EmployeeRepository {
    fun observeAll(): Flow<List<Employee>>
    fun observeLimited(sort: SortOption, limit: Int): Flow<List<Employee>>
    fun observeCount(): Flow<Int>
    suspend fun getById(id: Long): Employee?
    suspend fun add(employee: Employee): Long
    suspend fun update(employee: Employee)
    suspend fun delete(employee: Employee)
    suspend fun allEmails(): List<String>
    suspend fun allNormalizedPhones(): List<String>
    suspend fun allFullNames(): List<String>
}
