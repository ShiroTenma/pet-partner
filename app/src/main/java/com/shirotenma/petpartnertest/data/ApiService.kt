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
}

@JsonClass(generateAdapter = true)
data class LoginReq(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class RegisterReq(val name: String, val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class LoginResp(val token: String, val user: UserDto)

@JsonClass(generateAdapter = true)
data class UserDto(val id: String, val name: String, val email: String)
