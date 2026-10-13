package com.empmanage.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UndoStackTest {

    @Test
    fun pushThenPopReturnsLastPushedItem() {
        val stack = UndoStack<String>()
        stack.push("A")
        stack.push("B")
        assertEquals("B", stack.pop())
        assertEquals("A", stack.pop())
    }

    @Test
    fun popOnEmptyStackReturnsNull() {
        val stack = UndoStack<String>()
        assertNull(stack.pop())
    }

    @Test
    fun exceedingMaxDepthEvictsTheOldestEntry() {
        val stack = UndoStack<Int>(maxDepth = 3)
        stack.push(1)
        stack.push(2)
        stack.push(3)
        stack.push(4) // evicts 1
        assertEquals(3, stack.size())
        assertEquals(4, stack.pop())
        assertEquals(3, stack.pop())
        assertEquals(2, stack.pop())
        assertNull(stack.pop()) // 1 was evicted
    }

    @Test
    fun peekDoesNotRemoveTheItem() {
        val stack = UndoStack<String>()
        stack.push("A")
        assertEquals("A", stack.peek())
        assertEquals(1, stack.size())
    }
}
