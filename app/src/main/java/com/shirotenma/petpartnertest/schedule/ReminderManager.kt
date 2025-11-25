package com.shirotenma.petpartnertest.schedule

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleReminder(schedule: Schedule) {
        val triggerAt = parseMillis(schedule.date, schedule.time) ?: return
        val delayMs = triggerAt - System.currentTimeMillis()
        if (delayMs <= 0) return

        val data = workDataOf(
            "title" to schedule.title,
            "notes" to (schedule.notes ?: ""),
            "scheduleId" to schedule.id
        )

        val request = OneTimeWorkRequestBuilder<ScheduleReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(workTag(schedule.id))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workTag(schedule.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelReminder(id: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(workTag(id))
    }

    private fun workTag(id: Long) = "schedule_reminder_$id"

    private fun parseMillis(date: String, time: String): Long? = runCatching {
        val localDate = LocalDate.parse(date)
        val localTime = LocalTime.parse(time)
        val zdt = localDate.atTime(localTime).atZone(ZoneId.systemDefault())
        zdt.toInstant().toEpochMilli()
    }.getOrNull()
}

@HiltWorker
class ScheduleReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "Pet schedule"
        val notes = inputData.getString("notes").orEmpty()
        ReminderNotification.show(applicationContext, title, notes)
        return Result.success()
    }
}
