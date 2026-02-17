package com.valentinerutto.divinedatagpt.data.network.ai

class AiRepository(
    private val aiApi: AiApi
) {


    suspend fun ask(feeling: String): String {


        val systemPrompt = "You are a compassionate biblical scholar. " +
                "Always respond with valid JSON only — no markdown, no extra text."

        val userPrompt = """
                A person is feeling "$feeling". Provide a comforting and relevant Bible verse,
                the book it's from, and a short, encouraging 2-3 sentence reflection.
                
                Respond ONLY with this JSON structure:
                {
                  "verse": "<full text of the Bible verse>",
                  "book": "<book, chapter, and verse number, e.g. John 3:16>",
                  "reflection": "<short, encouraging 2-3 sentence reflection>"
                }
            """.trimIndent()

        val response = aiApi.createChatCompletionOPenAi(

            ChatCompletionRequestOpenApi(
                messages = listOf(
                    ChatMessage("system", systemPrompt),
                    ChatMessage("user", userPrompt)
                )
            )
        )
        return response.reflection
    }

    suspend fun explainVerse(
        verseReference: String,
        userFeeling: String
    ): String {

        """
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

        val response = aiApi.createChatCompletion(

            ChatCompletionRequest(
                messages = userPrompt
                )
            )


        return response.verse
            ?: "Take a quiet moment to reflect on this verse and how it speaks to your heart."


    }
}
