// app/src/main/java/com/shirotenma/petpartnertest/pet/record/PetRecordEditScreen.kt
package com.shirotenma.petpartnertest.pet.record

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetRecordEditScreen(
    nav: NavController,
    petId: Long,
    id: Long? = null,
    vm: PetRecordViewModel = hiltViewModel()
) {
    LaunchedEffect(petId, id) {
        if (id == null) vm.startNew(petId) else vm.load(id)
    }

    val state = vm.ui.collectAsState().value
    if (state == null) {
        Box(Modifier.fillMaxSize()) { CircularProgressIndicator() }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.id == null) "Add Record" else "Edit Record") },
                actions = {
                    if (state.id != null) {
                        TextButton(onClick = { vm.delete { nav.popBackStack() } }) {
                            Text("Delete")
                        }
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.type,
                onValueChange = { new -> vm.edit { s -> s.copy(type = new) } },
                label = { Text("Type (e.g. Vaccination)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.title,
                onValueChange = { new -> vm.edit { s -> s.copy(title = new) } },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.date,
                onValueChange = { new -> vm.edit { s -> s.copy(date = new) } },
                label = { Text("Date (yyyy-mm-dd)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = { new -> vm.edit { s -> s.copy(notes = new) } },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { vm.save { nav.popBackStack() } }) { Text("Save") }
                OutlinedButton(onClick = { nav.popBackStack() }) { Text("Cancel") }
            }
        }
    }
}
