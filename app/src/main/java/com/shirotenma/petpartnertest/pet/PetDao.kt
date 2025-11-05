package com.shirotenma.petpartnertest.pet

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pets ORDER BY name ASC")
    fun observeAll(): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE id = :id")
    fun observeById(id: Long): Flow<Pet?>

    @Upsert
    suspend fun upsert(pet: Pet): Long

    @Delete
    suspend fun delete(pet: Pet)
}
