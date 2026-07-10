package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.BibleRepository
import com.valentinerutto.divinedatagpt.data.models.JournalUiState
import com.valentinerutto.divinedatagpt.data.models.VerseCitation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JournalViewModel(private val bibleRepository: BibleRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    private val entries = bibleRepository.observeEntries()

    init {

        viewModelScope.launch {
            bibleRepository.observeEntries().collect { entries ->
                _uiState.update { it.copy(entries = entries) }
            }
        }
    }
    fun onNewEntryTapped() {
        _uiState.update { it.copy(isComposerOpen = true, composerNote = "", composerVerse = null) }
    }

    fun onComposerDismissed() {
        _uiState.update { it.copy(isComposerOpen = false) }
    }

    fun onComposerNoteChanged(text: String) {
        _uiState.update { it.copy(composerNote = text) }
    }

    fun onAttachVerseTapped() {
        _uiState.update { it.copy(isVersePickerOpen = true) }
    }

    fun onVersePicked(verse: VerseCitation) {
        _uiState.update { it.copy(composerVerse = verse, isVersePickerOpen = false) }
    }

    fun onVerseRemoved() {
        _uiState.update { it.copy(composerVerse = null) }
    }

    fun onSaveComposerEntry() {

    }

}