// app/src/main/java/com/shirotenma/petpartnertest/settings/SettingsScreen.kt
package com.shirotenma.petpartnertest.settings

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    nav: NavController? = null,
    vm: SettingsViewModel = hiltViewModel()
) {
    val ui by vm.uiState.collectAsState()
    val ctx = LocalContext.current
    val versionText = remember { appVersionText(ctx) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    // Tampilkan tombol back hanya jika ada NavController
                    if (nav != null) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nama pemilik
            OutlinedTextField(
                value = ui.ownerName,
                onValueChange = vm::onNameChange,
                label = { Text("Owner name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { vm.save() })
            )

            // Dark mode
            SettingSwitchRow(
                title = "Dark mode",
                subtitle = "Use dark theme throughout the app",
                checked = ui.darkMode,
                onCheckedChange = vm::onDarkChange
            )

            // Notifikasi
            SettingSwitchRow(
                title = "Notifications",
                subtitle = "Enable reminders & updates",
                checked = ui.notifEnabled,
                onCheckedChange = vm::onNotifChange
            )

            // Tombol aksi
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { vm.save() },
                    enabled = !ui.saving && ui.ownerName.isNotBlank()
                ) {
                    if (ui.saving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 8.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    Text(if (ui.saving) "Saving…" else "Save")
                }

                OutlinedButton(
                    onClick = { vm.resetToDefaults() },
                    enabled = !ui.saving
                ) {
                    Text("Reset")
                }
            }

            Spacer(Modifier.weight(1f))

            // Versi aplikasi (tanpa BuildConfig)
            ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                Text(
                    text = versionText,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(2.dp))
            ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

/** Ambil versi app tanpa BuildConfig */
private fun appVersionText(ctx: Context): String {
    val pm = ctx.packageManager
    val pkg = ctx.packageName
    return try {
        val info = pm.getPackageInfo(pkg, 0)
        @Suppress("DEPRECATION")
        val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            info.longVersionCode.toString()
        else info.versionCode.toString()
        val name = info.versionName ?: "-"
        "Version $name ($code)"
    } catch (_: Throwable) {
        "Version –"
    }
}
