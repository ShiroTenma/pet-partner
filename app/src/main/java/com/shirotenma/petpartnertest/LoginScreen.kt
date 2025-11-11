// app/src/main/java/com/shirotenma/petpartnertest/LoginScreen.kt
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
fun LoginScreen(
    onRegister: () -> Unit,
    onLoggedIn: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    // form state
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var submitting by remember { mutableStateOf(false) }

    // helpers
    val emailOk = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passOk = pass.length >= 8
    val formOk = emailOk && passOk && !submitting

    // UX
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
            Text("Masuk", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = !emailOk && email.isNotBlank(),
                supportingText = {
                    if (!emailOk && email.isNotBlank())
                        Text("Format email tidak valid")
                }
            )

            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Password (min 8)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = !passOk && pass.isNotBlank(),
                supportingText = {
                    if (!passOk && pass.isNotBlank())
                        Text("Password minimal 8 karakter")
                },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPass = !showPass }, enabled = !submitting) {
                        Text(if (showPass) "Hide" else "Show")
                    }
                }
            )

            Button(
                onClick = {
                    submitting = true
                    focus.clearFocus(); keyboard?.hide()
                    vm.login(email.trim(), pass) { ok ->
                        submitting = false
                        if (ok) onLoggedIn() else scope.launch {
                            snackbarHostState.showSnackbar("Email atau password salah")
                        }
                    }
                },
                enabled = formOk,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (submitting) CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (submitting) "Memproses…" else "Masuk")
            }

            TextButton(
                onClick = onRegister,
                enabled = !submitting,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Belum punya akun? Daftar") }
        }
    }
}
