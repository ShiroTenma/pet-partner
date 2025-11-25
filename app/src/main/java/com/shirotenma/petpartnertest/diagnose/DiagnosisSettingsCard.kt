// app/src/main/java/com/shirotenma/petpartnertest/diagnose/DiagnosisSettingsCard.kt
package com.shirotenma.petpartnertest.diagnose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosisSettingsVM @Inject constructor(
    private val prefs: DiagnosisPrefs
) : androidx.lifecycle.ViewModel() {
    val consent = prefs.consent
    val cloud   = prefs.useCloud
    fun setConsent(v: Boolean) = kotlinx.coroutines.GlobalScope.launch { prefs.setConsent(v) }
    fun setCloud(v: Boolean)   = kotlinx.coroutines.GlobalScope.launch { prefs.setUseCloud(v) }
}

@Composable
fun DiagnosisSettingsCard(
    vm: DiagnosisSettingsVM = hiltViewModel(),
    onBack: (() -> Unit)? = null
) {
    val consent by vm.consent.collectAsState(initial = false)
    val cloud by vm.cloud.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    Card(Modifier.padding(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                onBack?.let {
                    IconButton(onClick = it) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
                Text("Diagnosis Settings")
            }
            RowWithSwitch(
                title = "Saya setuju pemrosesan gambar",
                checked = consent,
                onChange = { v -> scope.launch { vm.setConsent(v) } }
            )
            RowWithSwitch(
                title = "Gunakan Cloud Diagnosis (jika tersedia)",
                checked = cloud,
                onChange = { v -> scope.launch { vm.setCloud(v) } }
            )
        }
    }
}

@Composable
private fun RowWithSwitch(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
