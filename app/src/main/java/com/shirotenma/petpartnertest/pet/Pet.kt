package com.shirotenma.petpartnertest.pet

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val species: String,
    val birthDate: String?, // simpan string sederhana: "2024-10-21"
    val notes: String?
)
