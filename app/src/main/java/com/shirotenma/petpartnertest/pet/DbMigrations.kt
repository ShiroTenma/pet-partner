package com.shirotenma.petpartnertest.pet

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// v1 -> v2 : buat tabel record awal (tanpa attachmentUri)
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS pet_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                petId INTEGER NOT NULL,
                type TEXT NOT NULL,
                title TEXT NOT NULL,
                date TEXT NOT NULL,
                notes TEXT,
                FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS index_pet_records_petId ON pet_records(petId)")
    }
}

// v2 -> v3 : tambah kolom attachmentUri
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE pet_records ADD COLUMN attachmentUri TEXT")
    }
}
