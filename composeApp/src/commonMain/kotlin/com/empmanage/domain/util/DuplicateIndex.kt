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
     * O(1) average — single HashSet `contains()` call. Used to attribute a
     * duplicate error to the Email field specifically.
     */
    fun isEmailDuplicate(email: String, excludingEmail: String? = null): Boolean {
        val normalizedEmail = email.lowercase()
        val excluded = excludingEmail != null && normalizedEmail == excludingEmail.lowercase()
        return !excluded && normalizedEmail in emailIndex
    }

    /**
     * O(1) average — single HashSet `contains()` call. Used to attribute a
     * duplicate error to the Phone field specifically.
     */
    fun isPhoneDuplicate(normalizedPhone: String, excludingPhone: String? = null): Boolean {
        val excluded = excludingPhone != null && normalizedPhone == excludingPhone
        return !excluded && normalizedPhone in phoneIndex
    }

    /**
     * Checks for a duplicate email OR normalised phone before persist.
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
    ): Boolean = isEmailDuplicate(email, excludingEmail) || isPhoneDuplicate(normalizedPhone, excludingPhone)

    fun add(email: String, normalizedPhone: String) {
        emailIndex.add(email.lowercase())
        phoneIndex.add(normalizedPhone)
    }

    fun remove(email: String, normalizedPhone: String) {
        emailIndex.remove(email.lowercase())
        phoneIndex.remove(normalizedPhone)
    }
}
