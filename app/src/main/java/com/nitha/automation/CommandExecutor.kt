package com.nitha.automation

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.nitha.services.NithaAccessibilityService
import com.nitha.utils.Helpers
import com.nitha.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Command Executor - handles device control commands
 */
class CommandExecutor(private val context: Context) {

    /**
     * Execute a command based on intent detection
     */
    suspend fun execute(command: String): String = withContext(Dispatchers.Main) {
        val lower = command.lowercase()

        when {
            // App Control
            lower.contains("open youtube") || lower.contains("launch youtube") -> {
                if (Helpers.openApp(context, "com.google.android.youtube")) {
                    "Opening YouTube."
                } else {
                    Helpers.searchWeb(context, "youtube.com")
                    "Opening YouTube in browser."
                }
            }
            lower.contains("open settings") || lower.contains("launch settings") -> {
                Helpers.openSettings(context)
                "Opening settings."
            }
            lower.contains("open wifi") || lower.contains("wifi settings") -> {
                Helpers.openSettings(context, Settings.ACTION_WIFI_SETTINGS)
                "Opening Wi-Fi settings."
            }
            lower.contains("open bluetooth") || lower.contains("bluetooth settings") -> {
                Helpers.openSettings(context, Settings.ACTION_BLUETOOTH_SETTINGS)
                "Opening Bluetooth settings."
            }
            lower.contains("open display") || lower.contains("display settings") -> {
                Helpers.openSettings(context, Settings.ACTION_DISPLAY_SETTINGS)
                "Opening display settings."
            }

            // Navigation
            lower.contains("go back") || lower.contains("press back") -> {
                NithaAccessibilityService.performBack()
                "Going back."
            }
            lower.contains("go home") || lower.contains("press home") -> {
                NithaAccessibilityService.performHome()
                "Going home."
            }
            lower.contains("recent apps") || lower.contains("recent") -> {
                NithaAccessibilityService.performRecents()
                "Showing recent apps."
            }
            lower.contains("notifications") || lower.contains("open notification") -> {
                NithaAccessibilityService.performNotifications()
                "Opening notifications."
            }

            // Device Control
            lower.contains("flashlight on") || lower.contains("torch on") || 
            lower.contains("turn on flashlight") || lower.contains("turn on torch") -> {
                Helpers.toggleFlashlight(context, true)
                "Flashlight turned on."
            }
            lower.contains("flashlight off") || lower.contains("torch off") || 
            lower.contains("turn off flashlight") || lower.contains("turn off torch") -> {
                Helpers.toggleFlashlight(context, false)
                "Flashlight turned off."
            }
            lower.contains("brightness") -> {
                val level = extractNumber(lower)
                if (level != null) {
                    Helpers.setBrightness(context, (level * 255 / 100))
                    "Brightness set to $level%."
                } else {
                    Helpers.openSettings(context, Settings.ACTION_DISPLAY_SETTINGS)
                    "Opening brightness settings."
                }
            }
            lower.contains("volume") -> {
                val level = extractNumber(lower)
                if (level != null) {
                    Helpers.setVolume(context, android.media.AudioManager.STREAM_MUSIC, level)
                    "Volume set to $level%."
                } else {
                    "Please specify a volume level."
                }
            }

            // Web Search
            lower.contains("search") || lower.contains("google") -> {
                val query = command.replace(Regex("search for|search|google", RegexOption.IGNORE_CASE), "").trim()
                if (query.isNotBlank()) {
                    Helpers.searchWeb(context, query)
                    "Searching for: $query"
                } else {
                    "What would you like to search for?"
                }
            }
            lower.contains("open youtube and search") || lower.contains("youtube search") -> {
                val query = command.replace(Regex("open youtube and search for|youtube search for|youtube search", RegexOption.IGNORE_CASE), "").trim()
                if (query.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.youtube.com/results?search_query=$query"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    "Searching YouTube for: $query"
                } else {
                    "What would you like to search on YouTube?"
                }
            }

            // Time & Battery
            lower.contains("what time") || lower.contains("current time") -> {
                val time = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                    .format(java.util.Date())
                "It's $time."
            }
            lower.contains("battery") || lower.contains("how much battery") -> {
                val level = Helpers.getBatteryLevel(context)
                if (level >= 0) {
                    "Your battery is at $level%."
                } else {
                    "I can't read the battery level right now."
                }
            }

            // Default
            else -> {
                null
            }
        } ?: "I understand you want to '$command'. Let me help with that through the AI."
    }

    private fun extractNumber(text: String): Int? {
        val regex = Regex("""\d+""")
        return regex.find(text)?.value?.toIntOrNull()
    }
}
