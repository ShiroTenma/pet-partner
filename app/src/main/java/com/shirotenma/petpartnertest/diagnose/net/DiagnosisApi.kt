package com.shirotenma.petpartnertest.diagnose.net

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class DiagnosisDto(
    val species: String,
    val global_class: String,
    val global_conf: Double,
    val detail_class: String?,
    val detail_conf: Double?
)

interface DiagnosisApi {
    @Multipart
    @POST("/api/diagnosis")
    suspend fun diagnose(
        @Part image: MultipartBody.Part
    ): DiagnosisDto
}
