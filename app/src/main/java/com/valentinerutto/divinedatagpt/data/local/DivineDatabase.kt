package com.valentinerutto.divinedatagpt.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.valentinerutto.divinedatagpt.data.local.dao.MemorySummaryDao
import com.valentinerutto.divinedatagpt.data.local.dao.MessageDao
import com.valentinerutto.divinedatagpt.data.local.dao.VerseDao
import com.valentinerutto.divinedatagpt.data.local.entity.MemorySummaryEntity
import com.valentinerutto.divinedatagpt.data.local.entity.MessageEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleNoteEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BookmarkEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity

@Database(
    entities = [Verse::class, MemorySummaryEntity::class, MessageEntity::class, BookmarkEntity::class, BibleNoteEntity::class, VerseEntity::class],
    version = 4,
    exportSchema = false
)
abstract class DivineDatabase : RoomDatabase() {
    abstract fun memorySummaryDao(): MemorySummaryDao
    abstract fun messageDao(): MessageDao
    abstract fun verseDao(): VerseDao

    companion object Companion {
        @Volatile
        private var INSTANCE: DivineDatabase? = null

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `bible_notes` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `verseId` INTEGER NOT NULL,
                        `translation` TEXT NOT NULL,
                        `bookName` TEXT NOT NULL,
                        `book` INTEGER NOT NULL,
                        `chapter` INTEGER NOT NULL,
                        `verse` INTEGER NOT NULL,
                        `verseText` TEXT NOT NULL,
                        `note` TEXT NOT NULL,
                        `highlightColor` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_bible_notes_verseId` ON `bible_notes` (`verseId`)")
            }
        }

        fun getDatabase(context: Context): DivineDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, DivineDatabase::class.java, "divine_database"
                ).addMigrations(MIGRATION_3_4)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
