package com.nitha.memory

import android.content.Context
import com.nitha.models.ChatMessage
import com.nitha.utils.Constants
import kotlinx.coroutines.flow.Flow

/**
 * Chat History Manager for NITHA
 */
class ChatHistoryManager(context: Context) {
    private val database = NithaDatabase.getDatabase(context)
    private val chatMessageDao = database.chatMessageDao()

    val allMessages: Flow<List<ChatMessage>> = chatMessageDao.getAllMessages()

    suspend fun addMessage(role: String, content: String, isVoice: Boolean = false, voicePersona: String = "MIRA") {
        val message = ChatMessage(role = role, content = content, isVoice = isVoice, voicePersona = voicePersona)
        chatMessageDao.insertMessage(message)
    }

    suspend fun getRecentContext(limit: Int = Constants.MAX_CONTEXT_MESSAGES): List<ChatMessage> {
        return chatMessageDao.getRecentMessages(limit).reversed()
    }

    suspend fun clearHistory() {
        chatMessageDao.deleteAllMessages()
    }

    suspend fun getMessageCount(): Int {
        return chatMessageDao.getMessageCount()
    }

    suspend fun deleteOldMessages(days: Int = 30) {
        val cutoff = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        chatMessageDao.deleteOldMessages(cutoff)
    }
}
