package com.shirotenma.petpartnertest.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    photoUri: String?,
    vm: ChatViewModel = hiltViewModel()
) {
    // seed konteks sekali saat screen tampil
    LaunchedEffect(Unit) {
        vm.seedContext(petId, cond, sev, confidence, tips, photoUri)
    }

    val messages by vm.messages.collectAsState()
    var input by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Vet Chat") }) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Type a message…") }
                )
                Button(
                    enabled = input.isNotBlank() && !sending,
                    onClick = {
                        sending = true
                        vm.sendMessage(input)
                        input = ""
                        sending = false
                    }
                ) { Text(if (sending) "Sending…" else "Send") }
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { m ->
                val isBot = m.from == Sender.BOT
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        ProvideTextStyle(MaterialTheme.typography.labelMedium) {
                            Text(if (isBot) "Assistant" else "You")
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(m.text)
                    }
                }
            }
        }
    }
}
