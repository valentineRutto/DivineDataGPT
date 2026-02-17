package com.valentinerutto.divinedatagpt.data.network.bible

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v3/passage/text/")
    suspend fun getPassage(
        @Query("q") query: String,
        @Query("include-passage-references") includeRefs: Boolean = true,
        @Query("include-verse-numbers") includeVerseNumbers: Boolean = true,
        @Query("include-first-verse-numbers") includeFirstVerse: Boolean = true,
        @Query("include-footnotes") includeFootnotes: Boolean = false,
        @Query("include-headings") includeHeadings: Boolean = false,
        @Query("include-short-copyright") includeCopyright: Boolean = false
    ): ESVResponse

}