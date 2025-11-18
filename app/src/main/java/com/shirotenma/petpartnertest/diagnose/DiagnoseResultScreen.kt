// app/src/main/java/com/shirotenma/petpartnertest/diagnose/DiagnoseResultScreen.kt
package com.shirotenma.petpartnertest.diagnose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.shirotenma.petpartnertest.Route
import kotlinx.coroutines.launch

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
    vm: DiagnoseViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var saving by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

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
            AsyncImage(
                model = photoUri,
                contentDescription = "Scanned photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

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

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { nav.navigate("${Route.SCAN}/$petId") }) {
                    Text("Scan again")
                }
                Button(
                    onClick = {
                        // lempar konteks ke chat (biar langsung lanjut konsultasi)
                        val enc = { s: String -> java.net.URLEncoder.encode(s, "UTF-8") }
                        val packedTips = tips.joinToString("|;|")
                        nav.navigate(
                            "${Route.CHAT}?" +
                                    "petId=$petId&" +
                                    "cond=${enc(condition)}&" +
                                    "sev=${enc(severity)}&" +
                                    "conf=$confidence&" +
                                    "tips=${enc(packedTips)}&" +
                                    "uri=${enc(photoUri)}"
                        )
                    }
                ) { Text("Open Chat") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = !saving,
                    onClick = {
                        saving = true
                        scope.launch {
                            runCatching {
                                vm.saveAsRecord(petId, condition, severity, confidence, tips, photoUri)
                            }.onSuccess {
                                toast = "Saved to records"
                                // opsional: langsung buka list records
                                nav.navigate("${Route.RECORDS}/$petId")
                            }.onFailure {
                                toast = it.message ?: "Failed to save"
                            }
                            saving = false
                        }
                    }
                ) { Text(if (saving) "Saving…" else "Save to records") }

                // tombol ke Chat (FR-6 di bawah)
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
            }

            // snack/toast sederhana
            if (toast != null) {
                LaunchedEffect(toast) { kotlinx.coroutines.delay(1500); toast = null }
                Text(
                    text = toast!!,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        }
    }

