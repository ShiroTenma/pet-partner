package com.shirotenma.petpartnertest.journal.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BirdMessageDao {

    @Query("SELECT * FROM bird_messages ORDER BY id DESC")
    fun observeAll(): Flow<List<BirdMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BirdMessageEntity): Long

    @Query("SELECT COUNT(*) FROM bird_messages WHERE fromSelf = 1")
    suspend fun countMine(): Int

    @Query("UPDATE bird_messages SET lastReply = :reply WHERE id = :id")
    suspend fun updateLastReply(id: Long, reply: String)

    @Query("SELECT * FROM bird_messages WHERE (:excludeSelf = 0 OR fromSelf = 0) ORDER BY RANDOM() LIMIT 1")
    suspend fun random(excludeSelf: Boolean = true): BirdMessageEntity?
}
