package com.valentinerutto.divinedatagpt.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Verse::class], version = 1, exportSchema = false)
abstract class DivineDatabase : RoomDatabase() {
    companion object Companion {
        @Volatile
        private var INSTANCE: DivineDatabase? = null
        fun getDatabase(context: Context): DivineDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, DivineDatabase::class.java, "divine_database"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}