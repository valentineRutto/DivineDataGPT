package com.valentinerutto.divinedatagpt.data.network.ai.model


data class Emotion(
    val id: String,
    val label: String,
    val emoji: String
)

val defaultEmotions = listOf(
    Emotion("anxious", "Anxious", "💨"),
    Emotion("grateful", "Grateful", "🙏"),
    Emotion("lonely", "Lonely", "🌧"),
    Emotion("inspired", "Inspired", "✨"),
    Emotion("stressed", "Stressed", "🌊"),
    Emotion("peaceful", "Peaceful", "☀️"),
)