package com.empmanage.data.local.db

import androidx.room.TypeConverter

/**
 * Room TypeConverter for the `skills` column: List<String> <-> comma-
 * separated String, as required for the Employee entity's skills field.
 */
class Converters {
    @TypeConverter
    fun fromSkillsList(skills: List<String>): String = skills.joinToString(",")

    @TypeConverter
    fun toSkillsList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
