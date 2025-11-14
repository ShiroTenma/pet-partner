package com.shirotenma.petpartnertest

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nav: NavController,
    ownerName: String,                 // ⬅️ cukup terima dari atas
    vm: AuthViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ownerName.isNotBlank()) "Hi, $ownerName" else "Home") },
                actions = {
                    IconButton(onClick = { nav.navigate(Route.SETTINGS) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { nav.navigate(Route.PETS) }) { Text("Open Pets") }
            OutlinedButton(onClick = { vm.logout() }) { Text("Logout") }
            Button(onClick = { nav.navigate(Route.CHAT) }) { Text("Open Chatbot") }

        }
    }
}
