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
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/** Fake “DB” kecil untuk mock auth di memori proses */
private object FakeAuthDb {
    // email -> password
    val users = mutableMapOf(
        "demo@x" to "demo123" // akun demo supaya login uji cepat
    )
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
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val req = chain.request()
                val path = req.url.encodedPath
                val method = req.method.uppercase()

                // Baca body request jika ada (untuk cek duplikat email, dll)
                val bodyStr: String = try {
                    val buf = Buffer()
                    req.body?.writeTo(buf)
                    buf.readUtf8()
                } catch (_: Throwable) {
                    ""
                }

                // helper bikin response JSON
                fun respond(code: Int, json: String): okhttp3.Response {
                    val media = "application/json".toMediaType()
                    return okhttp3.Response.Builder()
                        .code(code)
                        .message(if (code in 200..299) "OK" else "ERR")
                        .request(req)
                        .protocol(Protocol.HTTP_1_1)
                        .body(json.toResponseBody(media))
                        .build()
                }

                // ---- Mock REGISTER ----
                if (method == "POST" && (path.endsWith("/auth/register") || path.contains("/auth/register/"))) {
                    // simple check: kalau email mengandung "exists@" maka 409
                    val duplicate = bodyStr.contains("\"email\":\"exists@", ignoreCase = true)
                    if (duplicate) {
                        return@addInterceptor respond(409, """{"message":"Email already used"}""")
                    }
                    // sukses → langsung anggap logged in
                    val json = """
                        {
                          "token":"dummy-token-reg",
                          "user":{"id":"2","name":"New User","email":"new@x"}
                        }
                    """.trimIndent()
                    return@addInterceptor respond(200, json)
                }

                // ---- Mock LOGIN ----
                if (method == "POST" && (path.endsWith("/auth/login") || path.contains("/auth/login/"))) {
                    val json = """
                        {
                          "token":"dummy-token",
                          "user":{"id":"1","name":"Demo","email":"demo@x"}
                        }
                    """.trimIndent()
                    return@addInterceptor respond(200, json)
                }

                // ---- Mock DIAGNOSE (contoh) ----
                if (method == "POST" && (path.endsWith("/diagnose") || path.contains("/diagnose"))) {
                    // balasan dummy: condition, severity, confidence, tips
                    val json = """
                        {
                          "condition":"Possible dermatitis",
                          "severity":"mild",
                          "confidence":0.78,
                          "tips":["Keep area clean","Use vet-approved ointment","Monitor scratching behavior"]
                        }
                    """.trimIndent()
                    return@addInterceptor respond(200, json)
                }

                // default: teruskan ke jaringan (kalau suatu saat pakai real API)
                chain.proceed(req)
            }
            .build()
    }

    @Provides
    @Singleton
    fun retrofit(
        @Named("baseUrl") baseUrl: String,
        ok: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(ok)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun api(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
