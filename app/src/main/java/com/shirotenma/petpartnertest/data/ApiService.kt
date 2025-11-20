package com.shirotenma.petpartnertest.data

import retrofit2.http.Body
import retrofit2.http.POST

// ==== AUTH ==== //
data class LoginReq(
    val email: String,
    val password: String
)

data class RegisterReq(
    val name: String,
    val email: String,
    val password: String
)

data class AuthUserDto(
    val id: String,
    val name: String,
    val email: String
)

data class AuthRes(
    val token: String,
    val user: AuthUserDto
)

// ==== DIAGNOSE ==== //
data class DiagnoseReq(
    val petId: Long,
    val photoUri: String
)

data class DiagnoseRes(
    val condition: String,
    val severity: String,
    val confidence: Double,
    val tips: List<String>
)

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body req: LoginReq): AuthRes

    @POST("auth/register")
    suspend fun register(@Body req: RegisterReq): AuthRes

    @POST("diagnose")
    suspend fun diagnose(@Body req: DiagnoseReq): DiagnoseRes
}
