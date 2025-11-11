// app/src/main/java/com/shirotenma/petpartnertest/data/AuthRepositoryImpl.kt
package com.shirotenma.petpartnertest.data

import android.util.Log
import com.shirotenma.petpartnertest.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val session: SessionManager
) : AuthRepository {

    // AuthRepositoryImpl.kt (potongan)
    override suspend fun login(email: String, pass: String): Boolean = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val cleanPass = pass.trim()
        try {
            val res = api.login(LoginReq(cleanEmail, cleanPass))
            session.setToken(res.token)
            true
        } catch (e: HttpException) {
            // 400/401 dari server = kredensial salah
            false
        } catch (_: Throwable) {
            false
        }
    }

    override suspend fun register(name: String, email: String, pass: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            val cleanName = name.trim()
            val cleanEmail = email.trim().lowercase()
            val cleanPass = pass.trim()
            try {
                val res = api.register(RegisterReq(cleanName, cleanEmail, cleanPass))
                // kalau server auto-login setelah register:
                if (!res.token.isNullOrBlank()) session.setToken(res.token)
                Result.success(true)
            } catch (e: HttpException) {
                if (e.code() == 409) Result.failure(IllegalStateException("EMAIL_ALREADY_USED"))
                else Result.failure(e)
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }


    override suspend fun logout() {
        session.setToken(null)
    }

    override fun observeToken() = session.tokenFlow
}
