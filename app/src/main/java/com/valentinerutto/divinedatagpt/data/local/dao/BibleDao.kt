package com.valentinerutto.divinedatagpt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseEntity2

@Dao
interface BibleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(verses: List<BibleVerseEntity2>)

    @Query("SELECT * FROM bible_verses WHERE book = :book AND chapter = :chapter")
    suspend fun getChapter(book: String, chapter: Int): List<BibleVerseEntity2>


    @Query("SELECT COUNT(*) FROM bible_verses")
    suspend fun count(): Int

}