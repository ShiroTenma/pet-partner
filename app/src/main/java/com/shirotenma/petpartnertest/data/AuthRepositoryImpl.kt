package com.shirotenma.petpartnertest.data

import android.util.Log
import com.shirotenma.petpartnertest.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val session: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, pass: String): Boolean {
        return try {
            val res = api.login(LoginReq(email, pass))
            val ok = res.token.isNotBlank()
            if (ok) session.setToken(res.token)
            ok
        } catch (t: Throwable) {
            Log.e("AuthRepo", "login failed", t)
            false
        }
    }

    override suspend fun register(name: String, email: String, pass: String): Boolean {
        return try {
            val res = api.register(RegisterReq(name, email, pass))
            val ok = res.token.isNotBlank()
            if (ok) session.setToken(res.token)
            ok
        } catch (t: Throwable) {
            Log.e("AuthRepo", "register failed", t)
            false
        }
    }

    override suspend fun logout() {
        session.setToken(null)
    }

    override fun observeToken() = session.tokenFlow
}
