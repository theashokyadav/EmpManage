package com.empmanage.data.repository

import com.empmanage.data.local.db.EmployeeDao
import com.empmanage.data.local.mapper.toDomain
import com.empmanage.data.local.mapper.toEntity
import com.empmanage.domain.model.Employee
import com.empmanage.domain.model.SortOption
import com.empmanage.domain.repository.EmployeeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class EmployeeRepositoryImpl(private val dao: EmployeeDao) : EmployeeRepository {

    override fun observeAll(): Flow<List<Employee>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeLimited(sort: SortOption, limit: Int): Flow<List<Employee>> {
        val entityFlow = when (sort) {
            SortOption.NAME_ASC -> dao.observeLimitedByNameAsc(limit)
            SortOption.NAME_DESC -> dao.observeLimitedByNameDesc(limit)
            SortOption.JOINING_NEWEST -> dao.observeLimitedByJoiningNewest(limit)
            SortOption.JOINING_OLDEST -> dao.observeLimitedByJoiningOldest(limit)
            SortOption.SALARY_HIGH -> dao.observeLimitedBySalaryHigh(limit)
            SortOption.SALARY_LOW -> dao.observeLimitedBySalaryLow(limit)
        }
        return entityFlow.map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeCount(): Flow<Int> = dao.observeCount()

    override suspend fun getById(id: Long): Employee? =
        withContext(Dispatchers.IO) { dao.getById(id)?.toDomain() }

    override suspend fun add(employee: Employee): Long =
        withContext(Dispatchers.IO) { dao.insert(employee.toEntity()) }

    override suspend fun update(employee: Employee) =
        withContext(Dispatchers.IO) { dao.update(employee.toEntity()) }

    override suspend fun delete(employee: Employee) =
        withContext(Dispatchers.IO) { dao.delete(employee.toEntity()) }

    override suspend fun allEmails(): List<String> =
        withContext(Dispatchers.IO) { dao.getAllEmails() }

    override suspend fun allNormalizedPhones(): List<String> =
        withContext(Dispatchers.IO) { dao.getAllNormalizedPhones() }

    override suspend fun allFullNames(): List<String> =
        withContext(Dispatchers.IO) { dao.getAllFullNames() }
}
