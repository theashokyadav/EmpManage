package com.empmanage.domain.util

/**
 * In-memory duplicate-detection index backed by two [HashSet]s, one for
 * emails and one for normalised phone numbers. Populated from the DB once
 * on app start, then kept in sync on every insert/update/delete so form
 * submission never needs to hit the database to check for duplicates.
 *
 * Time complexity: O(1) average per [isDuplicate]/[add]/[remove] call —
 * HashSet `contains`/`add`/`remove`.
 * Space complexity: O(n) where n = number of employees (one entry per set
 * per employee).
 */
class DuplicateIndex {
    private val emailIndex = HashSet<String>()
    private val phoneIndex = HashSet<String>()

    fun populate(emails: Collection<String>, normalizedPhones: Collection<String>) {
        emailIndex.clear()
        emailIndex.addAll(emails.map { it.lowercase() })
        phoneIndex.clear()
        phoneIndex.addAll(normalizedPhones)
    }

    /**
     * Checks for a duplicate email or normalised phone before persist.
     * When editing an existing employee, pass its current [excludingEmail]
     * and [excludingPhone] so the record doesn't collide with itself.
     *
     * Time complexity: O(1) average — two HashSet `contains()` calls.
     * Space complexity: O(1) beyond the index itself.
     */
    fun isDuplicate(
        email: String,
        normalizedPhone: String,
        excludingEmail: String? = null,
        excludingPhone: String? = null
    ): Boolean {
        val normalizedEmail = email.lowercase()
        val emailExcluded = excludingEmail != null && normalizedEmail == excludingEmail.lowercase()
        val phoneExcluded = excludingPhone != null && normalizedPhone == excludingPhone

        val emailIsDuplicate = !emailExcluded && normalizedEmail in emailIndex
        val phoneIsDuplicate = !phoneExcluded && normalizedPhone in phoneIndex
        return emailIsDuplicate || phoneIsDuplicate
    }

    fun add(email: String, normalizedPhone: String) {
        emailIndex.add(email.lowercase())
        phoneIndex.add(normalizedPhone)
    }

    fun remove(email: String, normalizedPhone: String) {
        emailIndex.remove(email.lowercase())
        phoneIndex.remove(normalizedPhone)
    }
}
