package com.empmanage.presentation.state

enum class SortOption(val label: String) {
    NAME_ASC("Name (A → Z)"),
    NAME_DESC("Name (Z → A)"),
    JOINING_NEWEST("Joining Date (Newest)"),
    JOINING_OLDEST("Joining Date (Oldest)"),
    SALARY_HIGH("Salary (High → Low)"),
    SALARY_LOW("Salary (Low → High)")
}
