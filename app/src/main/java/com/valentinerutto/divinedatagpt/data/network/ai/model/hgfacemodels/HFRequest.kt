package com.valentinerutto.divinedatagpt.data.network.ai.model.hgfacemodels

data class HuggingFaceRequest(
    val inputs: String,
    val parameters: Parameters = Parameters()
)

data class Parameters(
    val max_new_tokens: Int = 200,
    val temperature: Double = 0.7,
    val return_full_text: Boolean = false
)
