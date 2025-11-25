package com.shirotenma.petpartnertest.chatbot

object DiseaseKnowledgeBase {
    val items: Map<String, DiseaseInfo> = mapOf(
        "cat_Dermatitis" to DiseaseInfo(
            name = "Dermatitis pada kucing",
            species = "cat",
            shortDescription = "Peradangan kulit yang bisa dipicu alergi, iritasi, atau parasit.",
            commonSymptoms = listOf("Kemerahan atau gatal", "Lecet atau kerak", "Sering menggaruk atau menjilat"),
            homeCareTips = listOf("Jaga area tetap bersih dan kering", "Gunakan kerah pelindung jika perlu", "Pantau makanan/lingkungan yang memicu"),
            preventionTips = listOf("Mandikan dengan sampo lembut khusus kucing", "Kontrol kutu/tungau secara rutin", "Minimalkan alergen di lingkungan"),
            vetUrgency = "Periksa dokter hewan bila gatal berat, ada luka, atau tidak membaik."
        ),
        "cat_Flea_Allergy" to DiseaseInfo(
            name = "Alergi gigitan kutu pada kucing",
            species = "cat",
            shortDescription = "Reaksi alergi terhadap air liur kutu yang menyebabkan gatal parah.",
            commonSymptoms = listOf("Gatal hebat, terutama di punggung/ekor", "Rambut rontok bercak", "Lecet atau kerak akibat garukan"),
            homeCareTips = listOf("Gunakan sisir kutu dan bersihkan lingkungan", "Mandi dengan sampo anti-kutu aman untuk kucing", "Konsultasi produk anti-kutu dari dokter"),
            preventionTips = listOf("Rutin kontrol kutu pada hewan dan lingkungan", "Cuci alas tidur air panas", "Vacuum karpet/sofa secara berkala"),
            vetUrgency = "Segera konsultasi jika gatal berat atau ada infeksi kulit."
        ),
        "cat_Ringworm" to DiseaseInfo(
            name = "Ringworm (kurap) pada kucing",
            species = "cat",
            shortDescription = "Infeksi jamur kulit yang menular ke manusia dan hewan lain.",
            commonSymptoms = listOf("Lesi melingkar, rambut rontok", "Kerak atau sisik di kulit", "Kadang gatal"),
            homeCareTips = listOf("Isolasi hewan yang terinfeksi", "Bersihkan lingkungan, cuci sprei/mainan", "Gunakan sampo/obat topikal sesuai anjuran dokter"),
            preventionTips = listOf("Jaga kebersihan kandang/rumah", "Hindari kontak dengan hewan terinfeksi", "Disinfeksi rutin area lembap"),
            vetUrgency = "Periksa dokter untuk terapi antijamur dan mencegah penularan."
        ),
        "cat_Scabies" to DiseaseInfo(
            name = "Scabies (tungau) pada kucing",
            species = "cat",
            shortDescription = "Infestasi tungau yang menyebabkan gatal sangat kuat.",
            commonSymptoms = listOf("Gatal hebat", "Kerak tebal di telinga/wajah", "Rambut rontok di area terinfeksi"),
            homeCareTips = listOf("Bersihkan tempat tidur dan lingkungan", "Hindari kontak dengan hewan lain sementara", "Ikuti terapi antiparasit dari dokter"),
            preventionTips = listOf("Kontrol parasit rutin", "Jaga kebersihan alat grooming", "Pisahkan hewan baru sampai dipastikan sehat"),
            vetUrgency = "Segera konsultasi dokter karena sangat menular dan butuh obat resep."
        ),
        "dog_Dermatitis" to DiseaseInfo(
            name = "Dermatitis pada anjing",
            species = "dog",
            shortDescription = "Peradangan kulit akibat alergi, iritasi, atau infeksi ringan.",
            commonSymptoms = listOf("Kemerahan/gatal", "Lecet atau kerak", "Menjilat atau menggaruk terus-menerus"),
            homeCareTips = listOf("Jaga area bersih dan kering", "Gunakan kerah pelindung jika luka digaruk", "Mandikan dengan sampo lembut sesuai saran dokter"),
            preventionTips = listOf("Kontrol kutu/tungau rutin", "Cek alergen makanan/lingkungan", "Keringkan bulu setelah mandi/hujan"),
            vetUrgency = "Kunjungi dokter jika gatal berat, luka bernanah, atau tidak membaik."
        ),
        "dog_Fungal_infections" to DiseaseInfo(
            name = "Infeksi jamur pada anjing",
            species = "dog",
            shortDescription = "Infeksi jamur kulit yang bisa menyebabkan gatal dan kerontokan.",
            commonSymptoms = listOf("Lesi bersisik, rontok lokal", "Bau tidak sedap pada kulit", "Kadang gatal"),
            homeCareTips = listOf("Jaga kulit tetap kering", "Gunakan sampo antijamur sesuai arahan dokter", "Bersihkan lingkungan dan peralatan"),
            preventionTips = listOf("Keringkan bulu setelah basah", "Batasi lingkungan lembap", "Periksa kulit secara berkala"),
            vetUrgency = "Periksa dokter untuk terapi antijamur jika lesi luas/berulang."
        ),
        "dog_Hypersensitivity" to DiseaseInfo(
            name = "Hipersenstivitas/alergi pada anjing",
            species = "dog",
            shortDescription = "Reaksi alergi terhadap makanan, lingkungan, atau gigitan parasit.",
            commonSymptoms = listOf("Gatal menyebar", "Kemerahan atau hot spot", "Menjilat/menggaruk terus"),
            homeCareTips = listOf("Catat pemicu potensial (makanan/lingkungan)", "Gunakan kerah pelindung jika perlu", "Kompres dingin pada area meradang"),
            preventionTips = listOf("Kontrol parasit rutin", "Uji coba diet eliminasi atas arahan dokter", "Jaga kebersihan rumah"),
            vetUrgency = "Konsultasi dokter untuk rencana terapi alergi dan obat yang aman."
        ),
        "dog_demodicosis" to DiseaseInfo(
            name = "Demodicosis (kutu demodex) pada anjing",
            species = "dog",
            shortDescription = "Infestasi Demodex yang menyebabkan kerontokan dan lesi kulit.",
            commonSymptoms = listOf("Rontok bercak", "Kulit bersisik atau kemerahan", "Kadang gatal ringan-sedang"),
            homeCareTips = listOf("Jaga kebersihan kulit", "Ikuti terapi antiparasit dari dokter", "Pantau stres/imunitas hewan"),
            preventionTips = listOf("Perawatan kulit rutin", "Pakan bergizi untuk imunitas", "Kontrol stres"),
            vetUrgency = "Periksa dokter untuk konfirmasi scrap test dan terapi antiparasit."
        ),
        "dog_ringworm" to DiseaseInfo(
            name = "Ringworm (kurap) pada anjing",
            species = "dog",
            shortDescription = "Infeksi jamur kulit yang menular dan butuh terapi antijamur.",
            commonSymptoms = listOf("Lesi melingkar, rontok", "Kulit bersisik", "Kadang gatal"),
            homeCareTips = listOf("Isolasi jika mungkin menular", "Bersihkan lingkungan, cuci alas tidur", "Gunakan sampo/obat topikal sesuai saran dokter"),
            preventionTips = listOf("Jaga kebersihan kandang/rumah", "Hindari kontak dengan hewan terinfeksi", "Disinfeksi rutin area lembap"),
            vetUrgency = "Kunjungi dokter untuk terapi antijamur dan mencegah penularan."
        )
    )
}
