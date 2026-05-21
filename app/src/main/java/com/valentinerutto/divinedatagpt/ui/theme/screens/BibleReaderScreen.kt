package com.valentinerutto.divinedatagpt.ui.theme.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.BorderColor
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinerutto.divinedatagpt.BibleReaderUiState
import com.valentinerutto.divinedatagpt.BibleViewModel
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import com.valentinerutto.divinedatagpt.data.models.BibleBook
import com.valentinerutto.divinedatagpt.ui.theme.DarkSurface
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextSecondary
import com.valentinerutto.divinedatagpt.ui.theme.TextMuted
import org.koin.androidx.compose.koinViewModel

private val Ink = Color(0xFFF4EDF8)
private val MutedInk = Color(0xFF8C8294)
private val Page = Color(0xFF0E0714)
private val TopBar = Color(0xFF050817)
private val Panel = Color(0xFF1B1025)
private val PanelSoft = Color(0xFF261132)
private val SearchPanel = Color(0xFF26202A)
private val Purple = Color(0xFFC15CFF)

private data class HighlightOption(
    val key: String,
    val color: Color
)

private val HighlightColors = listOf(
    HighlightOption("yellow", Color(0xFFFFD166)),
    HighlightOption("green", Color(0xFF74D99F)),
    HighlightOption("blue", Color(0xFF7AB7FF)),
    HighlightOption("pink", Color(0xFFFF8FC7))
)

@Composable
fun BibleReaderRoute(
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onBibleClick: () -> Unit,
    onNotesClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BibleViewModel = koinViewModel()
) {
    BibleReaderScreen(
        onHomeClick = onHomeClick,
        onSettingsClick = onSettingsClick,
        onBibleClick = onBibleClick,
        onNotesClick = onNotesClick,
        modifier = modifier,
        viewModel = viewModel
    )
}

@Composable
fun BibleReaderScreen(
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onBibleClick: () -> Unit,
    onNotesClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BibleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BibleReaderContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onVerseSelected = viewModel::onVerseSelected,
        onBookSelected = viewModel::loadBook,
        onChapterSelected = viewModel::loadChapter,
        onSearchResultSelected = viewModel::openSearchResult,
        onClearSelection = viewModel::clearSelection,
        onSaveBibleNote = viewModel::saveBibleNote,
        onHomeClick = onHomeClick,
        onBibleClick = onBibleClick,
        onNotesClick = onNotesClick,
        onSettingsClick = onSettingsClick,
        modifier = modifier
    )
}

@Composable
private fun BibleReaderContent(
    uiState: BibleReaderUiState,
    onSearchQueryChange: (String) -> Unit,
    onVerseSelected: (Int) -> Unit,
    onBookSelected: (Int) -> Unit,
    onChapterSelected: (Int) -> Unit,
    onSearchResultSelected: (VerseEntity) -> Unit,
    onClearSelection: () -> Unit,
    onSaveBibleNote: (VerseEntity, String, String) -> Unit,
    onHomeClick: () -> Unit,
    onBibleClick: () -> Unit,
    onNotesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    var selectedTab by remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    var noteEditorVerse by remember { mutableStateOf<VerseEntity?>(null) }
    var noteEditorColor by remember { mutableStateOf(HighlightColors.first().key) }
    val selectedVerse = uiState.verses.firstOrNull { verse ->
        verse.verse == uiState.selectedVerse
    }
    val activeNoteEditorVerse = noteEditorVerse

    if (activeNoteEditorVerse != null) {
        val savedNote = uiState.savedNotes.firstOrNull { note ->
            note.verseId == activeNoteEditorVerse.id
        }
        NoteEditorScreen(
            verse = activeNoteEditorVerse,
            initialNote = savedNote?.note.orEmpty(),
            highlightColor = noteEditorColor,
            onHighlightColorChange = { noteEditorColor = it },
            onSave = { note ->
                onSaveBibleNote(activeNoteEditorVerse, note, noteEditorColor)
                noteEditorVerse = null
                onClearSelection()
            },
            onBack = { noteEditorVerse = null },
            modifier = modifier
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Page,
        topBar = {
            ReaderTopBar(
                title = "${uiState.request.bookName} ${uiState.request.chapter}".trim(),
                books = uiState.books,
                chapters = uiState.availableChapters,
                selectedBook = uiState.request.book,
                selectedChapter = uiState.request.chapter,
                onBookSelected = onBookSelected,
                onChapterSelected = onChapterSelected
            )
        },
        bottomBar = {

            NavigationBar(
                containerColor = DarkSurface,
                contentColor = TextSecondary,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        onHomeClick()
                    },
                    icon = {
                        Icon(
                            Icons.Default.Home, contentDescription = "Home",
                            tint = if (selectedTab == 0) PurplePrimary else TextMuted
                        )
                    },
                    label = {
                        Text(
                            "HOME", fontSize = 10.sp,
                            color = if (selectedTab == 0) PurplePrimary else TextMuted
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        onBibleClick()
                    },
                    icon = {
                        Icon(
                            Icons.Default.MenuBook, contentDescription = "Bible",
                            tint = if (selectedTab == 1) PurplePrimary else TextMuted
                        )
                    },
                    label = {
                        Text(
                            "BIBLE", fontSize = 10.sp,
                            color = if (selectedTab == 1) PurplePrimary else TextMuted
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        onNotesClick()
                    },
                    icon = {
                        Icon(
                            Icons.Rounded.EditNote, contentDescription = "Notes",
                            tint = if (selectedTab == 2) PurplePrimary else TextMuted
                        )
                    },
                    label = {
                        Text(
                            "NOTES", fontSize = 10.sp,
                            color = if (selectedTab == 2) PurplePrimary else TextMuted
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )

//                NavigationBarItem(
//                    selected = selectedTab == 3,
//                    onClick = {
//                        selectedTab = 3
//                        onSettingsClick()
//
//                    },
//                    icon = {
//                        Icon(
//                            Icons.Default.Settings, contentDescription = "Settings",
//                            tint = if (selectedTab == 3) PurplePrimary else TextMuted
//                        )
//                    },
//                    label = {
//                        Text(
//                            "SETTINGS", fontSize = 10.sp,
//                            color = if (selectedTab == 3) PurplePrimary else TextMuted
//                        )
//                    },
//                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
//                )
            }
        }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 128.dp)
            ) {
                item {
                    SearchBox(
                        query = uiState.searchQuery,
                        onQueryChange = onSearchQueryChange,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 22.dp)
                    )
                }

                if (uiState.searchQuery.isNotBlank()) {
                    item {
                        SearchResultsHeader(
                            resultCount = uiState.searchResults.size
                        )
                    }
                    items(
                        items = uiState.searchResults,
                        key = { verse -> "search-${verse.id}" }
                    ) { verse ->
                        SearchResultRow(
                            verse = verse,
                            onClick = { onSearchResultSelected(verse) }
                        )
                    }
                } else if (uiState.isLoading) {
                    item {
                        Text(
                            text = "Loading...",
                            color = MutedInk,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    item {
                        ChapterHeader(
                            bookName = uiState.request.bookName,
                            chapter = uiState.request.chapter
                        )
                    }
                    items(
                        items = uiState.verses,
                        key = { verse -> verse.id }
                    ) { verse ->
                        VerseRow(
                            verse = verse,

                            isSelected = verse.verse == uiState.selectedVerse,
                            highlightColor = uiState.highlightedVerseColors[verse.id].toHighlightColor(),
                            onClick = { onVerseSelected(verse.verse) }
                        )
                    }
                }
            }

            if (selectedVerse != null) {
                val selectedNote = uiState.savedNotes.firstOrNull { note ->
                    note.verseId == selectedVerse.id
                }
                VerseActionBar(
                    onHighlightSelected = { option ->
                        onSaveBibleNote(
                            selectedVerse,
                            selectedNote?.note.orEmpty(),
                            option.key
                        )
                    },
                    onNote = {
                        noteEditorColor =
                            selectedNote?.highlightColor ?: HighlightColors.first().key
                        noteEditorVerse = selectedVerse
                    },
                    onShare = {
                        shareVerse(context, selectedVerse)
                    },
                    onDismiss = onClearSelection,

                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 56.dp)
                        .padding(horizontal = 40.dp)
                )
            }
        }
    }
}


@Composable
private fun SearchResultsHeader(resultCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Search Results",
            color = Ink,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp
        )
        Text(
            text = "$resultCount matches",
            color = Purple,
            fontFamily = FontFamily.Serif,
            fontSize = 13.sp,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
private fun NoteEditorScreen(
    verse: VerseEntity,
    initialNote: String,
    highlightColor: String,
    onHighlightColorChange: (String) -> Unit,
    onSave: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var noteDraft by remember(verse.id) { mutableStateOf(initialNote) }
    val wordCount = noteDraft.trim()
        .split(Regex("\\s+"))
        .count { word -> word.isNotBlank() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Page,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TopBar)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cancel",
                    color = MutedInk,
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable(onClick = onBack)
                )
                Text(
                    text = "Note",
                    color = Ink,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Save",
                    color = Purple,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onSave(noteDraft) }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            highlightColor.toHighlightColor() ?: PanelSoft,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(18.dp)
                ) {
                    Text(
                        text = "${verse.bookName} ${verse.chapter}:${verse.verse}",
                        color = Page,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = verse.text,
                        color = Page,
                        fontFamily = FontFamily.Serif,
                        fontSize = 23.sp,
                        lineHeight = 34.sp,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    HighlightColors.forEach { option ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(option.color, RoundedCornerShape(18.dp))
                                .then(
                                    if (option.key == highlightColor) {
                                        Modifier.drawBehind {
                                            drawCircle(
                                                color = Ink,
                                                radius = 22.dp.toPx(),
                                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                                    width = 2.dp.toPx()
                                                )
                                            )
                                        }
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { onHighlightColorChange(option.key) }
                        )
                    }
                }
            }

            item {
                TextField(
                    value = noteDraft,
                    onValueChange = { noteDraft = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 260.dp)
                        .padding(top = 22.dp),
                    placeholder = {
                        Text(
                            text = "Write your note...",
                            color = MutedInk,
                            fontFamily = FontFamily.Serif
                        )
                    },
                    textStyle = TextStyle(
                        color = Ink,
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        lineHeight = 30.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Panel,
                        unfocusedContainerColor = Panel,
                        focusedIndicatorColor = Purple,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Purple
                    )
                )
            }

            item {
                Text(
                    text = "$wordCount words",
                    color = MutedInk,
                    fontFamily = FontFamily.Serif,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun ReaderTopBar(
    title: String,
    books: List<BibleBook>,
    chapters: List<Int>,
    selectedBook: Int,
    selectedChapter: Int,
    onBookSelected: (Int) -> Unit,
    onChapterSelected: (Int) -> Unit
) {
    var pickerExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBar)
            .statusBarsPadding()
            .height(84.dp)
            .padding(horizontal = 44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = books.isNotEmpty()) {
                    pickerExpanded = true
                }
        ) {
            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title.ifBlank { "Bible" },
                        color = Ink,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Change book or chapter",
                        tint = Purple,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(22.dp)
                    )
                }

                BiblePickerMenu(
                    expanded = pickerExpanded,
                    books = books,
                    chapters = chapters,
                    selectedBook = selectedBook,
                    selectedChapter = selectedChapter,
                    onDismiss = { pickerExpanded = false },
                    onBookSelected = onBookSelected,
                    onChapterSelected = { chapter ->
                        onChapterSelected(chapter)
                        pickerExpanded = false
                    }
                )
            }
        }

//        Icon(
//            imageVector = Icons.Rounded.AutoAwesome,
//            contentDescription = "Open assistant",
//            tint = Purple,
//            modifier = Modifier.size(30.dp)
//        )
    }
}

@Composable
private fun BiblePickerMenu(
    expanded: Boolean,
    books: List<BibleBook>,
    chapters: List<Int>,
    selectedBook: Int,
    selectedChapter: Int,
    onDismiss: () -> Unit,
    onBookSelected: (Int) -> Unit,
    onChapterSelected: (Int) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier
            .background(Panel)
            .width(360.dp)
            .heightIn(max = 420.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .padding(vertical = 8.dp)
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(344.dp)
                    .verticalScroll(rememberScrollState())
            ) {


                books.forEach { book ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = book.bookName,
                                color = if (book.book == selectedBook) Purple else Ink
                            )
                        },
                        onClick = {
                            onBookSelected(book.book)
                        }
                    )
                }

            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(344.dp)
                    .background(Color(0xFF33273A))
            )

            Column(
                modifier = Modifier
                    .width(104.dp)
                    .height(344.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                chapters.forEach { chapter ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = chapter.toString(),
                                color = if (chapter == selectedChapter) Purple else Ink
                            )
                        },
                        onClick = {
                            onChapterSelected(chapter)
                        }
                    )
                }
            }

        }
    }
}

@Composable
private fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        placeholder = {
            Text(
                text = "Search by emotion or keyword...",
                color = MutedInk,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MutedInk
            )
        },
        shape = RoundedCornerShape(34.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SearchPanel,
            unfocusedContainerColor = SearchPanel,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Purple,
            focusedTextColor = Ink,
            unfocusedTextColor = Ink
        ),
        textStyle = TextStyle(
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(66.dp)
    )
}

@Composable
private fun ChapterHeader(
    bookName: String,
    chapter: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp, bottom = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = bookName,
            color = Ink,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 42.sp
        )
        Text(
            text = "C H A P T E R  $chapter",
            color = Purple,
            fontFamily = FontFamily.Serif,
            fontSize = 14.sp,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(top = 14.dp)
        )
    }
}

@Composable
private fun VerseRow(
    verse: VerseEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    highlightColor: Color?,
) {
    val shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
    val leftAccent = highlightColor ?: Purple
    val rowBackground = when {
        isSelected -> PanelSoft
        highlightColor != null -> highlightColor.copy(alpha = 0.18f)
        else -> null
    }
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    fontSize = 14.sp,
                    baselineShift = BaselineShift.Superscript,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(verse.verse.toString())
            }
            append(" ${verse.text}")
        },
        color = Ink,
        fontFamily = FontFamily.Serif,
        fontSize = 26.sp,
        lineHeight = 44.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(bottom = 16.dp)
            .then(
                if (rowBackground != null) {
                    Modifier
                        .background(rowBackground, shape)
                        .drawBehind {
                            drawLine(
                                color = leftAccent,
                                start = Offset(2.dp.toPx(), 0f),
                                end = Offset(2.dp.toPx(), size.height),
                                strokeWidth = 3.dp.toPx()
                            )
                            drawCircle(
                                color = leftAccent,
                                radius = 6.dp.toPx(),
                                center = Offset(2.dp.toPx(), size.height / 2f)
                            )
                        }
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(
                start = if (isSelected) 24.dp else 22.dp,
                end = 18.dp,
                top = if (isSelected) 18.dp else 0.dp,
                bottom = if (isSelected) 18.dp else 0.dp
            )
    )
}

@Composable
private fun SearchResultRow(
    verse: VerseEntity,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp, vertical = 8.dp)
            .background(Panel, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Text(
            text = "${verse.bookName} ${verse.chapter}:${verse.verse}",
            color = Purple,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Text(
            text = verse.text,
            color = Ink,
            fontFamily = FontFamily.Serif,
            fontSize = 21.sp,
            lineHeight = 32.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun VerseActionBar(
    onHighlightSelected: (HighlightOption) -> Unit,
    onNote: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showHighlightColors by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showHighlightColors) {
            Row(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .background(Panel, RoundedCornerShape(28.dp))
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HighlightColors.forEach { option ->
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(option.color, RoundedCornerShape(17.dp))
                            .clickable {
                                onHighlightSelected(option)
                                showHighlightColors = false
                            }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(Panel, RoundedCornerShape(36.dp))
                .padding(horizontal = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionItem(
                icon = Icons.Rounded.BorderColor,
                label = "Highlight",
                onClick = { showHighlightColors = !showHighlightColors }
            )
            ActionDivider()
            ActionItem(
                icon = Icons.Rounded.EditNote,
                label = "Note",
                onClick = onNote
            )
            ActionDivider()
            ActionItem(
                icon = Icons.Rounded.Share,
                label = "Share",
                onClick = onShare
            )
        }
    }

}

@Composable
private fun ActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFFF0B8FF),
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = Ink,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
private fun ActionDivider() {
    Box(
        modifier = Modifier
            .height(34.dp)
            .width(1.dp)
            .background(Color(0xFF33273A))
    )
}

private fun shareVerse(
    context: android.content.Context,
    verse: VerseEntity
) {
    val verseText = "${verse.bookName} ${verse.chapter}:${verse.verse}\n${verse.text}"
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, verseText)
    }
    context.startActivity(
        Intent.createChooser(sendIntent, "Share verse")
    )
}

private fun String?.toHighlightColor(): Color? {
    return HighlightColors.firstOrNull { option ->
        option.key == this
    }?.color
}

@Composable
private fun ReaderBottomBar(
    onHomeClick: () -> Unit,
    onBibleClick: () -> Unit,
    onSettingsClick: () -> Unit
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBar)
            .navigationBarsPadding()
            .height(90.dp)
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomNavItem(
            icon = Icons.Rounded.Home,
            label = "HOME",
            selected = false,
            onClick = onHomeClick
        )
        BottomNavItem(
            icon = Icons.Rounded.Book,
            label = "BIBLE",
            selected = true,
            onClick = onBibleClick
        )

        BottomNavItem(
            icon = Icons.Rounded.Settings,
            label = "SETTINGS",
            selected = false,
            onClick = onSettingsClick
        )
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) Color(0xFFE1A4FF) else Color(0xFF79869A)
    val itemModifier = if (selected) {
        Modifier.background(Panel, RoundedCornerShape(18.dp))
    } else {
        Modifier
    }

    Column(
        modifier = itemModifier
            .width(82.dp)
            .height(66.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(27.dp)
        )
        Text(
            text = label,
            color = tint,
            fontFamily = FontFamily.Serif,
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}
