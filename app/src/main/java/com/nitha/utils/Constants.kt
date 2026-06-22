package com.nitha.utils

/**
 * NITHA Constants
 */
object Constants {
    // OpenRouter API
    const val OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1/"
    const val OPENROUTER_CHAT_ENDPOINT = "chat/completions"

    // Free Models (with fallback chain)
    const val MODEL_DEEPSEEK_CHAT = "deepseek/deepseek-chat:free"
    const val MODEL_DEEPSEEK_R1 = "deepseek/deepseek-r1:free"
    const val MODEL_QWEN3 = "qwen/qwen3-235b-a22b:free"
    const val MODEL_LLAMA4_MAVERICK = "meta-llama/llama-4-maverick:free"
    const val MODEL_LLAMA4_SCOUT = "meta-llama/llama-4-scout:free"
    const val MODEL_GEMMA3 = "google/gemma-3-27b-it:free"
    const val MODEL_MISTRAL_SMALL = "mistralai/mistral-small-3.1-24b-instruct:free"
    const val MODEL_OPENROUTER_FREE = "openrouter/free"

    val FALLBACK_MODELS = listOf(
        MODEL_DEEPSEEK_CHAT,
        MODEL_LLAMA4_SCOUT,
        MODEL_QWEN3,
        MODEL_GEMMA3,
        MODEL_OPENROUTER_FREE
    )

    // Voice Personas
    const val VOICE_LUNA = "LUNA"
    const val VOICE_MIRA = "MIRA"
    const val VOICE_NOVA = "NOVA"

    // Themes
    const val THEME_NITHA_DARK = "NITHA_DARK"
    const val THEME_CYBER_PURPLE = "CYBER_PURPLE"
    const val THEME_MATRIX_GREEN = "MATRIX_GREEN"
    const val THEME_IRON_HUD = "IRON_HUD"

    // Preferences Keys
    const val PREFS_API_KEY = "api_key"
    const val PREFS_SELECTED_MODEL = "selected_model"
    const val PREFS_VOICE_PERSONA = "voice_persona"
    const val PREFS_SPEECH_SPEED = "speech_speed"
    const val PREFS_SPEECH_PITCH = "speech_pitch"
    const val PREFS_AUTO_SPEAK = "auto_speak"
    const val PREFS_SHORT_MODE = "short_mode"
    const val PREFS_WAKE_WORD = "wake_word"
    const val PREFS_WAKE_WORD_ENABLED = "wake_word_enabled"
    const val PREFS_THEME = "theme"
    const val PREFS_ACCENT_COLOR = "accent_color"
    const val PREFS_USER_NAME = "user_name"
    const val PREFS_NICKNAME = "nickname"
    const val PREFS_LANGUAGE = "language"
    const val PREFS_MEMORY_ENABLED = "memory_enabled"
    const val PREFS_CONTEXT_MEMORY = "context_memory"
    const val PREFS_AUTO_LEARN = "auto_learn"
    const val PREFS_BUBBLE_ENABLED = "bubble_enabled"
    const val PREFS_ASSISTANT_ENABLED = "assistant_enabled"

    // Service Actions
    const val ACTION_START_SERVICE = "com.nitha.action.START_SERVICE"
    const val ACTION_STOP_SERVICE = "com.nitha.action.STOP_SERVICE"
    const val ACTION_VOICE_COMMAND = "com.nitha.action.VOICE_COMMAND"

    // Notification Channels
    const val CHANNEL_ID_FOREGROUND = "nitha_foreground"
    const val CHANNEL_ID_NOTIFICATIONS = "nitha_notifications"

    // Request Codes
    const val REQ_CODE_OVERLAY = 1001
    const val REQ_CODE_ACCESSIBILITY = 1002
    const val REQ_CODE_NOTIFICATION = 1003
    const val REQ_CODE_RECORD_AUDIO = 1004
    const val REQ_CODE_STORAGE = 1005

    // Max context messages
    const val MAX_CONTEXT_MESSAGES = 20

    // Max tokens
    const val MAX_TOKENS = 2048
    const val MAX_TOKENS_SHORT = 512
}
