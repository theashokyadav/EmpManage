package com.empmanage.domain.util

/**
 * Normalises raw phone number input into the canonical digits-only form
 * stored in the `normalizedPhone` DB column and used for duplicate lookups.
 *
 * Time complexity: O(k) where k = length of the raw input string (one regex
 * pass plus two prefix checks).
 * Space complexity: O(k) for the resulting string.
 */
object PhoneNormalizer {
    private val STRIP_CHARS = Regex("[\\s()\\-+]")

    fun normalize(raw: String): String =
        raw.replace(STRIP_CHARS, "")
            .removePrefix("91")
            .removePrefix("0")
}
