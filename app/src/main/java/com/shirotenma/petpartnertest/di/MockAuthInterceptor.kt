package com.shirotenma.petpartnertest.di

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

/**
 * Interceptor mock untuk endpoint auth.
 * - POST /auth/register : jika email sudah terdaftar -> 409, jika belum -> 200 dan email disimpan.
 * - POST /auth/login    : selalu 200 (token dummy) kalau email sudah terdaftar; selain itu 401.
 *
 * NB: Data tersimpan di memori proses (reset saat app restart).
 */
class MockAuthInterceptor : Interceptor {

    companion object {
        // Seed 1 user demo
        private val registeredEmails: MutableSet<String> =
            java.util.Collections.synchronizedSet(mutableSetOf("demo@x"))
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val path = req.url.encodedPath
        val isPost = req.method.equals("POST", ignoreCase = true)
        val isLogin = isPost && (path.endsWith("/auth/login") || path.contains("/auth/login/"))
        val isRegister = isPost && (path.endsWith("/auth/register") || path.contains("/auth/register/"))

        if (!isLogin && !isRegister) {
            return chain.proceed(req)
        }

        // Baca body JSON
        val buf = Buffer()
        req.body?.writeTo(buf)
        val bodyStr = buf.readUtf8()

        val email = Regex("\"email\"\\s*:\\s*\"([^\"]+)\"").find(bodyStr)?.groupValues?.getOrNull(1).orEmpty()
        val name  = Regex("\"name\"\\s*:\\s*\"([^\"]*)\"").find(bodyStr)?.groupValues?.getOrNull(1).orEmpty()

        return when {
            isRegister -> {
                // Duplikat?
                if (email.isBlank()) {
                    val json = """{"code":400,"message":"Email required"}"""
                    response(req, 400, "Bad Request", json)
                } else if (registeredEmails.contains(email.lowercase())) {
                    val json = """{"code":409,"message":"Email already used"}"""
                    response(req, 409, "Conflict", json)
                } else {
                    registeredEmails.add(email.lowercase())
                    val safeName = if (name.isNotBlank()) name else email.substringBefore("@")
                    val json = """{"token":"dummy-token","user":{"id":"${registeredEmails.size}","name":"$safeName","email":"$email"}}"""
                    response(req, 200, "OK", json)
                }
            }
            isLogin -> {
                // Login hanya sukses kalau email sudah terdaftar
                if (registeredEmails.contains(email.lowercase())) {
                    val json = """{"token":"dummy-token","user":{"id":"1","name":"Demo","email":"$email"}}"""
                    response(req, 200, "OK", json)
                } else {
                    val json = """{"code":401,"message":"Invalid credentials"}"""
                    response(req, 401, "Unauthorized", json)
                }
            }
            else -> chain.proceed(req)
        }
    }

    private fun response(req: okhttp3.Request, code: Int, msg: String, json: String): Response {
        return Response.Builder()
            .code(code)
            .message(msg)
            .request(req)
            .protocol(Protocol.HTTP_1_1)
            .body(json.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()
    }
}
