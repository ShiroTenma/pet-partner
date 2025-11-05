package com.shirotenma.petpartnertest

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onRegister: () -> Unit,
    onLoggedIn: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(uiState.loggedIn) { if (uiState.loggedIn) onLoggedIn() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Masuk", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
        OutlinedTextField(pass, { pass = it }, label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        Button(onClick = { vm.login(email, pass) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp), enabled = !uiState.loading) {
            Text(if (uiState.loading) "Loading..." else "Masuk")
        }
        TextButton(onClick = onRegister, modifier = Modifier.padding(top = 8.dp)) {
            Text("Daftar")
        }
        if (uiState.error != null) Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
    }
}
