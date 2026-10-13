package com.empmanage.domain.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DuplicateIndexTest {

    @Test
    fun detectsDuplicateEmailCaseInsensitively() {
        val index = DuplicateIndex()
        index.add("Jane@Example.com", "9876543210")
        assertTrue(index.isDuplicate("jane@example.com", "1112223333"))
    }

    @Test
    fun detectsDuplicatePhone() {
        val index = DuplicateIndex()
        index.add("jane@example.com", "9876543210")
        assertTrue(index.isDuplicate("new@example.com", "9876543210"))
    }

    @Test
    fun noDuplicateWhenNeitherMatches() {
        val index = DuplicateIndex()
        index.add("jane@example.com", "9876543210")
        assertFalse(index.isDuplicate("new@example.com", "1112223333"))
    }

    @Test
    fun editingOwnRecordIsNotADuplicate() {
        val index = DuplicateIndex()
        index.add("jane@example.com", "9876543210")
        val result = index.isDuplicate(
            email = "jane@example.com",
            normalizedPhone = "9876543210",
            excludingEmail = "jane@example.com",
            excludingPhone = "9876543210"
        )
        assertFalse(result)
    }

    @Test
    fun removeTakesEntryOutOfTheIndex() {
        val index = DuplicateIndex()
        index.add("jane@example.com", "9876543210")
        index.remove("jane@example.com", "9876543210")
        assertFalse(index.isDuplicate("jane@example.com", "9876543210"))
    }

    @Test
    fun populateSeedsBothSetsFromExistingData() {
        val index = DuplicateIndex()
        index.populate(listOf("a@example.com", "b@example.com"), listOf("1111111111", "2222222222"))
        assertTrue(index.isDuplicate("A@example.com", "0000000000"))
        assertTrue(index.isDuplicate("nobody@example.com", "2222222222"))
    }
}
