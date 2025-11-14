package com.shirotenma.petpartnertest.chat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ChatMessage(
    val id: Long,
    val role: String,          // "user" | "assistant" | "system"
    val text: String,
    val photoUri: String? = null,
    val suggestions: List<String> = emptyList()
)

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    // Seed sekali ketika masuk dari hasil diagnosis
    fun seedFromDiagnosis(
        petId: Long?,
        cond: String?,
        sev: String?,
        confidence: Double?,
        tips: List<String>,
        photoUri: String?
    ) {
        if (_messages.value.isNotEmpty()) return // cegah duplikasi saat recomposition

        val header = buildString {
            appendLine("📋 Ringkasan Diagnosis")
            if (cond != null) appendLine("• Kondisi: $cond")
            if (sev != null) appendLine("• Severity: $sev")
            if (confidence != null) appendLine("• Confidence: ${"%.2f".format(confidence)}")
            if (tips.isNotEmpty()) {
                appendLine("• Saran awal:")
                tips.forEach { appendLine("  – $it") }
            }
        }.trim()

        _messages.value = listOf(
            ChatMessage(
                id = System.nanoTime(),
                role = "assistant",
                text = header,
                photoUri = photoUri,
                suggestions = listOf(
                    "Apa langkah pertama yang harus saya lakukan?",
                    "Apakah ini perlu ke dokter hewan segera?",
                    "Apakah aman memberi obat rumahan?",
                    "Bagaimana cara mencegah kambuh?"
                )
            )
        )
    }

    fun sendUser(text: String) {
        if (text.isBlank()) return
        append(ChatMessage(System.nanoTime(), "user", text))
        // mock balasan rules sederhana (nanti bisa diganti LLM/Cloud)
        val reply = when {
            text.contains("segera", ignoreCase = true) || text.contains("darurat", true) ->
                "Jika terlihat nyeri berat, demam, lesu parah, atau keluar nanah darah ➜ segera bawa ke vet hari ini."
            text.contains("obat", true) ->
                "Hindari pemberian obat manusia. Untuk kulit, kompres hangat & jaga kering. Untuk mata, bersihkan dengan saline steril."
            text.contains("mencegah", true) || text.contains("prevent", true) ->
                "Jaga kebersihan, diet seimbang, jadwalkan vaksin & pemeriksaan gigi rutin. Batasi paparan iritan (debu, parfum)."
            else ->
                "Catat gejala harian (nafsu makan, energi, buang air). Jika memburuk 24–48 jam, hubungi dokter hewan."
        }
        append(ChatMessage(System.nanoTime(), "assistant", reply))
    }

    fun clickSuggestion(s: String) = sendUser(s)

    private fun append(msg: ChatMessage) = _messages.update { it + msg }
}
