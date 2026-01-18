package com.valentinerutto.divinedatagpt.data.network.bible

import android.util.Log
import com.valentinerutto.divinedatagpt.data.local.Verse
import com.valentinerutto.divinedatagpt.data.network.ESVResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("v3/passage/text/")
    suspend fun getPassage(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("include-passage-references") includeRefs: Boolean = true,
        @Query("include-verse-numbers") includeVerseNumbers: Boolean = true,
        @Query("include-first-verse-numbers") includeFirstVerse: Boolean = true,
        @Query("include-footnotes") includeFootnotes: Boolean = false,
        @Query("include-headings") includeHeadings: Boolean = false,
        @Query("include-short-copyright") includeCopyright: Boolean = false
    ): ESVResponse

}