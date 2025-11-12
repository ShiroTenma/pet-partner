package com.shirotenma.petpartnertest.pet

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
    @Query("SELECT * FROM pets ORDER BY name")
    fun observeAll(): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE id=:id")
    fun observeById(id: Long): Flow<Pet?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pet: Pet): Long

    @Delete
    suspend fun delete(pet: Pet)
}
