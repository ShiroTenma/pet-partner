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
import okio.Buffer
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
        put("demo@x", "demo123") // akun demo supaya login uji cepat
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
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun okHttp(moshi: Moshi): OkHttpClient {
        val loginAdapter = moshi.adapter(LoginReq::class.java)
        val regAdapter = moshi.adapter(RegisterReq::class.java)

        fun jsonResponse(req: okhttp3.Request, code: Int, body: String) =
            okhttp3.Response.Builder()
                .code(code)
                .message(if (code == 200) "OK" else "ERR")
                .request(req)
                .protocol(Protocol.HTTP_1_1)
                .body(body.toResponseBody("application/json".toMediaType()))
                .build()

        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val req = chain.request()
                val path = req.url.encodedPath
                val method = req.method

                // --- Mock: POST /auth/login ---
                if (method == "POST" && path.endsWith("/auth/login")) {
                    val buf = Buffer().also { req.body?.writeTo(it) }
                    val bodyStr = buf.readUtf8()
                    val payload = runCatching { loginAdapter.fromJson(bodyStr) }.getOrNull()
                    val email = payload?.email.orEmpty()
                    val pass = payload?.password.orEmpty()

                    val saved = FakeAuthDb.users[email]
                    return@addInterceptor if (saved != null && saved == pass) {
                        val json = """
                          {"token":"dummy-token",
                           "user":{"id":"1","name":"${email.substringBefore('@')}",
                                   "email":"$email"}}
                        """.trimIndent()
                        jsonResponse(req, 200, json)
                    } else {
                        jsonResponse(req, 401, """{"error":"INVALID_CREDENTIALS"}""")
                    }
                }

                // --- Mock: POST /auth/register ---
                if (method == "POST" && path.endsWith("/auth/register")) {
                    val buf = Buffer().also { req.body?.writeTo(it) }
                    val bodyStr = buf.readUtf8()
                    val payload = runCatching { regAdapter.fromJson(bodyStr) }.getOrNull()
                    val name = payload?.name?.ifBlank { "User" } ?: "User"
                    val email = payload?.email.orEmpty()
                    val pass = payload?.password.orEmpty()

                    return@addInterceptor if (FakeAuthDb.users.containsKey(email)) {
                        jsonResponse(req, 409, """{"error":"EMAIL_EXISTS"}""")
                    } else {
                        FakeAuthDb.users[email] = pass
                        val json = """
                          {"token":"dummy-token",
                           "user":{"id":"${FakeAuthDb.users.size}",
                                   "name":"$name","email":"$email"}}
                        """.trimIndent()
                        jsonResponse(req, 200, json)
                    }
                }

                // --- Short-circuit host dummy supaya tidak pernah benar2 ke jaringan ---
                if (req.url.host == "example.petpartner.api") {
                    return@addInterceptor jsonResponse(
                        req, 404, """{"error":"NO_BACKEND_MOCKED"}"""
                    )
                }

                chain.proceed(req)
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
