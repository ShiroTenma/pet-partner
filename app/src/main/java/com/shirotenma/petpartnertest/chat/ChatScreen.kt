package com.shirotenma.petpartnertest.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shirotenma.petpartnertest.chatbot.ChatContext
import com.shirotenma.petpartnertest.chatbot.Sender

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
    LaunchedEffect(cond, sev, confidence) {
        val ctx = ChatContext(
            species = cond?.substringBefore("_")?.takeIf { it == "cat" || it == "dog" },
            diseaseCode = cond,
            diseaseConfidence = confidence?.toFloat()
        )
        vm.setContext(ctx)
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
                    label = { Text("Tulis pesan...") }
                )
                Button(
                    enabled = input.isNotBlank() && !sending,
                    onClick = {
                        sending = true
                        vm.sendMessage(input)
                        input = ""
                        sending = false
                    }
                ) { Text(if (sending) "Sending..." else "Send") }
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
