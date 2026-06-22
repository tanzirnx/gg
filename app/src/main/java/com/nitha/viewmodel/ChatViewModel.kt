package com.nitha.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nitha.ai.AIManager
import com.nitha.memory.ChatHistoryManager
import com.nitha.models.ChatMessage
import com.nitha.models.UserProfile
import com.nitha.repository.SettingsRepository
import com.nitha.voice.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Chat ViewModel - manages chat screen
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatHistory = ChatHistoryManager(application)
    private val aiManager = AIManager(application)
    private val settingsRepo = SettingsRepository(application)
    val textToSpeech = TextToSpeechManager(application)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())

    init {
        viewModelScope.launch {
            chatHistory.allMessages.collect { msgs ->
                _messages.value = msgs.reversed()
            }
        }

        viewModelScope.launch {
            settingsRepo.userProfile.collect { profile ->
                _userProfile.value = profile
                textToSpeech.configureVoice(profile.voicePersona, profile.speechSpeed, profile.speechPitch)
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true

            val profile = _userProfile.value

            if (profile.apiKey.isBlank()) {
                chatHistory.addMessage("assistant", "Please set your OpenRouter API key in settings first.")
                _isLoading.value = false
                return@launch
            }

            val response = aiManager.processInput(text, profile, isVoice = false)

            _isLoading.value = false
        }
    }

    fun speakMessage(text: String) {
        textToSpeech.speak(text)
    }

    fun clearChat() {
        viewModelScope.launch {
            chatHistory.clearHistory()
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech.shutdown()
    }
}
