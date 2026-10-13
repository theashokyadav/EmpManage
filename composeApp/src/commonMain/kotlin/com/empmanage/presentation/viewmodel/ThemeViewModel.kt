package com.empmanage.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empmanage.domain.repository.ThemePreferenceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(private val repository: ThemePreferenceRepository) : ViewModel() {

    val darkModeOverride: StateFlow<Boolean?> = repository.isDarkModeOverride
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setDarkModeOverride(enabled: Boolean?) {
        viewModelScope.launch { repository.setDarkModeOverride(enabled) }
    }
}
