package com.valentinerutto.divinedatagpt.data.network.ai

import com.valentinerutto.divinedatagpt.data.network.HFChatRequest
import com.valentinerutto.divinedatagpt.data.network.HuggingFaceRequest
import com.valentinerutto.divinedatagpt.data.network.HuggingFaceResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AiApi {

    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun createChatCompletion(
        @Body
        request: ChatCompletionRequest
    ): BibleVerseResponse {
        return BibleVerseResponse("John 3:16", "John", "Reflection text")
    }


    @POST("v1/chat/completions")
    suspend fun createChatCompletionOPenAi(
        @Body
        request: ChatCompletionRequestOpenApi
    ): BibleVerseResponse {
        return BibleVerseResponse("John 3:16", "John", "Reflection text")
    }



    @POST("models/{model_id}")
    suspend fun generateText(
        @Path("model_id") modelId: String,
        @Body request: HuggingFaceRequest
    ): List<HuggingFaceResponse>

    // Alternative endpoint for conversational models
    @POST("models/{model_id}")
    suspend fun chatCompletion(
        @Path("model_id") modelId: String,
        @Body request: HFChatRequest
    ): Map<String, Any>
}