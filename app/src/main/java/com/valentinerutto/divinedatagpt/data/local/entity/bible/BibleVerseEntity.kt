package com.valentinerutto.divinedatagpt.data.local.entity.bible

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


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


@Entity(
    tableName = "verses",
    indices = [
        Index(value = ["translation", "book", "chapter", "verse"], unique = true)
    ]
)
data class VerseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val translation: String,
    val bookName: String,
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)

fun VerseJson.toEntity(translation: String): VerseEntity {
    return VerseEntity(
        translation = translation,
        bookName = bookName,
        book = book,
        chapter = chapter,
        verse = verse,
        text = text
    )
}

