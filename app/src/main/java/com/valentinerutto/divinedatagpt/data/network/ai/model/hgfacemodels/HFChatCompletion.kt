package com.valentinerutto.divinedatagpt.data.network.ai.model.hgfacemodels

data class HuggingFaceChatRequest(
    val model: String,
    val messages: List<HuggingFaceChatMessage>,
    val max_tokens: Int = 260,
    val temperature: Double = 0.45
)

data class HuggingFaceChatMessage(
    val role: String,
    val content: String
)

data class HuggingFaceChatResponse(
    val choices: List<HuggingFaceChatChoice> = emptyList()
)

data class HuggingFaceChatChoice(
    val message: HuggingFaceChatMessage? = null
)
