package com.shirotenma.petpartnertest.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    // TODO: inject Chat API kalau sudah ada
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun addBot(text: String) {
        _messages.update { it + ChatMessage(from = Sender.BOT, text = text) }
    }

    fun addUser(text: String) {
        _messages.update { it + ChatMessage(from = Sender.USER, text = text) }
    }

    fun sendMessage(userText: String) = viewModelScope.launch {
        if (userText.isBlank()) return@launch
        addUser(userText)

        // TODO: panggil LLM / backend di sini.
        val reply = "Oke, saya proses: ${userText.take(120)}"
        addBot(reply)
    }

    /** Seed konteks awal dari hasil diagnosis */
    fun seedContext(
        petId: Long?,
        cond: String?, sev: String?, confidence: Double?,
        tips: List<String>, photoUri: String?
    ) {
        if (_messages.value.isNotEmpty()) return
        val intro = buildString {
            append("Konteks diagnosis:\n")
            cond?.let { append("- Condition: $it\n") }
            sev?.let { append("- Severity: $it\n") }
            confidence?.let { append("- Confidence: ${"%.0f%%".format(it * 100)}\n") }
            if (tips.isNotEmpty()) {
                append("- Tips:\n")
                tips.forEach { append("  • $it\n") }
            }
            photoUri?.takeIf { it.isNotBlank() }?.let { append("- Foto: $it\n") }
            append("\nTanya lanjutan atau minta rencana perawatan harian.")
        }
        addBot(intro)
    }

    fun applyInitialContext(
        cond: String? = null,
        sev: String? = null,
        confidence: Double? = null,
        tips: List<String> = emptyList(),
        photoUri: String? = null
    ) {
        if (cond == null && sev == null && confidence == null && tips.isEmpty() && photoUri == null) return
        val intro = buildString {
            appendLine("Context from last diagnosis:")
            cond?.let { appendLine("- Condition: $it") }
            sev?.let { appendLine("- Severity: $it") }
            confidence?.let { appendLine("- Confidence: ${(it * 100).toInt()}%") }
            if (tips.isNotEmpty()) {
                appendLine("- Tips:")
                tips.forEach { appendLine("  • $it") }
            }
        }
        addBot(intro.trim())
    }

}
