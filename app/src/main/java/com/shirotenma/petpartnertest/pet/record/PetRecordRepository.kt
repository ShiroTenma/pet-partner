// app/src/main/java/com/shirotenma/petpartnertest/pet/record/PetRecordRepository.kt
package com.shirotenma.petpartnertest.pet.record

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import com.squareup.moshi.Moshi
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types

@Singleton
class PetRecordRepository @Inject constructor(
    private val dao: PetRecordDao,
    private val moshi: Moshi
) {
    fun records(petId: Long): Flow<List<PetRecord>> = dao.observeByPet(petId)

    suspend fun get(id: Long): PetRecord? = dao.getById(id)

    suspend fun upsert(
        id: Long?,
        petId: Long,
        type: String,
        title: String,
        date: String,
        notes: String?,
        attachmentUri: String? // pastikan signature sudah ada
    ): Long {
        return if (id == null) {
            dao.insert(
                PetRecord(
                    petId = petId,
                    type = type,
                    title = title,
                    date = date,
                    notes = notes,
                    attachmentUri = attachmentUri    // ⬅️ penting
                )
            )
        } else {
            dao.update(
                PetRecord(
                    id = id,
                    petId = petId,
                    type = type,
                    title = title,
                    date = date,
                    notes = notes,
                    attachmentUri = attachmentUri    // ⬅️ penting
                )
            )
            id
        }
    }

    suspend fun delete(id: Long) = dao.deleteById(id)

    suspend fun exportAllAsJson(): String {
        val list = dao.getAllOnce() // suspend fun getAllOnce(): List<PetRecord>
        val listType = Types.newParameterizedType(List::class.java, PetRecord::class.java)
        val adapter: JsonAdapter<List<PetRecord>> = moshi.adapter(listType)
        return adapter.toJson(list)
    }
}
