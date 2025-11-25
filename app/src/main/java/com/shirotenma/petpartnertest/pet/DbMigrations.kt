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

// v3 -> v4 : tambah tabel diagnosis
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS diagnosis (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                petId INTEGER NOT NULL,
                condition TEXT NOT NULL,
                severity TEXT NOT NULL,
                confidence REAL NOT NULL,
                tipsJoined TEXT NOT NULL,
                photoUri TEXT,
                createdAt INTEGER NOT NULL,
                FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_diagnosis_petId ON diagnosis(petId)")
    }
}

// v4 -> v5 : tambah tabel journals dan bird_messages
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS journals (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                petId INTEGER NOT NULL,
                mood TEXT NOT NULL,
                content TEXT NOT NULL,
                date TEXT NOT NULL,
                FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_journals_petId ON journals(petId)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS bird_messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                preview TEXT NOT NULL,
                sourceJournalId INTEGER,
                lastReply TEXT,
                fromSelf INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(sourceJournalId) REFERENCES journals(id) ON DELETE SET NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_bird_messages_sourceJournalId ON bird_messages(sourceJournalId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_bird_messages_fromSelf ON bird_messages(fromSelf)")
    }
}

// v5 -> v6 : tambah tabel schedules
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS schedules (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                petId INTEGER NOT NULL,
                title TEXT NOT NULL,
                type TEXT NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                notes TEXT,
                FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_schedules_petId ON schedules(petId)")
    }
}

// v6 -> v7 : tambah kolom remind ke schedules
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE schedules ADD COLUMN remind INTEGER NOT NULL DEFAULT 0")
    }
}
