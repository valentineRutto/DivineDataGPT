package com.valentinerutto.divinedatagpt.ui.theme.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.valentinerutto.divinedatagpt.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(viewModel: JournalViewModel = viewModel()) {

    viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    "Journal", fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onNewEntryTapped) {
                Icon(Icons.Filled.Add, contentDescription = "New Entry")
            }
        }
    ) {


    }