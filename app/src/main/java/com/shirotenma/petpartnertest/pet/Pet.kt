package com.shirotenma.petpartnertest.pet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val species: String,
    val birthDate: String? = null,
    val notes: String? = null
)
