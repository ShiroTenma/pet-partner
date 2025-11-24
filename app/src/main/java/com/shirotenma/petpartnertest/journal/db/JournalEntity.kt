package com.shirotenma.petpartnertest.journal.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.shirotenma.petpartnertest.journal.JournalEntry
import com.shirotenma.petpartnertest.pet.Pet

@Entity(
    tableName = "journals",
    foreignKeys = [
        ForeignKey(
            entity = Pet::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    val mood: String,
    val content: String,
    val date: String
)

fun JournalEntity.toDomain() = JournalEntry(
    id = id,
    petId = petId,
    mood = mood,
    content = content,
    date = date
)

fun JournalEntry.toEntity() = JournalEntity(
    id = id,
    petId = petId,
    mood = mood,
    content = content,
    date = date
)
