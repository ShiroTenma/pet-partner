package com.shirotenma.petpartnertest.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shirotenma.petpartnertest.chatbot.ChatContext
import com.shirotenma.petpartnertest.chatbot.ChatMessage
import com.shirotenma.petpartnertest.chatbot.ChatbotEngine
import com.shirotenma.petpartnertest.chatbot.Sender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val engine: ChatbotEngine
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private var context: ChatContext = ChatContext()

    fun setContext(ctx: ChatContext) {
        context = ctx
        if (_messages.value.isEmpty()) {
            addBot(engine.introMessage(ctx))
            // Pesan pembuka tambahan: reminder batasan dan contoh pertanyaan
            addBot(
                "Catatan: hasil AI bukan diagnosis pasti, hindari obat/dosis sendiri, dan tetap konsultasi vet jika ragu. " +
                        "Contoh pertanyaan: \"gejala apa?\", \"perawatan rumahan?\", \"ringkas hasilnya?\", \"perlu ke dokter?\""
            )
        }
    }

    private fun addBot(text: String) {
        _messages.update { it + ChatMessage(from = Sender.BOT, text = text) }
    }

    private fun addUser(text: String) {
        _messages.update { it + ChatMessage(from = Sender.USER, text = text) }
    }

    fun sendMessage(userText: String) = viewModelScope.launch {
        if (userText.isBlank()) return@launch
        addUser(userText)
        val reply = engine.reply(userText, context)
        addBot(reply.text)
    }
}
