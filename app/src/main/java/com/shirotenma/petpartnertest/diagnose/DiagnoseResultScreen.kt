// app/src/main/java/com/shirotenma/petpartnertest/diagnose/DiagnoseResultScreen.kt
package com.shirotenma.petpartnertest.diagnose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.shirotenma.petpartnertest.Route
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnoseResultScreen(
    nav: NavController,
    petId: Long,
    condition: String,
    severity: String,
    confidence: Double,
    tips: List<String>,
    photoUri: String?,
    vm: DiagnosisViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var saving by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    fun enc(s: String?): String = URLEncoder.encode(s ?: "", "UTF-8")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Diagnosis Result") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Preview foto (opsional)
            if (!photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Scanned photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            ElevatedCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Condition: $condition", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Severity: $severity")
                    Text(text = "Confidence: ${(confidence * 100).toInt()}%")
                }
            }

            if (tips.isNotEmpty()) {
                ElevatedCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Suggested Care", style = MaterialTheme.typography.titleMedium)
                        tips.forEach { tip -> Text("• $tip") }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Aksi utama
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = !saving,
                    onClick = {
                        saving = true
                        scope.launch {
                            try {
                                vm.saveAsRecord(
                                    petId = petId,
                                    condition = condition,
                                    severity = severity,
                                    confidence = confidence,
                                    tips = tips,
                                    photoUri = photoUri
                                )
                                toast = "Saved to records"
                                nav.navigate("${Route.RECORDS}/$petId")
                            } catch (e: Exception) {
                                toast = e.message ?: "Failed to save"
                            } finally {
                                saving = false
                            }
                        }
                    }
                ) { Text(if (saving) "Saving…" else "Save to records") }

                OutlinedButton(
                    onClick = {
                        fun enc(s: String) = java.net.URLEncoder.encode(s, "UTF-8")
                        val tipsStr = tips.joinToString("|;|")
                        nav.navigate(
                            "${Route.CHAT}?petId=$petId" +
                                    "&cond=${enc(condition)}&sev=${enc(severity)}" +
                                    "&conf=${confidence}&tips=${enc(tipsStr)}&uri=${enc(photoUri ?: "")}"
                        )
                    }
                ) { Text("Discuss in Chat") }
                // diagnose/DiagnoseResultScreen.kt (di bagian tombol aksi)
                OutlinedButton(onClick = {
                    val text = buildString {
                        append("Diagnosis: $condition\n")
                        append("Severity: $severity\n")
                        append("Confidence: ${"%.0f%%".format(confidence*100)}\n")
                        if (tips.isNotEmpty()) {
                            append("Tips:\n")
                            tips.forEach { append("• $it\n") }
                        }
                        photoUri?.let { append("\nPhoto: $it") }
                    }
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Pet Diagnosis ($condition)")
                        putExtra(android.content.Intent.EXTRA_TEXT, text)
                    }
                    nav.context.startActivity(android.content.Intent.createChooser(intent, "Share diagnosis"))
                }) { Text("Share") }


            }

            // “Toast” sederhana
            if (toast != null) {
                LaunchedEffect(toast) {
                    kotlinx.coroutines.delay(1500)
                    toast = null
                }
                Text(
                    text = toast!!,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
