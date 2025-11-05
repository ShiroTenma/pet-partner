package com.shirotenma.petpartnertest.di

import android.content.Context
import androidx.room.Room
import com.shirotenma.petpartnertest.pet.PetDao
import com.shirotenma.petpartnertest.pet.PetDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun db(@ApplicationContext ctx: Context): PetDatabase =
        Room.databaseBuilder(ctx, PetDatabase::class.java, "pet_partner.db")
            .fallbackToDestructiveMigration() // dev only
            .build()

    @Provides
    fun petDao(db: PetDatabase): PetDao = db.petDao()
}
