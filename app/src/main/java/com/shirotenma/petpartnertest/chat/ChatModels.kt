package com.shirotenma.petpartnertest.chat

enum class Sender { USER, BOT }

data class ChatMessage(
    val id: Long = System.nanoTime(),
    val from: Sender,
    val text: String
)
