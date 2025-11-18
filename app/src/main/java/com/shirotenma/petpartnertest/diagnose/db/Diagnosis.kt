package com.shirotenma.petpartnertest.diagnose.db

import androidx.room.*
import com.shirotenma.petpartnertest.pet.Pet

@Entity(
    tableName = "diagnosis",
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
data class Diagnosis(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    val condition: String,
    val severity: String,
    val confidence: Double,
    /** Disimpan jadi satu string dipisah |;| biar simpel */
    val tipsJoined: String,
    val photoUri: String?,
    val createdAt: Long = System.currentTimeMillis()
)
