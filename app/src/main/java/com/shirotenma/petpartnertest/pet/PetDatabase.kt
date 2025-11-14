package com.shirotenma.petpartnertest.pet

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shirotenma.petpartnertest.pet.record.PetRecord
import com.shirotenma.petpartnertest.pet.record.PetRecordDao

@Database(
    entities = [Pet::class, com.shirotenma.petpartnertest.pet.record.PetRecord::class],
    version = 3,
    exportSchema = true
)
abstract class PetDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun petRecordDao(): com.shirotenma.petpartnertest.pet.record.PetRecordDao
}

