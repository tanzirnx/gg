package com.nitha.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nitha.models.UserProfile
import com.nitha.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nitha_settings")

/**
 * Settings Repository - manages user preferences using DataStore
 */
class SettingsRepository(private val context: Context) {

    private val dataStore = context.dataStore

    // Keys
    private val API_KEY = stringPreferencesKey(Constants.PREFS_API_KEY)
    private val SELECTED_MODEL = stringPreferencesKey(Constants.PREFS_SELECTED_MODEL)
    private val VOICE_PERSONA = stringPreferencesKey(Constants.PREFS_VOICE_PERSONA)
    private val SPEECH_SPEED = floatPreferencesKey(Constants.PREFS_SPEECH_SPEED)
    private val SPEECH_PITCH = floatPreferencesKey(Constants.PREFS_SPEECH_PITCH)
    private val AUTO_SPEAK = booleanPreferencesKey(Constants.PREFS_AUTO_SPEAK)
    private val SHORT_MODE = booleanPreferencesKey(Constants.PREFS_SHORT_MODE)
    private val WAKE_WORD = stringPreferencesKey(Constants.PREFS_WAKE_WORD)
    private val WAKE_WORD_ENABLED = booleanPreferencesKey(Constants.PREFS_WAKE_WORD_ENABLED)
    private val THEME = stringPreferencesKey(Constants.PREFS_THEME)
    private val ACCENT_COLOR = stringPreferencesKey(Constants.PREFS_ACCENT_COLOR)
    private val USER_NAME = stringPreferencesKey(Constants.PREFS_USER_NAME)
    private val NICKNAME = stringPreferencesKey(Constants.PREFS_NICKNAME)
    private val LANGUAGE = stringPreferencesKey(Constants.PREFS_LANGUAGE)
    private val MEMORY_ENABLED = booleanPreferencesKey(Constants.PREFS_MEMORY_ENABLED)
    private val CONTEXT_MEMORY = booleanPreferencesKey(Constants.PREFS_CONTEXT_MEMORY)
    private val AUTO_LEARN = booleanPreferencesKey(Constants.PREFS_AUTO_LEARN)
    private val BUBBLE_ENABLED = booleanPreferencesKey(Constants.PREFS_BUBBLE_ENABLED)
    private val ASSISTANT_ENABLED = booleanPreferencesKey(Constants.PREFS_ASSISTANT_ENABLED)

    val userProfile: Flow<UserProfile> = dataStore.data.map { prefs ->
        UserProfile(
            apiKey = prefs[API_KEY] ?: "",
            selectedModel = prefs[SELECTED_MODEL] ?: Constants.MODEL_DEEPSEEK_CHAT,
            voicePersona = prefs[VOICE_PERSONA] ?: Constants.VOICE_MIRA,
            speechSpeed = prefs[SPEECH_SPEED] ?: 1.0f,
            speechPitch = prefs[SPEECH_PITCH] ?: 1.0f,
            autoSpeak = prefs[AUTO_SPEAK] ?: true,
            shortMode = prefs[SHORT_MODE] ?: false,
            wakeWord = prefs[WAKE_WORD] ?: "Hey Nitha",
            wakeWordEnabled = prefs[WAKE_WORD_ENABLED] ?: false,
            theme = prefs[THEME] ?: Constants.THEME_NITHA_DARK,
            accentColor = prefs[ACCENT_COLOR] ?: "#00E5FF",
            name = prefs[USER_NAME] ?: "User",
            nickname = prefs[NICKNAME] ?: "",
            preferredLanguage = prefs[LANGUAGE] ?: "en"
        )
    }

    suspend fun updateApiKey(key: String) {
        dataStore.edit { it[API_KEY] = key }
    }

    suspend fun updateModel(model: String) {
        dataStore.edit { it[SELECTED_MODEL] = model }
    }

    suspend fun updateVoicePersona(persona: String) {
        dataStore.edit { it[VOICE_PERSONA] = persona }
    }

    suspend fun updateSpeechSpeed(speed: Float) {
        dataStore.edit { it[SPEECH_SPEED] = speed }
    }

    suspend fun updateSpeechPitch(pitch: Float) {
        dataStore.edit { it[SPEECH_PITCH] = pitch }
    }

    suspend fun updateAutoSpeak(enabled: Boolean) {
        dataStore.edit { it[AUTO_SPEAK] = enabled }
    }

    suspend fun updateShortMode(enabled: Boolean) {
        dataStore.edit { it[SHORT_MODE] = enabled }
    }

    suspend fun updateTheme(theme: String) {
        dataStore.edit { it[THEME] = theme }
    }

    suspend fun updateAccentColor(color: String) {
        dataStore.edit { it[ACCENT_COLOR] = color }
    }

    suspend fun updateUserName(name: String) {
        dataStore.edit { it[USER_NAME] = name }
    }

    suspend fun updateNickname(nickname: String) {
        dataStore.edit { it[NICKNAME] = nickname }
    }

    suspend fun updateLanguage(language: String) {
        dataStore.edit { it[LANGUAGE] = language }
    }

    suspend fun updateMemoryEnabled(enabled: Boolean) {
        dataStore.edit { it[MEMORY_ENABLED] = enabled }
    }

    suspend fun updateContextMemory(enabled: Boolean) {
        dataStore.edit { it[CONTEXT_MEMORY] = enabled }
    }

    suspend fun updateAutoLearn(enabled: Boolean) {
        dataStore.edit { it[AUTO_LEARN] = enabled }
    }

    suspend fun updateBubbleEnabled(enabled: Boolean) {
        dataStore.edit { it[BUBBLE_ENABLED] = enabled }
    }

    suspend fun updateAssistantEnabled(enabled: Boolean) {
        dataStore.edit { it[ASSISTANT_ENABLED] = enabled }
    }

    suspend fun saveProfile(profile: UserProfile) {
        dataStore.edit { prefs ->
            prefs[API_KEY] = profile.apiKey
            prefs[SELECTED_MODEL] = profile.selectedModel
            prefs[VOICE_PERSONA] = profile.voicePersona
            prefs[SPEECH_SPEED] = profile.speechSpeed
            prefs[SPEECH_PITCH] = profile.speechPitch
            prefs[AUTO_SPEAK] = profile.autoSpeak
            prefs[SHORT_MODE] = profile.shortMode
            prefs[THEME] = profile.theme
            prefs[ACCENT_COLOR] = profile.accentColor
            prefs[USER_NAME] = profile.name
            prefs[NICKNAME] = profile.nickname
            prefs[LANGUAGE] = profile.preferredLanguage
        }
    }
}
