package com.shirotenma.petpartnertest.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    nav: NavController,
    petId: Long?,
    cond: String?,
    sev: String?,
    confidence: Double?,
    tips: List<String>,
    photoUri: String?
) {
    // State chat super sederhana (sementara)
    var input by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }

    // Prefill pembuka jika datang dari hasil diagnosis
    LaunchedEffect(cond, sev, confidence, tips, photoUri, petId) {
        if (messages.isEmpty()) {
            val header = buildString {
                appendLine("📋 Context awal:")
                petId?.let { appendLine("- Pet ID: $it") }
                cond?.let { appendLine("- Dugaan kondisi: $it") }
                sev?.let { appendLine("- Tingkat keparahan: $it") }
                confidence?.let { appendLine("- Confidence: ${"%.2f".format(it)}") }
                if (tips.isNotEmpty()) {
                    appendLine("- Tips awal:")
                    tips.forEach { t -> appendLine("  • $t") }
                }
                photoUri?.let { appendLine("- Foto: $it") }
            }.trim()
            if (header.isNotBlank()) messages += header
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vet Chat") },
                navigationIcon = {
                    TextButton(onClick = { nav.popBackStack() }) { Text("Back") }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Daftar pesan (dummy dulu)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages) { msg ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            msg,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Input bar
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Ketik pesan…") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (input.isNotBlank()) {
                            messages += "👤: $input"
                            // TODO: panggil backend/LLM kamu di sini, lalu tambahkan balasan:
                            // messages += "🤖: <jawaban model>"
                            input = ""
                        }
                    }
                ) { Text("Send") }
            }
        }
    }
}
