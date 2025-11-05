// app/src/main/java/com/shirotenma/petpartnertest/di/NetworkModule.kt
package com.shirotenma.petpartnertest.di

import com.shirotenma.petpartnertest.data.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import java.util.concurrent.TimeUnit


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Named("baseUrl")
    fun baseUrl(): String = "https://example.petpartner.api/"

    @Provides
    @Singleton
    fun okHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            // DEV MOCK: balas sukses untuk /auth/login
            .addInterceptor { chain ->
                val req = chain.request()
                val path = req.url.encodedPath
                val isLogin = req.method == "POST" && (path.endsWith("/auth/login") || path.contains("/auth/login/"))
                val isRegister = req.method == "POST" && (path.endsWith("/auth/register") || path.contains("/auth/register/"))

                // --- MOCK REGISTER ---
                if (isRegister) {
                    // trik: kalau email mengandung "used", anggap sudah terdaftar → 409
                    val emailIsUsed = req.body != null && req.body.toString().contains("used", ignoreCase = true)
                    return@addInterceptor if (emailIsUsed) {
                        val jsonErr = """{"code":409,"message":"Email already used"}"""
                        okhttp3.Response.Builder()
                            .code(409)
                            .message("Conflict")
                            .request(req)
                            .protocol(okhttp3.Protocol.HTTP_1_1)
                            .body(jsonErr.toResponseBody("application/json".toMediaTypeOrNull()))
                            .build()
                    } else {
                        val jsonOk = """{"token":"dummy-token","user":{"id":"2","name":"New User","email":"demo@x"}}"""
                        okhttp3.Response.Builder()
                            .code(200)
                            .message("OK")
                            .request(req)
                            .protocol(okhttp3.Protocol.HTTP_1_1)
                            .body(jsonOk.toResponseBody("application/json".toMediaTypeOrNull()))
                            .build()
                    }
                }

                // --- MOCK LOGIN (punyamu yang lama, biarkan) ---
                if (isLogin) {
                    val json = """{"token":"dummy-token","user":{"id":"1","name":"Demo","email":"demo@x"}}"""
                    return@addInterceptor okhttp3.Response.Builder()
                        .code(200).message("OK").request(req)
                        .protocol(okhttp3.Protocol.HTTP_1_1)
                        .body(json.toResponseBody("application/json".toMediaTypeOrNull()))
                        .build()
                }

                chain.proceed(req)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())   // penting untuk data class Kotlin
        .build()

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
    fun api(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
