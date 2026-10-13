package com.empmanage.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.empmanage.domain.repository.ThemePreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val DARK_MODE_OVERRIDE_SET = booleanPreferencesKey("dark_mode_override_set")
private val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")

class ThemePreferenceRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : ThemePreferenceRepository {

    override val isDarkModeOverride: Flow<Boolean?> = dataStore.data.map { prefs ->
        if (prefs[DARK_MODE_OVERRIDE_SET] == true) prefs[DARK_MODE_ENABLED] else null
    }

    override suspend fun setDarkModeOverride(enabled: Boolean?) {
        dataStore.edit { prefs ->
            if (enabled == null) {
                prefs[DARK_MODE_OVERRIDE_SET] = false
            } else {
                prefs[DARK_MODE_OVERRIDE_SET] = true
                prefs[DARK_MODE_ENABLED] = enabled
            }
        }
    }
}
