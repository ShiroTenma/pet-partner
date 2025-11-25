package com.shirotenma.petpartnertest.diagnose.net

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class DiagnosisDto(
    val species: String?,
    val global_class: String?,
    val global_conf: Double?,
    val detail_class: String?,
    val detail_conf: Double?,
    val is_supported_animal: Boolean?,
    val unknown_threshold: Double? = null,
    val note: String? = null,
    val request_meta: Map<String, Any>? = null
)

interface DiagnosisApi {
    @Multipart
    @POST("api/diagnosis")
    suspend fun diagnose(
        @Part image: MultipartBody.Part,
        @Part meta: RequestBody
    ): DiagnosisDto
}
