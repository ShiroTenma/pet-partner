package com.shirotenma.petpartnertest.journal.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.shirotenma.petpartnertest.journal.BirdMessage

@Entity(
    tableName = "bird_messages",
    foreignKeys = [
        ForeignKey(
            entity = JournalEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceJournalId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("sourceJournalId"), Index("fromSelf")]
)
data class BirdMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val preview: String,
    val sourceJournalId: Long? = null,
    val lastReply: String? = null,
    val fromSelf: Boolean = false
)

fun BirdMessageEntity.toDomain() = BirdMessage(
    id = id,
    title = title,
    preview = preview,
    sourceJournalId = sourceJournalId,
    lastReply = lastReply,
    fromSelf = fromSelf
)

fun BirdMessage.toEntity() = BirdMessageEntity(
    id = id,
    title = title,
    preview = preview,
    sourceJournalId = sourceJournalId,
    lastReply = lastReply,
    fromSelf = fromSelf
)
