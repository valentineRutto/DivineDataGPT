package com.valentinerutto.divinedatagpt.util

import com.valentinerutto.divinedatagpt.MyApplication
import java.io.File

object GemmaModelManager {

    const val modelPath = "/data/local/tmp/llm/gemma3-1b-it-int4.task"

    const val HF_CHAT_MODEL = "meta-llama/Meta-Llama-3-8B-Instruct"


    fun getModelFile(): File {
        return File(MyApplication.INSTANCE.filesDir, "models/gemma.task")
    }

    fun getDebugModelPath(): String {
        return "/data/local/tmp/llm/gemma3-1b-it-int4.task"
    }

    fun getProductionModelPath(): String {
        return File(
            MyApplication.INSTANCE.filesDir,
            "models/gemma3-1b-it-int4.task"
        ).absolutePath
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