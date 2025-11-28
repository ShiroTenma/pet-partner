// app/src/main/java/com/shirotenma/petpartnertest/di/NetworkModule.kt
package com.shirotenma.petpartnertest.di

import com.shirotenma.petpartnertest.data.ApiService
import com.shirotenma.petpartnertest.data.MockApiService
import com.shirotenma.petpartnertest.diagnose.net.DiagnosisApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val USE_MOCK = true

    @Provides
    @Named("baseUrl")
    fun baseUrl(): String = "http://10.160.20.190:8000/"

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun okHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            // Jika butuh mock, aktifkan di USE_MOCK dan tambahkan interceptor lain
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
    fun api(retrofit: Retrofit): ApiService =
        if (USE_MOCK) MockApiService() else retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun diagnosisApi(retrofit: Retrofit): DiagnosisApi =
        retrofit.create(DiagnosisApi::class.java)
}
