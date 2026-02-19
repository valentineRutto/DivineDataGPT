package com.valentinerutto.divinedatagpt.util

import android.content.Context
import android.content.Intent
import com.valentinerutto.divinedatagpt.data.network.ai.model.Reflection

fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Reflection"))
}

fun shareToWhatsApp(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage("com.whatsapp")
    }
    context.startActivity(intent)
}

fun shareReflection(context: Context, reflection: Reflection) {

    val shareText = buildString {
        appendLine("Divine Reflection")
        appendLine()
        appendLine("\"${reflection.verse}\"")
        appendLine(reflection.reference)
        appendLine()
        appendLine(reflection.insight)
        appendLine()
        appendLine("- Shared from Divine Reflection by DivineData AI")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share Reflection")
    context.startActivity(shareIntent)

}
