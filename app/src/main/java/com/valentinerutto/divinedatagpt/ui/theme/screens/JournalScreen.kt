package com.valentinerutto.divinedatagpt.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinerutto.divinedatagpt.JournalViewModel
import com.valentinerutto.divinedatagpt.data.models.JournalEntry
import com.valentinerutto.divinedatagpt.data.models.JournalSourceType
import com.valentinerutto.divinedatagpt.data.models.VerseCitation
import com.valentinerutto.divinedatagpt.ui.theme.PurpleLight
import com.valentinerutto.divinedatagpt.util.formatTimestamp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalRoute(
    modifier: Modifier = Modifier,
    viewModel: JournalViewModel = koinViewModel()
) {

    JournalScreen(

        onDeleteJournal = {
            viewModel.deleteJournal(it.id)
        },

        onEditJournal = { note, newText ->
            viewModel.updateJournal(
                note.copy(
                    note = newText.trim(),
                    createdAt = System.currentTimeMillis()
                )
            )

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel = koinViewModel(),
    onReopenSession: (sessionId: String, messageId: String) -> Unit = { _, _ -> },
    onEditJournal: (JournalEntry, String) -> Unit,
    onDeleteJournal: (JournalEntry) -> Unit,
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editingEntry by remember { mutableStateOf<JournalEntry?>(null) }
    var editingNote by remember { mutableStateOf("") }

    Scaffold(

        topBar = {
            TopAppBar(title = {
                Text(
                    "My Journal", style = MaterialTheme.typography.headlineMedium,
                )
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onNewEntryTapped) {
                Icon(Icons.Filled.Add, contentDescription = "New Entry")
            }
        }

    ) { padding ->

    if (state.entries.isEmpty()) {
            JournalEmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(state.entries) { entry ->

                    JournalEntryCard(

                        entry = entry,

                        onDelete = { onDeleteJournal(entry) },

                        onOpenEditor = { entryToEdit ->
                            editingEntry = entryToEdit
                            editingNote = entryToEdit.note ?: ""
                        },

                        onClick = {


                            if (entry.sourceType == JournalSourceType.REFLECTION_CHAT && entry.sessionId != null && entry.messageId != null
                        ) {
                            onReopenSession(entry.sessionId, entry.messageId)
                            } else {
                        }


                        }

                    )
                }

            }


        }

        if (state.isComposerOpen) {

            JournalComposerSheet(
                note = state.composerNote,
                verse = state.composerVerse,
                isSaving = state.isSaving,
                error = state.composerError,
                onNoteChanged = viewModel::onComposerNoteChanged,
                onAttachVerse = viewModel::onAttachVerseTapped,
                onRemoveVerse = viewModel::onVerseRemoved,
                onCancel = viewModel::onComposerCancelled,
                onSave = viewModel::onSaveComposerEntry,
                title = state.composerTitle,
                onTitleChanged = viewModel::onTitleChanged
            )

        }

        if (editingEntry != null) {

            JournalComposerSheet(
                note = editingNote,
                verse = editingEntry?.verse,
                isSaving = false,
                error = null,
                onNoteChanged = { editingNote = it },
                onAttachVerse = { },
                onRemoveVerse = { },
                onCancel = {
                    editingEntry = null
                    editingNote = ""
                },
                title = editingEntry?.title ?: "",
                onTitleChanged = { editingEntry = editingEntry?.copy(title = it) },
                onSave = {
                    editingEntry?.let { entry ->
                        onEditJournal(entry, editingNote)
                        editingEntry = null
                        editingNote = ""
                    }
                }
            )
        }


    }
}

@Composable
private fun JournalEntryCard(
    entry: JournalEntry,
    onDelete: () -> Unit,
    onOpenEditor: (JournalEntry) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(Modifier.padding(12.dp)) {

            entry.title?.let { title ->
                Spacer(Modifier.height(6.dp))
                Text(title, style = MaterialTheme.typography.bodyLarge, lineHeight = 18.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    formatTimestamp(entry.createdAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        onOpenEditor(entry)
                    }) {
                        Text(
                            "Edit",
                            color = PurpleLight,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    TextButton(onClick = { onDelete() }) {
                        Text(
                            "Delete",
                            color = Color(0xFFFF8A80),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                if (entry.sourceType == JournalSourceType.REFLECTION_CHAT) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            "from a reflection",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

            }

            entry.verse?.let { verse ->
                Spacer(Modifier.height(6.dp))
                Text(
                    "\"${verse.text}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    verse.reference,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            entry.note?.let { note ->
                Spacer(Modifier.height(6.dp))
                Text(note, style = MaterialTheme.typography.bodyLarge, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun JournalEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Book, contentDescription = null, modifier = Modifier.size(32.dp))
        Spacer(Modifier.height(12.dp))
        Text(
            "Save your first verse from today's reading",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalComposerSheet(
    note: String,
    title: String,
    verse: VerseCitation?,
    isSaving: Boolean,
    error: String?,
    onNoteChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onAttachVerse: () -> Unit,
    onRemoveVerse: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = {
            if (!isSaving) onCancel()
        }
    ) {
        Column(Modifier
            .padding(horizontal = 18.dp)
            .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel, enabled = !isSaving) {
                    Text("Cancel", style = MaterialTheme.typography.bodyMedium)
                }
                TextButton(
                    onClick = onSave,
                    enabled = !isSaving && (note.isNotBlank() || verse != null)
                ) {
                    Text(
                        if (isSaving) "Saving…" else "Save",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (verse != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                    Column(Modifier.weight(1f)) {
                            Text(
                                "\"${verse.text}\"",
                                style = MaterialTheme.typography.bodySmall,

                                )
                            Text(
                                verse.reference,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onRemoveVerse, modifier = Modifier.size(20.dp)) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove verse",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            } else {

                OutlinedButton(onClick = onAttachVerse, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Filled.Book,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Attach a verse", style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }


            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = title,

                onValueChange = onTitleChanged,
                placeholder = {
                    Text(
                        "Title of your reflection", style = MaterialTheme.typography.bodyLarge,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChanged,
                placeholder = {
                    Text(
                        "What's on your heart today?", style = MaterialTheme.typography.bodyLarge,
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .height(140.dp)
            )
            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
