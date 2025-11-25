package com.shirotenma.petpartnertest.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE petId = :petId ORDER BY date ASC, time ASC")
    fun observeByPet(petId: Long): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Schedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: Schedule): Long

    @Update
    suspend fun update(entity: Schedule)

    @Delete
    suspend fun delete(entity: Schedule)

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteById(id: Long)
}
