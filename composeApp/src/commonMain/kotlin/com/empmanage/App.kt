package com.empmanage

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
import com.empmanage.platform.BackHandler
import com.empmanage.presentation.navigation.Screen
import com.empmanage.presentation.viewmodel.ThemeViewModel
import com.empmanage.ui.screen.EmployeeDetailScreen
import com.empmanage.ui.screen.EmployeeFormScreen
import com.empmanage.ui.screen.EmployeeListScreen
import com.empmanage.ui.theme.EmpManageTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun App() {
    val themeViewModel: ThemeViewModel = koinViewModel()
    val darkModeOverride by themeViewModel.darkModeOverride.collectAsState()
    val isDarkTheme = darkModeOverride ?: isSystemInDarkTheme()

    EmpManageTheme(darkTheme = isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            var screen by remember { mutableStateOf<Screen>(Screen.List) }

            BackHandler(enabled = screen != Screen.List) {
                screen = Screen.List
            }

            // Shared element transition (bonus #5) between the list card's avatar
            // and the detail screen's avatar: both screens sit inside the same
            // AnimatedContent so Modifier.sharedElement can animate between them.
            SharedTransitionLayout {
                AnimatedContent(targetState = screen, label = "screen-nav") { target ->
                    when (target) {
                        is Screen.List -> EmployeeListScreen(
                            onAddEmployee = { screen = Screen.Form() },
                            onEditEmployee = { id -> screen = Screen.Form(id) },
                            onViewDetails = { id -> screen = Screen.Detail(id) },
                            isDarkTheme = isDarkTheme,
                            onToggleDarkTheme = { themeViewModel.setDarkModeOverride(!isDarkTheme) },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@AnimatedContent
                        )

                        is Screen.Form -> EmployeeFormScreen(
                            employeeId = target.employeeId,
                            onSaved = { screen = Screen.List },
                            onCancel = { screen = Screen.List }
                        )

                        is Screen.Detail -> EmployeeDetailScreen(
                            employeeId = target.employeeId,
                            onBack = { screen = Screen.List },
                            onEdit = { id -> screen = Screen.Form(id) },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@AnimatedContent
                        )
                    }
                }
            }
        }
    }
}
