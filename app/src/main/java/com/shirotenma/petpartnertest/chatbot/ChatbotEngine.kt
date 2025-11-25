package com.shirotenma.petpartnertest.chatbot

class ChatbotEngine(
    private val diseaseDb: Map<String, DiseaseInfo>
) {

    fun introMessage(ctx: ChatContext): String {
        val base = "Hai! Aku asisten awal, bukan dokter. Hasil ini hanya skrining awal."
        return when {
            ctx.diseaseCode != null -> {
                val info = diseaseDb[ctx.diseaseCode]
                val name = info?.name ?: ctx.diseaseCode
                val conf = ctx.diseaseConfidence?.let { " (~${(it * 100).toInt()}% keyakinan)" } ?: ""
                "$base Aku melihat kemungkinan $name$conf. Tanyakan gejala, perawatan rumahan, pencegahan, atau apakah perlu ke dokter."
            }
            else -> "$base Kirim foto dan jalankan analisis dulu, lalu tanya apa saja tentang hasilnya."
        }
    }

    fun reply(userMessage: String, context: ChatContext): ChatMessage {
        val text = userMessage.lowercase()
        val intent = detectIntent(text)

        val info = context.diseaseCode?.let { diseaseDb[it] }
        val hasDiag = info != null

        val answer = when (intent) {
            Intent.GREETING -> "Halo! Aku siap bantu jelaskan hasil skrining. Apa yang ingin kamu ketahui?"
            Intent.ASK_FEATURE -> "Aplikasi ini bisa skrining awal masalah kulit kucing/anjing lewat foto, menyimpan catatan, dan memberi tips dasar. Untuk diagnosis pasti, tetap konsultasi dokter hewan."
            Intent.ASK_RESULT_EXPLANATION -> {
                if (!hasDiag) needDiagMessage()
                else buildString {
                    append("Dari analisis gambar, ada kemungkinan ${info!!.name}. ")
                    append(info.shortDescription)
                    append(". Ingat, ini skrining awal, silakan konfirmasi ke dokter hewan.")
                }
            }
            Intent.ASK_SYMPTOM -> {
                if (!hasDiag) needDiagMessage()
                else formatList("Gejala umum ${info!!.name}:", info.commonSymptoms)
            }
            Intent.ASK_TREATMENT -> {
                if (!hasDiag) needDiagMessage()
                else {
                    val care = formatList("Perawatan rumahan yang bisa dicoba:", info!!.homeCareTips)
                    "$care Harap dicatat: ini bukan resep obat. Untuk obat dan dosis, wajib konsultasi dokter hewan."
                }
            }
            Intent.ASK_PREVENTION -> {
                if (!hasDiag) needDiagMessage()
                else formatList("Pencegahan yang bisa dilakukan:", info!!.preventionTips)
            }
            Intent.ASK_URGENCY -> {
                if (!hasDiag) needDiagMessage()
                else "Tingkat urgensi: ${info!!.vetUrgency}. Jika ada luka berat, demam, atau hewan makin tidak nyaman, segera ke dokter hewan."
            }
            Intent.FALLBACK -> "Maaf, aku belum paham. Kamu bisa tanya gejala, perawatan rumahan, pencegahan, atau perlu ke dokter?"
        }

        return ChatMessage(from = Sender.BOT, text = answer)
    }

    private fun needDiagMessage() =
        "Aku belum punya hasil skrining. Silakan kirim foto hewan dan jalankan analisis dulu."

    private fun formatList(title: String, items: List<String>): String =
        if (items.isEmpty()) "$title (data belum tersedia)"
        else title + "\n" + items.joinToString("\n") { "• $it" }

    private fun detectIntent(text: String): Intent = when {
        listOf("halo", "hai", "pagi", "malam", "hi").any { text.contains(it) } -> Intent.GREETING
        listOf("bisa apa", "fitur", "fungsi").any { text.contains(it) } -> Intent.ASK_FEATURE
        listOf("maksud", "arti", "penjelasan", "jelasin").any { text.contains(it) } -> Intent.ASK_RESULT_EXPLANATION
        listOf("gejala", "ciri").any { text.contains(it) } -> Intent.ASK_SYMPTOM
        listOf("harus gimana", "obat", "rawat", "perawatan", "apa yang harus", "gimana").any { text.contains(it) } -> Intent.ASK_TREATMENT
        listOf("mencegah", "cegah", "supaya tidak").any { text.contains(it) } -> Intent.ASK_PREVENTION
        listOf("dokter", "urgent", "gawat", "bahaya", "segera").any { text.contains(it) } -> Intent.ASK_URGENCY
        else -> Intent.FALLBACK
    }
}

private enum class Intent {
    GREETING, ASK_FEATURE, ASK_RESULT_EXPLANATION, ASK_SYMPTOM, ASK_TREATMENT, ASK_PREVENTION, ASK_URGENCY, FALLBACK
}
