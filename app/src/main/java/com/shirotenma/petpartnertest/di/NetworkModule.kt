// app/src/main/java/com/shirotenma/petpartnertest/di/NetworkModule.kt
package com.shirotenma.petpartnertest.di

import com.shirotenma.petpartnertest.data.ApiService
import com.shirotenma.petpartnertest.data.LoginReq
import com.shirotenma.petpartnertest.data.RegisterReq
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/** Fake “DB” kecil untuk mock auth di memori proses */
private object FakeAuthDb {
    // email -> password
    val users = ConcurrentHashMap<String, String>().apply {
        put("demo@x", "demo123") // akun demo
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Named("baseUrl")
    fun baseUrl(): String = "https://example.petpartner.api/"

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // opsional kalau pakai KSP; boleh dibiarkan
        .build()

    @Provides
    @Singleton
    fun okHttp(moshi: Moshi): OkHttpClient {
        val loginAdapter = moshi.adapter(LoginReq::class.java)
        val regAdapter = moshi.adapter(RegisterReq::class.java)
        val json = "application/json".toMediaType()

        fun jsonResponse(req: okhttp3.Request, code: Int, body: String) =
            okhttp3.Response.Builder()
                .code(code)
                .message(if (code in 200..299) "OK" else "ERR")
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .body(body.toResponseBody(json))
                .build()

        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val req = chain.request()
                val path = req.url.encodedPath
                val isPost = req.method.equals("POST", ignoreCase = true)

                // --- REGISTER ---
                val isRegister = isPost && (path.endsWith("/auth/register") || path.contains("/auth/register/"))
                if (isRegister) {
                    val bodyStr = runCatching {
                        val buf = okio.Buffer()
                        req.body?.writeTo(buf)
                        buf.readUtf8()
                    }.getOrNull().orEmpty()

                    val parsed = runCatching { regAdapter.fromJson(bodyStr) }.getOrNull()
                    if (parsed == null) {
                        return@addInterceptor jsonResponse(req, 400, """{"message":"Bad JSON"}""")
                    }

                    val email = parsed.email.trim().lowercase()
                    val pass  = parsed.password

                    // kalau sudah ada → 409
                    if (FakeAuthDb.users.containsKey(email)) {
                        return@addInterceptor jsonResponse(req, 409, """{"message":"Email already used"}""")
                    }

                    // simpan user baru
                    FakeAuthDb.users[email] = pass

                    val okBody = """
                        {"token":"dummy-token-reg",
                         "user":{"id":"${email.hashCode()}","name":"${parsed.name}","email":"$email"}}
                    """.trimIndent()
                    return@addInterceptor jsonResponse(req, 200, okBody)
                }

                // --- LOGIN ---
                val isLogin = isPost && (path.endsWith("/auth/login") || path.contains("/auth/login/"))
                if (isLogin) {
                    val bodyStr = runCatching {
                        val buf = okio.Buffer()
                        req.body?.writeTo(buf)
                        buf.readUtf8()
                    }.getOrNull().orEmpty()

                    val parsed = runCatching { loginAdapter.fromJson(bodyStr) }.getOrNull()
                    if (parsed == null) {
                        return@addInterceptor jsonResponse(req, 400, """{"message":"Bad JSON"}""")
                    }

                    val email = parsed.email.trim().lowercase()
                    val pass  = parsed.password

                    val ok = FakeAuthDb.users[email]?.let { it == pass } == true
                    if (!ok) {
                        return@addInterceptor jsonResponse(req, 401, """{"message":"Invalid email or password"}""")
                    }

                    val okBody = """
                        {"token":"dummy-token",
                         "user":{"id":"${email.hashCode()}","name":"Demo","email":"$email"}}
                    """.trimIndent()
                    return@addInterceptor jsonResponse(req, 200, okBody)
                }

                // Fallback: balas 404 biar tak benar2 ke jaringan saat dev
                return@addInterceptor jsonResponse(req, 404, """{"message":"Not mocked in dev"}""")
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides @Singleton
    fun retrofit(
        @Named("baseUrl") baseUrl: String,
        ok: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(ok)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides @Singleton
    fun api(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
