package com.shirotenma.petpartnertest.diagnose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shirotenma.petpartnertest.pet.record.PetRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

@HiltViewModel
class DiagnoseResultViewModel @Inject constructor(
    private val repo: PetRecordRepository
) : ViewModel() {

    fun saveAsRecord(
        petId: Long,
        condition: String,
        severity: String,
        confidence: Double,
        tips: List<String>,
        photoUri: String?,
        onDone: (Long) -> Unit
    ) = viewModelScope.launch {
        val date = LocalDate.now().toString() // yyyy-MM-dd
        val notes = buildString {
            appendLine("Severity: $severity")
            appendLine("Confidence: ${ (confidence * 100).roundToInt() }%")
            if (tips.isNotEmpty()) {
                appendLine("Tips:")
                tips.forEach { appendLine("- $it") }
            }
        }.trimEnd()

        val id = repo.upsert(
            id = null,
            petId = petId,
            type = "Diagnosis",
            title = condition,
            date = date,
            notes = notes,
            attachmentUri = photoUri
        )
        onDone(id)
    }
}
