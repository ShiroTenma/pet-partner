package com.shirotenma.petpartnertest.schedule

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.shirotenma.petpartnertest.pet.Pet

@Entity(
    tableName = "schedules",
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
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    val title: String,
    val type: String,
    val date: String,   // yyyy-MM-dd
    val time: String,   // HH:mm
    val notes: String? = null,
    val remind: Boolean = false
)
