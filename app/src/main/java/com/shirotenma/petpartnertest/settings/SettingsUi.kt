package com.shirotenma.petpartnertest.settings

/** State yang dipakai SettingsScreen */
data class SettingsUi(
    val ownerName: String = "",
    val darkMode: Boolean = false,
    val notifEnabled: Boolean = true,
    // hanya untuk UI (tombol Save loading), tidak disimpan di DataStore
    val saving: Boolean = false
)
