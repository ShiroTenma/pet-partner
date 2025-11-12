package com.shirotenma.petpartnertest.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        // Sinkronkan dari DataStore → UI
        viewModelScope.launch {
            combine(repo.nameFlow, repo.darkFlow, repo.notifFlow) { name, dark, notif ->
                Triple(name, dark, notif)
            }.collect { (name, dark, notif) ->
                _uiState.update { it.copy(ownerName = name, darkMode = dark, notifEnabled = notif) }
            }
        }
    }

    // Handlers yang dipakai di SettingsScreen
    fun onNameChange(newName: String) {
        _uiState.update { it.copy(ownerName = newName) }
    }

    fun onDarkChange(enabled: Boolean) {
        _uiState.update { it.copy(darkMode = enabled) }
        // QoL: simpan langsung toggle
        viewModelScope.launch { repo.setDark(enabled) }
    }

    fun onNotifChange(enabled: Boolean) {
        _uiState.update { it.copy(notifEnabled = enabled) }
        // QoL: simpan langsung toggle
        viewModelScope.launch { repo.setNotif(enabled) }
    }

    fun save() {
        val snapshot = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true) }
            try {
                repo.setName(snapshot.ownerName.trim())
                // dark/notif sudah disimpan realtime saat toggle; optional re-sync:
                repo.setDark(snapshot.darkMode)
                repo.setNotif(snapshot.notifEnabled)
            } finally {
                _uiState.update { it.copy(saving = false) }
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            _uiState.update { it.copy(ownerName = "", darkMode = false, notifEnabled = true) }
            repo.setName("")
            repo.setDark(false)
            repo.setNotif(true)
        }
    }
}
