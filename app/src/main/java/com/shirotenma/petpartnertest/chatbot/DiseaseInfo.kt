package com.shirotenma.petpartnertest.chatbot

data class DiseaseInfo(
    val name: String,
    val species: String, // "cat" or "dog"
    val shortDescription: String,
    val commonSymptoms: List<String>,
    val homeCareTips: List<String>,
    val preventionTips: List<String>,
    val vetUrgency: String // e.g. "segera ke dokter", "dalam 1–3 hari"
)

data class ChatContext(
    val species: String? = null,
    val diseaseCode: String? = null,
    val diseaseConfidence: Float? = null,
    val severity: String? = null,
    val tips: List<String> = emptyList(),
    val isSupportedAnimal: Boolean = true
)

data class ChatMessage(
    val id: Long = System.nanoTime(),
    val from: Sender,
    val text: String
)

enum class Sender { USER, BOT }
