package com.valentinerutto.divinedatagpt.data.network.ai.model


data class Reflection(
    val verse: String,
    val reference: String,
    val insight: String
)

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val verse: String? = null,
    val reference: String? = null
)