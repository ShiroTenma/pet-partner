// app/src/main/java/com/shirotenma/petpartnertest/core/ReminderWorker.kt
package com.shirotenma.petpartnertest.core

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.shirotenma.petpartnertest.pet.record.PetRecordDao

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val recordDao: PetRecordDao
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val today = LocalDate.now()
        val fmt = DateTimeFormatter.ISO_LOCAL_DATE

        val all = recordDao.getAllOnce() // sudah tidak ambigu

        for (r in all) {
            val d = runCatching { LocalDate.parse(r.date, fmt) }.getOrNull() ?: continue
            val days = ChronoUnit.DAYS.between(today, d).toInt()
            if (days == 7 || days == 0) {
                val title = if (days == 7) "Upcoming: ${r.title}" else "Today: ${r.title}"
                val text = listOfNotNull(
                    r.type.ifBlank { null },
                    r.date
                ).joinToString(" • ")

                NotificationHelper.notify(
                    applicationContext,
                    ((r.id % Int.MAX_VALUE).toInt()) + days,
                    title,
                    text
                )
            }
        }
        Result.success()
    }
}
