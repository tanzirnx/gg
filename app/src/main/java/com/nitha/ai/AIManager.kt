package com.nitha.ai

import android.content.Context
import com.nitha.models.ChatMessage
import com.nitha.models.UserProfile
import com.nitha.memory.ChatHistoryManager
import com.nitha.memory.MemoryManager
import com.nitha.utils.Constants
import com.nitha.utils.Helpers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * AI Manager - central brain of NITHA
 */
class AIManager(private val context: Context) {

    private val client = OpenRouterClient(context)
    private val chatHistory = ChatHistoryManager(context)
    private val memoryManager = MemoryManager(context)

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _currentModel = MutableStateFlow(Constants.MODEL_DEEPSEEK_CHAT)
    val currentModel: StateFlow<String> = _currentModel.asStateFlow()

    private val _apiStatus = MutableStateFlow(false)
    val apiStatus: StateFlow<Boolean> = _apiStatus.asStateFlow()

    private val _todayRequests = MutableStateFlow(0)
    val todayRequests: StateFlow<Int> = _todayRequests.asStateFlow()

    /**
     * Process user input and get AI response
     */
    suspend fun processInput(
        input: String,
        profile: UserProfile,
        isVoice: Boolean = false
    ): String {
        if (input.isBlank()) return "I didn't hear anything. Can you repeat?"

        _isProcessing.value = true

        try {
            // Save user message
            chatHistory.addMessage("user", input, isVoice, profile.voicePersona)

            // Check internet
            if (!Helpers.isOnline(context)) {
                return "I'm offline right now. Please check your internet connection."
            }

            // Get context from memory
            val memoryContext = memoryManager.getRelevantContext(input)

            // Build messages
            val messages = buildMessageList(input, profile, memoryContext)

            // Send to AI
            val result = client.sendMessage(
                apiKey = profile.apiKey,
                messages = messages,
                model = profile.selectedModel,
                maxTokens = if (profile.shortMode) Constants.MAX_TOKENS_SHORT else Constants.MAX_TOKENS,
                temperature = 0.7
            )

            return when (result) {
                is AIResult.Success -> {
                    _currentModel.value = result.model
                    _todayRequests.value += 1
                    _apiStatus.value = true

                    // Save assistant response
                    chatHistory.addMessage("assistant", result.content, isVoice, profile.voicePersona)

                    // Auto-learn if enabled
                    if (profile.name.isNotEmpty()) {
                        memoryManager.saveUserPreference("user_name", profile.name)
                    }

                    result.content
                }
                is AIResult.Error -> {
                    _apiStatus.value = false
                    "Sorry, I encountered an issue: ${result.message}"
                }
            }
        } catch (e: Exception) {
            return "Something went wrong. Please try again."
        } finally {
            _isProcessing.value = false
        }
    }

    /**
     * Build message list with system prompt and context
     */
    private suspend fun buildMessageList(
        input: String,
        profile: UserProfile,
        memoryContext: String
    ): List<Message> {
        val messages = mutableListOf<Message>()

        // System prompt
        val systemPrompt = PromptManager.getSystemPrompt(profile)
        messages.add(Message("system", systemPrompt))

        // Memory context
        if (memoryContext.isNotBlank()) {
            messages.add(Message("system", "User memory context:\n$memoryContext"))
        }

        // Recent chat history
        val recentMessages = chatHistory.getRecentContext(10)
        for (msg in recentMessages) {
            messages.add(Message(msg.role, msg.content))
        }

        // Current input
        messages.add(Message("user", input))

        return messages
    }

    /**
     * Quick command processing without AI
     */
    fun processQuickCommand(command: String): String? {
        val lower = command.lowercase()
        return when {
            lower.contains("open youtube") -> "Opening YouTube..."
            lower.contains("open settings") -> "Opening settings..."
            lower.contains("turn on flashlight") || lower.contains("flashlight on") -> "Turning on flashlight..."
            lower.contains("turn off flashlight") || lower.contains("flashlight off") -> "Turning off flashlight..."
            lower.contains("go back") -> "Going back..."
            lower.contains("go home") -> "Going home..."
            lower.contains("recent apps") -> "Opening recent apps..."
            lower.contains("what time") -> {
                val time = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                    .format(java.util.Date())
                "It's $time."
            }
            else -> null
        }
    }

    suspend fun checkApiKey(apiKey: String): Boolean {
        return client.checkConnection(apiKey)
    }

    suspend fun clearHistory() {
        chatHistory.clearHistory()
    }

    suspend fun getMessageCount(): Int {
        return chatHistory.getMessageCount()
    }
}
