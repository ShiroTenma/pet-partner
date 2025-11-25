package com.shirotenma.petpartnertest.pet

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shirotenma.petpartnertest.pet.record.PetRecord

@Database(
    entities = [
        com.shirotenma.petpartnertest.pet.Pet::class,
        com.shirotenma.petpartnertest.pet.record.PetRecord::class,
        com.shirotenma.petpartnertest.diagnose.db.Diagnosis::class,   // ��.�,? baru
        com.shirotenma.petpartnertest.journal.db.JournalEntity::class,
        com.shirotenma.petpartnertest.journal.db.BirdMessageEntity::class,
        com.shirotenma.petpartnertest.schedule.Schedule::class
    ],
    version = 7, // ��.�,? NAIKKAN
    exportSchema = true
)
abstract class PetDatabase : RoomDatabase() {
    abstract fun petDao(): com.shirotenma.petpartnertest.pet.PetDao
    abstract fun petRecordDao(): com.shirotenma.petpartnertest.pet.record.PetRecordDao
    abstract fun diagnosisDao(): com.shirotenma.petpartnertest.diagnose.db.DiagnosisDao // ��.�,? baru
    abstract fun journalDao(): com.shirotenma.petpartnertest.journal.db.JournalDao
    abstract fun birdMessageDao(): com.shirotenma.petpartnertest.journal.db.BirdMessageDao
    abstract fun scheduleDao(): com.shirotenma.petpartnertest.schedule.ScheduleDao
}
