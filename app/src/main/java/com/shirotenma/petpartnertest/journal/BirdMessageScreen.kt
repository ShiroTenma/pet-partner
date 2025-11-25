package com.shirotenma.petpartnertest.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirdMessageScreen(
    nav: NavController,
    vm: BirdMessageViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val replyText = remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    androidx.compose.runtime.LaunchedEffect(Unit) { vm.loadRandom() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesan Burung") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    replyText.value = ""
                    vm.loadRandom()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("From another forest")
            }

            when {
                state.loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }

                state.message == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(state.error ?: "Belum ada Pesan Burung untuk dibalas.")
                    }
                }

                else -> {
                    val msg = state.message!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(msg.title)
                            Text(
                                msg.preview,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            msg.lastReply?.let {
                                Text(
                                    "Balasan terakhir: $it",
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = replyText.value,
                            onValueChange = { replyText.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Tulis balasan") },
                            minLines = 3
                        )
                        Button(
                            onClick = {
                                vm.reply(
                                    msg.id,
                                    replyText.value,
                                    onDone = {
                                        scope.launch { snackbar.showSnackbar("Balasan dikirim") }
                                        replyText.value = ""
                                    },
                                    onError = { err ->
                                        scope.launch { snackbar.showSnackbar(err.message ?: "Gagal mengirim") }
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = replyText.value.isNotBlank()
                        ) {
                            Text("Kirim")
                        }
                    }
                }
            }
        }
    }
}
