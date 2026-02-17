package com.valentinerutto.divinedatagpt.data.network


// ESV API Models
data class ESVResponse(
    val query: String,
    val canonical: String,
    val parsed: List<List<Int>>,
    val passage_meta: List<PassageMeta>,
    val passages: List<String>
)

data class PassageMeta(
    val canonical: String,
    val chapter_start: List<Int>,
    val chapter_end: List<Int>
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