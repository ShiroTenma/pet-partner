package com.shirotenma.petpartnertest.diagnose.net

import retrofit2.http.Body
import retrofit2.http.POST

data class DiagnoseReq(val imageUri: String)
data class DiagnoseResp(
    val condition: String,
    val severity: String,
    val confidence: Double,
    val tips: List<String>?
)

interface DiagnoseApi {
    @POST("diagnose")
    suspend fun diagnose(@Body body: DiagnoseReq): DiagnoseResp

    // overload praktis kalau kamu mau langsung kirim string:
    suspend fun diagnose(imageUri: String): DiagnoseResp = diagnose(DiagnoseReq(imageUri))
}
