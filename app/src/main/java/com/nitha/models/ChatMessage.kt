package com.nitha.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Chat message entity for Room database
 */
@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVoice: Boolean = false,
    val voicePersona: String = "MIRA" // LUNA, MIRA, NOVA
)

/**
 * AI response wrapper
 */
data class AIResponse(
    val content: String,
    val model: String,
    val tokensUsed: Int = 0,
    val success: Boolean = true,
    val errorMessage: String? = null
)

/**
 * User profile data
 */
data class UserProfile(
    val name: String = "User",
    val nickname: String = "",
    val preferredLanguage: String = "en",
    val apiKey: String = "",
    val selectedModel: String = "deepseek/deepseek-chat:free",
    val voicePersona: String = "MIRA",
    val speechSpeed: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val autoSpeak: Boolean = true,
    val shortMode: Boolean = false,
    val wakeWordEnabled: Boolean = false,
    val wakeWord: String = "Hey Nitha",
    val theme: String = "NITHA_DARK",
    val accentColor: String = "#00E5FF"
)

/**
 * Command data class
 */
data class Command(
    val id: String,
    val type: CommandType,
    val action: String,
    val params: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)

enum class CommandType {
    APP_CONTROL,
    DEVICE_CONTROL,
    FILE_MANAGER,
    WEB_SEARCH,
    VISION,
    AUTOMATION,
    CHAT,
    SYSTEM
}

/**
 * Memory entity for long-term memory
 */
@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val value: String,
    val category: String = "general",
    val timestamp: Long = System.currentTimeMillis(),
    val importance: Int = 1 // 1-5
)

/**
 * Settings entity
 */
@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey
    val key: String,
    val value: String
)

/**
 * Notification data
 */
data class NithaNotification(
    val id: String,
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long,
    val isImportant: Boolean = false
)
