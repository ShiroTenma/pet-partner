package com.shirotenma.petpartnertest.pet.record

import androidx.room.TypeConverter

enum class RecordType { VACCINATION, DEWORMING, CHECKUP, SURGERY, OTHER }

class Converters {
    @TypeConverter fun fromRecordType(t: RecordType?): String? = t?.name
    @TypeConverter fun toRecordType(s: String?): RecordType? = s?.let { RecordType.valueOf(it) }
}
