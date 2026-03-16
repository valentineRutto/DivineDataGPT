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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.divinedatagpt.DivineDataViewModel
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleBookEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleVerseEntity2
import com.valentinerutto.divinedatagpt.ui.theme.CardBackground
import com.valentinerutto.divinedatagpt.ui.theme.DarkBackground
import com.valentinerutto.divinedatagpt.ui.theme.DarkSurface
import com.valentinerutto.divinedatagpt.ui.theme.PurpleAccent
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.TextMuted
import com.valentinerutto.divinedatagpt.ui.theme.TextPrimary
import com.valentinerutto.divinedatagpt.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleScreen(
    viewModel: DivineDataViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit,
) {

    val uiState by viewModel.bibleuistate.collectAsState()
    var showBookSelector by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = { BibleBottomBar() },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {

            BibleTopBar(
                    currentBook = uiState.currentBook,
                    currentChapter = uiState.currentChapter,
                    selectedTranslation = selectedTranslation,
                    onBookClick = { showBookSelector = true },
                    onTranslationChange = { selectedTranslation = it },
                    onSearchClick = { showSearch = !showSearch }
                )

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
        }
    )
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
    verse: BibleVerseEntity2,
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
            text = verse.kingJamesBibleKjv ?: "",
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

@Composable
fun BookItem(
    book: BibleBookEntity,
    isSelected: Boolean,
    onSelected: (BibleBookEntity) -> Unit
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
                onClick = { onNavigateToHome },
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
    icon: ImageVector,
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
    books: List<BibleBookEntity>,
    currentBook: String,
    onBookSelected: (BibleBookEntity) -> Unit,
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
fun BottomNavigationBar(
    currentBook: String,
    currentChapter: Int,
    totalChapters: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onHighlight: () -> Unit,
    onNote: () -> Unit,
    onShare: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column {
        // Chapter Progress
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Previous",
                    tint = TextPrimary
                )
            }
            Text(
                "$currentChapter / $totalChapters",
                color = TextSecondary,
                fontSize = 14.sp
            )
            IconButton(onClick = onNext) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Next",
                    tint = TextPrimary
                )
            }
        }

        // Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(
                icon = Icons.Default.Bookmark,
                label = "HIGHLIGHT",
                onClick = onHighlight
            )
            ActionButton(icon = Icons.Default.Edit, label = "NOTE", onClick = onNote)
            ActionButton(icon = Icons.Default.Share, label = "SHARE", onClick = onShare)
        }

        NavigationBar(
            containerColor = DarkSurface,
            contentColor = TextSecondary,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = false,
                onClick = onNavigateToHome,
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        tint = TextMuted
                    )
                },
                label = { Text("HOME", fontSize = 10.sp, color = TextMuted) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
            NavigationBarItem(
                selected = true,
                onClick = { /* Already on Bible */ },
                icon = {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = "Bible",
                        tint = PurplePrimary
                    )
                },
                label = { Text("BIBLE", fontSize = 10.sp, color = PurplePrimary) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )

            NavigationBarItem(
                selected = false,
                onClick = onNavigateToSettings,
                icon = {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextMuted
                    )
                },
                label = { Text("SETTINGS", fontSize = 10.sp, color = TextMuted) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }

    }
}


@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = PurplePrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(label, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
