package com.empmanage.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empmanage.domain.model.Employee
import com.empmanage.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployeeDetailViewModel(private val repository: EmployeeRepository) : ViewModel() {

    private val _employee = MutableStateFlow<Employee?>(null)
    val employee: StateFlow<Employee?> = _employee.asStateFlow()

    fun load(employeeId: Long) {
        viewModelScope.launch {
            _employee.value = repository.getById(employeeId)
        }
    }
}
