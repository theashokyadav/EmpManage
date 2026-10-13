package com.empmanage.platform

import androidx.room.Room
import androidx.room.RoomDatabase
import com.empmanage.data.local.db.AppDatabase

private const val DB_FILE_NAME = "empmanage.db"

actual fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val context = AndroidAppContext.instance
    val dbFile = context.getDatabasePath(DB_FILE_NAME)
    return Room.databaseBuilder(context, AppDatabase::class.java, dbFile.absolutePath)
}
