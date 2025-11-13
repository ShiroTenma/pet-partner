// app/src/main/java/com/shirotenma/petpartnertest/pet/record/PetRecordListScreen.kt
package com.shirotenma.petpartnertest.pet.record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shirotenma.petpartnertest.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetRecordListScreen(
    nav: NavController,
    petId: Long,
    vm: PetRecordListViewModel = hiltViewModel()
) {
    val items by vm.records(petId).collectAsState(initial = emptyList())

    // (optional) dialog placeholder buat future actions
    val (showInfo, setShowInfo) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Medical Records") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // ADD → record_edit/{petId}
                    nav.navigate("${Route.RECORD_EDIT}/$petId")
                }
            ) { Text("+") }
        }
    ) { pad ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text("No records yet.\nTap + to add your first record.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(items, key = { it.id }) { r ->
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
                            .fillMaxSize()
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

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { setShowInfo(false) },
            title = { Text("Info") },
            text = { Text("Coming soon") },
            confirmButton = {
                TextButton(onClick = { setShowInfo(false) }) { Text("OK") }
            }
        )
    }
}
