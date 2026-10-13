package com.empmanage.domain.usecase

import com.empmanage.domain.model.Employee
import com.empmanage.domain.util.MinHeap

/**
 * Returns the top [n] employees by salary using a min-heap of size [n].
 *
 * Time complexity: O(m log n) where m = total employees, n = top count.
 * Space complexity: O(n) — the heap holds at most n entries at any time.
 *
 * Why not sort the whole list? A full sort is O(m log m). When n << m
 * (e.g. n=5, m=500) the fixed-size min-heap only ever pays log n per
 * comparison/swap instead of log m, and never allocates more than n
 * slots, so it's both faster and more memory-efficient than sorting
 * everyone just to keep the top few.
 */
class TopNSalaryUseCase {

    companion object {
        const val DEFAULT_N = 5
    }

    operator fun invoke(employees: List<Employee>, n: Int = DEFAULT_N): List<Employee> {
        if (n <= 0) return emptyList()
        val minHeap = MinHeap<Employee>(compareBy { it.salary })
        for (employee in employees) {
            if (minHeap.size < n) {
                minHeap.add(employee)
            } else if (employee.salary > (minHeap.peek()?.salary ?: Double.NEGATIVE_INFINITY)) {
                minHeap.poll()
                minHeap.add(employee)
            }
        }
        return minHeap.toSortedList().sortedByDescending { it.salary }
    }
}
