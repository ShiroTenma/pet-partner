package com.shirotenma.petpartnertest.diagnose

import android.content.Context
import android.net.Uri
import com.shirotenma.petpartnertest.diagnose.net.DiagnosisApi
import com.shirotenma.petpartnertest.diagnose.net.DiagnosisDto
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class DiagnosisResult(
    val species: String,
    val globalClass: String,
    val globalConf: Double,
    val detailClass: String?,
    val detailConf: Double?,
    val isSupportedAnimal: Boolean,
    val note: String?
) {
    val condition: String = detailClass ?: globalClass
    val severity: String = if (globalClass.contains("healthy", ignoreCase = true)) "healthy" else "skin_issue"
    val confidence: Double = detailConf ?: globalConf
    val tips: List<String> = emptyList() // placeholder, dapat diisi dari knowledge base/ backend
}

@Singleton
class DiagnosisRepository @Inject constructor(
    private val api: DiagnosisApi,
    @ApplicationContext private val appContext: Context
) {
    suspend fun diagnose(
        photoUri: Uri,
        petId: Long? = null,
        metaJson: String? = null
    ): DiagnosisResult {
        val part = uriToMultipart(photoUri)
        val meta: RequestBody = (metaJson ?: buildMeta(petId, photoUri)).toRequestBody("application/json".toMediaType())
        val dto: DiagnosisDto = api.diagnose(part, meta)
        val globalConfVal = dto.global_conf ?: 0.0
        val threshold = dto.unknown_threshold ?: 0.6
        val supported = (dto.is_supported_animal == true) &&
                !dto.species.isNullOrBlank() &&
                globalConfVal >= threshold
        return DiagnosisResult(
            species = dto.species ?: "",
            globalClass = dto.global_class ?: "",
            globalConf = globalConfVal,
            detailClass = dto.detail_class,
            detailConf = dto.detail_conf,
            isSupportedAnimal = supported,
            note = dto.note
        )
    }

    private fun uriToMultipart(uri: Uri): MultipartBody.Part {
        val resolver = appContext.contentResolver
        val input = resolver.openInputStream(uri) ?: throw IllegalArgumentException("Cannot open uri $uri")
        val tempFile = File.createTempFile("upload_", ".jpg", appContext.cacheDir)
        tempFile.outputStream().use { out -> input.copyTo(out) }
        val body = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", tempFile.name, body)
    }

    private fun buildMeta(petId: Long?, uri: Uri): String {
        val ts = System.currentTimeMillis()
        val version = "unknown"
        val pidStr = petId?.toString() ?: "null"
        val photo = uri.toString().replace("\"", "\\\"")
        return """
            {
              "petId": $pidStr,
              "timestamp": $ts,
              "appVersion": "$version",
              "photoUri": "$photo"
            }
        """.trimIndent()
    }
}
