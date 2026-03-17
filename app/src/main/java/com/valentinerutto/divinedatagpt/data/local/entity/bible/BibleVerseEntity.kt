package com.valentinerutto.divinedatagpt.data.local.entity.bible

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bible_verses", indices = [
        Index(value = ["book", "chapter", "verse"]),
        Index(value = ["book"]),
        Index(value = ["bookAbbrev"])]
)
data class BibleVerseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,              // "Genesis"
    val book: String,        // "genesis"
    val bookAbbrev: String,              // 1, 2, 3...
    val chapter: Int,                // 1, 2, 3...
    val verse: Int,              // The actual verse text
    val text: String,    // Section within chapter
    val sectionNumber: Int = 1,            // 1-66 for sorting
    val bookOrder: Int,
    val testament: String
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val book: String,
    val chapter: Int,
    val verse: Int,
    val note: String? = null,
    val color: String = "purple",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val book: String,
    val chapter: Int,
    val readAt: Long = System.currentTimeMillis()
)

/**
 * Room entity representing a single Bible verse.
 *
 * Indexes are placed on the most queried columns to keep lookups fast
 * even when the full ~31,000-verse table is loaded.
 */
@Entity(
    tableName = "bible_book",
    indices = [
        Index(value = ["book_id"]),
        Index(value = ["book_id", "chapter"]),
        Index(value = ["book_id", "chapter", "verse"], unique = true)
    ]
)
data class BibleVerseEntity2(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "book_id")
    val bookId: Int? = null,

    @ColumnInfo(name = "book_name")
    val bookName: String? = null,

    @ColumnInfo(name = "chapter")
    val chapter: Int,

    @ColumnInfo(name = "verse")
    val verse: Int,

    // ── Translations ──────────────────────────────────────────────────────────

    @ColumnInfo(name = "web")
    val worldEnglishBibleWeb: String? = null,

    @ColumnInfo(name = "kjv")
    val kingJamesBibleKjv: String? = null,

    @ColumnInfo(name = "leningrad_codex")
    val leningradCodex: String? = null,

    @ColumnInfo(name = "jps")
    val jewishPublicationSocietyJps: String? = null,

    @ColumnInfo(name = "codex_alexandrinus")
    val codexAlexandrinus: String? = null,

    @ColumnInfo(name = "brenton")
    val brenton: String? = null,

    @ColumnInfo(name = "samaritan_pentateuch")
    val samaritanPentateuch: String? = null,

    @ColumnInfo(name = "samaritan_pentateuch_english")
    val samaritanPentateuchEnglish: String? = null,

    @ColumnInfo(name = "onkelos_aramaic")
    val onkelosAramaic: String? = null,

    @ColumnInfo(name = "onkelos_english")
    val onkelosEnglish: String? = null
)