package com.empmanage.domain.util

/**
 * Prefix trie over employee full names, used to drive fast prefix
 * search/autocomplete in the employee list (Section 4) without scanning
 * every record on each keystroke.
 *
 * - [insert] / [remove]: O(L) where L = length of the (lowercased) name.
 * - [search] (exact match) and [startsWith]: O(L).
 * - [wordsWithPrefix]: O(L + k) where k = number of matching stored
 *   characters returned.
 * Space complexity: O(total characters across all inserted names) in the
 * worst case (no shared prefixes); shared prefixes reduce this.
 */
class NameTrie {
    private class Node {
        val children: HashMap<Char, Node> = HashMap()
        var isEndOfWord: Boolean = false
        var originalValue: String? = null
    }

    private val root = Node()

    fun insert(name: String) {
        var node = root
        for (ch in name.lowercase()) {
            node = node.children.getOrPut(ch) { Node() }
        }
        node.isEndOfWord = true
        node.originalValue = name
    }

    fun remove(name: String) {
        val path = ArrayList<Node>()
        var node = root
        path.add(node)
        for (ch in name.lowercase()) {
            node = node.children[ch] ?: return
            path.add(node)
        }
        if (!node.isEndOfWord) return
        node.isEndOfWord = false
        node.originalValue = null
        for (i in path.lastIndex downTo 1) {
            val current = path[i]
            if (current.children.isEmpty() && !current.isEndOfWord) {
                val parent = path[i - 1]
                val key = name.lowercase()[i - 1]
                parent.children.remove(key)
            } else break
        }
    }

    fun startsWith(prefix: String): Boolean = findNode(prefix) != null

    fun wordsWithPrefix(prefix: String): List<String> {
        val startNode = findNode(prefix) ?: return emptyList()
        val results = ArrayList<String>()
        collect(startNode, results)
        return results
    }

    private fun findNode(prefix: String): Node? {
        var node = root
        for (ch in prefix.lowercase()) {
            node = node.children[ch] ?: return null
        }
        return node
    }

    private fun collect(node: Node, results: MutableList<String>) {
        if (node.isEndOfWord) node.originalValue?.let { results.add(it) }
        for (child in node.children.values) collect(child, results)
    }
}
