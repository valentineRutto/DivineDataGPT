package com.valentinerutto.divinedatagpt.data.network.ai

import retrofit2.http.Body
import retrofit2.http.POST

class AiApi {

    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse{
        return ChatCompletionResponse(emptyList())

    }
}