package com.shirotenma.petpartnertest.chatbot

import org.junit.Assert.assertTrue
import org.junit.Test

class ChatbotQuickActionsTest {

    private val engine = ChatbotEngine(DiseaseKnowledgeBase.items)
    private val ctx = ChatContext(diseaseCode = "cat_Ringworm", diseaseConfidence = 0.9f)

    @Test
    fun `quick ask gejala gives symptom response`() {
        val reply = engine.reply("gejala apa saja", ctx)
        assertTrue(reply.text.contains("Gejala umum", ignoreCase = true))
    }

    @Test
    fun `maps wa intent responded safely`() {
        val reply = engine.reply("hubungi klinik via whatsapp", ctx)
        assertTrue(reply.text.contains("Google Maps", ignoreCase = true))
        assertTrue(reply.text.contains("WA", ignoreCase = true))
    }
}
