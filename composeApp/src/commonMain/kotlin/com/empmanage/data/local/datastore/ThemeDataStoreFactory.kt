package com.empmanage.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import com.empmanage.platform.themeDataStorePath
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * One cross-platform DataStore implementation backed by Okio's file
 * system, which runs unmodified on every target — only the file path
 * ([themeDataStorePath]) needs an expect/actual.
 */
fun createThemeDataStore(): DataStore<Preferences> {
    val storage = OkioStorage(
        fileSystem = FileSystem.SYSTEM,
        serializer = PreferencesSerializer,
        producePath = { themeDataStorePath().toPath() }
    )
    return DataStoreFactory.create(storage = storage)
}
