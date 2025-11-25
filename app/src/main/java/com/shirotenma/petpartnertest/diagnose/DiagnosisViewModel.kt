package com.shirotenma.petpartnertest.diagnose

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.shirotenma.petpartnertest.chatbot.DiseaseKnowledgeBase
import com.shirotenma.petpartnertest.pet.record.PetRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class DiagnosisResponse(
    val condition: String,
    val severity: String,
    val confidence: Double,
    val tips: List<String>,
    val species: String?,
    val globalClass: String?,
    val detailClass: String?,
    val isSupported: Boolean
)

@HiltViewModel
class DiagnosisViewModel @Inject constructor(
    private val repo: DiagnosisRepository,
    private val recordRepo: PetRecordRepository
) : ViewModel() {

    suspend fun diagnose(
        ctx: Context,
        petId: Long,
        photoUri: String
    ): DiagnosisResponse = withContext(Dispatchers.IO) {
        val uri = Uri.parse(photoUri)
        val result = repo.diagnose(uri)
        val info = DiseaseKnowledgeBase.items[result.detailClass ?: result.globalClass]
        val supported = result.isSupportedAnimal
        DiagnosisResponse(
            condition = result.detailClass ?: result.globalClass.ifBlank { "Unknown" },
            severity = when {
                result.globalClass.contains("healthy", ignoreCase = true) -> "healthy"
                result.globalClass.isBlank() -> "unknown"
                else -> "skin_issue"
            },
            confidence = result.detailConf ?: result.globalConf,
            tips = if (supported) info?.homeCareTips ?: emptyList() else emptyList(),
            species = result.species.ifBlank { null },
            globalClass = result.globalClass.ifBlank { null },
            detailClass = result.detailClass,
            isSupported = supported
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
