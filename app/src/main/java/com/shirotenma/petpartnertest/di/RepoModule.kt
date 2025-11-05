// app/src/main/java/com/shirotenma/petpartnertest/di/RepoModule.kt
package com.shirotenma.petpartnertest.di

import com.shirotenma.petpartnertest.AuthRepository
import com.shirotenma.petpartnertest.data.AuthRepositoryImpl
import com.shirotenma.petpartnertest.pet.PetRepository
import com.shirotenma.petpartnertest.pet.PetRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPetRepository(impl: PetRepositoryImpl): PetRepository
}
