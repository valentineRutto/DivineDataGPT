package com.valentinerutto.divinedatagpt.ui.theme.screens


import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinerutto.divinedatagpt.BibleViewModel
import com.valentinerutto.divinedatagpt.ui.theme.DarkBackground
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleScreen(
    viewModel: BibleViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToJournal: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            BibleTopBar(
                currentBook = uiState.currentBook,
                currentChapter = uiState.currentChapter,
                selectedTranslation = uiState.selectedTranslation,
                onBookClick = { viewModel.onEvent(BibleUiEvent.ToggleBookSelector) },
                onTranslationChange = { viewModel.onEvent(BibleUiEvent.SelectTranslation(it)) },
                onSearchClick = { viewModel.onEvent(BibleUiEvent.ToggleSearch) }
            )
        },
        bottomBar = {
            BibleBottomBar(
                onHighlightClick = { /* Handle highlight */ },
                onNoteClick = { /* Handle note */ },
                onShareClick = { /* Handle share */ },
                onHomeClick = onNavigateToHome,
                onJournalClick = onNavigateToJournal,
                onSettingsClick = onNavigateToSettings
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingScreen()
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error!!,
                        onRetry = {
                            viewModel.onEvent(
                                BibleUiEvent.LoadChapter(
                                    uiState.currentBook,
                                    uiState.currentChapter
                                )
                            )
                        }
                    )
                }

                uiState.verses.isNotEmpty() -> {
                    Column {
                        // Search Bar
                        AnimatedVisibility(
                            visible = uiState.showSearch,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            SearchBar(
                                query = uiState.searchQuery,
                                onQueryChange = { viewModel.onEvent(BibleUiEvent.SearchQuery(it)) },
                                onClose = { viewModel.onEvent(BibleUiEvent.ToggleSearch) }
                            )
                        }

                        // Verses List
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(
                                horizontal = 20.dp,
                                vertical = 16.dp,
                                bottom = 80.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Chapter Header
                            item {
                                ChapterHeader(
                                    bookName = uiState.currentBook,
                                    chapterNumber = uiState.currentChapter
                                )
                            }

                            // Verses
                            items(
                                items = uiState.verses,
                                key = { it.id }
                            ) { verse ->
                                VerseItem(
                                    verse = verse,
                                    isHighlighted = verse.id == uiState.highlightedVerseId,
                                    onClick = {
                                        viewModel.onEvent(BibleUiEvent.HighlightVerse(verse.id))
                                    },
                                    onLongClick = {
                                        // Show verse options menu
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Book Selector Dialog
    if (uiState.showBookSelector) {
        BookSelectorDialog(
            books = uiState.books,
            currentBook = uiState.currentBook,
            onBookSelected = { book ->
                viewModel.onEvent(BibleUiEvent.LoadChapter(book.name, 1))
                viewModel.onEvent(BibleUiEvent.ToggleBookSelector)
            },
            onDismiss = { viewModel.onEvent(BibleUiEvent.ToggleBookSelector) }
        )
    }
}