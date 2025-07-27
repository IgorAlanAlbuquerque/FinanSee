package com.igor.finansee.utils


import android.content.Context
import java.time.LocalDate

class NotificationTracker(context: Context) {

    private val prefs = context.getSharedPreferences("notification_tracker_prefs", Context.MODE_PRIVATE)

    private fun generateKey(month: LocalDate, categoryId: String): String {
        return "alert_${month.year}_${month.monthValue}_$categoryId"
    }

    fun hasNotificationBeenSent(month: LocalDate, categoryId: String): Boolean {
        val key = generateKey(month, categoryId)
        return prefs.getBoolean(key, false)
    }

    fun markNotificationAsSent(month: LocalDate, categoryId: String) {
        val key = generateKey(month, categoryId)
        prefs.edit().putBoolean(key, true).apply()
    }
}