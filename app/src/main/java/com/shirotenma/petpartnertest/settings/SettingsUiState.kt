package com.shirotenma.petpartnertest.settings

data class SettingsUiState(
    val ownerName: String = "",
    val darkMode: Boolean = false,
    val notifEnabled: Boolean = true,
    val saving: Boolean = false
)
