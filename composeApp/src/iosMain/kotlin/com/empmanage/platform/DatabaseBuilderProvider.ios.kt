package com.empmanage.platform

import androidx.room.Room
import androidx.room.RoomDatabase
import com.empmanage.data.local.db.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

private const val DB_FILE_NAME = "empmanage.db"

@OptIn(ExperimentalForeignApi::class)
actual fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    val dbFilePath = requireNotNull(documentDirectory?.path) { "Unable to resolve iOS documents directory" } +
        "/$DB_FILE_NAME"
    return Room.databaseBuilder<AppDatabase>(name = dbFilePath)
}
