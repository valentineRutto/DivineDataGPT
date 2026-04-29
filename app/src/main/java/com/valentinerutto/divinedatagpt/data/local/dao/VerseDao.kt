package com.valentinerutto.divinedatagpt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity


@Dao
interface VerseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(verses: List<VerseEntity>)

    @Query(
        """
        SELECT * FROM verses
        WHERE translation = :translation
        AND book = :book
        AND chapter = :chapter
        ORDER BY verse ASC
    """
    )
    suspend fun getChapter(
        translation: String,
        book: Int,
        chapter: Int
    ): List<VerseEntity>
}
