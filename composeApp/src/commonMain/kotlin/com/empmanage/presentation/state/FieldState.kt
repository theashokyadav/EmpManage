package com.empmanage.presentation.state

/**
 * A form field's current value plus its validation error (always kept
 * fresh, even before [touched]) and whether the user has blurred it yet.
 * [touched] gates whether [error] is actually shown beneath the field —
 * the Employee Form spec requires errors to appear on blur, not as the
 * user types.
 */
data class FieldState<T>(
    val value: T,
    val error: String? = null,
    val touched: Boolean = false
)
