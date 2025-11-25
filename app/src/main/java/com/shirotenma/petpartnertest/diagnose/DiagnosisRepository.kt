package com.shirotenma.petpartnertest.diagnose

import android.content.Context
import android.net.Uri
import com.shirotenma.petpartnertest.diagnose.net.DiagnosisApi
import com.shirotenma.petpartnertest.diagnose.net.DiagnosisDto
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class DiagnosisResult(
    val species: String,
    val globalClass: String,
    val globalConf: Double,
    val detailClass: String?,
    val detailConf: Double?
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
        photoUri: Uri
    ): DiagnosisResult {
        val part = uriToMultipart(photoUri)
        val dto: DiagnosisDto = api.diagnose(part)
        return DiagnosisResult(
            species = dto.species,
            globalClass = dto.global_class,
            globalConf = dto.global_conf,
            detailClass = dto.detail_class,
            detailConf = dto.detail_conf
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
}
