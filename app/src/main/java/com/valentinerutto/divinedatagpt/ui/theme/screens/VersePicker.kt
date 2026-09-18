package com.valentinerutto.divinedatagpt.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinerutto.divinedatagpt.BibleViewModel
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import com.valentinerutto.divinedatagpt.data.models.VerseCitation
import org.koin.androidx.compose.koinViewModel

/**
 * A small verse picker that re-uses the BibleViewModel to let the user pick a verse.
 * When a verse is tapped the picker returns a `VerseCitation` via [onPick].
 */
@Composable
fun VersePicker(
    modifier: Modifier = Modifier,
    viewModel: BibleViewModel = koinViewModel(),
    onPick: (VerseCitation) -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            TextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.onSearchQueryChange(it)
                },
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                placeholder = { Text("Search verses") },
                colors = TextFieldDefaults.colors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(56.dp)
            )

            LazyColumn(contentPadding = PaddingValues(12.dp)) {
                val list = if (query.isNotBlank()) uiState.searchResults else uiState.verses
                items(list, key = { v -> v.id }) { verse ->
                    VersePickerRow(verse = verse, onClick = {
                        onPick(
                            VerseCitation(
                                id = verse.id,
                                book = verse.book,
                                chapter = verse.chapter,
                                verse = verse.verse,
                                text = verse.text,
                                translation = verse.translation
                            )
                        )
                        onDismiss()
                    })
                }
            }
        }
    }
}

@Composable
private fun VersePickerRow(
    verse: VerseEntity,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "${verse.bookName} ${verse.chapter}:${verse.verse}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(text = verse.text, style = MaterialTheme.typography.bodyLarge)
    }
}
