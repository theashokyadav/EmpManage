package com.empmanage.domain.usecase

import com.empmanage.domain.model.Department
import com.empmanage.domain.model.Employee
import com.empmanage.domain.model.EmploymentType
import com.empmanage.domain.model.Gender
import kotlin.test.Test
import kotlin.test.assertEquals

class TopNSalaryUseCaseTest {

    private fun employee(name: String, salary: Double) = Employee(
        id = 0,
        fullName = name,
        email = "$name@example.com",
        phoneNumber = "9876543210",
        normalizedPhone = "9876543210",
        address = "123 Main Street",
        gender = Gender.PREFER_NOT_TO_SAY,
        department = Department.ENGINEERING,
        skills = emptyList(),
        employmentType = EmploymentType.FULL_TIME,
        joiningDateMillis = 0,
        salary = salary
    )

    @Test
    fun returnsTopNSortedDescendingBySalary() {
        val employees = listOf(
            employee("A", 50_000.0),
            employee("B", 90_000.0),
            employee("C", 70_000.0),
            employee("D", 30_000.0),
            employee("E", 110_000.0),
            employee("F", 60_000.0)
        )

        val top3 = TopNSalaryUseCase()(employees, n = 3)

        assertEquals(listOf("E", "B", "C"), top3.map { it.fullName })
    }

    @Test
    fun fewerEmployeesThanNReturnsAllOfThemSorted() {
        val employees = listOf(employee("A", 50_000.0), employee("B", 90_000.0))
        val result = TopNSalaryUseCase()(employees, n = 5)
        assertEquals(listOf("B", "A"), result.map { it.fullName })
    }

    @Test
    fun emptyInputReturnsEmptyList() {
        assertEquals(emptyList(), TopNSalaryUseCase()(emptyList(), n = 5))
    }

    @Test
    fun defaultNIsFive() {
        val employees = (1..10).map { employee("E$it", it * 1000.0) }
        val result = TopNSalaryUseCase()(employees)
        assertEquals(5, result.size)
        assertEquals(listOf("E10", "E9", "E8", "E7", "E6"), result.map { it.fullName })
    }
}
