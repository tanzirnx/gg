package com.nitha.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nitha.MainActivity
import com.nitha.R
import com.nitha.utils.Constants

/**
 * NITHA Foreground Service - keeps AI assistant running in background
 */
class NithaForegroundService : Service() {

    companion object {
        const val ACTION_START = "com.nitha.action.START_FOREGROUND"
        const val ACTION_STOP = "com.nitha.action.STOP_FOREGROUND"
        const val ACTION_VOICE = "com.nitha.action.VOICE_COMMAND"

        fun startService(context: Context) {
            val intent = Intent(context, NithaForegroundService::class.java)
            intent.action = ACTION_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, NithaForegroundService::class.java)
            intent.action = ACTION_STOP
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForegroundService()
            }
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_VOICE -> {
                // Handle voice command from notification
            }
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = buildNotification()
        startForeground(1, notification)
    }

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.CHANNEL_ID_FOREGROUND)
            .setContentTitle("NITHA AI Assistant")
            .setContentText("Voice assistant is active")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID_FOREGROUND,
                "NITHA Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps NITHA AI assistant running"
                setShowBadge(false)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
