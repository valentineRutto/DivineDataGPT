package com.valentinerutto.divinedatagpt.data.models



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


