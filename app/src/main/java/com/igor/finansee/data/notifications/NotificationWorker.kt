package com.igor.finansee.data.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.igor.finansee.R

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "lembretes_channel"
        const val CHANNEL_NAME = "Lembretes"
        const val NOTIFICATION_ID_KEY = "notification_id"
        const val NOTIFICATION_TITLE_KEY = "notification_title"
        const val NOTIFICATION_MESSAGE_KEY = "notification_message"
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        createNotificationChannel()

        val notificationId = inputData.getInt(NOTIFICATION_ID_KEY, 0)
        val title = inputData.getString(NOTIFICATION_TITLE_KEY) ?: "Lembrete"
        val message = inputData.getString(NOTIFICATION_MESSAGE_KEY) ?: "Você tem um novo lembrete."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }

        return Result.success()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = "Canal para lembretes do aplicativo."
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}