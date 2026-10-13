package com.empmanage.domain.model

/**
 * Lives in domain (not presentation) because it now also selects which
 * ORDER BY/LIMIT query [com.empmanage.data.local.db.EmployeeDao] runs for
 * DB-level pagination, not just a client-side comparator.
 */
enum class SortOption(val label: String) {
    NAME_ASC("Name (A → Z)"),
    NAME_DESC("Name (Z → A)"),
    JOINING_NEWEST("Joining Date (Newest)"),
    JOINING_OLDEST("Joining Date (Oldest)"),
    SALARY_HIGH("Salary (High → Low)"),
    SALARY_LOW("Salary (Low → High)")
}
