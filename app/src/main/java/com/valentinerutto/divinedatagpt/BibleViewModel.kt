package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.BibleRepository
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import com.valentinerutto.divinedatagpt.data.models.BibleBook
import com.valentinerutto.divinedatagpt.data.models.BibleVerse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChapterRequest(
    val translation: String = "WEB",
    val book: Int = 1,
    val bookName: String = "",
    val chapter: Int = 1
)

val isLoading: Boolean = true


private data class ReaderContentState(
    val request: ChapterRequest,
    val books: List<BibleBook>,
    val availableChapters: List<Int>,
    val verses: List<VerseEntity>
)

data class BibleReaderUiState(
    val request: ChapterRequest = ChapterRequest(),
    val books: List<BibleBook> = emptyList(),
    val availableChapters: List<Int> = emptyList(),
    val verses: List<VerseEntity> = emptyList(),
    val searchResults: List<VerseEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedVerse: Int? = null,
    val isLoading: Boolean = true
)


class BibleViewModel(private val repository: BibleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(BibleUiState())
    val uiState: StateFlow<BibleUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    private val searchQuery = MutableStateFlow("")
    private val selectedVerse = MutableStateFlow<Int?>(null)
    private val request = MutableStateFlow(ChapterRequest())

    private val books = request
        .flatMapLatest { current ->
            repository.observeChapter(current.translation)
        }



    private val initialRequest = ChapterRequest(
        book = initialBook.number,
        bookName = initialBook.name,
        chapter = 1
    )


    private val chapterVerses = request
        .flatMapLatest { current ->
            repository.observeChapter(
                translation = current.translation,
                book = current.book,
                chapter = current.chapter
            )
        }


    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onVerseSelected(verse: Int) {
        selectedVerse.value = if (selectedVerse.value == verse) null else verse
    }

    fun clearSelection() {
        selectedVerse.value = null
    }

    fun loadChapter(
        translation: String,
        book: Int,
        bookName: String,
        chapter: Int
    ) {
        selectedVerse.value = null
        request.value = ChapterRequest(
            translation = translation,
            book = book,
            bookName = bookName,
            chapter = chapter
        )
    }






    init {
        loadBooks()
        observeSearchQuery()
    }

    private fun loadBooks() {

        viewModelScope.launch {
            repository.observeChapter().collect { books ->
                _uiState.update { it.copy(books = books) }
                if (books.isNotEmpty()) {
                    loadChapter(_uiState.value.currentBook, _uiState.value.currentChapter)
                }
            }


        }
    }


    fun onEvent(event: BibleUiEvent) {
        when (event) {
            is BibleUiEvent.LoadChapter -> loadChapter(event.book, event.chapter)
            is BibleUiEvent.NextChapter -> navigateToNextChapter()
            is BibleUiEvent.PreviousChapter -> navigateToPreviousChapter()
            is BibleUiEvent.SelectTranslation -> selectTranslation(event.translation)
            is BibleUiEvent.HighlightVerse -> highlightVerse(event.verseId)
            is BibleUiEvent.SearchQuery -> updateSearchQuery(event.query)
            is BibleUiEvent.ToggleBookSelector -> toggleBookSelector()
            is BibleUiEvent.ToggleSearch -> toggleSearch()
            is BibleUiEvent.AddBookmark -> addBookmark(event.verseId)
            is BibleUiEvent.AddNote -> addNote(event.verseId, event.note)
            is BibleUiEvent.ShareVerse -> shareVerse(event.verseId)
        }
    }

    private fun loadChapter(book: String, chapter: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getChapterVerses(book, chapter)
                .catch { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load chapter"
                        )
                    }
                }
                .collect { verses ->
                    _uiState.update {
                        it.copy(
                            currentBook = book,
                            currentChapter = chapter,
                            verses = verses,
                            isLoading = false,
                            error = null
                        )
                    }

                    repository.recordReading(book, chapter)
                }
        }
    }

    private fun navigateToNextChapter() {
        val currentState = _uiState.value
        val currentBookData = currentState.books.find { it.name == currentState.currentBook }

        currentBookData?.let { book ->
            if (currentState.currentChapter < book.totalChapters) {
                loadChapter(currentState.currentBook, currentState.currentChapter + 1)
            } else {
                // Move to next book
                val nextBook = currentState.books.getOrNull(book.order)
                nextBook?.let {
                    loadChapter(it.name, 1)
                }
            }
        }
    }

    private fun navigateToPreviousChapter() {
        val currentState = _uiState.value

        if (currentState.currentChapter > 1) {
            loadChapter(currentState.currentBook, currentState.currentChapter - 1)
        } else {
            // Move to previous book's last chapter
            val currentBookData = currentState.books.find { it.name == currentState.currentBook }
            currentBookData?.let { book ->
                if (book.order > 1) {
                    val previousBook = currentState.books.getOrNull(book.order - 2)
                    previousBook?.let {
                        loadChapter(it.name, it.totalChapters)
                    }
                }
            }
        }
    }

    private fun selectTranslation(translation: String) {
        _uiState.update { it.copy(selectedTranslation = translation) }
        // Reload chapter with new translation
        loadChapter(_uiState.value.currentBook, _uiState.value.currentChapter)
    }

    private fun highlightVerse(verseId: Long?) {
        _uiState.update { it.copy(highlightedVerseId = verseId) }
    }

    private fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .filter { it.length >= 3 }
                .collectLatest { query ->
                    repository.searchVerses(query).collect { results ->
                        _uiState.update { it.copy(searchResults = results) }
                    }
                }
        }
    }

    private fun toggleBookSelector() {
        _uiState.update { it.copy(showBookSelector = !it.showBookSelector) }
    }

    private fun toggleSearch() {
        _uiState.update {
            it.copy(
                showSearch = !it.showSearch,
                searchQuery = if (!it.showSearch) "" else it.searchQuery
            )
        }
    }

    private fun addBookmark(verseId: Long) {
        viewModelScope.launch {
            val verse = _uiState.value.verses.find { it.id == verseId }
            verse?.let {
                repository.addBookmark(it.book, it.chapter, it.verse, null)
            }
        }
    }

    private fun addNote(verseId: Long, note: String) {
        viewModelScope.launch {
            val verse = _uiState.value.verses.find { it.id == verseId }
            verse?.let {
                repository.addBookmark(it.book, it.chapter, it.verse, note)
            }
        }
    }

    private fun shareVerse(verseId: Long) {
        // Implementation for sharing verse
        // This would typically use Android's share intent
    }


}



data class BibleUiState(
    val books: List<BibleBook> = emptyList(),
    val currentBook: String = "Genesis",
    val currentChapter: Int = 1,
    val verses: List<BibleVerse> = emptyList(),
    val selectedTranslation: String = "NIV",
    val highlightedVerseId: Long? = null,
    val searchQuery: String = "",
    val searchResults: List<BibleVerse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showBookSelector: Boolean = false,
    val showSearch: Boolean = false
)

sealed class BibleUiEvent {
    data class LoadChapter(val book: String, val chapter: Int) : BibleUiEvent()
    object NextChapter : BibleUiEvent()
    object PreviousChapter : BibleUiEvent()
    data class SelectTranslation(val translation: String) : BibleUiEvent()
    data class HighlightVerse(val verseId: Long?) : BibleUiEvent()
    data class SearchQuery(val query: String) : BibleUiEvent()
    object ToggleBookSelector : BibleUiEvent()
    object ToggleSearch : BibleUiEvent()
    data class AddBookmark(val verseId: Long) : BibleUiEvent()
    data class AddNote(val verseId: Long, val note: String) : BibleUiEvent()
    data class ShareVerse(val verseId: Long) : BibleUiEvent()
}


