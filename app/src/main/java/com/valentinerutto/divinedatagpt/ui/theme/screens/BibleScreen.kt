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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.divinedatagpt.BibleViewModel
import com.valentinerutto.divinedatagpt.ui.theme.DarkBackground
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleScreen(
    viewModel: BibleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBookSelector by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var selectedTranslation by remember { mutableStateOf("NIV") }
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            BibleBottomBar()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top Bar
            BibleTopBar(
                currentBook = uiState.currentBook,
                currentChapter = uiState.currentChapter,
                selectedTranslation = selectedTranslation,
                onBookClick = { showBookSelector = true },
                onTranslationChange = { selectedTranslation = it },
                onSearchClick = { showSearch = !showSearch }
            )

            // Search Bar
            AnimatedVisibility(
                visible = showSearch,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.searchBible(it) },
                    onClose = { showSearch = false }
                )
            }

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        LoadingScreen()
                    }

                    uiState.error != null -> {
                        ErrorContent(
                            error = uiState.error!!,
                            onRetry = {
                                viewModel.loadChapter(
                                    uiState.currentBook,
                                    uiState.currentChapter
                                )
                            }
                        )
                    }

                    uiState.verses.isNotEmpty() -> {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Chapter Header
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = uiState.currentBook,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Chapter ${uiState.currentChapter}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = PurpleAccent
                                    )
                                }
                            }

                            // Verses
                            items(
                                items = uiState.verses,
                                key = { it.id }
                            ) { verse ->
                                VerseItem(
                                    verse = verse,
                                    isHighlighted = verse.verse == uiState.highlightedVerse,
                                    onClick = { viewModel.highlightVerse(verse.verse) },
                                    onLongClick = { /* Show verse menu */ }
                                )
                            }

                            // Bottom spacing
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Book Selector Dialog
    if (showBookSelector) {
        BookSelectorDialog(
            books = uiState.books,
            currentBook = uiState.currentBook,
            onBookSelected = { book ->
                viewModel.loadChapter(book.bookName, 1)
                showBookSelector = false
            },
            onDismiss = { showBookSelector = false }
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
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Book Selector and Search
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Selector
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

            // Search Icon
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
                    tint = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
fun TranslationChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
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
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )

        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Search by emotion or keyword...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = TextMuted
            )
        }
    }
}

@Composable
fun VerseItem(
    verse: BibleVerseEntity,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val backgroundColor = when {
        isHighlighted -> PurplePrimary.copy(alpha = 0.15f)
        verse.verse == 3 -> PurplePrimary.copy(alpha = 0.3f) // Special highlight like in image
        else -> Color.Transparent
    }

    val borderColor = if (verse.verse == 3) PurplePrimary else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = if (verse.verse == 3) 2.dp else 0.dp,
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
fun BibleBottomBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
    ) {
        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomActionButton(
                icon = Icons.Default.Bookmark,
                label = "HIGHLIGHT",
                onClick = { /* Handle highlight */ }
            )
            BottomActionButton(
                icon = Icons.Default.Edit,
                label = "NOTE",
                onClick = { /* Handle note */ }
            )
            BottomActionButton(
                icon = Icons.Default.Share,
                label = "SHARE",
                onClick = { /* Handle share */ }
            )
        }

        // Bottom Navigation
        NavigationBar(
            containerColor = DarkSurface,
            contentColor = TextSecondary,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = false,
                onClick = { /* Navigate to Home */ },
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        tint = TextMuted
                    )
                },
                label = {
                    Text(
                        "HOME",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = true,
                onClick = { },
                icon = {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = "Bible",
                        tint = PurplePrimary
                    )
                },
                label = {
                    Text(
                        "BIBLE",
                        fontSize = 10.sp,
                        color = PurplePrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = false,
                onClick = { /* Navigate to Journal */ },
                icon = {
                    Icon(
                        Icons.Default.BorderColor,
                        contentDescription = "Journal",
                        tint = TextMuted
                    )
                },
                label = {
                    Text(
                        "JOURNAL",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )

            NavigationBarItem(
                selected = false,
                onClick = { /* Navigate to Settings */ },
                icon = {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextMuted
                    )
                },
                label = {
                    Text(
                        "SETTINGS",
                        fontSize = 10.sp,
                        color = TextMuted
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
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
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
    books: List<com.divinemirror.app.data.local.entity.BibleBookEntity>,
    currentBook: String,
    onBookSelected: (com.divinemirror.app.data.local.entity.BibleBookEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Select Book",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Old Testament
                item {
                    Text(
                        "Old Testament",
                        style = MaterialTheme.typography.titleMedium,
                        color = PurpleAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(books.filter { it.testament == "Old Testament" }) { book ->
                    BookItem(
                        book = book,
                        isSelected = book.bookName == currentBook,
                        onSelected = onBookSelected
                    )
                }

                // New Testament
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "New Testament",
                        style = MaterialTheme.typography.titleMedium,
                        color = PurpleAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(books.filter { it.testament == "New Testament" }) { book ->
                    BookItem(
                        book = book,
                        isSelected = book.bookName == currentBook,
                        onSelected = onBookSelected
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Close",
                    style = MaterialTheme.typography.labelLarge,
                    color = PurplePrimary
                )
            }
        },
        containerColor = CardBackground
    )
}

@Composable
fun BookItem(
    book: com.divinemirror.app.data.local.entity.BibleBookEntity,
    isSelected: Boolean,
    onSelected: (com.divinemirror.app.data.local.entity.BibleBookEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PurplePrimary.copy(0.2f) else Color.Transparent)
            .clickable { onSelected(book) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = book.bookName,
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

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⚠️ $error",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary
                )
            ) {
                Text(
                    "Retry",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}