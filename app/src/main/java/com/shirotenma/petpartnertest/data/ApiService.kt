package com.shirotenma.petpartnertest.data

import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body req: LoginReq): LoginResp
}

data class LoginReq(val email: String, val password: String)
data class LoginResp(val token: String, val user: UserDto)
data class UserDto(val id: String, val name: String, val email: String)
