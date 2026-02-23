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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.divinedatagpt.BibleViewModel
import com.valentinerutto.divinedatagpt.ui.theme.DarkBackground
import com.valentinerutto.divinedatagpt.ui.theme.DarkSurface
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.TextPrimary
import com.valentinerutto.divinedatagpt.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleScreen(
    viewModel: BibleViewModel = koinViewModel()
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
                Text("thisis the bible screen")
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
    onShare: () -> Unit
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