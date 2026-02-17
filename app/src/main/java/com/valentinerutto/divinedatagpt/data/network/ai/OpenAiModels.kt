package com.valentinerutto.divinedatagpt.data.network.ai

data class ChatCompletionRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: String,
    val temperature: Double = 0.7
)

data class ChatCompletionRequestOpenApi(
    val model: String = "gpt-5",
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7
)

data class ChatMessage(
    val role: String, // "system", "user"
    val content: String
)

data class ChatCompletionResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: ChatMessage
)

data class BibleVerseResponse(
    val verse: String,
    val book: String,
    val reflection: String
)

/**
 * Represents the Word of the Day verse.
 */
data class WordOfTheDayResponse(
    val verse: String,
    val book: String
)