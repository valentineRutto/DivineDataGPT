package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.BibleRepository
import com.valentinerutto.divinedatagpt.data.local.entity.JournalEntryEntity
import com.valentinerutto.divinedatagpt.data.models.JournalEntry
import com.valentinerutto.divinedatagpt.data.models.JournalSourceType
import com.valentinerutto.divinedatagpt.data.models.JournalUiState
import com.valentinerutto.divinedatagpt.data.models.VerseCitation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class JournalViewModel(private val bibleRepository: BibleRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()


    init {

        viewModelScope.launch {
            bibleRepository.observeEntries().collect { entries ->
                _uiState.update { it.copy(entries = entries) }
            }
        }
    }
    fun onNewEntryTapped() {
        _uiState.update {
            it.copy(
                isComposerOpen = true,
                composerNote = "",
                composerVerse = null,
                composerError = null
            )
        }
    }

    fun onComposerCancelled() {
        _uiState.update {
            it.copy(
                isComposerOpen = false,
                composerNote = "",
                composerVerse = null,
                isVersePickerOpen = false,
                isSaving = false,
                composerError = null
            )
        }
    }

    fun onComposerNoteChanged(text: String) {
        _uiState.update { it.copy(composerNote = text, composerError = null) }
    }

    fun onAttachVerseTapped() {
        _uiState.update { it.copy(isVersePickerOpen = true) }
    }

    fun onVersePicked(verse: VerseCitation) {
        _uiState.update {
            it.copy(composerVerse = verse, isVersePickerOpen = false, composerError = null)
        }
    }

    fun updateJournal(note: JournalEntryEntity, newText: String, highlightColor: String) {
    }

    fun deleteJournal(noteId: String) {
        viewModelScope.launch {
            bibleRepository.deleteJournal(noteId)
        }
    }
    fun onVerseRemoved() {
        _uiState.update { it.copy(composerVerse = null) }
    }

    fun onSaveComposerEntry() {
        val draft = _uiState.value
        val note = draft.composerNote.trim().ifEmpty { null }

        if (note == null && draft.composerVerse == null) {
            _uiState.update { it.copy(composerError = "Add a note or attach a verse.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, composerError = null) }

            runCatching {
                bibleRepository.save(
                    JournalEntry(
                        id = UUID.randomUUID().toString(),
                        verse = draft.composerVerse,
                        note = note,
                        sourceType = JournalSourceType.MANUAL,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isComposerOpen = false,
                        composerNote = "",
                        composerVerse = null,
                        isSaving = false,
                        composerError = null
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        composerError = "Couldn't save this entry. Please try again."
                    )
                }
            }
        }
    }

}
