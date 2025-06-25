package com.igor.finansee.utils

import android.content.Context
import android.provider.Settings

object PermissionUtils {
    fun isNotificationServiceEnabled(context: Context): Boolean {
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return flat?.contains(context.packageName) == true
    }
}