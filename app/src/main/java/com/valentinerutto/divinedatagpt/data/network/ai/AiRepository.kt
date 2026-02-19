package com.valentinerutto.divinedatagpt.data.network.ai

import com.valentinerutto.divinedatagpt.data.network.ai.model.Content
import com.valentinerutto.divinedatagpt.data.network.ai.model.GeminiRequest
import com.valentinerutto.divinedatagpt.data.network.ai.model.Part
import com.valentinerutto.divinedatagpt.data.network.ai.model.Reflection
import com.valentinerutto.divinedatagpt.util.Resource

class AiRepository(
    private val aiApi: AiApi
) {


    suspend fun getReflectionForEmotion(apikey: String, emotion: String): Resource<Reflection> {
        return try {
            val prompt = """
                You are a compassionate Bible companion. The user feels: "$emotion".
                
                Respond ONLY in this exact JSON format (no markdown, no extra text):
                {
                  "verse": "the full Bible verse text",
                  "reference": "Book Chapter:Verse (Translation)",
                  "insight": "2-3 sentences of warm, personal spiritual insight connecting this verse to their feeling"
                }
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(prompt))))
            )
            val response = aiApi.generateContent(apikey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return Resource.Error("Empty response")

            val json = org.json.JSONObject(rawText.trim())
            Resource.Success(
                Reflection(
                    verse = json.getString("verse"),
                    reference = json.getString("reference"),
                    insight = json.getString("insight")
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    suspend fun chatReflection(
        apiKey: String,
        userMessage: String,
        conversationHistory: List<Pair<String, String>>
    ): Result<Pair<String, String?>> {
        return try {
            val historyText = conversationHistory.joinToString("\n") { (role, msg) ->
                "${if (role == "user") "User" else "AI"}: $msg"
            }
            val prompt = """
                You are a compassionate AI Bible Companion named DivineData AI.
                Provide comfort, wisdom, and relevant Bible verses for emotional support.
                
                ${if (historyText.isNotBlank()) "Conversation history:\n$historyText\n\n" else ""}
                User: $userMessage
                
                Respond with empathy. Include ONE relevant Bible verse in italics if appropriate.
                Keep your response warm, personal, and under 100 words. End with a reflective question.
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(prompt))))
            )
            val response = aiApi.generateContent(apiKey, request)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return Result.failure(Exception("Empty response"))

            Result.success(Pair(text, null))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getDailyReflection(apiKey: String): Result<Reflection> {
        return try {
            val prompt = """
                You are a Bible devotional generator. Provide today's uplifting daily reflection.
                
                Respond ONLY in this exact JSON format (no markdown, no extra text):
                {
                  "verse": "the full Bible verse text",
                  "reference": "Book Chapter:Verse (NIV)",
                  "insight": "3-4 sentences of deep, inspiring spiritual insight for today's meditation"
                }
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(prompt))))
            )
            val response = aiApi.generateContent(apiKey, request)

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return Result.failure(Exception("Empty response"))

            val json = org.json.JSONObject(rawText.trim())
            Result.success(
                Reflection(
                    verse = json.getString("verse"),
                    reference = json.getString("reference"),
                    insight = json.getString("insight")
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun ask(apikey: String, feeling: String) {


        "You are a compassionate biblical scholar. " +
                "Always respond with valid JSON only — no markdown, no extra text."

        """
                A person is feeling "$feeling". Provide a comforting and relevant Bible verse,
                the book it's from, and a short, encouraging 2-3 sentence reflection.
                
                Respond ONLY with this JSON structure:
                {
                  "verse": "<full text of the Bible verse>",
                  "book": "<book, chapter, and verse number, e.g. John 3:16>",
                  "reflection": "<short, encouraging 2-3 sentence reflection>"
                }
            """.trimIndent()

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

        """
            The user is feeling "$userFeeling".
            Explain the Bible verse $verseReference in a way that
            comforts them emotionally and offers gentle hope.
        """.trimIndent()



        return "Take a quiet moment to reflect on this verse and how it speaks to your heart."

    }
}
