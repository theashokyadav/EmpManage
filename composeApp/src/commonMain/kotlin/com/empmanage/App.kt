package com.empmanage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.empmanage.presentation.navigation.Screen
import com.empmanage.ui.screen.EmployeeDetailScreen
import com.empmanage.ui.screen.EmployeeFormScreen
import com.empmanage.ui.screen.EmployeeListScreen

@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            var screen by remember { mutableStateOf<Screen>(Screen.List) }

            when (val current = screen) {
                is Screen.List -> EmployeeListScreen(
                    onAddEmployee = { screen = Screen.Form() },
                    onEditEmployee = { id -> screen = Screen.Form(id) },
                    onViewDetails = { id -> screen = Screen.Detail(id) }
                )

                is Screen.Form -> EmployeeFormScreen(
                    employeeId = current.employeeId,
                    onSaved = { screen = Screen.List },
                    onCancel = { screen = Screen.List }
                )

                is Screen.Detail -> EmployeeDetailScreen(
                    employeeId = current.employeeId,
                    onBack = { screen = Screen.List },
                    onEdit = { id -> screen = Screen.Form(id) }
                )
            }
        }
    }
}
