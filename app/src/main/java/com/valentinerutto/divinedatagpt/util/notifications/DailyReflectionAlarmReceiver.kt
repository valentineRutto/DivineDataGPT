package com.valentinerutto.divinedatagpt.util.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class DailyReflectionAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            DailyReflectionNotificationConfig.EXACT_ALARM_ACTION -> {
                enqueueNotificationWork(context)
                DailyReflectionScheduler.scheduleNextExactAlarmIfAllowed(context)
            }

            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                DailyReflectionScheduler.scheduleDailyReminder(context)
            }
        }
    }

    private fun enqueueNotificationWork(context: Context) {
        val request = OneTimeWorkRequestBuilder<DailyReflectionWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            DailyReflectionNotificationConfig.UNIQUE_IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
