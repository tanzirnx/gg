package com.nitha

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.nitha.utils.Constants

/**
 * NITHA Application class
 */
class NithaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    Constants.CHANNEL_ID_FOREGROUND,
                    "NITHA Service",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Keeps NITHA AI assistant running in background"
                    setShowBadge(false)
                },
                NotificationChannel(
                    Constants.CHANNEL_ID_NOTIFICATIONS,
                    "NITHA Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications from NITHA AI"
                }
            )

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(channels)
        }
    }
}
