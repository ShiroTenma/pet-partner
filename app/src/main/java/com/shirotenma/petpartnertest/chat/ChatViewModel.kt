package com.shirotenma.petpartnertest.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(val from: String, val text: String)

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun bootWithDiagnosis(cond: String?, sev: String?, confidence: Double?, tips: List<String>) {
        val intro = buildString {
            appendLine("Ringkasan diagnosis:")
            if (!cond.isNullOrBlank()) appendLine("• Kondisi: $cond")
            if (!sev.isNullOrBlank()) appendLine("• Tingkat: $sev")
            if (confidence != null) appendLine("• Confidence: ${"%.2f".format(confidence)}")
            if (tips.isNotEmpty()) appendLine("• Tips awal: ${tips.joinToString()}")
            append("Silakan tanya apa pun tentang perawatan berikutnya.")
        }
        _messages.value = listOf(ChatMessage("bot", intro))
    }

    fun send(userText: String) = viewModelScope.launch {
        _messages.value = _messages.value + ChatMessage("user", userText)

        // MOCK jawaban lokal (nanti bisa diganti hit API)
        val reply = when {
            userText.contains("obat", true) -> "Untuk obat, konsultasikan dulu ke dokter hewan. Pantau nafsu makan & hidrasi."
            userText.contains("makan", true) -> "Berikan porsi kecil tapi sering, pilih makanan mudah dicerna."
            else -> "Catat gejala harian (makan, minum, aktivitas). Jika gejala memburuk 24–48 jam, kunjungi vet."
        }
        _messages.value = _messages.value + ChatMessage("bot", reply)
    }
}
