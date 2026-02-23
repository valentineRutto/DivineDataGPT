package com.valentinerutto.divinedatagpt.data.network.bible


// ESV API Models
data class ESVResponse(
    val query: String,
    val canonical: String,
    val parsed: List<List<Int>>,
    val passage_meta: List<PassageMeta>,
    val passages: List<String>
)

// Response Models
data class EsvPassageTextResponse(
    val query: String,
    val canonical: String,
    val parsed: List<List<Int>>,
    val passage_meta: List<PassageMeta>,
    val passages: List<String>
)

data class EsvPassageHtmlResponse(
    val query: String,
    val canonical: String,
    val parsed: List<List<Int>>,
    val passage_meta: List<PassageMeta>,
    val passages: List<String>
)

data class EsvSearchResponse(
    val page: Int,
    val total_results: Int,
    val results: List<SearchResult>,
    val total_pages: Int
)

data class PassageMeta(
    val canonical: String,
    val chapter_start: List<Int>,
    val chapter_end: List<Int>,
    val prev_verse: Int?,
    val next_verse: Int?,
    val prev_chapter: List<Int>?,
    val next_chapter: List<Int>?
)

data class SearchResult(
    val reference: String,
    val content: String
)



// Bible Insight Model
data class BibleInsight(
    val verse: String,
    val reference: String,
    val summary: String,
    val themes: List<String>,
    val application: String,
    val related_verses: List<String>
)

// Wrapper for parsed JSON
data class BibleInsightResponse(
    val insight: BibleInsight
)

data class VerseOfDay(val verse: String, val reference: String, val imageUrl: String? = null)