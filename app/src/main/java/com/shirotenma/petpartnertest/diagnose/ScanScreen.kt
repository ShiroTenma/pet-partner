// app/src/main/java/com/shirotenma/petpartnertest/diagnose/ScanScreen.kt
package com.shirotenma.petpartnertest.diagnose

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.shirotenma.petpartnertest.Route
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    nav: NavController,
    petId: Long,
    // di parameter composable:
    vm: DiagnosisViewModel = hiltViewModel()
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var consentAccepted by remember { mutableStateOf(false) }

    // Ambil foto (camera) -> simpan ke cache uri
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { ok ->
        if (!ok) photoUri = null // kalau user batal
    }

    // Permission camera
    val permLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera(ctx) { uri ->
                // set dulu uri target lalu launch kamera
                photoUri = uri
                takePicture.launch(uri)
            }
        } else {
            error = "Camera permission denied."
        }
    }

    // Pick dari galeri
    val pickPhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) photoUri = uri
    }

    fun askCamera() {
        permLauncher.launch(Manifest.permission.CAMERA)
    }

    fun doDiagnose(uri: Uri) {
        loading = true; error = null
        scope.launch {
            runCatching {
                val resp = vm.diagnose(ctx, petId, uri.toString())
                fun enc(s: String) = java.net.URLEncoder.encode(s, "UTF-8")
                val tipsPacked = resp.tips.joinToString("|;|")
                nav.navigate(
                    "${Route.DIAG_RESULT}?" +
                            "petId=$petId&" +
                            "cond=${enc(resp.condition)}&" +
                            "sev=${enc(resp.severity)}&" +
                            "conf=${resp.confidence}&" +
                            "tips=${enc(tipsPacked)}&" +
                            "uri=${enc(uri.toString())}&" +
                            "supported=${resp.isSupported}&" +
                            "note=${enc(resp.note ?: "")}"
                )
            }.onFailure {
                error = it.message ?: "Diagnosis failed"
            }
            loading = false
        }
    }


    Scaffold(
        topBar = { TopAppBar(title = { Text("Scan Kesehatan") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!consentAccepted) {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Persetujuan Pemrosesan Gambar", style = MaterialTheme.typography.titleMedium)
                        Text("Foto akan diproses untuk memberi saran kesehatan. Jangan unggah info sensitif.")
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = { consentAccepted = true }) { Text("Setuju") }
                            OutlinedButton(onClick = { nav.popBackStack() }) { Text("Batal") }
                        }
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { askCamera() }) { Text("Ambil Foto") }
                    OutlinedButton(onClick = {
                        pickPhoto.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) { Text("Pilih dari Galeri") }
                }

                if (photoUri != null) {
                    Text("Foto siap. Tekan 'Kirim Diagnosis'.")
                    Button(
                        enabled = !loading,
                        onClick = { doDiagnose(photoUri!!) }
                    ) { Text(if (loading) "Memproses..." else "Kirim Diagnosis") }
                }
            }

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

/** Siapkan Uri file sementara; kembalikan lewat callback. */
private fun launchCamera(context: Context, onReady: (Uri) -> Unit) {
    val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
    val file = File.createTempFile("scan_", ".jpg", imagesDir)
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    onReady(uri)
}
