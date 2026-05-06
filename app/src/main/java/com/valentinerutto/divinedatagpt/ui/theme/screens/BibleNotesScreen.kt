package com.valentinerutto.divinedatagpt.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinerutto.divinedatagpt.BibleViewModel
import com.valentinerutto.divinedatagpt.data.local.entity.bible.BibleNoteEntity
import com.valentinerutto.divinedatagpt.ui.theme.DarkSurface
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextSecondary
import com.valentinerutto.divinedatagpt.ui.theme.TextMuted
import org.koin.androidx.compose.koinViewModel

private val Ink = Color(0xFFF4EDF8)
private val MutedInk = Color(0xFF8C8294)
private val Page = Color(0xFF0E0714)
private val Panel = Color(0xFF1B1025)
private val Purple = Color(0xFFC15CFF)
private val HighlightColors = mapOf(
    "yellow" to Color(0xFFFFD166),
    "green" to Color(0xFF74D99F),
    "blue" to Color(0xFF7AB7FF),
    "pink" to Color(0xFFFF8FC7)
)

@Composable
fun BibleNotesRoute(
    onHomeClick: () -> Unit,
    onBibleClick: () -> Unit,
    onNotesClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BibleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BibleNotesScreen(
        notes = uiState.savedNotes,
        onHomeClick = onHomeClick,
        onBibleClick = onBibleClick,
        onNotesClick = onNotesClick,
        onSettingsClick = onSettingsClick,
        modifier = modifier
    )
}

@Composable
private fun BibleNotesScreen(
    notes: List<BibleNoteEntity>,
    onHomeClick: () -> Unit,
    onBibleClick: () -> Unit,
    onNotesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Page,
        bottomBar = {
            BibleNotesBottomNavBar(
                selectedTab = 2,
                onHomeClick = onHomeClick,
                onBibleClick = onBibleClick,
                onNotesClick = onNotesClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 26.dp)
        ) {
            item {
                Text(
                    text = "Notes",
                    color = Ink,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                    modifier = Modifier.padding(bottom = 18.dp)
                )
            }

            if (notes.isEmpty()) {
                item {
                    Text(
                        text = "No notes yet",
                        color = MutedInk,
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 36.dp)
                    )
                }
            } else {
                items(
                    items = notes,
                    key = { note -> note.id }
                ) { note ->
                    BibleNoteRow(note = note)
                }
            }
        }
    }
}

@Composable
private fun BibleNoteRow(note: BibleNoteEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(Panel, RoundedCornerShape(8.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "${note.bookName} ${note.chapter}:${note.verse}",
            color = note.highlightColor.toHighlightColor() ?: Purple,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = note.verseText,
            color = Ink,
            fontFamily = FontFamily.Serif,
            fontSize = 20.sp,
            lineHeight = 30.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = note.note.ifBlank { "No note added yet" },
            color = if (note.note.isBlank()) MutedInk else Ink,
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp,
            lineHeight = 26.sp,
            modifier = Modifier.padding(top = 14.dp)
        )
    }
}

@Composable
private fun BibleNotesBottomNavBar(
    selectedTab: Int,
    onHomeClick: () -> Unit,
    onBibleClick: () -> Unit,
    onNotesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextSecondary,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = onHomeClick,
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selectedTab == 0) PurplePrimary else TextMuted
                )
            },
            label = {
                Text(
                    "HOME",
                    fontSize = 10.sp,
                    color = if (selectedTab == 0) PurplePrimary else TextMuted
                )
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = onBibleClick,
            icon = {
                Icon(
                    Icons.Default.MenuBook,
                    contentDescription = "Bible",
                    tint = if (selectedTab == 1) PurplePrimary else TextMuted
                )
            },
            label = {
                Text(
                    "BIBLE",
                    fontSize = 10.sp,
                    color = if (selectedTab == 1) PurplePrimary else TextMuted
                )
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = onNotesClick,
            icon = {
                Icon(
                    Icons.Rounded.EditNote,
                    contentDescription = "Notes",
                    tint = if (selectedTab == 2) PurplePrimary else TextMuted
                )
            },
            label = {
                Text(
                    "NOTES",
                    fontSize = 10.sp,
                    color = if (selectedTab == 2) PurplePrimary else TextMuted
                )
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = onSettingsClick,
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (selectedTab == 3) PurplePrimary else TextMuted
                )
            },
            label = {
                Text(
                    "SETTINGS",
                    fontSize = 10.sp,
                    color = if (selectedTab == 3) PurplePrimary else TextMuted
                )
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
        )
    }
}

private fun String?.toHighlightColor(): Color? {
    return HighlightColors[this]
}
