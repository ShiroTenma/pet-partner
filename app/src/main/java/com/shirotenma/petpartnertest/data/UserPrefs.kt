package com.shirotenma.petpartnertest.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefs @Inject constructor(
    private val store: DataStore<Preferences>
) {
    private object Keys {
        val OWNER_NAME = stringPreferencesKey("owner_name")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIF_ENABLED = booleanPreferencesKey("notif_enabled")
    }

    val ownerName: Flow<String?> = store.data.map { it[Keys.OWNER_NAME] }
    val darkMode: Flow<Boolean> = store.data.map { it[Keys.DARK_MODE] ?: false }
    val notifEnabled: Flow<Boolean> = store.data.map { it[Keys.NOTIF_ENABLED] ?: true }

    suspend fun setOwnerName(name: String) = store.edit { it[Keys.OWNER_NAME] = name }
    suspend fun setDarkMode(enabled: Boolean) = store.edit { it[Keys.DARK_MODE] = enabled }
    suspend fun setNotifEnabled(enabled: Boolean) = store.edit { it[Keys.NOTIF_ENABLED] = enabled }
}
