package com.valentinerutto.divinedatagpt.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.valentinerutto.divinedatagpt.data.network.ai.AiApi
import com.valentinerutto.divinedatagpt.data.network.ai.model.Reflection
import com.valentinerutto.divinedatagpt.data.network.ai.model.hgfacemodels.HuggingFaceRequest
import com.valentinerutto.divinedatagpt.data.network.bible.ApiService
import com.valentinerutto.divinedatagpt.data.network.bible.BibleInsight
import com.valentinerutto.divinedatagpt.data.network.bible.ESVResponse


class BibleRepository(
    private val esvApi: ApiService,
    private val huggingFaceApi: AiApi,

    ) {

    // Popular Hugging Face instruction-tuned models
    companion object {
        const val MISTRAL_7B_INSTRUCT = "mistralai/Mistral-7B-Instruct-v0.2"
        const val LLAMA_2_7B_CHAT = "meta-llama/Llama-2-7b-chat-hf"
        const val ZEPHYR_7B_BETA = "HuggingFaceH4/zephyr-7b-beta"
        const val PHI_2 = "microsoft/phi-2"
        const val MIXTRAL_8X7B = "mistralai/Mixtral-8x7B-Instruct-v0.1"
    }

    // Fetch Bible verse from ESV API
    suspend fun getBibleVerse(reference: String): Result<ESVResponse> {
        return try {
            val response = esvApi.getPassage(
                query = reference
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getDailyReflection(): Result<Reflection> {
        return try {
            val prompt = buildMistralPrompt(
                """You are a Bible devotional generator. Provide today's uplifting daily reflection.
                
Respond ONLY in this exact JSON format (no markdown, no extra text):
{
  "verse": "the full Bible verse text",
  "reference": "Book Chapter:Verse (NIV)",
  "insight": "3-4 sentences of deep, inspiring spiritual insight for today's meditation"
}"""
            )

            val request = HuggingFaceRequest(
                inputs = prompt,
                parameters = MistralParameters(
                    max_new_tokens = 500,
                    temperature = 0.9f
                )
            )

            val response = huggingFaceApi.generateText(request)

            if (response.error != null) {
                return Result.failure(Exception(response.error))
            }

            val generatedText =
                response.generated_text ?: return Result.failure(Exception("Empty response"))

            val jsonText = extractJson(generatedText)
            val json = JSONObject(jsonText)

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

    // Create instruction-following prompt
    private fun createInstructPrompt(reference: String, verseText: String): String {
        return """<s>[INST] You are a Bible scholar and theologian. Analyze the following Bible verse and provide insights in JSON format.

Verse Reference: $reference
Verse Text: $verseText

Provide your analysis in this exact JSON structure (respond with ONLY the JSON, no other text):
{
    "summary": "a concise 2-3 sentence summary of the verse's meaning",
    "themes": ["theme1", "theme2", "theme3"],
    "application": "practical application for daily Christian life (2-3 sentences)",
    "related_verses": ["reference1", "reference2", "reference3"]
}
Remember: Respond with ONLY valid JSON, nothing else. [/INST]"""

    }

    // Alternative prompt for chat models
    private fun createChatPrompt(reference: String, verseText: String): String {
        return """Analyze this Bible verse and respond with ONLY a JSON object:

Reference: $reference
Text: $verseText

JSON format:
{
    "summary": "brief summary",
    "themes": ["theme1", "theme2", "theme3"],
    "application": "practical application",
    "related_verses": ["verse1", "verse2", "verse3"]
}"""
    }

    // Parse the LLM response and extract JSON
    private fun parseInsightFromResponse(
        response: String,
        reference: String,
        verseText: String
    ): BibleInsight {
        return try {
            // Try to extract JSON from the response
            val jsonStart = response.indexOf("{")
            val jsonEnd = response.lastIndexOf("}") + 1

            if (jsonStart != -1 && jsonEnd > jsonStart) {
                val jsonString = response.substring(jsonStart, jsonEnd)
                val gson = Gson()

                // Parse the JSON
                val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)

                BibleInsight(
                    verse = verseText.trim(),
                    reference = reference,
                    summary = jsonObject.get("summary")?.asString ?: "No summary available",
                    themes = jsonObject.getAsJsonArray("themes")?.map {
                        it.asString
                    } ?: listOf("Faith", "Hope", "Love"),
                    application = jsonObject.get("application")?.asString
                        ?: "No application available",
                    related_verses = jsonObject.getAsJsonArray("related_verses")?.map {
                        it.asString
                    } ?: emptyList()
                )
            } else {
                // Fallback if JSON extraction fails
                createFallbackInsight(reference, verseText, response)
            }
        } catch (e: Exception) {
            // Fallback parsing
            createFallbackInsight(reference, verseText, response)
        }
    }
    private fun buildMistralPrompt(instruction: String): String {
        return "<s>[INST] $instruction [/INST]"
    }

    // Helper function to extract JSON from Mistral's response
    private fun extractJson(text: String): String {
        // Try to find JSON object in the response
        val jsonStart = text.indexOf("{")
        val jsonEnd = text.lastIndexOf("}") + 1

        return if (jsonStart >= 0 && jsonEnd > jsonStart) {
            text.substring(jsonStart, jsonEnd)
        } else {
            text.trim()
        }
    }
    // Fallback if JSON parsing fails
    private fun createFallbackInsight(
        reference: String,
        verseText: String,
        response: String
    ): BibleInsight {
        return BibleInsight(
            verse = verseText.trim(),
            reference = reference,
            summary = response.take(200).trim(),
            themes = listOf("Faith", "Scripture", "Truth"),
            application = "Reflect on this passage and seek God's guidance in applying it to your life.",
            related_verses = emptyList()
        )
    }
}