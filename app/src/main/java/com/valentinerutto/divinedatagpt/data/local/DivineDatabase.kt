package com.valentinerutto.divinedatagpt.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.valentinerutto.divinedatagpt.data.local.dao.JournalDao
import com.valentinerutto.divinedatagpt.data.local.dao.MemorySummaryDao
import com.valentinerutto.divinedatagpt.data.local.dao.MessageDao
import com.valentinerutto.divinedatagpt.data.local.dao.ReadingPlanDao
import com.valentinerutto.divinedatagpt.data.local.dao.VerseDao
import com.valentinerutto.divinedatagpt.data.local.entity.JournalEntryEntity
import com.valentinerutto.divinedatagpt.data.local.entity.MemorySummaryEntity
import com.valentinerutto.divinedatagpt.data.local.entity.MessageEntity
import com.valentinerutto.divinedatagpt.data.local.entity.VersesFTS
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleNoteEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BookmarkEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanCompletionEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanDayEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.ReadingPlanEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity

@Database(
    version = 6,
    entities = [
        VerseEntity::class,
        Verse::class,
        VersesFTS::class,
        MemorySummaryEntity::class,
        MessageEntity::class,
        BookmarkEntity::class,
        BibleNoteEntity::class,
        ReadingPlanEntity::class,
        ReadingPlanDayEntity::class,
        ReadingPlanCompletionEntity::class,
        JournalEntryEntity::class
    ],
    exportSchema = false
)
abstract class DivineDatabase : RoomDatabase() {
    abstract fun memorySummaryDao(): MemorySummaryDao
    abstract fun messageDao(): MessageDao
    abstract fun verseDao(): VerseDao
    abstract fun readingPlanDao(): ReadingPlanDao
    abstract fun journalDao(): JournalDao


    companion object Companion {
        @Volatile
        private var INSTANCE: DivineDatabase? = null


        fun getDatabase(context: Context): DivineDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, DivineDatabase::class.java, "divine_database"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
