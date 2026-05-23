package com.valentinerutto.divinedatagpt.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.valentinerutto.divinedatagpt.data.local.dao.MemorySummaryDao
import com.valentinerutto.divinedatagpt.data.local.dao.MessageDao
import com.valentinerutto.divinedatagpt.data.local.dao.ReadingPlanDao
import com.valentinerutto.divinedatagpt.data.local.dao.VerseDao
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
        MemorySummaryEntity::class,
        MessageEntity::class,
        BookmarkEntity::class,
        BibleNoteEntity::class,
        VersesFTS::class,
        ReadingPlanEntity::class,
        ReadingPlanDayEntity::class,
        ReadingPlanCompletionEntity::class
    ],
    exportSchema = false
)
abstract class DivineDatabase : RoomDatabase() {
    abstract fun memorySummaryDao(): MemorySummaryDao
    abstract fun messageDao(): MessageDao
    abstract fun verseDao(): VerseDao
    abstract fun readingPlanDao(): ReadingPlanDao

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

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `messages` ADD COLUMN `verse` TEXT")
                db.execSQL("ALTER TABLE `messages` ADD COLUMN `reference` TEXT")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `reading_plans` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `templateId` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `subtitle` TEXT NOT NULL,
                        `durationDays` INTEGER NOT NULL,
                        `category` TEXT NOT NULL,
                        `accentColor` TEXT NOT NULL,
                        `startEpochDay` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `reading_plan_days` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `planId` INTEGER NOT NULL,
                        `dayNumber` INTEGER NOT NULL,
                        `title` TEXT NOT NULL,
                        `bookName` TEXT NOT NULL,
                        `book` INTEGER NOT NULL,
                        `chapter` INTEGER NOT NULL,
                        `focus` TEXT NOT NULL,
                        FOREIGN KEY(`planId`) REFERENCES `reading_plans`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_reading_plan_days_planId` ON `reading_plan_days` (`planId`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_reading_plan_days_planId_dayNumber` ON `reading_plan_days` (`planId`, `dayNumber`)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `reading_plan_completions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `planDayId` INTEGER NOT NULL,
                        `completedEpochDay` INTEGER NOT NULL,
                        `completedAt` INTEGER NOT NULL,
                        FOREIGN KEY(`planDayId`) REFERENCES `reading_plan_days`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_reading_plan_completions_planDayId` ON `reading_plan_completions` (`planDayId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_reading_plan_completions_completedEpochDay` ON `reading_plan_completions` (`completedEpochDay`)")
            }
        }

        fun getDatabase(context: Context): DivineDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, DivineDatabase::class.java, "divine_database"
                ).addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration(false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
