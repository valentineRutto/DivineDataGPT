package com.valentinerutto.divinedatagpt.data.network.ai.model

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    val contents: List<Content>,
    @SerializedName("generationConfig")
    val generationConfig: GenerationConfig = GenerationConfig()
)

data class Content(
    val parts: List<Part>,
    val role: String = "user"
)

data class Part(val text: String)

data class GenerationConfig(
    val temperature: Float = 0.8f,
    @SerializedName("maxOutputTokens")
    val maxOutputTokens: Int = 1024
)

data class GeminiResponse(
    val candidates: List<Candidate>?
)

data class Candidate(
    val content: Content?
)