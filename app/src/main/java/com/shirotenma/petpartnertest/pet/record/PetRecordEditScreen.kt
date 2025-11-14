// app/src/main/java/com/shirotenma/petpartnertest/pet/record/PetRecordEditScreen.kt
package com.shirotenma.petpartnertest.pet.record

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.activity.result.PickVisualMediaRequest


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

    var showConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.id == null) "Add Record" else "Edit Record") },
                actions = {
                    if (state.id != null) {
                        TextButton(onClick = { showConfirm = true }) { Text("Delete") }
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


// --- Attachment picker ---
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                if (uri != null) {
                    vm.edit { s -> s.copy(attachmentUri = uri.toString()) }
                }
            }

            OutlinedButton(
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text(if (state.attachmentUri == null) "Add Attachment" else "Change Attachment")
            }

// Preview kecil (jika ada)
            if (state.attachmentUri != null) {
                AsyncImage(
                    model = state.attachmentUri,
                    contentDescription = "Attachment",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

        }
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                title = { Text("Delete record?") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = false
                        vm.deleteCurrent { nav.popBackStack() }   // ⬅️ versi baru
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirm = false }) { Text("Cancel") }
                }
            )
        }
    }
    }