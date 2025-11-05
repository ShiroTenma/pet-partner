package com.shirotenma.petpartnertest.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("session_prefs")
private val KEY_TOKEN = stringPreferencesKey("auth_token")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }

    suspend fun setToken(token: String?) {
        context.dataStore.edit { prefs ->
            if (token.isNullOrBlank()) {
                prefs.remove(KEY_TOKEN)        // OK di MutablePreferences
            } else {
                prefs[KEY_TOKEN] = token       // OK di MutablePreferences
            }
        }
    }
}
