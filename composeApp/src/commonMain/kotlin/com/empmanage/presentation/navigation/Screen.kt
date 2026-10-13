package com.empmanage.presentation.navigation

sealed class Screen {
    data object List : Screen()
    data class Form(val employeeId: Long? = null) : Screen()
    data class Detail(val employeeId: Long) : Screen()
}
