package com.shirotenma.petpartnertest.pet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetEditScreen(
    nav: NavController,
    petId: Long?,                        // null = tambah baru, >0 = edit
    vm: PetViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    // State form (rememberSaveable biar rotasi aman)
    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var species by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var birth by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var notes by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    // Saat edit, load data existing sekali lalu isi form
    LaunchedEffect(petId) {
        if (petId != null && petId > 0) {
            vm.observePet(petId)
                .filterNotNull()
                .collect { p ->
                    name = TextFieldValue(p.name)
                    species = TextFieldValue(p.species)
                    birth = TextFieldValue(p.birthDate.orEmpty())
                    notes = TextFieldValue(p.notes.orEmpty())
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (petId == null) "Add Pet" else "Edit Pet") }
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
                value = name, onValueChange = { name = it },
                label = { Text("Name*") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = species, onValueChange = { species = it },
                label = { Text("Species*") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = birth, onValueChange = { birth = it },
                label = { Text("Birth date (yyyy-mm-dd)") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                label = { Text("Notes") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            vm.savePet(
                                id = petId,
                                name = name.text,
                                species = species.text,
                                birthDate = birth.text.ifBlank { null },
                                notes = notes.text.ifBlank { null }
                            ) { nav.popBackStack() }
                        }
                    }
                ) { Text("Save") }

                if (petId != null && petId > 0) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                vm.deletePet(
                                    Pet(
                                        id = petId,
                                        name = name.text,
                                        species = species.text,
                                        birthDate = birth.text.ifBlank { null },
                                        notes = notes.text.ifBlank { null }
                                    )
                                ) { nav.popBackStack() }
                            }
                        }
                    ) { Text("Delete") }
                }

                OutlinedButton(onClick = { nav.popBackStack() }) {
                    Text("Cancel")
                }
            }
        }
    }
}
