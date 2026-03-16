package com.valentinerutto.divinedatagpt.ui.theme.screens


import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.valentinerutto.divinedatagpt.ui.theme.DarkSurface
import com.valentinerutto.divinedatagpt.ui.theme.PurpleAccent
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextPrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextSecondary
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

@Composable
fun BibleTopBar(
    currentBook: String,
    currentChapter: Int,
    selectedTranslation: String,
    onBookClick: () -> Unit,
    onTranslationChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Row: Book Selector and Search
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Selector Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .clickable(onClick = onBookClick)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$currentBook $currentChapter",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select Book",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Search Button
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CardBackground)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Translation Selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TranslationChip(
                text = "NIV",
                isSelected = selectedTranslation == "NIV",
                onClick = { onTranslationChange("NIV") }
            )
            TranslationChip(
                text = "KJV",
                isSelected = selectedTranslation == "KJV",
                onClick = { onTranslationChange("KJV") }
            )
        }
    }
}

@Composable
fun BibleBottomBar(
    onHighlightClick: () -> Unit,
    onNoteClick: () -> Unit,
    onShareClick: () -> Unit,
    onHomeClick: () -> Unit,
    onJournalClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
    ) {
        // Action Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomActionButton(
                icon = Icons.Default.Bookmark,
                label = "HIGHLIGHT",
                onClick = onHighlightClick
            )
            BottomActionButton(
                icon = Icons.Default.Edit,
                label = "NOTE",
                onClick = onNoteClick
            )
            BottomActionButton(
                icon = Icons.Default.Share,
                label = "SHARE",
                onClick = onShareClick
            )
        }

        // Bottom Navigation Bar
        NavigationBar(
            containerColor = DarkSurface,
            contentColor = TextSecondary,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = false,
                onClick = onHomeClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = TextMuted
                    )
                },
                label = {
                    Text(
                        text = "HOME",
                        fontSize = 10.sp,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = true,
                onClick = { /* Already on Bible */ },
                icon = {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Bible",
                        tint = PurplePrimary
                    )
                },
                label = {
                    Text(
                        text = "BIBLE",
                        fontSize = 10.sp,
                        color = PurplePrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = false,
                onClick = onJournalClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.BorderColor,
                        contentDescription = "Journal",
                        tint = TextMuted
                    )
                },
                label = {
                    Text(
                        text = "JOURNAL",
                        fontSize = 10.sp,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = false,
                onClick = onSettingsClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextMuted
                    )
                },
                label = {
                    Text(
                        text = "SETTINGS",
                        fontSize = 10.sp,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun BottomActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = PurplePrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            ),
            color = TextPrimary
        )
    }
}

