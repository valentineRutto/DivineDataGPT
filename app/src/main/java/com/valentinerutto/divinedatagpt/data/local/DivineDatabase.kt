package com.valentinerutto.divinedatagpt.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.valentinerutto.divinedatagpt.data.local.dao.MemorySummaryDao
import com.valentinerutto.divinedatagpt.data.local.dao.MessageDao
import com.valentinerutto.divinedatagpt.data.local.entity.MemorySummaryEntity
import com.valentinerutto.divinedatagpt.data.local.entity.MessageEntity

@Database(
    entities = [Verse::class, MemorySummaryEntity::class, MessageEntity::class],
    version = 2,
    exportSchema = false
)
abstract class DivineDatabase : RoomDatabase() {
    abstract fun memorySummaryDao(): MemorySummaryDao
    abstract fun messageDao(): MessageDao
    companion object Companion {
        @Volatile
        private var INSTANCE: DivineDatabase? = null
        fun getDatabase(context: Context): DivineDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, DivineDatabase::class.java, "divine_database"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}