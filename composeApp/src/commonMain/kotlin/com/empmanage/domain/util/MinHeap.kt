package com.empmanage.domain.util

/**
 * A minimal binary min-heap, implemented from scratch because
 * `java.util.PriorityQueue` is JVM-only and unavailable in `commonMain`
 * for a Kotlin Multiplatform target.
 *
 * - [add]: O(log n) — sift-up after appending.
 * - [peek]: O(1) — root element.
 * - [poll]: O(log n) — swap root with last element, then sift-down.
 * Space complexity: O(n) for n stored elements.
 */
class MinHeap<T>(private val comparator: Comparator<T>) {
    private val elements = ArrayList<T>()

    val size: Int get() = elements.size
    fun isEmpty(): Boolean = elements.isEmpty()

    fun peek(): T? = elements.firstOrNull()

    fun add(item: T) {
        elements.add(item)
        siftUp(elements.lastIndex)
    }

    fun poll(): T? {
        if (elements.isEmpty()) return null
        val root = elements[0]
        val last = elements.removeAt(elements.lastIndex)
        if (elements.isNotEmpty()) {
            elements[0] = last
            siftDown(0)
        }
        return root
    }

    fun toSortedList(): List<T> = elements.sortedWith(comparator)

    private fun siftUp(startIndex: Int) {
        var index = startIndex
        while (index > 0) {
            val parent = (index - 1) / 2
            if (comparator.compare(elements[index], elements[parent]) < 0) {
                swap(index, parent)
                index = parent
            } else break
        }
    }

    private fun siftDown(startIndex: Int) {
        var index = startIndex
        val n = elements.size
        while (true) {
            val left = 2 * index + 1
            val right = 2 * index + 2
            var smallest = index
            if (left < n && comparator.compare(elements[left], elements[smallest]) < 0) smallest = left
            if (right < n && comparator.compare(elements[right], elements[smallest]) < 0) smallest = right
            if (smallest == index) break
            swap(index, smallest)
            index = smallest
        }
    }

    private fun swap(i: Int, j: Int) {
        val tmp = elements[i]
        elements[i] = elements[j]
        elements[j] = tmp
    }
}
