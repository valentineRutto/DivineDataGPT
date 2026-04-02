package com.valentinerutto.divinedatagpt.ui.theme.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.valentinerutto.divinedatagpt.BibleUiEvent
import com.valentinerutto.divinedatagpt.BibleViewModel
import com.valentinerutto.divinedatagpt.data.models.BibleBook
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
                            contentPadding = PaddingValues(16.dp),
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


@Composable
fun BookSelectorDialog(
    books: List<BibleBook>,
    currentBook: String,
    onBookSelected: (BibleBook) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Book",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Close",
                            style = MaterialTheme.typography.labelLarge,
                            color = PurplePrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = DarkSurface, thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // Books List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Old Testament Section
                    item {
                        Text(
                            text = "Old Testament",
                            style = MaterialTheme.typography.titleMedium,
                            color = PurpleAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(books.filter { it.testament == "Old Testament" }) { book ->
                        BookItem(
                            book = book,
                            isSelected = book.name == currentBook,
                            onSelected = onBookSelected
                        )
                    }

                    // New Testament Section
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "New Testament",
                            style = MaterialTheme.typography.titleMedium,
                            color = PurpleAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(books.filter { it.testament == "New Testament" }) { book ->
                        BookItem(
                            book = book,
                            isSelected = book.name == currentBook,
                            onSelected = onBookSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookItem(
    book: BibleBook,
    isSelected: Boolean,
    onSelected: (BibleBook) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) PurplePrimary.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .clickable { onSelected(book) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = book.name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) PurpleAccent else TextPrimary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )

        Text(
            text = "${book.totalChapters} ch",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}

