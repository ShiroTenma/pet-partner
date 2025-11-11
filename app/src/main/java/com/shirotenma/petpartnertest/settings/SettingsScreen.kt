// app/src/main/java/com/shirotenma/petpartnertest/settings/SettingsScreen.kt
package com.shirotenma.petpartnertest.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    nav: androidx.navigation.NavController,
    vm: com.shirotenma.petpartnertest.settings.SettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = vm.name,
                onValueChange = vm::onNameChange,
                label = { Text("Owner name") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dark mode")
                Switch(checked = vm.dark, onCheckedChange = vm::onDarkChange)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Enable notifications")
                Switch(checked = vm.notif, onCheckedChange = vm::onNotifChange)
            }

            Button(
                onClick = { vm.save { nav.popBackStack() } },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}
