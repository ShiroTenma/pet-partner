package com.shirotenma.petpartnertest.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEditScreen(
    nav: NavController,
    petId: Long,
    scheduleId: Long?,
    vm: ScheduleEditViewModel = hiltViewModel()
) {
    LaunchedEffect(petId, scheduleId) {
        if (scheduleId == null) vm.startNew(petId) else vm.load(scheduleId)
    }

    val ui = vm.ui.collectAsState().value
    if (ui == null) {
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ui.id == 0L) "Add Schedule" else "Edit Schedule") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                value = ui.title,
                onValueChange = { vm.edit { cur -> cur.copy(title = it) } },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = ui.type,
                onValueChange = { vm.edit { cur -> cur.copy(type = it) } },
                label = { Text("Type (e.g. Vaksin, Grooming)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = ui.date,
                onValueChange = { vm.edit { cur -> cur.copy(date = it) } },
                label = { Text("Date (yyyy-mm-dd)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = ui.time,
                onValueChange = { vm.edit { cur -> cur.copy(time = it) } },
                label = { Text("Time (HH:mm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = ui.notes,
                onValueChange = { vm.edit { cur -> cur.copy(notes = it) } },
                label = { Text("Notes (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, false),
                minLines = 3
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Remind me")
                Switch(
                    checked = ui.remind,
                    onCheckedChange = { vm.edit { cur -> cur.copy(remind = it) } }
                )
            }

            Button(
                onClick = { vm.save { nav.popBackStack() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = !ui.saving
            ) { Text(if (ui.saving) "Saving..." else "Save") }

            OutlinedButton(
                onClick = { nav.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cancel") }
        }
    }
}
