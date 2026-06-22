package com.nitha.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nitha.ai.AIManager
import com.nitha.models.UserProfile
import com.nitha.repository.SettingsRepository
import com.nitha.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Settings ViewModel
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)
    private val aiManager = AIManager(application)

    val userProfile: kotlinx.coroutines.flow.Flow<UserProfile> = settingsRepo.userProfile

    private val _apiKeyValid = MutableStateFlow<Boolean?>(null)
    val apiKeyValid: StateFlow<Boolean?> = _apiKeyValid.asStateFlow()

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    fun updateApiKey(key: String) {
        viewModelScope.launch {
            settingsRepo.updateApiKey(key)
        }
    }

    fun updateModel(model: String) {
        viewModelScope.launch {
            settingsRepo.updateModel(model)
        }
    }

    fun updateVoicePersona(persona: String) {
        viewModelScope.launch {
            settingsRepo.updateVoicePersona(persona)
        }
    }

    fun updateSpeechSpeed(speed: Float) {
        viewModelScope.launch {
            settingsRepo.updateSpeechSpeed(speed)
        }
    }

    fun updateSpeechPitch(pitch: Float) {
        viewModelScope.launch {
            settingsRepo.updateSpeechPitch(pitch)
        }
    }

    fun updateAutoSpeak(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateAutoSpeak(enabled)
        }
    }

    fun updateShortMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.updateShortMode(enabled)
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            settingsRepo.updateTheme(theme)
        }
    }

    fun updateAccentColor(color: String) {
        viewModelScope.launch {
            settingsRepo.updateAccentColor(color)
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            settingsRepo.updateUserName(name)
        }
    }

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            settingsRepo.updateNickname(nickname)
        }
    }

    fun checkApiKey(key: String) {
        viewModelScope.launch {
            _isChecking.value = true
            _apiKeyValid.value = aiManager.checkApiKey(key)
            _isChecking.value = false
        }
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            settingsRepo.saveProfile(profile)
        }
    }
}
