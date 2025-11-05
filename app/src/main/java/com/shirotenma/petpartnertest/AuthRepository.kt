package com.shirotenma.petpartnertest

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, pass: String): Boolean
    suspend fun register(name: String, email: String, pass: String): Boolean   // <-- baru
    suspend fun logout()
    fun observeToken(): Flow<String?>
}
