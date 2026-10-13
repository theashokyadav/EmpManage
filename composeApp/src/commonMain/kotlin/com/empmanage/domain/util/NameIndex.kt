package com.empmanage.domain.util

/**
 * O(1) average full-name duplicate lookup, mirroring [DuplicateIndex] but
 * for the "no duplicate employee names" rule. Kept separate because name
 * matching normalises differently (trim + lowercase only) and is consumed
 * by [FormValidators.validateFullName] via a predicate rather than at
 * insert time.
 *
 * Time complexity: O(1) average per [contains]/[add]/[remove].
 * Space complexity: O(n) where n = number of employees.
 */
class NameIndex {
    private val names = HashSet<String>()

    private fun normalize(name: String) = name.trim().lowercase()

    fun populate(allNames: Collection<String>) {
        names.clear()
        names.addAll(allNames.map(::normalize))
    }

    fun contains(name: String, excluding: String? = null): Boolean {
        val normalized = normalize(name)
        val excludedNormalized = excluding?.let(::normalize)
        return normalized != excludedNormalized && normalized in names
    }

    fun add(name: String) {
        names.add(normalize(name))
    }

    fun remove(name: String) {
        names.remove(normalize(name))
    }
}
