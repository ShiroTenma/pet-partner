package com.shirotenma.petpartnertest.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shirotenma.petpartnertest.Route
import com.shirotenma.petpartnertest.chatbot.ChatContext
import com.shirotenma.petpartnertest.chatbot.Sender
import com.shirotenma.petpartnertest.chatbot.ChatMessage
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    nav: NavController,
    petId: Long?,
    cond: String?,
    sev: String?,
    confidence: Double?,
    tips: List<String>,
    photoUri: String?,
    supported: Boolean = true,
    vm: ChatViewModel = hiltViewModel()
) {
    LaunchedEffect(cond, sev, confidence) {
        val ctx = ChatContext(
            species = cond?.substringBefore("_")?.takeIf { it == "cat" || it == "dog" },
            diseaseCode = cond,
            diseaseConfidence = confidence?.toFloat(),
            severity = sev,
            tips = tips,
            isSupportedAnimal = supported
        )
        vm.setContext(ctx)
    }

    val messages by vm.messages.collectAsState()
    var input by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }

    val quickActions = vm.quickActions
    val contextText = buildString {
        cond?.let { append("Kemungkinan: $it\n") }
        sev?.let { append("Perkiraan keparahan: $sev\n") }
        confidence?.let { append("Keyakinan model: ${(it * 100).toInt()}%\n") }
        append("Hasil AI hanya skrining awal, bukan diagnosis pasti.")
    }.trim()
    val disclaimer = if (supported)
        "Jika ragu atau gejala berat, segera konsultasi dokter hewan."
    else "Foto tidak terdeteksi kucing/anjing. Unggah foto baru yang jelas, atau hubungi klinik terdekat."
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val actions = if (supported) quickActions else quickActions.filter { it.first.contains("Maps") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vet Chat") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Tulis pesan...") }
                )
                Button(
                    enabled = input.isNotBlank() && !sending,
                    onClick = {
                        sending = true
                        vm.sendMessage(input)
                        input = ""
                        sending = false
                    }
                ) { Text(if (sending) "Sending..." else "Send") }
                IconButton(
                    onClick = {
                        if (petId != null) {
                            nav.navigate("${Route.SCAN}/$petId")
                        } else {
                            scope.launch { snackbar.showSnackbar("Pilih hewan dulu sebelum foto.") }
                        }
                    }
                ) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = "Scan from chat")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ElevatedCard {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Info, contentDescription = null)
                            Text("Konteks Skrining", style = MaterialTheme.typography.titleMedium)
                        }
                        Text(contextText, style = MaterialTheme.typography.bodyMedium)
                        Text(disclaimer, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        if (!supported) {
                            ElevatedCard(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Text(
                                    "Foto tidak terdeteksi kucing/anjing. Unggah foto yang lebih jelas.",
                                    modifier = Modifier.padding(8.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    actions.forEach { (label, query) ->
                        val isMaps = label.contains("Maps", ignoreCase = true)
                        Button(
                            enabled = supported || isMaps,
                            onClick = {
                                if (isMaps) {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.google.com/maps/search/klinik+hewan+terdekat")
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    runCatching { ctx.startActivity(intent) }
                                        .onFailure { scope.launch { snackbar.showSnackbar("Tidak bisa membuka Maps") } }
                                } else {
                                    vm.quickAsk(query)
                                }
                            }
                        ) { Text(label) }
                    }
                }
            }
            items(messages, key = { it.id }) { m ->
                val isBot = m.from == Sender.BOT
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        ProvideTextStyle(MaterialTheme.typography.labelMedium) {
                            Text(if (isBot) "Assistant" else "You")
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(m.text)
                    }
                }
            }
        }
    }
}
