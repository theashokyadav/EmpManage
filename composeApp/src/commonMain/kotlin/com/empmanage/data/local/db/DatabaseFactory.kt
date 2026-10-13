package com.empmanage.data.local.db

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.empmanage.platform.provideDatabaseBuilder
import kotlinx.coroutines.Dispatchers

fun createDatabase(): AppDatabase =
    provideDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
