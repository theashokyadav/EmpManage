package com.empmanage.platform

import androidx.room.RoomDatabase
import com.empmanage.data.local.db.AppDatabase

expect fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
