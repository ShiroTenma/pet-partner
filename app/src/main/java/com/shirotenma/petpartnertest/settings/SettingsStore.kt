// app/src/main/java/com/shirotenma/petpartnertest/settings/SettingsStore.kt
package com.shirotenma.petpartnertest.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Top-level delegate utk DataStore
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore("settings")

@Singleton
class SettingsStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val ds = context.settingsDataStore

    // Keys
    private object Keys {
        val OWNER_NAME = stringPreferencesKey("owner_name")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIF_ENABLED = booleanPreferencesKey("notif_enabled")
    }

    // Flow UI tunggal (SettingsUi sudah kamu buat sebelumnya)
    val flow: Flow<SettingsUi> = ds.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { p ->
            SettingsUi(
                ownerName = p[Keys.OWNER_NAME] ?: "",
                darkMode = p[Keys.DARK_MODE] ?: false,
                notifEnabled = p[Keys.NOTIF_ENABLED] ?: true,
                saving = false
            )
        }

    // Setters
    suspend fun setOwnerName(name: String) = ds.edit { it[Keys.OWNER_NAME] = name }
    suspend fun setDarkMode(enabled: Boolean) = ds.edit { it[Keys.DARK_MODE] = enabled }
    suspend fun setNotifEnabled(enabled: Boolean) = ds.edit { it[Keys.NOTIF_ENABLED] = enabled }

    // Reset ke default
    suspend fun reset() = ds.edit {
        it[Keys.OWNER_NAME] = ""
        it[Keys.DARK_MODE] = false
        it[Keys.NOTIF_ENABLED] = true
    }
}
