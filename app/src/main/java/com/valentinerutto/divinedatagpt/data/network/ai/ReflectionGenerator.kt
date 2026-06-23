package com.valentinerutto.divinedatagpt.data.network.ai


interface ReflectionGenerator {
    suspend fun generate(
        userText: String,
        emotion: String,
        verseReference: String,
        verseText: String
    ): GeneratedReflection
}

data class GeneratedReflection(
    val insight: String,
    val prayer: String
)