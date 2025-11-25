package com.shirotenma.petpartnertest.chatbot

import org.junit.Assert.assertTrue
import org.junit.Test

class ChatbotEngineTest {

    private val engine = ChatbotEngine(DiseaseKnowledgeBase.items)

    @Test
    fun `fallback when no diagnosis`() {
        val reply = engine.reply("gejala apa", ChatContext())
        assertTrue(reply.text.contains("skrining"))
    }

    @Test
    fun `emergency triggers urgent advice`() {
        val reply = engine.reply("ada pendarahan dan susah napas", ChatContext())
        assertTrue(reply.text.contains("Segera bawa ke dokter", ignoreCase = true))
    }

    @Test
    fun `unsupported species handled`() {
        val reply = engine.reply("bagaimana kelinci?", ChatContext())
        assertTrue(reply.text.contains("fokus", ignoreCase = true))
    }

    @Test
    fun `summary intent uses context`() {
        val ctx = ChatContext(diseaseCode = "cat_Ringworm", diseaseConfidence = 0.8f)
        val reply = engine.reply("ringkas hasilnya", ctx)
        assertTrue(reply.text.contains("Ringkas", ignoreCase = true))
    }
}
