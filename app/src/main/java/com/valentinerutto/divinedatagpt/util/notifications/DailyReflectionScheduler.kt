package com.valentinerutto.divinedatagpt.util.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object DailyReflectionScheduler {

    fun scheduleDailyReminder(
        context: Context,
        hour: Int = DailyReflectionNotificationConfig.DEFAULT_HOUR,
        minute: Int = DailyReflectionNotificationConfig.DEFAULT_MINUTE
    ) {
        if (canScheduleExactAlarms(context)) {
            WorkManager.getInstance(context).cancelUniqueWork(
                DailyReflectionNotificationConfig.UNIQUE_PERIODIC_WORK_NAME
            )
            if (!scheduleNextExactAlarm(context, hour, minute)) {
                schedulePeriodicWork(context, hour, minute)
            }
        } else {
            schedulePeriodicWork(context, hour, minute)
        }
    }

    fun scheduleNextExactAlarmIfAllowed(
        context: Context,
        hour: Int = DailyReflectionNotificationConfig.DEFAULT_HOUR,
        minute: Int = DailyReflectionNotificationConfig.DEFAULT_MINUTE
    ) {
        if (!canScheduleExactAlarms(context)) return
        scheduleNextExactAlarm(context, hour, minute)
    }

    private fun canScheduleExactAlarms(context: Context): Boolean {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
    }

    private fun scheduleNextExactAlarm(context: Context, hour: Int, minute: Int): Boolean {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pendingIntent = dailyReflectionAlarmIntent(context)
        val triggerAtMillis = nextTriggerMillis(hour, minute)

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
            return true
        } catch (_: SecurityException) {
            // WorkManager remains the fallback path when exact alarms are restricted.
            return false
        }
    }

    private fun schedulePeriodicWork(context: Context, hour: Int, minute: Int) {
        val request = PeriodicWorkRequestBuilder<DailyReflectionWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelayMillis(hour, minute), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DailyReflectionNotificationConfig.UNIQUE_PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun dailyReflectionAlarmIntent(context: Context): PendingIntent {
        val intent = Intent(context, DailyReflectionAlarmReceiver::class.java).apply {
            action = DailyReflectionNotificationConfig.EXACT_ALARM_ACTION
        }
        return PendingIntent.getBroadcast(
            context,
            DailyReflectionNotificationConfig.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun initialDelayMillis(hour: Int, minute: Int): Long {
        return (nextTriggerMillis(hour, minute) - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    private fun nextTriggerMillis(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        var next = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }
        return System.currentTimeMillis() + Duration.between(now, next).toMillis()
    }
}
