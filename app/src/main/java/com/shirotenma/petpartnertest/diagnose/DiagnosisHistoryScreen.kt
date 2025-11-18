package com.shirotenma.petpartnertest.diagnose

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
import com.shirotenma.petpartnertest.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisHistoryScreen(
    nav: NavController,
    petId: Long,
    vm: DiagnosisHistoryViewModel = hiltViewModel()
) {
    val list by vm.history(petId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Diagnosis History") }) }
    ) { pad ->
        if (list.isEmpty()) {
            Box(Modifier.padding(pad).fillMaxSize().padding(24.dp)) {
                Text("Belum ada riwayat.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(pad).fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(list, key = { it.id }) { d ->
                    ListItem(
                        headlineContent = { Text("${d.condition} • ${d.severity}") },
                        supportingContent = {
                            Text("Conf: ${"%.2f".format(d.confidence)}  •  ${java.text.DateFormat.getDateTimeInstance().format(d.createdAt)}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val tips = d.tipsJoined
                                nav.navigate(
                                    "${Route.DIAG_RESULT}?petId=${d.petId}" +
                                            "&cond=${enc(d.condition)}&sev=${enc(d.severity)}" +
                                            "&conf=${d.confidence}&tips=${enc(tips)}&uri=${enc(d.photoUri ?: "")}"
                                )
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

private fun enc(s: String) = java.net.URLEncoder.encode(s, "UTF-8")
