// app/src/main/java/com/shirotenma/petpartnertest/pet/record/PetRecordRepository.kt
package com.shirotenma.petpartnertest.pet.record

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRecordRepository @Inject constructor(
    private val dao: PetRecordDao
) {
    fun records(petId: Long): Flow<List<PetRecord>> = dao.observeByPet(petId)

    suspend fun get(id: Long): PetRecord? = dao.getById(id)

    suspend fun upsert(
        id: Long?,
        petId: Long,
        type: String,
        title: String,
        date: String,
        notes: String?
    ): Long {
        return if (id == null) {
            dao.insert(PetRecord(petId = petId, type = type, title = title, date = date, notes = notes))
        } else {
            dao.update(PetRecord(id = id, petId = petId, type = type, title = title, date = date, notes = notes))
            id
        }
    }

    suspend fun delete(id: Long) = dao.deleteById(id)
}
