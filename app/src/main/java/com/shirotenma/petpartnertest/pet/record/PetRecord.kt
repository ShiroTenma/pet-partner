package com.shirotenma.petpartnertest.pet.record

import androidx.room.*
import com.shirotenma.petpartnertest.pet.Pet

@Entity(
    tableName = "pet_records",
    foreignKeys = [
        ForeignKey(
            entity = Pet::class,            // ⬅️ WAJIB ada
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class PetRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    val type: String,
    val title: String,
    val date: String,
    val notes: String? = null
)
