package com.shirotenma.petpartnertest.settings

import kotlinx.coroutines.flow.Flow

data class SettingsUi(
    val ownerName: String = "",
    val darkMode: Boolean = false,
    val notifEnabled: Boolean = true
)

interface SettingsRepository {
    val ui: Flow<SettingsUi>
    suspend fun save(ownerName: String, dark: Boolean, notif: Boolean)
}
