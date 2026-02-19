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

fun shareToInstagramStory(context: Context, text: String) {

    val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra("android.intent.extra.TEXT", text)
    }
    context.startActivity(intent)
}

fun shareToWhatsApp(context: Context, reflection: Reflection) {


    val text = """
${reflection.verse}

— ${reflection.reference}

${reflection.insight}

✨ Daily Reflection from DivineData AI
Download on Play Store: 
""".trimIndent()
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage("com.whatsapp")
    }
    val shareIntent = Intent.createChooser(intent, "Share Reflection")
    context.startActivity(shareIntent)
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
        appendLine(
            "✨ Daily Reflection from DivineData AI\n" +
                    "Download on Play Store: \n"
        )
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share Reflection")
    context.startActivity(shareIntent)

}
