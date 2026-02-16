package com.valentinerutto.divinedatagpt.data

import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.valentinerutto.divinedatagpt.data.network.ai.AiApi
import com.valentinerutto.divinedatagpt.data.network.ai.BibleVerseResponse
import com.valentinerutto.divinedatagpt.data.network.ai.ChatCompletionRequest
import com.valentinerutto.divinedatagpt.util.Result

class DivineDataRepository(val apiService: AiApi) {

    private val model: GenerativeModel? by lazy {
        try {
            GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = BuildConfig.VERSION_NAME
            )
        } catch (e: Exception) {
            println("API Key not found or invalid: ${e.message}")
            "API Key not configured. This applet will not function."
            null
        }
    }

    suspend fun sendEmotionToServer(emotion: ChatCompletionRequest): Result<BibleVerseResponse> {
        val response = apiService.createChatCompletion(emotion)
        return response
    }


}