package com.valentinerutto.divinedatagpt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseEntity

@Dao
interface BibleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(verses: List<BibleVerseEntity>)

    @Query("SELECT * FROM bible_verses WHERE book = :book AND chapter = :chapter")
    suspend fun getChapter(book: String, chapter: Int): List<BibleVerseEntity>


}