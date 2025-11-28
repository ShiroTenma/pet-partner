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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlinx.coroutines.launch

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

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val notifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) scope.launch { snackbar.showSnackbar("Izinkan notifikasi agar reminder aktif.") }
    }

    fun ensureFutureDate(): Boolean {
        return try {
            val dt = LocalDateTime.of(LocalDate.parse(ui.date), LocalTime.parse(ui.time))
            if (dt.isBefore(LocalDateTime.now())) {
                scope.launch { snackbar.showSnackbar("Tanggal/jam sudah lewat.") }
                false
            } else true
        } catch (_: Exception) {
            scope.launch { snackbar.showSnackbar("Format tanggal/jam tidak valid.") }
            false
        }
    }

    fun requestNotifIfNeeded(): Boolean {
        if (!ui.remind) return true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        val granted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        if (!granted) notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        return granted
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
        },
        snackbarHost = { SnackbarHost(snackbar) }
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
                onClick = {
                    if (!ensureFutureDate()) return@Button
                    if (!requestNotifIfNeeded()) return@Button
                    vm.save { nav.popBackStack() }
                },
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
