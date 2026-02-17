package com.valentinerutto.divinedatagpt.data.network.ai

import com.valentinerutto.divinedatagpt.data.network.ai.model.GeminiRequest
import com.valentinerutto.divinedatagpt.data.network.ai.model.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AiApi {

    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun createChatCompletion(
        @Body
        request: ChatCompletionRequest
    ): BibleVerseResponse {
        return BibleVerseResponse("John 3:16", "John", "Reflection text")
    }

    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun createChatCompletionOPenAi(
        @Query("key") apiKey: String,
        @Body
        request: ChatCompletionRequestOpenApi
    ): retrofit2.Response<BibleVerseResponse>


}