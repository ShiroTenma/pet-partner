package com.shirotenma.petpartnertest.journal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

private data class BirdMessageUi(
    val id: Long,
    val title: String,
    val preview: String
)

@Composable
fun BirdMessageScreen(nav: NavController) {
    val messages = remember {
        mutableStateOf(
            listOf(
                BirdMessageUi(1, "Worried owner", "My cat has been sneezing for two days..."),
                BirdMessageUi(2, "Puppy advice", "First time grooming, any tips to calm him?")
            )
        )
    }
    val selected = remember { mutableStateOf<BirdMessageUi?>(null) }
    val replyText = remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesan Burung") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pad ->
        if (messages.value.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Belum ada Pesan Burung untuk dibalas.")
                Text("Tarik untuk refresh atau coba lagi nanti.")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages.value) { message ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selected.value = message
                                    replyText.value = ""
                                },
                            colors = CardDefaults.cardColors()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(message.title)
                                Text(
                                    message.preview,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                selected.value?.let { chosen ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Balas: ${chosen.title}")
                        OutlinedTextField(
                            value = replyText.value,
                            onValueChange = { replyText.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Tulis balasan") },
                            minLines = 3
                        )
                        Button(
                            onClick = {
                                // TODO: call reply endpoint
                                scope.launch {
                                    snackbar.showSnackbar("Balasan dikirim (stub)")
                                }
                                selected.value = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Kirim")
                        }
                    }
                }
            }
        }
    }
}
