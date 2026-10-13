package com.empmanage.domain.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NameTrieTest {

    @Test
    fun prefixSearchReturnsAllMatchingNames() {
        val trie = NameTrie()
        trie.insert("Jane Doe")
        trie.insert("Jane Smith")
        trie.insert("John Appleseed")

        val results = trie.wordsWithPrefix("jane").sorted()
        assertEquals(listOf("Jane Doe", "Jane Smith"), results)
    }

    @Test
    fun startsWithIsCaseInsensitive() {
        val trie = NameTrie()
        trie.insert("Amit Kumar")
        assertTrue(trie.startsWith("AMIT"))
    }

    @Test
    fun removeDropsNameFromFurtherSearches() {
        val trie = NameTrie()
        trie.insert("Jane Doe")
        trie.remove("Jane Doe")
        assertFalse(trie.startsWith("Jane"))
    }

    @Test
    fun unmatchedPrefixReturnsEmptyList() {
        val trie = NameTrie()
        trie.insert("Jane Doe")
        assertEquals(emptyList(), trie.wordsWithPrefix("xyz"))
    }
}
