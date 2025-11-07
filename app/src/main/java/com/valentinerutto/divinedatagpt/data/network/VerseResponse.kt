package com.valentinerutto.divinedatagpt.data.network

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
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