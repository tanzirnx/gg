package com.nitha.utils

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper utilities for NITHA
 */
object Helpers {

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun openApp(context: Context, packageName: String): Boolean {
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun openSettings(context: Context, action: String = Settings.ACTION_SETTINGS) {
        try {
            val intent = Intent(action)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            showToast(context, "Could not open settings")
        }
    }

    fun setBrightness(context: Context, level: Int) {
        try {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                level.coerceIn(0, 255)
            )
        } catch (e: Exception) {
            showToast(context, "Cannot change brightness: permission needed")
        }
    }

    fun setVolume(context: Context, streamType: Int, level: Int) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(streamType)
            audioManager.setStreamVolume(streamType, (level * maxVolume / 100).coerceIn(0, maxVolume), 0)
        } catch (e: Exception) {
            showToast(context, "Cannot change volume")
        }
    }

    fun toggleFlashlight(context: Context, on: Boolean) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, on)
        } catch (e: Exception) {
            showToast(context, "Flashlight not available")
        }
    }

    fun searchWeb(context: Context, query: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$query"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            showToast(context, "Cannot open browser")
        }
    }

    fun getBatteryLevel(context: Context): Int {
        val intent = context.registerReceiver(null, android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) (level * 100 / scale) else -1
    }

    fun getRamUsage(context: Context): String {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memoryInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalMem = memoryInfo.totalMem / (1024 * 1024 * 1024)
        val availMem = memoryInfo.availMem / (1024 * 1024 * 1024)
        val usedMem = totalMem - availMem
        return "${usedMem}GB / ${totalMem}GB"
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun cleanSpeechInput(input: String): String {
        return input
            .replace(Regex("""\s+"""), " ")
            .replace(Regex("""^(hey\s+)?nitha[,.]?\s*""", RegexOption.IGNORE_CASE), "")
            .trim()
    }

    fun detectIntent(input: String): String {
        val lower = input.lowercase()
        return when {
            lower.contains("open") || lower.contains("launch") || lower.contains("start") -> "app_control"
            lower.contains("settings") || lower.contains("brightness") || lower.contains("volume") || 
            lower.contains("flashlight") || lower.contains("wifi") || lower.contains("bluetooth") -> "device_control"
            lower.contains("file") || lower.contains("folder") || lower.contains("create") || 
            lower.contains("delete") || lower.contains("move") || lower.contains("copy") -> "file_manager"
            lower.contains("search") || lower.contains("find") || lower.contains("google") || 
            lower.contains("youtube") -> "web_search"
            lower.contains("read") && lower.contains("notification") -> "notification"
            lower.contains("screenshot") || lower.contains("camera") || lower.contains("photo") || 
            lower.contains("ocr") || lower.contains("scan") -> "vision"
            lower.contains("note") || lower.contains("remind") || lower.contains("todo") || 
            lower.contains("calendar") -> "productivity"
            lower.contains("weather") || lower.contains("translate") || lower.contains("news") -> "internet"
            else -> "chat"
        }
    }
}
