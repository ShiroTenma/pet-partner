// app/src/main/java/com/shirotenma/petpartnertest/RegisterScreen.kt
package com.shirotenma.petpartnertest

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit, // ⬅️ dipanggil saat sukses agar redirect ke HOME
    vm: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var showPass2 by remember { mutableStateOf(false) }
    var submitting by remember { mutableStateOf(false) }

    val nameOk = name.trim().isNotEmpty()
    val emailOk = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passOk = pass.length >= 8
    val same = pass == pass2
    val formOk = nameOk && emailOk && passOk && same && !submitting

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Daftar", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nama") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                isError = !nameOk && name.isNotBlank(),
                supportingText = { if (!nameOk && name.isNotBlank()) Text("Nama wajib diisi") }
            )

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                isError = !emailOk && email.isNotBlank(),
                supportingText = { if (!emailOk && email.isNotBlank()) Text("Format email tidak valid") }
            )

            OutlinedTextField(
                value = pass, onValueChange = { pass = it },
                label = { Text("Password (min 8)") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                isError = !passOk && pass.isNotBlank(),
                supportingText = { if (!passOk && pass.isNotBlank()) Text("Password minimal 8 karakter") },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPass = !showPass }, enabled = !submitting) {
                        Text(if (showPass) "Hide" else "Show")
                    }
                }
            )

            OutlinedTextField(
                value = pass2, onValueChange = { pass2 = it },
                label = { Text("Konfirmasi Password") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                isError = !same && pass2.isNotBlank(),
                supportingText = { if (!same && pass2.isNotBlank()) Text("Konfirmasi tidak sama") },
                visualTransformation = if (showPass2) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPass2 = !showPass2 }, enabled = !submitting) {
                        Text(if (showPass2) "Hide" else "Show")
                    }
                }
            )

            Button(
                onClick = {
                    submitting = true
                    focus.clearFocus(); keyboard?.hide()
                    vm.register(name.trim(), email.trim(), pass) { ok, err ->
                        submitting = false
                        if (ok) {
                            // langsung redirect ke HOME
                            onRegistered()
                        } else {
                            scope.launch {
                                val msg = when (err) {
                                    "EMAIL_ALREADY_USED" -> "Email sudah terdaftar"
                                    else -> "Terjadi kesalahan. Coba lagi."
                                }
                                snackbarHostState.showSnackbar(msg)
                            }
                        }
                    }
                },
                enabled = formOk,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (submitting) CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (submitting) "Memproses…" else "Daftar")
            }

            TextButton(
                onClick = onBack,
                enabled = !submitting,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Sudah punya akun? Masuk") }
        }
    }
}
