package com.shirotenma.petpartnertest.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore untuk settings
private val Context.settingsDataStore by preferencesDataStore(name = "settings_prefs")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val app: Context
) : SettingsRepository {

    private val KEY_NAME = stringPreferencesKey("owner_name")
    private val KEY_DARK = booleanPreferencesKey("dark_mode")
    private val KEY_NOTIF = booleanPreferencesKey("notif_enabled")

    override val nameFlow: Flow<String> =
        app.settingsDataStore.data.map { it[KEY_NAME].orEmpty() }

    override val darkFlow: Flow<Boolean> =
        app.settingsDataStore.data.map { it[KEY_DARK] ?: false }

    override val notifFlow: Flow<Boolean> =
        app.settingsDataStore.data.map { it[KEY_NOTIF] ?: true }

    override suspend fun setName(name: String) {
        app.settingsDataStore.edit { it[KEY_NAME] = name }
    }

    override suspend fun setDark(enabled: Boolean) {
        app.settingsDataStore.edit { it[KEY_DARK] = enabled }
    }

    override suspend fun setNotif(enabled: Boolean) {
        app.settingsDataStore.edit { it[KEY_NOTIF] = enabled }
    }
}
