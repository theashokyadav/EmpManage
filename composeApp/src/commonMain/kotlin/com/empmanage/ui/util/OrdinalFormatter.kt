package com.empmanage.ui.util

object OrdinalFormatter {
    /** 1 -> "1st", 2 -> "2nd", 3 -> "3rd", 4 -> "4th", 11 -> "11th", ... */
    fun format(n: Int): String {
        if (n % 100 in 11..13) return "${n}th"
        val suffix = when (n % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
        return "$n$suffix"
    }
}
