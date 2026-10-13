package com.empmanage.ui.util

object CurrencyFormatter {
    /** Formats a salary as `$12,345` — no locale APIs are used since this is commonMain. */
    fun format(amount: Double): String {
        val whole = amount.toLong()
        val grouped = whole.toString().reversed().chunked(3).joinToString(",").reversed()
        return "$$grouped"
    }
}
