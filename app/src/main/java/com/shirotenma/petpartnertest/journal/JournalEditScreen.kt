package com.shirotenma.petpartnertest.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shirotenma.petpartnertest.Route
import kotlinx.coroutines.launch

@Composable
fun JournalEditScreen(nav: NavController, petId: Long, journalId: Long?) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val title = if (journalId == null) "Add Journal" else "Edit Journal"

    val moodState = remember { androidx.compose.runtime.mutableStateOf("") }
    val noteState = remember { androidx.compose.runtime.mutableStateOf("") }

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
                value = moodState.value,
                onValueChange = { moodState.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mood") },
                singleLine = true
            )
            OutlinedTextField(
                value = noteState.value,
                onValueChange = { noteState.value = it },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text("Journal") },
                minLines = 6
            )

            Button(
                onClick = {
                    // TODO: send to repository then navigate back.
                    nav.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        snackbar.showSnackbar("Shared as Bird Message (stub)")
                    }
                    nav.navigate(Route.BIRD_MESSAGES)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Share")
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Bagikan sebagai Pesan Burung")
            }
        }
    }
}
