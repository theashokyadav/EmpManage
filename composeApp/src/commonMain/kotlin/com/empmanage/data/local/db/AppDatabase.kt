package com.empmanage.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters

@Database(entities = [EmployeeEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
}

/**
 * Room generates the platform `actual` for this object via KSP on every
 * target (Android, each iOS arch) — Kotlin/Native has no reflection, so
 * this expect/actual seam is how Room KMP instantiates the generated
 * `AppDatabase_Impl` without it.
 */
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
