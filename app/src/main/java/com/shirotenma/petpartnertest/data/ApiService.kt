// app/src/main/java/com/shirotenma/petpartnertest/data/ApiService.kt
package com.shirotenma.petpartnertest.data

import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body req: LoginReq): LoginResp

    @POST("auth/register")
    suspend fun register(@Body req: RegisterReq): LoginResp

    @POST("diagnose")
    suspend fun diagnose(@Body req: DiagnoseReq): DiagnoseResp
}

@JsonClass(generateAdapter = true)
data class LoginReq(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class RegisterReq(val name: String, val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class LoginResp(val token: String, val user: UserDto)

@JsonClass(generateAdapter = true)
data class UserDto(val id: String, val name: String, val email: String)


@JsonClass(generateAdapter = true)
data class DiagnoseReq(val petId: Long, val imageBase64: String?)

@JsonClass(generateAdapter = true)
data class DiagnoseResp(
    val condition: String,
    val severity: String,     // Low|Moderate|High
    val confidence: Double,   // 0..1
    val tips: List<String>,   // saran
    val bbox: List<Int>? = null // [x,y,w,h] opsional
)