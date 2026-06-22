package com.nitha.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.nitha.utils.Helpers

/**
 * NITHA Accessibility Service - enables device control
 */
class NithaAccessibilityService : AccessibilityService() {

    companion object {
        var instance: NithaAccessibilityService? = null

        fun isRunning(): Boolean = instance != null

        fun performBack() {
            instance?.performGlobalAction(GLOBAL_ACTION_BACK)
        }

        fun performHome() {
            instance?.performGlobalAction(GLOBAL_ACTION_HOME)
        }

        fun performRecents() {
            instance?.performGlobalAction(GLOBAL_ACTION_RECENTS)
        }

        fun performNotifications() {
            instance?.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
        }

        fun performPowerDialog() {
            instance?.performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
        }

        fun performQuickSettings() {
            instance?.performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Monitor accessibility events for smart features
        event?.let {
            when (it.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    // Track app changes for learning
                }
                else -> {}
            }
        }
    }

    override fun onInterrupt() {}

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }
}
