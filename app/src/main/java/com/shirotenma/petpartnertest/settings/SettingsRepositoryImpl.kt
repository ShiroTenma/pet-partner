package com.shirotenma.petpartnertest.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @Named("settings") private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object Keys {
        val NAME = stringPreferencesKey("owner_name")
        val DARK = booleanPreferencesKey("dark_mode")
        val NOTIF = booleanPreferencesKey("notif_enabled")
    }

    override val ui: Flow<SettingsUi> = dataStore.data.map { p ->
        SettingsUi(
            ownerName = p[Keys.NAME] ?: "",
            darkMode = p[Keys.DARK] ?: false,
            notifEnabled = p[Keys.NOTIF] ?: true
        )
    }

    override suspend fun save(ownerName: String, dark: Boolean, notif: Boolean) {
        dataStore.edit { pref ->
            pref[Keys.NAME] = ownerName
            pref[Keys.DARK] = dark
            pref[Keys.NOTIF] = notif
        }
    }
}
