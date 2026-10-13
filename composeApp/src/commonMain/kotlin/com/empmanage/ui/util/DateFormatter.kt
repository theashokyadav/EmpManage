package com.empmanage.ui.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateFormatter {
    private val MONTHS = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    /** Formats an epoch-millis timestamp as `DD MMM YYYY`, e.g. `05 Mar 2026`. */
    fun formatDate(epochMillis: Long): String {
        val date = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val month = MONTHS[date.monthNumber - 1]
        return "$day $month ${date.year}"
    }
}
