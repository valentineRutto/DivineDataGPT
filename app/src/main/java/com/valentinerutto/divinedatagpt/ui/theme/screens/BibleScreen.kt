package com.valentinerutto.divinedatagpt.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.divinedatagpt.BibleViewModel
import com.valentinerutto.divinedatagpt.ui.theme.DarkBackground
import com.valentinerutto.divinedatagpt.ui.theme.DarkSurface
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.TextMuted
import com.valentinerutto.divinedatagpt.ui.theme.TextPrimary
import com.valentinerutto.divinedatagpt.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleScreen(
    viewModel: BibleViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToJournal: () -> Unit
) {
    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                        }
                    ) {
                        Text(
                            "john 3:16",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }


                })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                Text("this is the bible screen")
                BottomNavigationBar(
                    onNavigateToHome = onNavigateToHome,
                    onNavigateToJournal = onNavigateToJournal,
                    onNavigateToSettings = onNavigateToHome,
                    currentBook = "John",
                    currentChapter = 3,
                    totalChapters = 10,
                    onPrevious = {},
                    onNext = { },
                    onHighlight = { },
                    onNote = {},
                    onShare = { }
                )
            }
        }
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
    onNavigateToJournal: () -> Unit,
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
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = TextPrimary)
            }
            Text(
                "$currentChapter / $totalChapters",
                color = TextSecondary,
                fontSize = 14.sp
            )
            IconButton(onClick = onNext) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = TextPrimary)
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
            ActionButton(icon = Icons.Default.Bookmark, label = "HIGHLIGHT", onClick = onHighlight)
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
                icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = TextMuted) },
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
                onClick = onNavigateToJournal,
                icon = {
                    Icon(
                        Icons.Default.BorderColor,
                        contentDescription = "Journal",
                        tint = TextMuted
                    )
                },
                label = { Text("JOURNAL", fontSize = 10.sp, color = TextMuted) },
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