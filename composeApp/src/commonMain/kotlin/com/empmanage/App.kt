package com.empmanage

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.empmanage.presentation.navigation.Screen
import com.empmanage.presentation.viewmodel.ThemeViewModel
import com.empmanage.ui.screen.EmployeeDetailScreen
import com.empmanage.ui.screen.EmployeeFormScreen
import com.empmanage.ui.screen.EmployeeListScreen
import com.empmanage.ui.theme.EmpManageTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val themeViewModel: ThemeViewModel = koinViewModel()
    val darkModeOverride by themeViewModel.darkModeOverride.collectAsState()
    val isDarkTheme = darkModeOverride ?: isSystemInDarkTheme()

    EmpManageTheme(darkTheme = isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            var screen by remember { mutableStateOf<Screen>(Screen.List) }

            when (val current = screen) {
                is Screen.List -> EmployeeListScreen(
                    onAddEmployee = { screen = Screen.Form() },
                    onEditEmployee = { id -> screen = Screen.Form(id) },
                    onViewDetails = { id -> screen = Screen.Detail(id) },
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = { themeViewModel.setDarkModeOverride(!isDarkTheme) }
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
