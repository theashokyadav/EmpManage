package com.empmanage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.empmanage.ui.screen.EmployeeFormScreen

/**
 * Renders the Employee Form screen directly as the app root for now.
 * Navigation and the employee list become the real root in Phase 5/6.
 */
@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            EmployeeFormScreen()
        }
    }
}
