package com.valentinerutto.divinedatagpt.data.network.ai

import com.valentinerutto.divinedatagpt.data.local.dao.MemorySummaryDao
import com.valentinerutto.divinedatagpt.data.local.dao.MessageDao
import com.valentinerutto.divinedatagpt.data.local.entity.MessageEntity
import com.valentinerutto.divinedatagpt.data.network.ai.model.Content
import com.valentinerutto.divinedatagpt.data.network.ai.model.GeminiRequest
import com.valentinerutto.divinedatagpt.data.network.ai.model.Part
import com.valentinerutto.divinedatagpt.data.network.ai.model.Reflection
import com.valentinerutto.divinedatagpt.data.network.ai.model.hgfacemodels.HuggingFaceChatMessage
import com.valentinerutto.divinedatagpt.data.network.ai.model.hgfacemodels.HuggingFaceChatRequest
import com.valentinerutto.divinedatagpt.util.Resource
import org.json.JSONObject

class AiRepository(
    private val aiApi: AiApi,
    private val huggingFaceApi: AiApi,
    private val messageDao: MessageDao,
    private val memorySummaryDao: MemorySummaryDao
) {
    private companion object {
        const val HF_CHAT_MODEL = "meta-llama/Llama-3.1-8B-Instruct:cerebras"
    }

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

            val json = JSONObject(rawText.trim())
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

    suspend fun getMistralReflectionForEmotion(emotion: String): Resource<Reflection> {
        return try {
            val prompt = """
                You are DivineData AI, a compassionate Bible companion.
                Analyze this user's emotional state: "$emotion".

                Choose a Bible verse that directly comforts or guides that emotion.
                Respond ONLY as valid JSON. Do not include markdown, explanations, or code fences.

                {
                  "verse": "the full Bible verse text",
                  "reference": "Book Chapter:Verse",
                  "insight": "2-3 warm sentences explaining why this verse speaks to the user's emotion"
                }
            """.trimIndent()

            val response = huggingFaceApi.generateChatCompletion(
                HuggingFaceChatRequest(
                    model = HF_CHAT_MODEL,
                    messages = listOf(
                        HuggingFaceChatMessage(
                            role = "user",
                            content = prompt
                        )
                    ),
                    max_tokens = 260,
                    temperature = 0.45
                )
            )

            if (!response.isSuccessful) {
                return Resource.Error(response.errorBody()?.string() ?: "Mistral request failed")
            }

            val rawText = response.body()?.choices?.firstOrNull()?.message?.content
                ?: return Resource.Error("Empty Mistral response")
            val json = JSONObject(extractJsonObject(rawText))

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

    suspend fun addMessageToDB(message: MessageEntity) {

        messageDao.insert(message)

        val allMessages = messageDao.getAllMessages()

        if (allMessages.size >= 5) compressMemory(allMessages)

    }

    suspend fun getRecentReflectionMessages(limit: Int = 5): List<MessageEntity> {
        return messageDao.getRecentMessages(limit).asReversed()
    }

    suspend fun trimReflectionMessages(keepCount: Int = 5) {
        messageDao.deleteAllExceptLast(keepCount)
    }


    private suspend fun compressMemory(messages: List<MessageEntity>) {
        buildString {
            append("Summarize for context retention:\n")
            messages.forEach { append("${it.role}: ${it.content}\n") }
        }

        //doaicall

//    val summary = response.choices.firstOrNull()?.message?.content ?: ""
//    memoryDao.insert(MemorySummaryEntity(summary = summary))
//    messageDao.deleteOldMessages(messages.dropLast(5))


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

    suspend fun chatReflectionWithMistral(
        userMessage: String,
        conversationHistory: List<Pair<String, String>>
    ): Result<Pair<String, String?>> {
        return try {
            val historyText = conversationHistory.joinToString("\n") { (role, msg) ->
                "${if (role == "user") "User" else "AI"}: $msg"
            }
            val prompt = """
                You are DivineData AI, a compassionate Bible companion.
                Analyze the user's emotion from their message and respond with biblical comfort.

                ${if (historyText.isNotBlank()) "Conversation history:\n$historyText\n\n" else ""}
                User: $userMessage

                Respond warmly in under 100 words.
                Include ONE relevant Bible verse if helpful.
                End with one gentle reflective question.
            """.trimIndent()

            val response = huggingFaceApi.generateChatCompletion(
                HuggingFaceChatRequest(
                    model = HF_CHAT_MODEL,
                    messages = listOf(
                        HuggingFaceChatMessage(
                            role = "user",
                            content = prompt
                        )
                    ),
                    max_tokens = 180,
                    temperature = 0.6
                )
            )

            if (!response.isSuccessful) {
                return Result.failure(
                    Exception(response.errorBody()?.string() ?: "Mistral request failed")
                )
            }

            val text = response.body()?.choices?.firstOrNull()?.message?.content?.trim()
                ?: return Result.failure(Exception("Empty Mistral response"))

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
                  "reference": "Book Chapter:Verse ",
                  "insight": "3-4 sentences of deep, inspiring spiritual insight for today's meditation"
                }
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(prompt))))
            )
            val response = aiApi.generateContent(apiKey, request)

            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return Result.failure(Exception("Empty response"))

            val json = JSONObject(rawText.trim())
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


    fun buildReflectionPrompt(
        emotion: String,
        verses: List<String>
    ): String {

        val formattedVerses = verses.joinToString("\n\n")

        return """
        A user is feeling: $emotion

        Here are Bible verses:
        $formattedVerses

        TASK:
        1. Write one short encouragement paragraph (max 100 words)
        2. Write one short prayer (max 60 words)

        RULES:
        - Be warm, calm, and supportive
        - Do not repeat the verses
        - Do not quote new scripture
        - Keep it personal and relatable
        - Avoid preaching tone

        FORMAT:
        Encouragement:
        <text>

        Prayer:
        <text>
        """.trimIndent()
    }

    private fun buildMistralPrompt(instruction: String): String {
        return "<s>[INST] $instruction [/INST]"
    }

    private fun extractJsonObject(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start == -1 || end == -1 || end <= start) return text.trim()
        return text.substring(start, end + 1)
    }


}
