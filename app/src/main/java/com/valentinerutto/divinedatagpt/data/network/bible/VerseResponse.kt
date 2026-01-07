package com.valentinerutto.divinedatagpt.data.network.bible

import kotlinx.serialization.Serializable

@Serializable
data class VerseResponse(
    val id: Int,
    val text: String,
    val reference: String
)

// Example for a POST request body
@Serializable
data class EmotionRequest(
    val emotion: String,
)