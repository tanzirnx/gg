package com.nitha.memory

import android.content.Context
import com.nitha.models.Memory
import kotlinx.coroutines.flow.Flow

/**
 * Memory Manager for NITHA - handles long-term memory operations
 */
class MemoryManager(context: Context) {
    private val database = NithaDatabase.getDatabase(context)
    private val memoryDao = database.memoryDao()

    val allMemories: Flow<List<Memory>> = memoryDao.getAllMemories()

    suspend fun saveMemory(key: String, value: String, category: String = "general", importance: Int = 1) {
        val memory = Memory(key = key, value = value, category = category, importance = importance)
        memoryDao.insertMemory(memory)
    }

    suspend fun getMemory(key: String): String? {
        return memoryDao.getMemoryByKey(key)?.value
    }

    suspend fun searchMemories(query: String): List<Memory> {
        return memoryDao.searchMemories(query)
    }

    suspend fun getMemoriesByCategory(category: String): List<Memory> {
        return memoryDao.getMemoriesByCategory(category)
    }

    suspend fun deleteMemory(key: String) {
        memoryDao.deleteMemoryByKey(key)
    }

    suspend fun clearAllMemories() {
        memoryDao.deleteAllMemories()
    }

    suspend fun getMemoryCount(): Int {
        return memoryDao.getMemoryCount()
    }

    suspend fun saveUserPreference(key: String, value: String) {
        saveMemory(key, value, "preference", importance = 3)
    }

    suspend fun saveLearnedCommand(command: String, action: String) {
        saveMemory("cmd_$command", action, "learned_command", importance = 2)
    }

    suspend fun getRelevantContext(query: String): String {
        val memories = searchMemories(query)
        return if (memories.isNotEmpty()) {
            memories.take(5).joinToString("\n") { "${it.key}: ${it.value}" }
        } else {
            ""
        }
    }
}
