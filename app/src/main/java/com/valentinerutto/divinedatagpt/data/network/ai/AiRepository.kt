package com.valentinerutto.divinedatagpt.data.network.ai

class AiRepository(
    private val openAiApi: AiApi
) {

    suspend fun explainVerse(
        verseReference: String,
        userFeeling: String
    ): String {
        val systemPrompt = """
            You are a compassionate Christian spiritual guide.
            Explain Bible verses gently, clearly, and with encouragement.
            Do not preach aggressively.
            Keep responses under 120 words.
        """.trimIndent()

        val userPrompt = """
            The user is feeling "$userFeeling".

            Explain the Bible verse $verseReference in a way that
            comforts them emotionally and offers gentle hope.
        """.trimIndent()

        val response = openAiApi.createChatCompletion(
            ChatCompletionRequest(
                messages = listOf(
                    ChatMessage("system", systemPrompt),
                    ChatMessage("user", userPrompt)
                )
            )
        )

        return response.choices.firstOrNull()
            ?.message
            ?.content
            ?: "Take a quiet moment to reflect on this verse and how it speaks to your heart."
    }
}
