package com.shirotenma.petpartnertest.journal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shirotenma.petpartnertest.Route

private data class JournalUi(
    val id: Long,
    val mood: String,
    val content: String,
    val dateLabel: String
)

@Composable
fun JournalListScreen(nav: NavController, petId: Long) {
    // Placeholder data until repository is wired.
    val sample = remember {
        mutableStateOf(
            listOf(
                JournalUi(1, "Happy", "Walked in the park, good appetite.", "Today"),
                JournalUi(2, "Tired", "Slept most of the day, mild sneeze.", "Yesterday")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journals") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                nav.navigate("${Route.JOURNAL_EDIT}/$petId")
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Journal")
            }
        }
    ) { pad ->
        if (sample.value.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("No journal entries yet.", style = MaterialTheme.typography.bodyLarge)
                Text("Tap + to add your first entry.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sample.value) { journal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                nav.navigate("${Route.JOURNAL_EDIT}/$petId/${journal.id}")
                            },
                        colors = CardDefaults.cardColors()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(journal.dateLabel, style = MaterialTheme.typography.labelMedium)
                            Text(journal.mood, style = MaterialTheme.typography.titleMedium)
                            Text(
                                journal.content,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
