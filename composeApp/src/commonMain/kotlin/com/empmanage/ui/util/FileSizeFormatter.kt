package com.empmanage.ui.util

import kotlin.math.round

object FileSizeFormatter {
    fun format(bytes: Long): String = when {
        bytes >= 1024 * 1024 -> "${round(bytes / (1024.0 * 1024.0) * 10) / 10} MB"
        bytes >= 1024 -> "${round(bytes / 1024.0)} KB".replace(".0 KB", " KB")
        else -> "$bytes B"
    }
}
