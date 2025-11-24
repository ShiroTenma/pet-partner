package com.shirotenma.petpartnertest.journal.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Query("SELECT * FROM journals WHERE petId = :petId ORDER BY date DESC, id DESC")
    fun observeByPet(petId: Long): Flow<List<JournalEntity>>

    @Query("SELECT * FROM journals WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): JournalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: JournalEntity): Long

    @Delete
    suspend fun delete(entity: JournalEntity)

    @Query("DELETE FROM journals WHERE id = :id")
    suspend fun deleteById(id: Long)
}
