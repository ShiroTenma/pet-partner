package com.shirotenma.petpartnertest.chatbot

class ChatbotEngine(
    private val diseaseDb: Map<String, DiseaseInfo>
)
{

    private val disclaimerGeneral =
        "Hasil AI ini hanya skrining awal, bukan diagnosis pasti. Jika ragu atau gejala berat, segera ke dokter hewan."

    private val emergencyKeywords = listOf(
        "pendarahan", "berdarah", "kejang", "sulit bernapas", "susah napas", "sesak",
        "tidak mau makan", "tidak mau minum", "lemas", "pingsan", "muntah darah", "luka parah"
    )

    fun introMessage(ctx: ChatContext): String {
        val base = "Hai! Aku asisten edukasi, bukan dokter. Hasil ini hanya skrining awal berbasis foto."
        return when {
            ctx.isSupportedAnimal == false -> unsupportedAnimalMessage()
            ctx.diseaseCode != null -> {
                val info = diseaseDb[ctx.diseaseCode]
                val name = info?.name ?: ctx.diseaseCode
                val conf = ctx.diseaseConfidence?.let { " (~${(it * 100).toInt()}% keyakinan)" } ?: ""
                "$base Aku melihat kemungkinan $name$conf. Tanyakan gejala, perawatan rumahan, pencegahan, atau kapan perlu ke dokter. $disclaimerGeneral"
            }
            else -> "$base Jalankan analisis foto dulu supaya aku bisa memberi konteks. $disclaimerGeneral"
        }
    }

    fun reply(userMessage: String, context: ChatContext): ChatMessage {
        val text = userMessage.lowercase()

        // Emergency check
        if (emergencyKeywords.any { text.contains(it) }) {
            return ChatMessage(
                from = Sender.BOT,
                text = "Gejala yang kamu sebutkan terdengar berat. Segera bawa ke dokter hewan atau klinik darurat terdekat. $disclaimerGeneral"
            )
        }

        val intent = detectIntent(text)

        if (context.isSupportedAnimal == false) {
            return ChatMessage(from = Sender.BOT, text = unsupportedAnimalMessage())
        }

        val info = context.diseaseCode?.let { diseaseDb[it] }
        val hasDiag = info != null

        val answer = when (intent) {
            Intent.GREETING -> "Halo! Aku siap bantu jelaskan hasil skrining kulit kucing/anjing. Apa yang ingin kamu ketahui? $disclaimerGeneral"
            Intent.ASK_FEATURE -> "Aplikasi ini membantu skrining awal masalah kulit kucing/anjing lewat foto, memberi edukasi gejala, perawatan aman, dan saran kapan perlu ke dokter. Bukan pengganti vet. $disclaimerGeneral"
            Intent.ASK_RESULT_EXPLANATION -> {
                if (!hasDiag) needDiagMessage()
                else buildString {
                    append("Dari analisis gambar, ada kemungkinan ${info!!.name}. ${info.shortDescription}. ")
                    append("Ini hanya berdasarkan satu foto dan model AI; konfirmasi ke dokter hewan tetap diperlukan. $disclaimerGeneral")
                }
            }
            Intent.ASK_SYMPTOM -> {
                if (!hasDiag) needDiagMessage()
                else formatList("Gejala umum ${info!!.name}:", info.commonSymptoms) + "\n$disclaimerGeneral"
            }
            Intent.ASK_TREATMENT -> {
                if (!hasDiag) needDiagMessage()
                else {
                    val care = formatList("Perawatan rumahan yang aman:", info!!.homeCareTips)
                    "$care\nHindari memberi obat manusia/menyebut merek/dosis tanpa vet. $disclaimerGeneral"
                }
            }
            Intent.ASK_PREVENTION -> {
                if (!hasDiag) needDiagMessage()
                else formatList("Pencegahan yang bisa dilakukan:", info!!.preventionTips) + "\n$disclaimerGeneral"
            }
            Intent.ASK_URGENCY -> {
                if (!hasDiag) needDiagMessage()
                else "Tingkat urgensi: ${info!!.vetUrgency}. Jika luka menyebar cepat, bernanah, hewan lemas atau tidak mau makan/minum, segera ke dokter hewan. $disclaimerGeneral"
            }
            Intent.ASK_SUMMARY -> {
                if (!hasDiag) needDiagMessage()
                else {
                    val conf = context.diseaseConfidence?.let { "Keyakinan model ~${(it * 100).toInt()}%." } ?: ""
                    val sevTxt = context.severity?.let { "Perkiraan tingkat keparahan: $it." } ?: ""
                    "Ringkas: kemungkinan ${info!!.name}. ${info.shortDescription}. $conf $sevTxt $disclaimerGeneral"
                }
            }
            Intent.ASK_DOCTOR -> "Kalau gejala berat atau kamu ragu, sebaiknya buat janji dengan dokter hewan dalam 1-3 hari; jika gawat, segera ke klinik darurat. $disclaimerGeneral"
            Intent.ASK_WHATSAPP -> buildString {
                append("Aku tidak bisa menghubungi klinik/dokter langsung atau mengakses lokasimu. ")
                append("Silakan buka Google Maps, cari \"klinik hewan terdekat\", lalu bagikan linknya via WhatsApp ke klinik/dokter yang kamu percaya. ")
                append("Contoh pencarian: https://www.google.com/maps/search/klinik+hewan+terdekat. ")
                append("Jika kondisi darurat, segera datang ke klinik/RS hewan terdekat. $disclaimerGeneral")
            }
            Intent.ASK_UNSUPPORTED -> "Saat ini aku hanya fokus pada masalah kulit kucing dan anjing. Untuk spesies atau penyakit lain, silakan konsultasi langsung ke dokter hewan. $disclaimerGeneral"
            Intent.FALLBACK -> "Maaf, aku belum paham. Kamu bisa tanya: \"gejala apa?\", \"perawatan rumahan?\", \"perlu ke dokter?\", atau \"ringkas hasilnya\". $disclaimerGeneral"
        }

        return ChatMessage(from = Sender.BOT, text = answer)
    }

    private fun needDiagMessage() =
        "Aku belum punya hasil skrining. Jalankan analisis foto dulu supaya aku bisa memberi konteks. $disclaimerGeneral"

    private fun formatList(title: String, items: List<String>): String =
        if (items.isEmpty()) "$title (data belum tersedia)"
        else title + "\n" + items.joinToString("\n") { "- $it" }

    private fun unsupportedAnimalMessage(): String =
        "Dari analisis gambar, sistem tidak yakin ini adalah foto kucing atau anjing. " +
                "Coba kirim ulang foto yang jelas menunjukkan hewan peliharaanmu (kucing atau anjing), dan hindari foto benda lain atau hewan selain kucing/anjing ya."

    private fun detectIntent(text: String): Intent = when {
        listOf("kelinci", "hamster", "burung", "sapi", "organ", "paru", "pencernaan").any { text.contains(it) } -> Intent.ASK_UNSUPPORTED
        listOf("halo", "hai", "pagi", "malam", "hi").any { text.contains(it) } -> Intent.GREETING
        listOf("bisa apa", "fitur", "fungsi").any { text.contains(it) } -> Intent.ASK_FEATURE
        listOf("maksud", "arti", "penjelasan", "jelasin", "apa artinya", "hasilnya").any { text.contains(it) } -> Intent.ASK_RESULT_EXPLANATION
        listOf("gejala", "ciri").any { text.contains(it) } -> Intent.ASK_SYMPTOM
        listOf("harus gimana", "obat", "rawat", "perawatan", "apa yang harus", "gimana").any { text.contains(it) } -> Intent.ASK_TREATMENT
        listOf("mencegah", "cegah", "supaya tidak").any { text.contains(it) } -> Intent.ASK_PREVENTION
        listOf("dokter", "urgent", "gawat", "bahaya", "segera").any { text.contains(it) } -> Intent.ASK_URGENCY
        listOf("whatsapp", "wa ", " wa", "hubungi klinik", "kontak klinik", "nomor klinik", "hubungi dokter").any { text.contains(it) } -> Intent.ASK_WHATSAPP
        listOf("ringkas", "singkat", "summary", "ulang").any { text.contains(it) } -> Intent.ASK_SUMMARY
        listOf("ke dokter", "kapan ke vet", "perlu vet").any { text.contains(it) } -> Intent.ASK_DOCTOR
        else -> Intent.FALLBACK
    }
}

private enum class Intent {
    GREETING,
    ASK_FEATURE,
    ASK_RESULT_EXPLANATION,
    ASK_SYMPTOM,
    ASK_TREATMENT,
    ASK_PREVENTION,
    ASK_URGENCY,
    ASK_SUMMARY,
    ASK_DOCTOR,
    ASK_WHATSAPP,
    ASK_UNSUPPORTED,
    FALLBACK
}
