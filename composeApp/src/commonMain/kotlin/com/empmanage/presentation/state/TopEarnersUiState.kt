package com.empmanage.presentation.state

import com.empmanage.domain.model.Employee
import com.empmanage.domain.usecase.TopNSalaryUseCase

data class TopEarnersUiState(
    val n: Int = TopNSalaryUseCase.DEFAULT_N,
    val topEmployees: List<Employee> = emptyList()
)
