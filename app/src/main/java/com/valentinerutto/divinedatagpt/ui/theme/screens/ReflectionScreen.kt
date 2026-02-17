package com.valentinerutto.divinedatagpt.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.divinedatagpt.DivineDataViewModel
import com.valentinerutto.divinedatagpt.data.network.ai.model.ChatMessage
import com.valentinerutto.divinedatagpt.ui.theme.AiBubble
import com.valentinerutto.divinedatagpt.ui.theme.CardBackground
import com.valentinerutto.divinedatagpt.ui.theme.DarkBackground
import com.valentinerutto.divinedatagpt.ui.theme.EmotionCard
import com.valentinerutto.divinedatagpt.ui.theme.PurpleAccent
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextPrimary
import com.valentinerutto.divinedatagpt.ui.theme.TextMuted
import com.valentinerutto.divinedatagpt.ui.theme.UserBubble
import org.koin.androidx.compose.koinViewModel

val quickReplies =
    listOf("I feel anxious", "Seeking peace", "Gratitude", "Feeling lost", "Need strength")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflectionScreen(
    emotion: String,
    onBack: () -> Unit,
    onViewDaily: () -> Unit,
    viewModel: DivineDataViewModel = koinViewModel()
) {
    val uiState by viewModel.reflectionuistate.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    rememberCoroutineScope()

    LaunchedEffect(emotion) { viewModel.initWithEmotion(emotion) }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Divine Reflection",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "DIVINEDATA AI",
                            color = PurpleAccent,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ChevronLeft, contentDescription = "Back",
                            tint = TextPrimary, modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.MoreHoriz,
                            contentDescription = "More",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Chat Messages ────────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.messages) { message ->
                    ChatBubblereflection(message)
                }
                if (uiState.isLoading) {
                    item { TypingIndicator() }
                }
            }

            // ── Quick Replies ────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(quickReplies) { reply ->
                    QuickReplyChip(text = reply) {
                        viewModel.sendMessage(reply)
                    }
                }
            }

            // ── Input Bar ────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Add, contentDescription = "Add",
                        tint = TextMuted, modifier = Modifier.size(24.dp)
                    )
                }
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text("Speak from the heart...", color = TextMuted, fontSize = 14.sp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        }
                    ),
                    singleLine = false,
                    maxLines = 4
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary)
                        .clickable {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Send, contentDescription = "Send",
                        tint = TextPrimary, modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubblereflection(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        if (!message.isUser) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✦", color = TextPrimary, fontSize = 14.sp)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "DivineData AI", color = TextMuted, fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                            .background(AiBubble)
                            .padding(16.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Column {
                            if (message.verse != null) {
                                Text(
                                    text = "\"${message.verse}\" (${message.reference})",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 22.sp
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            Text(
                                message.content,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        } else {
            Row(verticalAlignment = Alignment.Bottom) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "You", color = TextMuted, fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp, 4.dp, 18.dp, 18.dp))
                            .background(UserBubble)
                            .padding(16.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(
                            message.content,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CardBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun QuickReplyChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CardBackground)
            .border(1.dp, EmotionCard, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text, color = TextPrimary, fontSize = 13.sp)
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("✦", color = TextPrimary, fontSize = 14.sp)
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                    .background(AiBubble)
                    .padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TextMuted)
                        )
                    }
                }
            }
        }
    }
}