package com.shirotenma.petpartnertest.journal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class JournalRepositoryImpl @Inject constructor() : JournalRepository {
    private val journals = MutableStateFlow(
        listOf(
            JournalEntry(id = 1, petId = 1, mood = "Happy", content = "Walked in the park, good appetite.", date = "2025-01-01"),
            JournalEntry(id = 2, petId = 1, mood = "Tired", content = "Slept most of the day, mild sneeze.", date = "2025-01-02")
        )
    )
    private val birdMessages = MutableStateFlow(
        listOf(
            BirdMessage(id = 1, title = "Worried owner", preview = "My cat has been sneezing for two days...", sourceJournalId = 2L),
            BirdMessage(id = 2, title = "Puppy advice", preview = "First time grooming, any tips to calm him?")
        )
    )

    private var journalIdCounter = 10L
    private var birdIdCounter = 20L
    private var myBirdMessageId: Long? = null

    override fun observeByPet(petId: Long): Flow<List<JournalEntry>> =
        journals.map { list -> list.filter { it.petId == petId }.sortedByDescending { it.date } }

    override suspend fun get(id: Long): JournalEntry? = journals.value.firstOrNull { it.id == id }

    override suspend fun upsert(entry: JournalEntry): Long {
        val current = journals.value.toMutableList()
        val id = if (entry.id == 0L) ++journalIdCounter else entry.id
        val newEntry = entry.copy(id = id)
        val idx = current.indexOfFirst { it.id == id }
        if (idx >= 0) current[idx] = newEntry else current.add(newEntry)
        journals.value = current
        return id
    }

    override suspend fun delete(id: Long) {
        journals.value = journals.value.filterNot { it.id == id }
    }

    override fun observeBirdMessages(): Flow<List<BirdMessage>> = birdMessages

    override suspend fun shareAsBirdMessage(journalId: Long?, title: String, preview: String): Long {
        if (myBirdMessageId != null) throw IllegalStateException("Sudah mengirim Pesan Burung")
        val id = ++birdIdCounter
        val newList = birdMessages.value.toMutableList()
        newList.add(0, BirdMessage(id = id, title = title, preview = preview, sourceJournalId = journalId, fromSelf = true))
        birdMessages.value = newList
        myBirdMessageId = id
        return id
    }

    override suspend fun canShareBirdMessage(): Boolean = myBirdMessageId == null

    override suspend fun sendReply(messageId: Long, reply: String) {
        birdMessages.value = birdMessages.value.map { msg ->
            if (msg.id == messageId) msg.copy(lastReply = reply) else msg
        }
    }

    override suspend fun randomForestMessage(excludeMine: Boolean): BirdMessage? {
        val pool = if (excludeMine) birdMessages.value.filterNot { it.fromSelf } else birdMessages.value
        if (pool.isEmpty()) return null
        return pool.random()
    }
}
