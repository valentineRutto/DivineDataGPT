package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.BibleRepository
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import com.valentinerutto.divinedatagpt.data.models.BibleBook
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

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

    private val _searchQuery = MutableStateFlow("")

    private val searchQuery = MutableStateFlow("")
    private val selectedVerse = MutableStateFlow<Int?>(null)
    private val request = MutableStateFlow(ChapterRequest())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val books = request
        .flatMapLatest { current ->
            repository.observeBooks(current.translation)
        }


    private val activeRequest = combine(
        request,
        books
    ) { current, books ->
        val selectedBook = books.firstOrNull { it.book == current.book }
            ?: books.firstOrNull()

        if (selectedBook == null) {
            current
        } else {
            current.copy(
                book = selectedBook.book,
                bookName = selectedBook.bookName,
                chapter = current.chapter.coerceIn(1, selectedBook.chapterCount)
            )
        }
    }
    private val availableChapters = activeRequest
        .flatMapLatest { current ->
            if (current.book == 0) {
                flowOf(emptyList())
            } else {
                repository.observeChapters(
                    translation = current.translation,
                    book = current.book
                )
            }
        }


    private val chapterVerses = activeRequest
        .flatMapLatest { current ->
            if (current.book == 0) {
                flowOf(emptyList())
            } else {
                repository.observeChapter(
                    translation = current.translation,
                    book = current.book,
                    chapter = current.chapter
                )
            }
        }

    private val searchResults = combine(
        activeRequest,
        searchQuery
    ) { current, query ->
        current.translation to query
    }.flatMapLatest { (translation, query) ->
        if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            repository.searchVerses(
                translation = translation,
                query = query
            )
        }
    }

    private val readerContent = combine(
        activeRequest,
        books,
        availableChapters,
        chapterVerses
    ) { currentRequest, books, chapters, verses ->
        ReaderContentState(
            request = currentRequest,
            books = books,
            availableChapters = chapters,
            verses = verses
        )
    }

    val uiState: StateFlow<BibleReaderUiState> = combine(
        readerContent,
        searchQuery,
        searchResults,
        selectedVerse
    ) { content, query, results, selected ->
        BibleReaderUiState(
            request = content.request,
            books = content.books,
            availableChapters = content.availableChapters,
            verses = content.verses,
            searchResults = results,
            searchQuery = query,
            selectedVerse = selected,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BibleReaderUiState()
    )




    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
        selectedVerse.value = null

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
        chapter: Int
    ) {
        val selectedBook = uiState.value.books.firstOrNull { it.book == book }
        selectedVerse.value = null
        searchQuery.value = ""
        request.value = ChapterRequest(
            translation = translation,
            book = selectedBook?.book ?: book,
            bookName = selectedBook?.bookName.orEmpty(),
            chapter = chapter.coerceAtLeast(1)
        )
    }

    fun loadBook(book: Int) {
        loadChapter(
            translation = uiState.value.request.translation,
            book = book,
            chapter = 1
        )
    }

    fun loadChapter(chapter: Int) {
        val current = uiState.value.request
        loadChapter(
            translation = current.translation,
            book = current.book,
            chapter = chapter
        )
    }

    fun openSearchResult(verse: VerseEntity) {
        request.value = ChapterRequest(
            translation = verse.translation,
            book = verse.book,
            bookName = verse.bookName,
            chapter = verse.chapter
        )
        selectedVerse.value = verse.verse
        searchQuery.value = ""
    }

    private fun shareVerse(verseId: Long) {
        // Implementation for sharing verse
        // This would typically use Android's share intent
    }


}




