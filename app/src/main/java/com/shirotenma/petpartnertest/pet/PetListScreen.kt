package com.shirotenma.petpartnertest.pet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shirotenma.petpartnertest.Route
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    nav: NavController,
    vm: PetViewModel = hiltViewModel()
) {
    val pets by vm.pets.collectAsState()

    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val filtered by remember(pets, query) {
        derivedStateOf {
            if (query.text.isBlank()) pets
            else {
                val q = query.text.trim().lowercase()
                pets.filter { p ->
                    p.name.lowercase().contains(q) ||
                            p.species.lowercase().contains(q) ||
                            (p.birthDate ?: "").lowercase().contains(q) ||
                            (p.notes ?: "").lowercase().contains(q)
                }
            }
        }
    }

    val ptrState = rememberPullToRefreshState()
    var refreshing by remember { mutableStateOf(false) }

    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(700) // TODO vm.refresh()
            refreshing = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pets") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate(Route.PET_EDIT) }) { Text("+") }
        }
    ) { pad ->
        PullToRefreshBox(
            state = ptrState,
            isRefreshing = refreshing,
            onRefresh = { refreshing = true },
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            Column(Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    singleLine = true,
                    label = { Text("Search pets") },
                    placeholder = { Text("Name, species, date, notes…") }
                )

                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = if (query.text.isBlank())
                                "No pets yet.\nPull to refresh or tap + to add your first pet."
                            else
                                "No results for “${query.text}”. Pull to refresh to retry."
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        items(filtered, key = { it.id }) { pet ->
                            ListItem(
                                headlineContent = { Text(pet.name) },
                                supportingContent = {
                                    val sub = listOfNotNull(
                                        pet.species.ifBlank { null },
                                        pet.birthDate?.ifBlank { null }
                                    ).joinToString(" • ")
                                    if (sub.isNotBlank()) Text(sub)
                                },
                                trailingContent = {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = { nav.navigate("${Route.PET_EDIT}/${pet.id}") }
                                        ) { Icon(Icons.Filled.Edit, contentDescription = "Edit") }

                                        IconButton(
                                            onClick = { nav.navigate("${Route.RECORDS}/${pet.id}") }
                                        ) { Icon(Icons.Filled.Assignment, contentDescription = "Records") }

                                        IconButton(
                                            onClick = { nav.navigate("${Route.JOURNALS}/${pet.id}") }
                                        ) { Icon(Icons.Filled.Description, contentDescription = "Journals") }

                                        IconButton(
                                            onClick = { nav.navigate("${Route.SCAN}/${pet.id}") }
                                        ) { Icon(Icons.Filled.CameraAlt, contentDescription = "Scan") }

                                        IconButton(
                                            onClick = { nav.navigate("${Route.CHAT}?petId=${pet.id}") }
                                        ) { Icon(Icons.Filled.Chat, contentDescription = "Chat") }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { nav.navigate("${Route.RECORDS}/${pet.id}") }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
