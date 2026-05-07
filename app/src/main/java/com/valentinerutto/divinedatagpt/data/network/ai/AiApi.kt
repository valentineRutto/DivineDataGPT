package com.valentinerutto.divinedatagpt.data.network.ai

import com.valentinerutto.divinedatagpt.data.network.ai.model.GeminiRequest
import com.valentinerutto.divinedatagpt.data.network.ai.model.GeminiResponse
import com.valentinerutto.divinedatagpt.data.network.ai.model.hgfacemodels.HuggingFaceChatRequest
import com.valentinerutto.divinedatagpt.data.network.ai.model.hgfacemodels.HuggingFaceChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AiApi {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    @POST("v1/chat/completions")
    suspend fun generateChatCompletion(
        @Body request: HuggingFaceChatRequest
    ): Response<HuggingFaceChatResponse>

}
