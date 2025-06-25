package com.igor.finansee.data.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    const val DAILY_REMINDER_WORK_NAME = "daily_reminder_work"

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

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        val inputData = Data.Builder()
            .putString(NotificationWorker.NOTIFICATION_TITLE_KEY, "É hora de cuidar das finanças! \uD83D\uDCB8")
            .putString(NotificationWorker.NOTIFICATION_MESSAGE_KEY, "Lembre-se de registrar seus gastos de hoje.")
            .build()

        val now = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (now.after(scheduledTime)) {
            scheduledTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = scheduledTime.timeInMillis - now.timeInMillis

        val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyWorkRequest
        )
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
    }

    fun cancel(context: Context, uniqueWorkName: String) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName)
    }
}