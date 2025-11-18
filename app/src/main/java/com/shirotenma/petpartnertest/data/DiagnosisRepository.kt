package com.shirotenma.petpartnertest.diagnose.data

import com.shirotenma.petpartnertest.diagnose.db.Diagnosis
import com.shirotenma.petpartnertest.diagnose.db.DiagnosisDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiagnosisRepository @Inject constructor(
    private val dao: DiagnosisDao
) {
    suspend fun insert(
        petId: Long,
        condition: String,
        severity: String,
        confidence: Double,
        tips: List<String>,
        photoUri: String?
    ): Long = dao.insert(
        Diagnosis(
            petId = petId,
            condition = condition,
            severity = severity,
            confidence = confidence,
            tipsJoined = tips.joinToString("|;|"),
            photoUri = photoUri
        )
    )

    fun history(petId: Long): Flow<List<Diagnosis>> = dao.observeByPet(petId)
    suspend fun delete(id: Long) = dao.deleteById(id)
}
