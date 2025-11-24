package com.shirotenma.petpartnertest.journal

import com.shirotenma.petpartnertest.journal.db.BirdMessageDao
import com.shirotenma.petpartnertest.journal.db.JournalDao
import com.shirotenma.petpartnertest.journal.db.toDomain
import com.shirotenma.petpartnertest.journal.db.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface JournalRepository {
    fun observeByPet(petId: Long): Flow<List<JournalEntry>>
    suspend fun get(id: Long): JournalEntry?
    suspend fun upsert(entry: JournalEntry): Long
    suspend fun delete(id: Long)

    fun observeBirdMessages(): Flow<List<BirdMessage>>
    suspend fun shareAsBirdMessage(journalId: Long?, title: String, preview: String): Long
    suspend fun canShareBirdMessage(): Boolean
    suspend fun sendReply(messageId: Long, reply: String)
    suspend fun randomForestMessage(excludeMine: Boolean = true): BirdMessage?
}

@Singleton
class JournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao,
    private val birdDao: BirdMessageDao
) : JournalRepository {

    override fun observeByPet(petId: Long): Flow<List<JournalEntry>> =
        journalDao.observeByPet(petId).map { list -> list.map { it.toDomain() } }

    override suspend fun get(id: Long): JournalEntry? = journalDao.getById(id)?.toDomain()

    override suspend fun upsert(entry: JournalEntry): Long {
        return if (entry.id == 0L) {
            journalDao.upsert(entry.toEntity())
        } else {
            journalDao.upsert(entry.toEntity())
            entry.id
        }
    }

    override suspend fun delete(id: Long) {
        journalDao.deleteById(id)
    }

    override fun observeBirdMessages(): Flow<List<BirdMessage>> =
        birdDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun shareAsBirdMessage(journalId: Long?, title: String, preview: String): Long {
        if (!canShareBirdMessage()) throw IllegalStateException("Sudah mengirim Pesan Burung")
        val entity = BirdMessage(
            id = 0,
            title = title,
            preview = preview,
            sourceJournalId = journalId,
            fromSelf = true
        ).toEntity()
        return birdDao.insert(entity)
    }

    override suspend fun canShareBirdMessage(): Boolean = birdDao.countMine() == 0

    override suspend fun sendReply(messageId: Long, reply: String) {
        birdDao.updateLastReply(messageId, reply)
    }

    override suspend fun randomForestMessage(excludeMine: Boolean): BirdMessage? =
        birdDao.random(excludeSelf = excludeMine)?.toDomain()
}
