package com.nitha.memory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nitha.models.ChatMessage
import com.nitha.models.Memory

/**
 * Room Database for NITHA memory and chat history
 */
@Database(
    entities = [ChatMessage::class, Memory::class],
    version = 1,
    exportSchema = false
)
abstract class NithaDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun memoryDao(): MemoryDao

    companion object {
        @Volatile
        private var INSTANCE: NithaDatabase? = null

        fun getDatabase(context: Context): NithaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NithaDatabase::class.java,
                    "nitha_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
