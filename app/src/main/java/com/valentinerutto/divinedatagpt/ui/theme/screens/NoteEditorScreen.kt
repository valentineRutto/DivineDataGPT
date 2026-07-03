package com.valentinerutto.divinedatagpt.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary

private val Ink = Color(0xFFF4EDF8)
private val MutedInk = Color(0xFF8C8294)
private val Page = Color(0xFF0E0714)
private val TopBar = Color(0xFF050817)
private val Panel = Color(0xFF1B1025)
private val PanelSoft = Color(0xFF261132)
private val Purple = Color(0xFFC15CFF)

private data class NoteHighlightOption(
    val key: String,
    val color: Color
)

private val HighlightColors = listOf(
    NoteHighlightOption("yellow", Color(0xFFFFD166)),
    NoteHighlightOption("green", Color(0xFF74D99F)),
    NoteHighlightOption("blue", Color(0xFF7AB7FF)),
    NoteHighlightOption("pink", Color(0xFFFF8FC7))
)

@Composable
fun NoteEditorScreen(
    verse: VerseEntity,
    initialNote: String,
    initialHighlightColor: String,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var noteDraft by remember(verse.id) { mutableStateOf(initialNote) }
    var selectedHighlightColor by remember(verse.id, initialHighlightColor) {
        mutableStateOf(initialHighlightColor)
    }
    val wordCount = noteDraft.trim()
        .split(Regex("\\s+"))
        .count { word -> word.isNotBlank() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Page,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TopBar)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cancel",
                    color = MutedInk,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable(onClick = onBack)
                )
                Text(
                    text = "Note",
                    color = Ink,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Save",
                    color = Purple,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onSave(noteDraft, selectedHighlightColor) }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            selectedHighlightColor.toHighlightColor() ?: PanelSoft,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(18.dp)
                ) {
                    Text(
                        text = "${verse.bookName} ${verse.chapter}:${verse.verse}",
                        color = Page,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = verse.text,
                        color = Page,
                        fontFamily = FontFamily.Serif,
                        fontSize = 23.sp,
                        lineHeight = 34.sp,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    HighlightColors.forEach { option ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(option.color, RoundedCornerShape(18.dp))
                                .then(
                                    if (option.key == selectedHighlightColor) {
                                        Modifier.drawBehind {
                                            drawCircle(
                                                color = Ink,
                                                radius = 22.dp.toPx(),
                                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                                    width = 2.dp.toPx()
                                                )
                                            )
                                        }
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { selectedHighlightColor = option.key }
                        )
                    }
                }
            }

            item {
                TextField(
                    value = noteDraft,
                    onValueChange = { noteDraft = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 260.dp)
                        .padding(top = 22.dp),
                    placeholder = {
                        Text(
                            text = "Write your note...",
                            color = MutedInk,
                            fontFamily = FontFamily.Serif
                        )
                    },
                    textStyle = TextStyle(
                        color = Ink,
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        lineHeight = 30.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Panel,
                        unfocusedContainerColor = Panel,
                        focusedIndicatorColor = Purple,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Purple
                    )
                )
            }

            item {
                Text(
                    text = "$wordCount words",
                    color = MutedInk,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

private fun String?.toHighlightColor(): Color? {
    return HighlightColors.firstOrNull { option ->
        option.key == this
    }?.color
}
