// app/src/main/java/com/shirotenma/petpartnertest/diagnose/DiagnosisPrefs.kt
package com.shirotenma.petpartnertest.diagnose

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Application.diagDataStore by preferencesDataStore("diagnosis_prefs")
private val KEY_CONSENT = booleanPreferencesKey("consentAccepted")
private val KEY_CLOUD   = booleanPreferencesKey("useCloudDiagnosis")

@Singleton
class DiagnosisPrefs @Inject constructor(private val app: Application) {
    val consent = app.diagDataStore.data.map { it[KEY_CONSENT] ?: false }
    val useCloud = app.diagDataStore.data.map { it[KEY_CLOUD] ?: false }

    suspend fun setConsent(v: Boolean) = app.diagDataStore.edit { it[KEY_CONSENT] = v }
    suspend fun setUseCloud(v: Boolean) = app.diagDataStore.edit { it[KEY_CLOUD] = v }
}
