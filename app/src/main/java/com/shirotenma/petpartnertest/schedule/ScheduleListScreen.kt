package com.shirotenma.petpartnertest.schedule

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shirotenma.petpartnertest.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(
    nav: NavController,
    petId: Long,
    vm: ScheduleListViewModel = hiltViewModel()
) {
    LaunchedEffect(petId) { vm.load(petId) }
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedules") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate("${Route.SCHEDULE_EDIT}/$petId") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add schedule")
            }
        }
    ) { pad ->
        when {
            state.loading -> {
                Column(
                    modifier = Modifier
                        .padding(pad)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
            }
            state.items.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .padding(pad)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No schedules yet.", style = MaterialTheme.typography.bodyLarge)
                    Text("Tap + to add a new one.", style = MaterialTheme.typography.bodyMedium)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(pad)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { nav.navigate("${Route.SCHEDULE_EDIT}/$petId/${item.id}") },
                            colors = CardDefaults.cardColors()
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(item.title, style = MaterialTheme.typography.titleMedium)
                                Text("${item.date} ${item.time}", style = MaterialTheme.typography.bodyMedium)
                                Text(item.type, style = MaterialTheme.typography.labelMedium)
                                item.notes?.takeIf { it.isNotBlank() }?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { vm.delete(item.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
