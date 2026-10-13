package com.empmanage.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empmanage.domain.usecase.GetEmployeesUseCase
import com.empmanage.domain.usecase.TopNSalaryUseCase
import com.empmanage.presentation.state.TopEarnersUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private const val MIN_N = 1
private const val MAX_N = 10

/** Drives the Top Earners tab: a min-heap top-N (see [TopNSalaryUseCase]) recomputed reactively as N changes. */
class TopEarnersViewModel(
    getEmployeesUseCase: GetEmployeesUseCase,
    private val topNSalaryUseCase: TopNSalaryUseCase
) : ViewModel() {

    private val nState = MutableStateFlow(TopNSalaryUseCase.DEFAULT_N)

    val uiState: StateFlow<TopEarnersUiState> = combine(
        getEmployeesUseCase(),
        nState
    ) { employees, n ->
        TopEarnersUiState(n = n, topEmployees = topNSalaryUseCase(employees, n))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TopEarnersUiState())

    fun onNChanged(n: Int) {
        nState.value = n.coerceIn(MIN_N, MAX_N)
    }
}
