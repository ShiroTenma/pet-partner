package com.shirotenma.petpartnertest.journal

data class JournalEntry(
    val id: Long = 0,
    val petId: Long,
    val mood: String,
    val content: String,
    val date: String
)
