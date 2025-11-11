// app/src/main/java/com/shirotenma/petpartnertest/settings/SettingsViewModel.kt
package com.shirotenma.petpartnertest.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    // Optional: stream read-only untuk yang butuh observe langsung
    val uiFlow = repo.ui.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUi()
    )

    // State form (single source of truth untuk input)
    var name: String by mutableStateOf("")
        private set
    var dark: Boolean by mutableStateOf(false)
        private set
    var notif: Boolean by mutableStateOf(true)
        private set

    init {
        // preload form dari repo
        viewModelScope.launch {
            repo.ui.collect { s ->
                name = s.ownerName
                dark = s.darkMode
                notif = s.notifEnabled
            }
        }
    }

    fun onNameChange(v: String) { name = v }
    fun onDarkChange(v: Boolean) { dark = v }
    fun onNotifChange(v: Boolean) { notif = v }

    fun save(onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.save(name, dark, notif)
            onDone?.invoke()
        }
    }
}
