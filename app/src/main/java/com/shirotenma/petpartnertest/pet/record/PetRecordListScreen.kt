// app/src/main/java/com/shirotenma/petpartnertest/pet/record/PetRecordListScreen.kt
package com.shirotenma.petpartnertest.pet.record

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetRecordListScreen(
    nav: NavController,
    petId: Long,
    vm: PetRecordListViewModel = hiltViewModel()
) {
    val items by vm.records(petId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Medical Records") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // arahkan ke editor (mode add)
                    nav.navigate("record_edit?petId=$petId")
                }
            ) { Text("+") }
        }
    ) { pad ->
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
                        val sub = listOfNotNull(r.type.ifBlank { null }, r.date.ifBlank { null })
                            .joinToString(" • ")
                        if (sub.isNotBlank()) Text(sub)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // menuju edit
                            nav.navigate("record_edit?petId=${r.petId}&id=${r.id}")
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                HorizontalDivider()
            }
        }
    }
}
