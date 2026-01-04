package com.valentinerutto.divinedatagpt.ui.theme.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.divinedatagpt.DivineDataViewModel
import com.valentinerutto.divinedatagpt.R
import com.valentinerutto.divinedatagpt.UiState
import com.valentinerutto.divinedatagpt.ui.theme.TextWhite
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmotionScreen(modifier: Modifier) {

    val emotionsList = listOf(
        Emotions("Anxious", Icons.Default.Air),
        Emotions("Joyful", Icons.Default.WbSunny),
        Emotions("Lonely", Icons.Default.Nightlight),
        Emotions("Grateful", Icons.Default.Favorite),
        Emotions("Angry", Icons.Default.Whatshot),
        Emotions("Lost", Icons.Default.Explore)
    )

    var selectedEmotion by remember { mutableStateOf("") }
    val myViewModel: DivineDataViewModel = koinViewModel()
    val uiState by myViewModel.uiState.collectAsState()


    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { padding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "HOW IS YOUR SPIRIT TODAY?",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Select an emotion or share your own thoughts.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            EmotionGrid(uiState,{ selectedEmotion = it },emotionsList)


        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF4F4F4)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextField(
                value = selectedEmotion,
                onValueChange = { selectedEmotion = it },
                placeholder = { Text("I'm feeling...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    if (selectedEmotion.isNotEmpty()) {

IconButton(onClick = {selectedEmotion = ""}) {

    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
}
                    }
}
                ,
                enabled = uiState !is UiState.Loading,

                singleLine = true,
                modifier = Modifier.weight(1f)
            )

           Button(

               onClick = {
                   if (selectedEmotion.isNotEmpty()) {
                       myViewModel.sendFeeling(selectedEmotion)
                   }
               },
               modifier = Modifier
                   .padding(end = 8.dp)
                   .height(48.dp),
               enabled = selectedEmotion.isNotEmpty() && uiState !is UiState.Loading,
               colors = ButtonDefaults.buttonColors(
                   containerColor = Color(0xFFFFA726)
               ),
               shape = CircleShape
            ) {

                when (uiState) {
                    is UiState.Loading -> {

                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thinking...")
                    }
                    else -> {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Think",
                            tint = Color.White
                        )
                    }
                }
            }
        }
            Spacer(modifier = Modifier.height(40.dp))

            when (val state = uiState) {

                is UiState.Success -> {
                    Snackbar(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(state.message)

                    }
                }
                is UiState.Error -> {
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(state.message)
                    }
                }
                else -> {
                    MaterialTheme.colorScheme.surface
                }
            }


        }
}}
data class Emotions(val name: String, val icon: ImageVector)

@Composable
fun EmotionGrid(uiState: UiState, onEmotionSelected: (String) -> Unit, list: List<Emotions>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.height(300.dp)
    ) {
        items(list) { emotion ->
            Card(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .clickable(
                        enabled = uiState !is UiState.Loading
                    ){
                        onEmotionSelected("I'm feeling "+emotion.name)
                              }
                ,
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(5.dp)



            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        emotion.icon,
                        contentDescription = emotion.name,
                        tint = Color(0xFF6B7890),
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = emotion.name,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7890),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.SelfImprovement, contentDescription = "Reflect") },
            label = { Text("Reflect") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Chat, contentDescription = "Companion") },
            label = { Text("Companion") }
        )
    }
}


data class Emotion(
    val label: String,
    val iconName: String, // e.g., "SentimentSatisfied"
    val tintHex: String = "#5B6572"
)

fun defaultEmotions(): List<Emotion> = listOf(
    Emotion("Anxious", iconName = "SentimentDissatisfied"),
    Emotion("Joyful", iconName = "SentimentSatisfied", tintHex = "#0EA5E9"),
    Emotion("Lonely", iconName = "PersonOutline"),
    Emotion("Grateful", iconName = "FavoriteBorder", tintHex = "#EF6C00"),
    Emotion("Angry", iconName = "SentimentVeryDissatisfied", tintHex = "#D32F2F"),
    Emotion("Lost", iconName = "ExploreOff")
)

data class SpiritUiModel(
    val appTitle: String = "DIVINEDATA",
    val headerTitle: String = "HOW IS YOUR SPIRIT TODAY?",
    val headerSubtitle: String = "Select an emotion or share your own thoughts.",
    val emotions: List<Emotion> = defaultEmotions(),
    val inputPlaceholder: String = "I'm feeling...",
    val primaryActionHex: String = "#FFA000" // orange send button color
)


fun sampleSpiritContent(): SpiritUiModel = SpiritUiModel()