package com.shirotenma.petpartnertest.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
private val KEY_TOKEN = stringPreferencesKey("auth_token")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val tokenFlow: Flow<String?> =
        context.appDataStore.data.map { prefs -> prefs[KEY_TOKEN] }

    suspend fun setToken(token: String?) {
        context.appDataStore.edit { prefs ->
            if (token.isNullOrBlank()) {
                prefs.remove(KEY_TOKEN)
            } else {
                prefs[KEY_TOKEN] = token
            }
        }
    }
}
