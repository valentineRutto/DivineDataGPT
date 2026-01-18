package com.valentinerutto.divinedatagpt.data.network


// ESV API Models
data class ESVResponse(
    val query: String,
    val canonical: String,
    val parsed: List<List<Int>>,
    val passage_meta: List<PassageMeta>,
    val passages: List<String>
)

data class PassageMeta(
    val canonical: String,
    val chapter_start: List<Int>,
    val chapter_end: List<Int>
)

// Hugging Face API Models
data class HuggingFaceRequest(
    val inputs: String,
    val parameters: HFParameters? = null
)

data class HFParameters(
    val max_new_tokens: Int = 500,
    val temperature: Double = 0.7,
    val top_p: Double = 0.95,
    val do_sample: Boolean = true,
    val return_full_text: Boolean = false
)

// Response for text generation models
data class HuggingFaceResponse(
    val generated_text: String
)

// Alternative: Chat completion format (for instruct models)
data class HFChatRequest(
    val inputs: ChatInputs,
    val parameters: HFParameters? = null
)

data class ChatInputs(
    val past_user_inputs: List<String> = emptyList(),
    val generated_responses: List<String> = emptyList(),
    val text: String
)

// Bible Insight Model
data class BibleInsight(
    val verse: String,
    val reference: String,
    val summary: String,
    val themes: List<String>,
    val application: String,
    val related_verses: List<String>
)

// Wrapper for parsed JSON
data class BibleInsightResponse(
    val insight: BibleInsight
)