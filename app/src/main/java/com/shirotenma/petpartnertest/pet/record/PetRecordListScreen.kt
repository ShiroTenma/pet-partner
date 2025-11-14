// app/src/main/java/com/shirotenma/petpartnertest/pet/record/PetRecordListScreen.kt
package com.shirotenma.petpartnertest.pet.record

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shirotenma.petpartnertest.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IosShare
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetRecordListScreen(
    nav: NavController,
    petId: Long,
    vm: PetRecordListViewModel = hiltViewModel()
) {
    val items by vm.records(petId).collectAsState(initial = emptyList())

    // --- Filter & Sort state ---
    val allTypes = remember(items) {
        items.map { it.type }.filter { it.isNotBlank() }.distinct().sorted()
    }
    var selectedType by remember { mutableStateOf<String?>(null) } // null = all
    var sortBy by remember { mutableStateOf("date") }               // "date" | "title"

    val shown = remember(items, selectedType, sortBy) {
        items
            .let { list -> if (selectedType == null) list else list.filter { it.type == selectedType } }
            .let { list ->
                when (sortBy) {
                    "title" -> list.sortedBy { it.title.lowercase() }
                    else    -> list.sortedByDescending { it.date } // terbaru duluan
                }
            }
    }

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medical Records") },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val json = vm.exportJson()
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "application/json"
                                putExtra(Intent.EXTRA_TEXT, json)
                                putExtra(Intent.EXTRA_SUBJECT, "Pet Medical Records (JSON)")
                            }
                            ctx.startActivity(Intent.createChooser(send, "Share records as JSON"))
                        }
                    }) {
                        Icon(Icons.Filled.IosShare, contentDescription = "Export JSON")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate("${Route.RECORD_EDIT}/$petId") }) {
                Text("+")
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            // --- Toolbar Filter & Sort (tanpa ExposedDropdownMenu) ---
            RecordListToolbar(
                allTypes = allTypes,
                selectedType = selectedType,
                onTypeChange = { selectedType = it },
                sortBy = sortBy,
                onSortChange = { sortBy = it }
            )

            if (shown.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = if (items.isEmpty())
                            "No records yet.\nTap + to add your first record."
                        else
                            "No records for this filter."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(shown, key = { it.id }) { r ->
                        ListItem(
                            headlineContent = { Text(r.title) },
                            supportingContent = {
                                val sub = listOfNotNull(
                                    r.type.ifBlank { null },
                                    r.date.ifBlank { null }
                                ).joinToString(" • ")
                                if (sub.isNotBlank()) Text(sub)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // EDIT → record_edit/{petId}/{recordId}
                                    nav.navigate("${Route.RECORD_EDIT}/${r.petId}/${r.id}")
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordListToolbar(
    allTypes: List<String>,
    selectedType: String?,
    onTypeChange: (String?) -> Unit,
    sortBy: String,
    onSortChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // === Filter by type (DropdownMenu biasa) ===
        var typeExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = selectedType ?: "All types",
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { typeExpanded = true }
            )
            DropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All types") },
                    onClick = { onTypeChange(null); typeExpanded = false }
                )
                allTypes.forEach { t ->
                    DropdownMenuItem(
                        text = { Text(t) },
                        onClick = { onTypeChange(t); typeExpanded = false }
                    )
                }
            }
        }

        // === Sort by (DropdownMenu biasa) ===
        var sortExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                value = if (sortBy == "title") "Title" else "Date",
                onValueChange = {},
                readOnly = true,
                label = { Text("Sort") },
                modifier = Modifier
                    .widthIn(min = 120.dp)
                    .clickable { sortExpanded = true }
            )
            DropdownMenu(
                expanded = sortExpanded,
                onDismissRequest = { sortExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Date (newest)") },
                    onClick = { onSortChange("date"); sortExpanded = false }
                )
                DropdownMenuItem(
                    text = { Text("Title (A–Z)") },
                    onClick = { onSortChange("title"); sortExpanded = false }
                )
            }
        }
    }
}
