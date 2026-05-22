package com.valentinerutto.divinedatagpt.util.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.valentinerutto.divinedatagpt.MainActivity
import com.valentinerutto.divinedatagpt.R
import com.valentinerutto.divinedatagpt.data.local.DivineDatabase

class DailyReflectionWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        createNotificationChannel()

        if (!canPostNotifications()) {
            return Result.success()
        }

        val verse = DivineDatabase.getDatabase(applicationContext)
            .verseDao()
            .getRandomDailyVerse()

        val title = "Your daily reflection is ready"
        val body = verse?.let {
            "${it.bookName} ${it.chapter}:${it.verse} - ${it.text.take(96)}"
        } ?: "Take a quiet moment with today's scripture."

        val notification = NotificationCompat.Builder(
            applicationContext,
            DailyReflectionNotificationConfig.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(dailyReflectionPendingIntent())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(
            DailyReflectionNotificationConfig.NOTIFICATION_ID,
            notification
        )

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            DailyReflectionNotificationConfig.CHANNEL_ID,
            DailyReflectionNotificationConfig.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "A daily scripture prompt that opens your reflection screen."
        }

        applicationContext.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    private fun canPostNotifications(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun dailyReflectionPendingIntent(): PendingIntent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(DailyReflectionNotificationConfig.DEEP_LINK_URI),
            applicationContext,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        return PendingIntent.getActivity(
            applicationContext,
            DailyReflectionNotificationConfig.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
