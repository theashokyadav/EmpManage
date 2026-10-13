package com.empmanage.presentation.state

import com.empmanage.domain.model.Department
import com.empmanage.domain.model.EmploymentType

data class FilterState(
    val departments: Set<Department> = emptySet(),
    val status: ActiveStatusFilter = ActiveStatusFilter.ALL,
    val employmentTypes: Set<EmploymentType> = emptySet()
) {
    val activeCount: Int
        get() = departments.size + employmentTypes.size + (if (status != ActiveStatusFilter.ALL) 1 else 0)
}
