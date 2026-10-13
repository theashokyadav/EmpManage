package com.empmanage.domain.util

/**
 * Fixed-depth undo stack backed by [ArrayDeque], used to let a deleted
 * employee be restored from the "Undo" snackbar within its display window.
 *
 * [push] and [pop] are O(1) — `ArrayDeque.addLast`/`removeLast` are O(1)
 * amortised, no shifting of other elements. Space complexity: O(maxDepth),
 * since pushing past the cap evicts the oldest entry instead of growing
 * unbounded.
 */
class UndoStack<T>(private val maxDepth: Int = 10) {
    private val deque = ArrayDeque<T>()

    fun push(item: T) {
        if (deque.size == maxDepth) deque.removeFirst()
        deque.addLast(item)
    }

    fun pop(): T? = if (deque.isEmpty()) null else deque.removeLast()

    fun peek(): T? = deque.lastOrNull()

    fun isEmpty(): Boolean = deque.isEmpty()

    fun size(): Int = deque.size
}
