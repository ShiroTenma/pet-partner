package com.shirotenma.petpartnertest.diagnose

import com.shirotenma.petpartnertest.diagnose.net.DiagnosisApi
import com.shirotenma.petpartnertest.diagnose.net.DiagnosisDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

/**
 * Helper: given image File + JSON string meta, build multipart and call API.
 */
suspend fun DiagnosisApi.uploadDiagnosis(
    imageFile: File,
    metaJson: String = "{}"
): DiagnosisDto = withContext(Dispatchers.IO) {
    val imagePart = MultipartBody.Part.createFormData(
        "image",
        imageFile.name,
        imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
    )
    val metaPart: RequestBody = metaJson.toRequestBody("application/json".toMediaType())
    diagnose(imagePart, metaPart)
}
