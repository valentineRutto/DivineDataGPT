package com.valentinerutto.divinedatagpt.ui.theme.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinerutto.divinedatagpt.DivineDataViewModel
import com.valentinerutto.divinedatagpt.UiState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// Data models
data class Message(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class Reflection(
    val id: String,
    val quote: String,
    val quoteSource: String,
    val reflection: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SuggestionChip(
    val text: String,
    val icon: String? = null
)

// Color scheme
object ReflectionTheme {
    val Background = Color(0xFF1A1625)
    val Surface = Color(0xFF2D2838)
    val Primary = Color(0xFF7C3AED)
    val PrimaryVariant = Color(0xFF6D28D9)
    val UserMessage = Color(0xFF7C3AED)
    val AIMessage = Color(0xFF2D2838)
    val TextPrimary = Color(0xFFE5E7EB)
    val TextSecondary = Color(0xFF9CA3AF)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Divinereflectionscreen(
    messages: List<Message> = emptyList(),
    reflections: List<Reflection> = emptyList(),
    suggestions: List<SuggestionChip> = listOf(

        SuggestionChip("I feel anxious"),
        SuggestionChip("Seeking peace"),
        SuggestionChip("Gratitude"),
        SuggestionChip("Forgiveness")

    ),
    onSendMessage: (String) -> Unit = {},
    onShareReflection: (Reflection) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val myViewModel: DivineDataViewModel = koinViewModel()
    val uiState by myViewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    // Listen to state changes and show snackbar
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            is UiState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        duration = SnackbarDuration.Long
                    )
                }
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Divine Reflection",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ReflectionTheme.TextPrimary
                        )
                        Text(
                            text = "DIVINEDATA AI",
                            fontSize = 12.sp,
                            color = ReflectionTheme.Primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ReflectionTheme.TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = ReflectionTheme.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ReflectionTheme.Background
                )
            )
        },
        containerColor = ReflectionTheme.Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Chat messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Initial greeting
                item {
                    AIMessageBubble(
                        content = "Welcome to your reflection space.\nHow is your soul feeling today?",
                        showAvatar = true
                    )
                }

                // Messages and reflections
                items(messages.size) { index ->
                    val message = messages[index]

                    if (message.isUser) {
                        UserMessageBubble(content = message.content)
                    } else {
                        AIMessageBubble(
                            content = message.content,
                            showAvatar = true
                        )
                    }

                    // Show reflection if exists for this message
                    reflections.find { it.timestamp > message.timestamp }?.let { reflection ->
                        Spacer(modifier = Modifier.height(8.dp))
                        ReflectionCard(
                            reflection = reflection,
                            onShare = { onShareReflection(reflection) }
                        )
                    }
                }
            }

            // Suggestion chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChipUI(suggestion.text)
                }
            }

            // Input field
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = ReflectionTheme.Surface,
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Add attachment */ }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = ReflectionTheme.TextSecondary
                        )
                    }

                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Speak from the heart...",
                                color = ReflectionTheme.TextSecondary
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = ReflectionTheme.TextPrimary,
                            unfocusedTextColor = ReflectionTheme.TextPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {

                                onSendMessage(messageText)
                                messageText = ""
                                scope.launch {
                                    listState.animateScrollToItem(messages.size)
                                }

                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(ReflectionTheme.Primary)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserMessageBubble(content: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.weight(1f, fill = false),
                color = ReflectionTheme.UserMessage,
                shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
            ) {
                Text(
                    text = content,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // User avatar
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = ReflectionTheme.Primary
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }

    // "You" label
    Text(
        text = "You",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, end = 48.dp),
        textAlign = TextAlign.End,
        color = ReflectionTheme.TextSecondary,
        fontSize = 12.sp
    )
}

@Composable
fun AIMessageBubble(
    content: String,
    showAvatar: Boolean = true
) {
    Column {
        Text(
            text = "DivineData AI",
            color = ReflectionTheme.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = if (showAvatar) 48.dp else 0.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if (showAvatar) {
                // AI avatar
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = ReflectionTheme.Primary
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨",
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                color = ReflectionTheme.AIMessage,
                shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
            ) {
                Text(
                    text = content,
                    modifier = Modifier.padding(16.dp),
                    color = ReflectionTheme.TextPrimary,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun ReflectionCard(
    reflection: Reflection,
    onShare: () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(start = 48.dp),
            color = ReflectionTheme.AIMessage,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Quote
                Text(
                    text = "\"${reflection.quote}\"",
                    color = ReflectionTheme.TextPrimary,
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 22.sp
                )

                Text(
                    text = reflection.quoteSource,
                    color = ReflectionTheme.TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp),
                    fontStyle = FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Reflection text
                Text(
                    text = reflection.reflection,
                    color = ReflectionTheme.TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                // Share button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ReflectionTheme.Primary.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = ReflectionTheme.Primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionChipUI(text: String) {
    Surface(
        color = ReflectionTheme.Surface,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = ReflectionTheme.TextPrimary,
            fontSize = 14.sp
        )
    }
}
