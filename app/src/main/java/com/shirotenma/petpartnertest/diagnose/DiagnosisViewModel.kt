package com.shirotenma.petpartnertest.diagnose

import android.content.Context
import androidx.lifecycle.ViewModel
import com.shirotenma.petpartnertest.data.ApiService
import com.shirotenma.petpartnertest.data.DiagnoseReq
import com.shirotenma.petpartnertest.pet.record.PetRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class DiagnosisResponse(
    val condition: String,
    val severity: String,
    val confidence: Double,
    val tips: List<String>
)

@HiltViewModel
class DiagnosisViewModel @Inject constructor(
    private val api: ApiService,
    private val recordRepo: PetRecordRepository
) : ViewModel() {

    suspend fun diagnose(
        ctx: Context,
        petId: Long,
        photoUri: String
    ): DiagnosisResponse = withContext(Dispatchers.IO) {

        // ✅ sekarang kirim req dengan parameter yang diminta
        val resp = api.diagnose(
            DiagnoseReq(
                petId = petId,
                photoUri = photoUri
            )
        )

        DiagnosisResponse(
            condition = resp.condition,
            severity = resp.severity,
            confidence = resp.confidence,
            tips = resp.tips
        )
    }

    suspend fun saveAsRecord(
        petId: Long,
        condition: String,
        severity: String,
        confidence: Double,
        tips: List<String>,
        photoUri: String?
    ) {
        val title = "Diagnosis: $condition"
        val notes = buildString {
            appendLine("Severity: $severity")
            appendLine("Confidence: ${(confidence * 100).toInt()}%")
            if (tips.isNotEmpty()) {
                appendLine("Tips:")
                tips.forEach { appendLine("- $it") }
            }
            if (!photoUri.isNullOrBlank()) {
                appendLine("Photo: $photoUri")
            }
        }

        recordRepo.upsert(
            id = null,
            petId = petId,
            type = "Diagnosis",
            title = title,
            date = java.time.LocalDate.now().toString(),
            notes = notes,
            attachmentUri = photoUri
        )
    }
}
