package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.BibleRepository
import com.valentinerutto.divinedatagpt.data.local.entity.MessageEntity
import com.valentinerutto.divinedatagpt.data.local.entity.bible.VerseEntity
import com.valentinerutto.divinedatagpt.data.network.ai.AiRepository
import com.valentinerutto.divinedatagpt.data.network.ai.model.ChatMessage
import com.valentinerutto.divinedatagpt.data.network.ai.model.Reflection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class DivineDataViewModel(
    private val bibleRepository: BibleRepository,
    private val aiRepository: AiRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _homeuiState = MutableStateFlow(HomeUiState())
    val homeuiState: StateFlow<HomeUiState> = _homeuiState.asStateFlow()

    private val _reflectionuiState = MutableStateFlow(ReflectionUiState())
    val reflectionuistate: StateFlow<ReflectionUiState> = _reflectionuiState.asStateFlow()

    private val conversationHistory = mutableListOf<Pair<String, String>>()
    private var hasLoadedRecentReflectionMessages = false


    private val _dailyUiState = MutableStateFlow(DailyUiState())
    val dailyUiState: StateFlow<DailyUiState> = _dailyUiState.asStateFlow()

    init {
        updateGreeting()
        loadVerseOfDay()
        loadRecentReflectionMessages()
    }

    //homescreen methods
    private fun updateGreeting() {

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "GOOD MORNING"
            in 12..16 -> "GOOD AFTERNOON"
            else -> "GOOD EVENING"
        }

        _homeuiState.value = _homeuiState.value.copy(greeting = greeting)
    }

    fun loadVerseOfDay() {
        viewModelScope.launch {

            _homeuiState.update { it.copy(isLoading = true, error = null) }
            _dailyUiState.update { it.copy(isLoading = true, error = null) }

            val reflection = bibleRepository.getRandomDailyVerse()?.toDailyReflection()
                ?: fallbackDailyReflection()

            _homeuiState.update {
                it.copy(verseOfDay = reflection, isLoading = false)
            }
            _dailyUiState.update {
                it.copy(reflection = reflection, isLoading = false)
            }

        }
    }


    private fun loadRecentReflectionMessages() {
        viewModelScope.launch {
            loadRecentReflectionMessagesFromDb()
        }
    }

    private suspend fun loadRecentReflectionMessagesFromDb() {
        if (hasLoadedRecentReflectionMessages) return

        val recentMessages = aiRepository.getRecentReflectionMessages(limit = 5)
        hasLoadedRecentReflectionMessages = true
        if (recentMessages.isEmpty()) return

        conversationHistory.clear()
        conversationHistory.addAll(
            recentMessages.map { message ->
                message.role to message.toHistoryText()
            }
        )

        val currentMessages = _reflectionuiState.value.messages
        val canReplaceWelcomeOnly = currentMessages.size == 1 && !currentMessages.first().isUser
        if (canReplaceWelcomeOnly) {
            _reflectionuiState.update {
                it.copy(
                    messages = recentMessages.map { message -> message.toChatMessage() },
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun initWithEmotion(emotion: String) {

        if (emotion == "general") return
        sendMessage(emotion)
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        viewModelScope.launch {
            loadRecentReflectionMessagesFromDb()

            val userMsg = ChatMessage(userText, isUser = true)
            conversationHistory.add("user" to userText)
            _reflectionuiState.update {
                it.copy(messages = it.messages + userMsg, isLoading = true, error = null)
            }

            aiRepository.addMessageToDB(MessageEntity(role = "user", content = userText))
            aiRepository.trimReflectionMessages()
            aiRepository.chatReflectionWithMistral(
                userText,
                conversationHistory.toList()
            ).fold(

                onFailure = { e ->
                    _reflectionuiState.update { it.copy(error = e.message, isLoading = false) }
                }, onSuccess = {

                conversationHistory.add("assistant" to it.first)
                    aiRepository.addMessageToDB(
                        MessageEntity(
                            role = "assistant",
                            content = it.first
                        )
                    )

                    aiRepository.trimReflectionMessages()

                    _reflectionuiState.update { state ->
                        state.copy(
                            messages = state.messages + ChatMessage(
                                it.first,
                                isUser = false
                            ), isLoading = false
                        )
                    }
                })

        }
    }


}

private fun MessageEntity.toChatMessage(): ChatMessage {
    return ChatMessage(
        content = content,
        isUser = role == "user",
        verse = verse,
        reference = reference
    )
}

private fun MessageEntity.toHistoryText(): String {
    return if (verse != null && reference != null) {
        "$verse ($reference)\n$content"
    } else {
        content
    }
}

private fun ChatMessage.toHistoryText(): String {
    return if (verse != null && reference != null) {
        "$verse ($reference)\n$content"
    } else {
        content
    }
}

private fun VerseEntity.toDailyReflection(): Reflection {
    return Reflection(
        verse = text,
        reference = "$bookName $chapter:$verse",
        insight = "Take a quiet moment with this verse today. Notice the word or phrase that stands out, and carry it into your next decision, conversation, or prayer."
    )
}

private fun fallbackDailyReflection(): Reflection {
    return Reflection(
        verse = "The Lord is my shepherd; I shall not want. He makes me lie down in green pastures. He leads me beside still waters.",
        reference = "PSALM 23:1-2",
        insight = "This verse reminds us that when we trust in God's guidance, we lack nothing essential because He provides, protects, and sustains us. It paints a picture of peace and restoration, showing that God leads us into places of rest and renewal even in the midst of life's pressures."
    )
}

data class DailyUiState(
    val isLoading: Boolean = false,
    val reflection: Reflection? = null,
    val error: String? = null
)

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

data class HomeUiState(
    val verseOfDay: Reflection? = null,
    val greeting: String = "GOOD MORNING",
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class ReflectionUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            "Welcome to your reflection space.\nHow is your soul feeling today?",
            isUser = false
        )
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)
