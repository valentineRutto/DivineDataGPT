package com.valentinerutto.divinedatagpt.ui.theme.screens


import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.divinedatagpt.BibleUiEvent
import com.valentinerutto.divinedatagpt.BibleViewModel
import com.valentinerutto.divinedatagpt.data.models.BibleVerse
import com.valentinerutto.divinedatagpt.ui.theme.CardBackground
import com.valentinerutto.divinedatagpt.ui.theme.DarkBackground
import com.valentinerutto.divinedatagpt.ui.theme.PurpleAccent
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextPrimary
import com.valentinerutto.divinedatagpt.ui.theme.TextMuted
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

@Composable
fun VerseItem(
    verse: BibleVerse,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isHighlighted -> PurplePrimary.copy(alpha = 0.15f)
        verse.verse == 3 -> PurplePrimary.copy(alpha = 0.3f) // Example special highlight
        else -> Color.Transparent
    }

    val borderColor = if (verse.verse == 3) PurplePrimary else Color.Transparent
    val borderWidth = if (verse.verse == 3) 2.dp else 0.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Verse Number
        Text(
            text = "${verse.verse}",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            ),
            color = if (verse.verse == 3) PurpleAccent else TextMuted,
            modifier = Modifier.padding(top = 2.dp)
        )

        // Verse Text
        Text(
            text = verse.text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 26.sp
            ),
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ChapterHeader(
    bookName: String,
    chapterNumber: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = bookName,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary
        )
        Text(
            text = "Chapter $chapterNumber",
            style = MaterialTheme.typography.titleLarge,
            color = PurpleAccent
        )
    }
}

@Composable
fun TranslationChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PurplePrimary else CardBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}