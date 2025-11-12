// app/src/main/java/com/shirotenma/petpartnertest/pet/record/PetRecordDao.kt
package com.shirotenma.petpartnertest.pet.record

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PetRecordDao {

    @Query("SELECT * FROM pet_records WHERE petId = :petId ORDER BY date DESC, id DESC")
    fun observeByPet(petId: Long): Flow<List<PetRecord>>

    @Query("SELECT * FROM pet_records WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PetRecord?

    @Insert
    suspend fun insert(r: PetRecord): Long

    @Update
    suspend fun update(r: PetRecord)

    @Delete
    suspend fun delete(r: PetRecord)

    @Query("DELETE FROM pet_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM pet_records WHERE petId = :petId ORDER BY date DESC, id DESC")
    fun listByPet(petId: Long): Flow<List<PetRecord>>

}
