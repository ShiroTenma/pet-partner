package com.shirotenma.petpartnertest.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val nameFlow: Flow<String>
    val darkFlow: Flow<Boolean>
    val notifFlow: Flow<Boolean>

    suspend fun setName(name: String)
    suspend fun setDark(enabled: Boolean)
    suspend fun setNotif(enabled: Boolean)
}
