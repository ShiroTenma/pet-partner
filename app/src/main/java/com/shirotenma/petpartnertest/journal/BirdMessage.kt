package com.shirotenma.petpartnertest.journal

data class BirdMessage(
    val id: Long = 0,
    val title: String,
    val preview: String,
    val sourceJournalId: Long? = null,
    val lastReply: String? = null,
    val fromSelf: Boolean = false
)
