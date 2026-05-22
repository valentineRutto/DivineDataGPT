package com.valentinerutto.divinedatagpt.util.notifications

object DailyReflectionNotificationConfig {
    const val CHANNEL_ID = "daily_reflection_channel"
    const val CHANNEL_NAME = "Daily reflections"
    const val NOTIFICATION_ID = 1001
    const val DEEP_LINK_URI = "divinedatagpt://daily-reflection"
    const val EXACT_ALARM_ACTION = "com.valentinerutto.divinedatagpt.DAILY_REFLECTION_ALARM"
    const val UNIQUE_PERIODIC_WORK_NAME = "daily_reflection_periodic_work"
    const val UNIQUE_IMMEDIATE_WORK_NAME = "daily_reflection_immediate_work"
    const val DEFAULT_HOUR = 8
    const val DEFAULT_MINUTE = 0
}
