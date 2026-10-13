package com.empmanage.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * `null` means "follow the system setting"; a non-null value is an
 * explicit user override persisted in DataStore.
 */
interface ThemePreferenceRepository {
    val isDarkModeOverride: Flow<Boolean?>
    suspend fun setDarkModeOverride(enabled: Boolean?)
}
