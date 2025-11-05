package com.shirotenma.petpartnertest

interface AuthRepository {
    suspend fun login(email: String, pass: String): Boolean
    suspend fun logout()
    fun observeToken(): kotlinx.coroutines.flow.Flow<String?>
}

