package com.shirotenma.petpartnertest.di

import android.content.Context
import androidx.room.Room
import com.shirotenma.petpartnertest.pet.MIGRATION_1_2
import com.shirotenma.petpartnertest.pet.MIGRATION_2_3
import com.shirotenma.petpartnertest.pet.PetDao
import com.shirotenma.petpartnertest.pet.PetDatabase
import com.shirotenma.petpartnertest.pet.record.PetRecordDao
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // urut!
            // .fallbackToDestructiveMigration() // JANGAN aktifkan kalau ingin jaga data
            .build()

    @Provides fun petDao(db: PetDatabase): PetDao = db.petDao()

    @Provides fun petRecordDao(db: PetDatabase): PetRecordDao = db.petRecordDao()

    @Provides
    fun diagnosisDao(db: PetDatabase): com.shirotenma.petpartnertest.diagnose.db.DiagnosisDao = db.diagnosisDao()


}
