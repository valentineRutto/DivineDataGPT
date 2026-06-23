package com.valentinerutto.divinedatagpt.data.network.ai

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class OnDeviceReflectionGenerator(context: Context) : ReflectionGenerator {
    private val llmInference = LlmInference.createFromOptions(
        context,
        LlmInference.LlmInferenceOptions.builder()
            .setModelPath("/data/local/tmp/model.bin") // or copied asset path
            .setMaxTokens(80)
            .build()
    )

    override suspend fun generate(
        userText: String,
        emotion: String,
        verseReference: String,
        verseText: String
    ): GeneratedReflection = withContext(Dispatchers.Default) {
        val prompt = """
            You are a gentle Christian emotional companion.
            User feeling: "$userText"
            Emotion: ${emotion.name}
            Verse: $verseReference - "$verseText"

            Write exactly:
            Insight: one encouraging sentence.
            Prayer: one short prayer sentence.
        """.trimIndent()

        val output = llmInference.generateResponse(prompt)
        parseGeneratedReflection(output)

    }

}

fun parseGeneratedReflection(output: String): GeneratedReflection {
    val insight = output
        .lineSequence()
        .firstOrNull { it.startsWith("Insight:", ignoreCase = true) }
        ?.substringAfter(":")
        ?.trim()
        ?: "This verse reminds you that God is present with you in this moment."

    val prayer = output
        .lineSequence()
        .firstOrNull { it.startsWith("Prayer:", ignoreCase = true) }
        ?.substringAfter(":")
        ?.trim()
        ?: "Lord, guide my heart and help me rest in Your peace today."

    return GeneratedReflection(insight, prayer)
}