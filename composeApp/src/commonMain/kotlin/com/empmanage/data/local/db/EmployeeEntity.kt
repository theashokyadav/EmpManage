package com.empmanage.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for the `employees` table. `skills` is persisted as a
 * comma-separated string via [Converters] (List<String> <-> String);
 * `normalizedPhone` is its own column so [EmployeeDao] can select just
 * that column cheaply to seed the in-memory duplicate index on app start.
 */
@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    @ColumnInfo(name = "normalized_phone")
    val normalizedPhone: String,
    val address: String,
    val gender: String,
    val department: String,
    val skills: List<String>,
    val employmentType: String,
    val isActive: Boolean,
    val joiningDateMillis: Long,
    val salary: Double,
    val profileImagePath: String?,
    val resumePath: String?,
    val resumeFileName: String?,
    val resumeFileSizeBytes: Long?,
    val resumeMimeType: String?,
    val createdAt: Long,
    val updatedAt: Long
)
