package com.valentinerutto.divinedatagpt.data.local.entity.bible


import com.google.gson.annotations.SerializedName


data class BibleJson(
    val metadata: BibleMetadata,
    val verses: List<VerseJson>
)

data class BibleMetadata(
    val name: String,
    val shortname: String,
    val module: String,
    val year: String?
)

data class VerseJson(
    @SerializedName("book_name")
    val bookName: String,
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val text: String
)

