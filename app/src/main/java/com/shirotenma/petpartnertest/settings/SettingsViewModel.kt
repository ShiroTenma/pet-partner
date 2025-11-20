// app/src/main/java/com/shirotenma/petpartnertest/settings/SettingsViewModel.kt
package com.shirotenma.petpartnertest.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val store: SettingsStore
) : ViewModel() {

    private val saving = MutableStateFlow(false)

    // UI = DataStore flow + saving flag managed locally
    val uiState: StateFlow<SettingsUi> =
        combine(store.flow, saving) { ui, isSaving -> ui.copy(saving = isSaving) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUi())

    // Handlers
    fun onNameChange(newName: String) = viewModelScope.launch {
        store.setOwnerName(newName)
    }

    fun onDarkChange(enabled: Boolean) = viewModelScope.launch {
        store.setDarkMode(enabled)
    }

    fun onNotifChange(enabled: Boolean) = viewModelScope.launch {
        store.setNotifEnabled(enabled)
    }

    // Optional explicit save button (kept for UX parity)
    fun save() = viewModelScope.launch {
        // If you had extra work (sync to server etc.), toggle saving here.
        saving.value = true
        // ... do extra work if any ...
        saving.value = false
    }

    fun resetToDefaults() = viewModelScope.launch {
        saving.value = true
        store.reset()
        saving.value = false
    }
}
