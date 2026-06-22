package com.nitha.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nitha.memory.MemoryManager
import com.nitha.repository.SettingsRepository
import com.nitha.utils.Helpers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Dashboard ViewModel
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val memoryManager = MemoryManager(application)
    private val settingsRepo = SettingsRepository(application)

    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    private val _ramUsage = MutableStateFlow("")
    val ramUsage: StateFlow<String> = _ramUsage.asStateFlow()

    private val _storageInfo = MutableStateFlow("")
    val storageInfo: StateFlow<String> = _storageInfo.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _memoryCount = MutableStateFlow(0)
    val memoryCount: StateFlow<Int> = _memoryCount.asStateFlow()

    private val _apiStatus = MutableStateFlow(false)
    val apiStatus: StateFlow<Boolean> = _apiStatus.asStateFlow()

    private val _selectedModel = MutableStateFlow("deepseek/deepseek-chat:free")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepo.userProfile.collect { profile ->
                _selectedModel.value = profile.selectedModel
            }
        }

        viewModelScope.launch {
            while (true) {
                _batteryLevel.value = Helpers.getBatteryLevel(application)
                _ramUsage.value = Helpers.getRamUsage(application)
                _isOnline.value = Helpers.isOnline(application)
                _memoryCount.value = memoryManager.getMemoryCount()

                val statFs = android.os.StatFs(android.os.Environment.getDataDirectory().path)
                val total = statFs.totalBytes / (1024 * 1024 * 1024)
                val free = statFs.availableBytes / (1024 * 1024 * 1024)
                _storageInfo.value = "${free}GB free / ${total}GB"

                kotlinx.coroutines.delay(5000)
            }
        }
    }
}
