package com.valentinerutto.divinedatagpt.data.models

import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleBookEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseEntity


data class BibleVerse(
    val id: Long,
    val book: String,
    val bookAbbrev: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val isHighlighted: Boolean = false,
    val highlightColor: String? = null
)

data class BibleBook(
    val name: String,
    val abbreviation: String,
    val order: Int,
    val testament: String,
    val totalChapters: Int,
    val totalVerses: Int
)

data class BibleChapter(
    val book: String,
    val chapter: Int,
    val verses: List<BibleVerse>
)

fun BibleVerseEntity.toDomain(): BibleVerse {
    return BibleVerse(
        id = id,
        book = book,
        bookAbbrev = bookAbbrev,
        chapter = chapter,
        verse = verse,
        text = text,
        isHighlighted = false,
        highlightColor = null
    )
}

fun BibleBookEntity.toDomain(): BibleBook {
    return BibleBook(
        name = bookName,
        abbreviation = abbreviation,
        order = bookOrder,
        testament = testament,
        totalChapters = totalChapters,
        totalVerses = totalVerses
    )
}