package com.valentinerutto.divinedatagpt.util

import android.content.Context
import java.io.File

class GemmaModelManager(
    private val context: Context
) {
    companion object {
        const val modelPath = "/data/local/tmp/llm/gemma3-1b-it-int4.task"

        const val HF_CHAT_MODEL = "meta-llama/Meta-Llama-3-8B-Instruct"
    }

    fun getModelFile(): File {
        return File(context.filesDir, "models/gemma.task")
    }

    fun isModelReady(): Boolean {
        return getModelFile().exists()
    }

    fun getModelPath(): String {
        val file = getModelFile()

        if (!file.exists()) {
            throw IllegalStateException("Gemma model has not been imported")
        }

        return file.absolutePath
    }
}