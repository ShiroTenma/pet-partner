package com.shirotenma.petpartnertest.diagnose.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {
    @Insert
    suspend fun insert(item: Diagnosis): Long

    @Query("SELECT * FROM diagnosis WHERE petId = :petId ORDER BY createdAt DESC")
    fun observeByPet(petId: Long): Flow<List<Diagnosis>>

    @Query("DELETE FROM diagnosis WHERE id = :id")
    suspend fun deleteById(id: Long)
}
