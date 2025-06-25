package com.igor.finansee.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationReaderService : NotificationListenerService() {

    private val TAG = "NotificationReader"

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString("android.title")
        val text = extras.getCharSequence("android.text")?.toString()

        if (packageName == "br.com.nubank.mobile" || packageName.contains("digio")) {
            Log.d(TAG, "Notificação do Nubank/Digio Capturada!")
            Log.d(TAG, "Título: $title")
            Log.d(TAG, "Texto: $text")

            // TODO: Aqui implementaria a lógica para processar o texto,
        }
    }
}
