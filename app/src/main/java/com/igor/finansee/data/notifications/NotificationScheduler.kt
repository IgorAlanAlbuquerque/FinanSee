package com.igor.finansee.data.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    fun schedule(
        context: Context,
        delay: Long,
        timeUnit: TimeUnit,
        uniqueWorkName: String,
        title: String,
        message: String
    ) {
        val inputData = Data.Builder()
            .putInt(NotificationWorker.NOTIFICATION_ID_KEY, uniqueWorkName.hashCode())
            .putString(NotificationWorker.NOTIFICATION_TITLE_KEY, title)
            .putString(NotificationWorker.NOTIFICATION_MESSAGE_KEY, message)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, timeUnit)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancel(context: Context, uniqueWorkName: String) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
    }
}