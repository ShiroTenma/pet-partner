package com.shirotenma.petpartnertest.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shirotenma.petpartnertest.Route
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEditScreen(
    nav: NavController,
    petId: Long,
    journalId: Long?,
    vm: JournalEditViewModel = hiltViewModel()
) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(petId, journalId) {
        if (journalId == null) vm.startNew(petId) else vm.load(petId, journalId)
    }

    val ui by vm.ui.collectAsState()

    if (ui == null) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
        return
    }

    val title = if (ui!!.id == null) "Add Journal" else "Edit Journal"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = ui!!.mood,
                onValueChange = { new -> vm.edit { it.copy(mood = new) } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mood") },
                singleLine = true
            )
            OutlinedTextField(
                value = ui!!.content,
                onValueChange = { new -> vm.edit { it.copy(content = new) } },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text("Journal") },
                minLines = 6
            )
            OutlinedTextField(
                value = ui!!.date,
                onValueChange = { new -> vm.edit { it.copy(date = new) } },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Date (yyyy-mm-dd)") },
                singleLine = true
            )

            Button(
                onClick = { vm.save { nav.popBackStack() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = ui?.saving != true
            ) {
                Text("Save")
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        vm.shareAsBirdMessage {
                            scope.launch { snackbar.showSnackbar("Shared as Pesan Burung") }
                            nav.navigate(Route.BIRD_MESSAGES)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = ui?.saving != true && ui?.canShare == true
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Share")
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(if (ui?.canShare == true) "Bagikan sebagai Pesan Burung" else "Sudah mengirim")
            }
            ui?.shareError?.let {
                Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
        }
    }
}
