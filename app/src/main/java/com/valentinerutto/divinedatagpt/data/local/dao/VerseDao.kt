package com.valentinerutto.divinedatagpt.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface VerseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(verses: List<VerseEntity>)

    @Query("SELECT COUNT(*) FROM verses")
    suspend fun count(): Int


    @Query(
        """
        SELECT *
        FROM verses
        WHERE translation = :translation
          AND book = :book
          AND chapter = :chapter
        ORDER BY verse ASC
        """
    )
    fun getChapter(
        translation: String,
        book: Int,
        chapter: Int
    ): Flow<List<VerseEntity>>

    @Query(
        """
        SELECT *
        FROM verses
        WHERE translation = :translation
          AND (
            bookName LIKE '%' || :query || '%'
            OR text LIKE '%' || :query || '%'
          )
        ORDER BY book ASC, chapter ASC, verse ASC
        LIMIT :limit
        """
    )
    fun searchVerses(
        translation: String,
        query: String,
        limit: Int = 80
    ): Flow<List<VerseEntity>>

}
