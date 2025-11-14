// app/src/main/java/com/shirotenma/petpartnertest/diagnose/DiagnoseViewModel.kt
package com.shirotenma.petpartnertest.diagnose

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shirotenma.petpartnertest.data.DiagnoseResp
import com.shirotenma.petpartnertest.pet.record.PetRecordRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class DiagnoseViewModel @Inject constructor(
    private val repo: DiagnosisRepository,
    private val recordRepo: PetRecordRepository
) : ViewModel() {

    suspend fun run(ctx: Context, petId: Long, photoUri: String): DiagnoseResp =
        repo.diagnose(ctx, petId, android.net.Uri.parse(photoUri))

    fun saveDiagnosis(
        petId: Long,
        photoUri: String,
        resp: DiagnoseResp,
        onDone: () -> Unit
    ) = viewModelScope.launch {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val notes = buildString {
            append("Severity: ${resp.severity}\n")
            append("Confidence: ${"%.2f".format(resp.confidence)}\n")
            if (resp.tips.isNotEmpty()) {
                append("Tips:\n")
                resp.tips.forEach { append("• $it\n") }
            }
        }
        recordRepo.upsert(
            id = null,
            petId = petId,
            type = "Diagnosis",
            title = resp.condition,
            date = date,
            notes = notes,
            attachmentUri = photoUri
        )
        onDone()
    }
}
