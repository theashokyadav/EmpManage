package com.empmanage.domain.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NameIndexTest {

    @Test
    fun detectsDuplicateNameCaseAndWhitespaceInsensitively() {
        val index = NameIndex()
        index.add("Jane Doe")
        assertTrue(index.contains("  jane doe  "))
    }

    @Test
    fun editingSameEmployeeIsNotADuplicate() {
        val index = NameIndex()
        index.add("Jane Doe")
        assertFalse(index.contains("Jane Doe", excluding = "Jane Doe"))
    }

    @Test
    fun removeTakesNameOutOfTheIndex() {
        val index = NameIndex()
        index.add("Jane Doe")
        index.remove("Jane Doe")
        assertFalse(index.contains("Jane Doe"))
    }

    @Test
    fun populateSeedsFromExistingNames() {
        val index = NameIndex()
        index.populate(listOf("Jane Doe", "John Smith"))
        assertTrue(index.contains("john smith"))
    }
}
