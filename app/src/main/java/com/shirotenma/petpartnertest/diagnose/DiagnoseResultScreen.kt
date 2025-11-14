package com.shirotenma.petpartnertest.diagnose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.shirotenma.petpartnertest.Route

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
    vm: DiagnoseResultViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Diagnosis Result") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            ElevatedCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Condition: $condition", style = MaterialTheme.typography.titleMedium)
                    Text("Severity: $severity")
                    Text("Confidence: ${"%.0f".format(confidence * 100)}%")
                    if (tips.isNotEmpty()) {
                        Text("Suggested care:")
                        tips.forEach { Text("• $it") }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        vm.saveAsRecord(
                            petId = petId,
                            condition = condition,
                            severity = severity,
                            confidence = confidence,
                            tips = tips,
                            photoUri = photoUri
                        ) {
                            // setelah save → buka list records
                            nav.navigate("${Route.RECORDS}/$petId")
                        }
                    }
                ) { Text("Save to Records") }

                OutlinedButton(
                    onClick = {
                        // kirim context diagnosis ke chat
                        fun enc(s: String) = java.net.URLEncoder.encode(s, "UTF-8")
                        val tipsParam = tips.joinToString("|;|")
                        nav.navigate(
                            "${Route.CHAT}?petId=$petId" +
                                    "&cond=${enc(condition)}" +
                                    "&sev=${enc(severity)}" +
                                    "&conf=${confidence}" +
                                    "&tips=${enc(tipsParam)}" +
                                    "&uri=${enc(photoUri ?: "")}"
                        )
                    }
                ) { Text("Ask Chatbot") }
            }

            OutlinedButton(onClick = { nav.popBackStack() }) { Text("Back") }
        }
    }
}
