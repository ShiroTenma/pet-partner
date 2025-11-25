package com.shirotenma.petpartnertest.schedule

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.shirotenma.petpartnertest.R

object ReminderNotification {
    private const val CHANNEL_ID = "schedule_reminders"
    private const val CHANNEL_NAME = "Schedule Reminders"

    fun show(context: Context, title: String, message: String) {
        createChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message.ifBlank { "Jangan lupa jadwal hewan peliharaanmu." })
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }

    private fun createChannel(context: Context) {
        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Pengingat jadwal perawatan hewan"
            enableLights(true)
            lightColor = Color.GREEN
        }
        mgr.createNotificationChannel(channel)
    }
}
