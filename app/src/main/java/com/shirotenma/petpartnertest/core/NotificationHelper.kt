// app/src/main/java/com/shirotenma/petpartnertest/core/NotificationHelper.kt
package com.shirotenma.petpartnertest.core

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


object NotificationHelper {
    private const val CHANNEL_ID = "pet_reminders"

    private fun ensureChannel(ctx: Context) {
        val ch = NotificationChannel(
            CHANNEL_ID,
            "Pet Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val nm = ctx.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(ch)
    }

    fun notify(ctx: Context, id: Int, title: String, text: String) {
        ensureChannel(ctx)

        val notif = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // <- ganti icon aman
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(ctx).notify(id, notif)
    }
}
