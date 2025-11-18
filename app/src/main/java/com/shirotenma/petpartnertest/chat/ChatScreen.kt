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
    petId: Long? = null,
    cond: String? = null,
    sev: String? = null,
    confidence: Double? = null,
    tips: List<String> = emptyList(),
    photoUri: String? = null,
    vm: ChatViewModel = hiltViewModel()
) {
    LaunchedEffect(cond, sev, confidence, tips) {
        vm.bootWithDiagnosis(cond, sev, confidence, tips)
    }

    val msgs by vm.messages.collectAsState()
    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pet Assistant") }) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("Tanya sesuatu…") }
                )
                Button(
                    enabled = input.isNotBlank(),
                    onClick = { vm.send(input); input = "" }
                ) { Text("Send") }
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
            items(msgs) { m ->
                Surface(
                    color = if (m.from == "bot") MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 1.dp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = m.text,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
