package com.shirotenma.petpartnertest

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavController, vm: AuthViewModel = hiltViewModel()) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Home") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = { nav.navigate(Route.PETS) }) {
                Text("Open Pets")
            }
            OutlinedButton(onClick = { vm.logout() }) {
                Text("Logout")
            }
        }
    }
}
