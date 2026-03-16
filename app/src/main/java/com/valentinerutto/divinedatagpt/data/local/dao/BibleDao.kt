package com.valentinerutto.divinedatagpt.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleBookEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseEntity2
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(verses: List<BibleVerseEntity2>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BibleBookEntity>)

    /**
     * Insert a single book
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BibleBookEntity)

    /**
     * Get all books ordered by book order (Genesis to Revelation)
     */
    @Query("SELECT * FROM bible_book ORDER BY book_id ASC")
    fun getAllBooks(): Flow<List<BibleVerseEntity2>>

    @Query("SELECT * FROM bible_books WHERE testament = :testament ORDER BY bookOrder ASC")
    fun getBooksByTestament(testament: String): Flow<List<BibleBookEntity>>

    /**
     * Get a single book by name
     */
    @Query("SELECT * FROM bible_books WHERE bookName = :bookName LIMIT 1")
    suspend fun getBookByName(bookName: String): BibleBookEntity?

    /**
     * Get a single book by abbreviation
     */
    @Query("SELECT * FROM bible_books WHERE abbreviation = :abbreviation LIMIT 1")
    suspend fun getBookByAbbreviation(abbreviation: String): BibleBookEntity?

    /**
     * Get a book by order number
     */
    @Query("SELECT * FROM bible_book WHERE book_id = :order LIMIT 1")
    suspend fun getBookByOrder(order: Int): BibleBookEntity?

    @Query("SELECT * FROM bible_verses WHERE book = :book AND chapter = :chapter")
    suspend fun getChapter(book: String, chapter: Int): List<BibleVerseEntity2>

    @Query(
        """
        SELECT * FROM bible_verses 
        WHERE book = :book AND chapter = :chapter 
        ORDER BY verse ASC
    """
    )
    fun getChapterVerses(book: String, chapter: Int): Flow<List<BibleVerseEntity>>

    @Query("SELECT * FROM bible_verses WHERE verse = :reference")
    suspend fun getVerseByReference(reference: String): BibleVerseEntity2?

    @Query("SELECT COUNT(*) FROM bible_verses")
    suspend fun count(): Int

    @Query(
        """
        SELECT * FROM bible_verses 
        WHERE book = :book AND chapter = :chapter AND verse = :verse 
        LIMIT 1
    """
    )
    suspend fun getSingleVerse(book: String, chapter: Int, verse: Int): BibleVerseEntity?

    /**
     * Get a verse by ID
     */
    @Query("SELECT * FROM bible_verses WHERE id = :verseId LIMIT 1")
    suspend fun getVerseById(verseId: Long): BibleVerseEntity?

    @Query(
        """
        SELECT * FROM bible_verses 
        WHERE text LIKE '%' || :query || '%' 
        ORDER BY bookOrder ASC, chapter ASC, verse ASC
        LIMIT :limit
    """
    )
    fun searchVerses(query: String, limit: Int = 50): Flow<List<BibleVerseEntity>>

    /**
     * Search verses in a specific book
     */
    @Query(
        """
        SELECT * FROM bible_verses 
        WHERE book = :book AND text LIKE '%' || :query || '%' 
        ORDER BY chapter ASC, verse ASC
        LIMIT :limit
    """
    )
    fun searchVersesInBook(
        book: String,
        query: String,
        limit: Int = 50
    ): Flow<List<BibleVerseEntity>>

    /**
     * Get total verse count in database
     */
    @Query("SELECT COUNT(*) FROM bible_verses")
    suspend fun getTotalVerseCount(): Int

    @Query("DELETE FROM bible_verses")
    suspend fun deleteAllVerses()

}


data class VerseWithBookmarkInfo(
    @Embedded val verse: BibleVerseEntity,
    val bookmarkColor: String? = null,
    val bookmarkNote: String? = null
)

/**
 * Reading statistics for a book
 */
data class ReadingStatistics(
    val bookName: String,
    val totalChapters: Int,
    val chaptersRead: Int,
    val lastReadAt: Long?
) {
    val progressPercentage: Float
        get() = if (totalChapters > 0) {
            (chaptersRead.toFloat() / totalChapters) * 100
        } else 0f
}