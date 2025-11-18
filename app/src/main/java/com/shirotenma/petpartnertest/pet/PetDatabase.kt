package com.shirotenma.petpartnertest.pet

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shirotenma.petpartnertest.pet.record.PetRecord
import com.shirotenma.petpartnertest.pet.record.PetRecordDao

@Database(
    entities = [
        com.shirotenma.petpartnertest.pet.Pet::class,
        com.shirotenma.petpartnertest.pet.record.PetRecord::class,
        com.shirotenma.petpartnertest.diagnose.db.Diagnosis::class   // ⬅️ baru
    ],
    version = 4, // ⬅️ NAIKKAN
    exportSchema = true
)
abstract class PetDatabase : RoomDatabase() {
    abstract fun petDao(): com.shirotenma.petpartnertest.pet.PetDao
    abstract fun petRecordDao(): com.shirotenma.petpartnertest.pet.record.PetRecordDao
    abstract fun diagnosisDao(): com.shirotenma.petpartnertest.diagnose.db.DiagnosisDao // ⬅️ baru
}


