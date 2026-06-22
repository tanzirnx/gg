package com.nitha.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nitha.ai.AIManager
import com.nitha.automation.CommandExecutor
import com.nitha.models.UserProfile
import com.nitha.repository.SettingsRepository
import com.nitha.services.NithaAccessibilityService
import com.nitha.services.NithaForegroundService
import com.nitha.services.NithaNotificationListener
import com.nitha.utils.Constants
import com.nitha.utils.Helpers
import com.nitha.voice.SpeechRecognizerManager
import com.nitha.voice.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * Home ViewModel - manages HomeScreen state and interactions
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)
    private val aiManager = AIManager(application)
    private val commandExecutor = CommandExecutor(application)

    val speechRecognizer = SpeechRecognizerManager(application)
    val textToSpeech = TextToSpeechManager(application)

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _aiStatus = MutableStateFlow("Idle")
    val aiStatus: StateFlow<String> = _aiStatus.asStateFlow()

    private val _lastResponse = MutableStateFlow("")
    val lastResponse: StateFlow<String> = _lastResponse.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _ramUsage = MutableStateFlow("")
    val ramUsage: StateFlow<String> = _ramUsage.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _apiStatus = MutableStateFlow(false)
    val apiStatus: StateFlow<Boolean> = _apiStatus.asStateFlow()

    private val _todayRequests = MutableStateFlow(0)
    val todayRequests: StateFlow<Int> = _todayRequests.asStateFlow()

    private val _accessibilityEnabled = MutableStateFlow(false)
    val accessibilityEnabled: StateFlow<Boolean> = _accessibilityEnabled.asStateFlow()

    private val _notificationEnabled = MutableStateFlow(false)
    val notificationEnabled: StateFlow<Boolean> = _notificationEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepo.userProfile.collect { profile ->
                _userProfile.value = profile
                textToSpeech.configureVoice(profile.voicePersona, profile.speechSpeed, profile.speechPitch)
            }
        }

        viewModelScope.launch {
            aiManager.isProcessing.collect { processing ->
                _aiStatus.value = if (processing) "Thinking..." else "Idle"
            }
        }

        viewModelScope.launch {
            aiManager.apiStatus.collect { status ->
                _apiStatus.value = status
            }
        }

        viewModelScope.launch {
            aiManager.todayRequests.collect { count ->
                _todayRequests.value = count
            }
        }

        viewModelScope.launch {
            speechRecognizer.isListening.collect { listening ->
                _isListening.value = listening
                if (listening) _aiStatus.value = "Listening..."
            }
        }

        viewModelScope.launch {
            textToSpeech.isSpeaking.collect { speaking ->
                _isSpeaking.value = speaking
                if (speaking) _aiStatus.value = "Speaking..."
            }
        }

        // Update device stats periodically
        viewModelScope.launch {
            while (true) {
                _batteryLevel.value = Helpers.getBatteryLevel(application)
                _ramUsage.value = Helpers.getRamUsage(application)
                _isOnline.value = Helpers.isOnline(application)
                _accessibilityEnabled.value = NithaAccessibilityService.isRunning()
                _notificationEnabled.value = NithaNotificationListener.isRunning
                kotlinx.coroutines.delay(5000)
            }
        }
    }

    fun startVoiceInput() {
        speechRecognizer.startListening(language = "en-US") { text ->
            viewModelScope.launch {
                processInput(text, isVoice = true)
            }
        }
    }

    fun stopVoiceInput() {
        speechRecognizer.stopListening()
    }

    fun processTextInput(text: String) {
        viewModelScope.launch {
            processInput(text, isVoice = false)
        }
    }

    private suspend fun processInput(input: String, isVoice: Boolean) {
        val profile = _userProfile.value

        if (profile.apiKey.isBlank()) {
            _lastResponse.value = "Please set your OpenRouter API key in settings first."
            return
        }

        // Try quick command first
        val quickResponse = commandExecutor.execute(input)
        if (!quickResponse.contains("AI")) {
            _lastResponse.value = quickResponse
            if (profile.autoSpeak && isVoice) {
                textToSpeech.speak(quickResponse)
            }
            return
        }

        // Process with AI
        _aiStatus.value = "Thinking..."
        val response = aiManager.processInput(input, profile, isVoice)
        _lastResponse.value = response

        if (profile.autoSpeak && isVoice) {
            textToSpeech.speak(response)
        }
    }

    fun speakText(text: String) {
        textToSpeech.speak(text)
    }

    fun stopSpeaking() {
        textToSpeech.stop()
    }

    fun startForegroundService() {
        NithaForegroundService.startService(getApplication())
    }

    fun stopForegroundService() {
        NithaForegroundService.stopService(getApplication())
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer.destroy()
        textToSpeech.shutdown()
    }
}
