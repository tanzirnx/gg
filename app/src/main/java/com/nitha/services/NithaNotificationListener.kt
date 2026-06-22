package com.nitha.services

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.nitha.models.NithaNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * NITHA Notification Listener - reads and manages notifications
 */
class NithaNotificationListener : NotificationListenerService() {

    companion object {
        private val _notifications = MutableStateFlow<List<NithaNotification>>(emptyList())
        val notifications: StateFlow<List<NithaNotification>> = _notifications.asStateFlow()

        private val _lastNotification = MutableStateFlow<NithaNotification?>(null)
        val lastNotification: StateFlow<NithaNotification?> = _lastNotification.asStateFlow()

        var isRunning = false
            private set
    }

    private val notificationList = mutableListOf<NithaNotification>()

    override fun onCreate() {
        super.onCreate()
        isRunning = true
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let { notification ->
            val nithaNotification = NithaNotification(
                id = notification.key,
                packageName = notification.packageName,
                title = notification.notification.extras.getString(Notification.EXTRA_TITLE) ?: "",
                text = notification.notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: "",
                timestamp = notification.postTime,
                isImportant = isImportantNotification(notification)
            )

            notificationList.add(0, nithaNotification)
            if (notificationList.size > 100) {
                notificationList.removeAt(notificationList.size - 1)
            }

            _notifications.value = notificationList.toList()
            _lastNotification.value = nithaNotification
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let { notification ->
            notificationList.removeAll { it.id == notification.key }
            _notifications.value = notificationList.toList()
        }
    }

    private fun isImportantNotification(sbn: StatusBarNotification): Boolean {
        val importantPackages = listOf(
            "com.whatsapp", "com.facebook.orca", "com.telegram.messenger",
            "com.google.android.gm", "com.android.messaging"
        )
        return importantPackages.contains(sbn.packageName)
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }
}
