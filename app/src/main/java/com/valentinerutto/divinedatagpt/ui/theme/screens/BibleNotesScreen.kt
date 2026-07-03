package com.valentinerutto.divinedatagpt.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinerutto.divinedatagpt.BibleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleNoteEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import com.valentinerutto.divinedatagpt.ui.theme.DarkSurface
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextSecondary
import com.valentinerutto.divinedatagpt.ui.theme.TextMuted
import org.koin.androidx.compose.koinViewModel

private val Ink = Color(0xFFF4EDF8)
private val MutedInk = Color(0xFF8C8294)
private val Page = Color(0xFF0E0714)
private val Panel = Color(0xFF1B1025)
private val Purple = Color(0xFFC15CFF)
private val HighlightColors = mapOf(
    "yellow" to Color(0xFFFFD166),
    "green" to Color(0xFF74D99F),
    "blue" to Color(0xFF7AB7FF),
    "pink" to Color(0xFFFF8FC7)
)

@Composable
fun BibleNotesRoute(
    onHomeClick: () -> Unit,
    onBibleClick: () -> Unit,
    onNotesClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BibleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BibleNotesScreen(
        notes = uiState.savedNotes,
        onHomeClick = onHomeClick,
        onBibleClick = onBibleClick,
        onNotesClick = onNotesClick,
        onSettingsClick = onSettingsClick,
        onCreateNote = { note ->
            viewModel.saveBibleNote(note)
        },
        onEditNote = { note, newText, highlightColor ->
            viewModel.updateBibleNote(
                note.copy(
                    note = newText.trim(),
                    highlightColor = highlightColor,
                    createdAt = System.currentTimeMillis()
                )
            )
        },
        onDeleteNote = { note ->
            viewModel.deleteBibleNote(note.id)
        },
        modifier = modifier
    )
}

@Composable
private fun BibleNotesScreen(
    notes: List<BibleNoteEntity>,
    onHomeClick: () -> Unit,
    onBibleClick: () -> Unit,
    onNotesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCreateNote: (String) -> Unit,
    onEditNote: (BibleNoteEntity, String, String) -> Unit,
    onDeleteNote: (BibleNoteEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var isCreatingNote by remember { mutableStateOf(false) }
    var plainEditorNote by remember { mutableStateOf<BibleNoteEntity?>(null) }
    var editorVerse by remember { mutableStateOf<VerseEntity?>(null) }
    var editorNote by remember { mutableStateOf<BibleNoteEntity?>(null) }
    val activeEditorVerse = editorVerse

    if (isCreatingNote || plainEditorNote != null) {
        val activePlainNote = plainEditorNote
        PlainNoteEditorScreen(
            initialNote = activePlainNote?.note.orEmpty(),
            onSave = { note ->
                if (activePlainNote == null) {
                    onCreateNote(note)
                } else {
                    onEditNote(activePlainNote, note, activePlainNote.highlightColor)
                }
                isCreatingNote = false
                plainEditorNote = null
            },
            onBack = {
                isCreatingNote = false
                plainEditorNote = null
            },
            modifier = modifier
        )
        return
    }

    if (activeEditorVerse != null) {
        val activeEditorNote = editorNote
        NoteEditorScreen(
            verse = activeEditorVerse,
            initialNote = activeEditorNote?.note.orEmpty(),
            initialHighlightColor = activeEditorNote?.highlightColor ?: "yellow",
            onSave = { note, highlightColor ->
                onEditNote(activeEditorNote ?: return@NoteEditorScreen, note, highlightColor)
                editorVerse = null
                editorNote = null
            },
            onBack = {
                editorVerse = null
                editorNote = null
            },
            modifier = modifier
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Page,
        bottomBar = {
            BibleNotesBottomNavBar(
                selectedTab = 2,
                onHomeClick = onHomeClick,
                onBibleClick = onBibleClick,
                onNotesClick = onNotesClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 26.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Notes",
                        color = Ink,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp
                    )
                    TextButton(onClick = { isCreatingNote = true }) {
                        Text("Create", color = Purple)
                    }
                }
            }

            if (notes.isEmpty()) {
                item {
                    Text(
                        text = "No notes yet",
                        color = MutedInk,
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 36.dp)
                    )
                }
            } else {
                items(
                    items = notes,
                    key = { note -> note.id }
                ) { note ->
                    BibleNoteRow(
                        note = note,
                        onEditClick = {
                            if (note.isStandaloneNote()) {
                                plainEditorNote = note
                            } else {
                                editorVerse = note.toVerseEntity()
                                editorNote = note
                            }
                        },
                        onDeleteNote = onDeleteNote
                    )
                }
            }
        }
    }
}

@Composable
private fun PlainNoteEditorScreen(
    initialNote: String,
    onSave: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var noteDraft by remember { mutableStateOf(initialNote) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Page,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Panel)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBack) {
                    Text("Cancel", color = MutedInk)
                }
                Text(
                    text = "Note",
                    color = Ink,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                TextButton(onClick = { onSave(noteDraft) }) {
                    Text("Save", color = Purple)
                }
            }
        }
    ) { innerPadding ->
        TextField(
            value = noteDraft,
            onValueChange = { noteDraft = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .heightIn(min = 260.dp),
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
}

@Composable
private fun BibleNoteRow(
    note: BibleNoteEntity,
    onEditClick: () -> Unit,
    onDeleteNote: (BibleNoteEntity) -> Unit
) {
    var noteToDelete by remember { mutableStateOf<BibleNoteEntity?>(null) }
    val isStandaloneNote = note.isStandaloneNote()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Panel, RoundedCornerShape(8.dp))
            .padding(18.dp)
    ) {
        Text(
            text = if (isStandaloneNote) "Personal note" else "${note.bookName} ${note.chapter}:${note.verse}",
            color = note.highlightColor.toHighlightColor() ?: Purple,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = formatNoteTimestamp(note.createdAt),
            color = MutedInk,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
        if (!isStandaloneNote) {
            Text(
                text = note.verseText,
                color = Ink,
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                lineHeight = 30.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Text(
            text = note.note.ifBlank { "No note added yet" },
            color = if (note.note.isBlank()) MutedInk else Ink,
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp,
            lineHeight = 26.sp,
            modifier = Modifier.padding(top = 14.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onEditClick) {
                Text("Edit", color = Purple)
            }
            TextButton(onClick = { noteToDelete = note }) {
                Text("Delete", color = Color(0xFFFF8A80))
            }
        }
    }

    noteToDelete?.let { noteToRemove ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Delete note?", color = Ink) },
            text = { Text("This note will be removed from your saved notes.", color = Ink) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteNote(noteToRemove)
                    noteToDelete = null
                }) {
                    Text("Delete", color = Color(0xFFFF8A80))
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("Cancel", color = MutedInk)
                }
            },
            containerColor = Panel,
            titleContentColor = Ink,
            textContentColor = Ink
        )
    }
}

private fun BibleNoteEntity.toVerseEntity(): VerseEntity {
    return VerseEntity(
        id = verseId,
        translation = translation,
        bookName = bookName,
        book = book,
        chapter = chapter,
        verse = verse,
        text = verseText
    )
}

private fun BibleNoteEntity.isStandaloneNote(): Boolean {
    return verseId < 0
}

@Composable
private fun BibleNotesBottomNavBar(
    selectedTab: Int,
    onHomeClick: () -> Unit,
    onBibleClick: () -> Unit,
    onNotesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextSecondary,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = onHomeClick,
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selectedTab == 0) PurplePrimary else TextMuted
                )
            },
            label = {
                Text(
                    "HOME",
                    fontSize = 10.sp,
                    color = if (selectedTab == 0) PurplePrimary else TextMuted
                )
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = onBibleClick,
            icon = {
                Icon(
                    Icons.Default.MenuBook,
                    contentDescription = "Bible",
                    tint = if (selectedTab == 1) PurplePrimary else TextMuted
                )
            },
            label = {
                Text(
                    "BIBLE",
                    fontSize = 10.sp,
                    color = if (selectedTab == 1) PurplePrimary else TextMuted
                )
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = onNotesClick,
            icon = {
                Icon(
                    Icons.Rounded.EditNote,
                    contentDescription = "Notes",
                    tint = if (selectedTab == 2) PurplePrimary else TextMuted
                )
            },
            label = {
                Text(
                    "NOTES",
                    fontSize = 10.sp,
                    color = if (selectedTab == 2) PurplePrimary else TextMuted
                )
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = onSettingsClick,
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (selectedTab == 3) PurplePrimary else TextMuted
                )
            },
            label = {
                Text(
                    "SETTINGS",
                    fontSize = 10.sp,
                    color = if (selectedTab == 3) PurplePrimary else TextMuted
                )
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
    }
}

private fun String?.toHighlightColor(): Color? {
    return HighlightColors[this]
}

private fun formatNoteTimestamp(createdAt: Long): String {
    return if (createdAt <= 0L) {
        "Just saved"
    } else {
        SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(createdAt))
    }
}
