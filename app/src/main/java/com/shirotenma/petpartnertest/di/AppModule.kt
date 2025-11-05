package com.shirotenma.petpartnertest.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named   // 👈 ADD THIS
import javax.inject.Singleton
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("greeting")
    fun provideGreeting(): String = "Hello from Hilt 👋"

    @Provides
    fun provideAppContext(
        @ApplicationContext ctx: Context
    ): Context = ctx
}
