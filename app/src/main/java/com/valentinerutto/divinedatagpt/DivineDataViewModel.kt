package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.BibleRepository
import com.valentinerutto.divinedatagpt.data.local.entity.MessageEntity
import com.valentinerutto.divinedatagpt.data.network.ai.AiRepository
import com.valentinerutto.divinedatagpt.data.network.ai.model.ChatMessage
import com.valentinerutto.divinedatagpt.data.network.ai.model.Reflection
import com.valentinerutto.divinedatagpt.util.Resource
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


    private val _dailyUiState = MutableStateFlow(DailyUiState())
    val dailyUiState: StateFlow<DailyUiState> = _dailyUiState.asStateFlow()

    init {
        updateGreeting()
        loadVerseOfDay()
        loadDailyReflection()
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
            aiRepository.getDailyReflection(BuildConfig.GEMINI_API_KEY).fold(
                onSuccess = { reflection ->
                    _homeuiState.update { it.copy(verseOfDay = reflection, isLoading = false) }
                },
                onFailure = { exception ->
                    _homeuiState.update {
                        it.copy(
                            error = exception.message, isLoading = false,
                            verseOfDay = Reflection(
                                verse = "The Lord is my shepherd; I shall not want. He makes me lie down in green pastures. He leads me beside still waters.",
                                reference = "PSALM 23:1-2",
                                "This verse reminds us that when we trust in God’s guidance, we lack nothing essential because He provides, protects, and sustains us. It paints a picture of peace and restoration, showing that God leads us into places of rest and renewal even in the midst of life’s pressures."
                            )
                        )
                    }
                }
            )

        }
    }

    //reflectionscreenmethods
    fun initWithEmotion(emotion: String) {

        if (emotion == "general" || conversationHistory.isNotEmpty()) return

         viewModelScope.launch {

             _reflectionuiState.update { it.copy(isLoading = true) }

             when (val result =
                 aiRepository.getReflectionForEmotion(BuildConfig.GEMINI_API_KEY, emotion)) {

                 is Resource.Success -> {

                     val aiMsg = ChatMessage(

                         content = result.data.insight,
                         isUser = false,
                         verse = result.data.verse,
                         reference = result.data.reference
                     )
                     conversationHistory.add("assistant" to "${result.data.verse} - ${result.data.insight}")

                     _reflectionuiState.value = ReflectionUiState(
                         messages = _reflectionuiState.value.messages + aiMsg,
                         isLoading = false
                     )

                 }

                 is Resource.Error -> {

                     _reflectionuiState.value =
                         ReflectionUiState(error = result.message, isLoading = false)

                 }

                 is Resource.Loading -> {
                     _reflectionuiState.value = ReflectionUiState(isLoading = true)
                 }

             }

         }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(userText, isUser = true)
        conversationHistory.add("user" to userText)
        _reflectionuiState.update { it.copy(messages = it.messages + userMsg, isLoading = true) }


        viewModelScope.launch {
            aiRepository.addMessageToDB(MessageEntity(role = "user", content = userText))
            aiRepository.chatReflection(
                BuildConfig.GEMINI_API_KEY,
                userText,
                conversationHistory.toList()
            ).fold(

                onFailure = { e ->
                    _reflectionuiState.update { it.copy(error = e.message, isLoading = false) }
                }, onSuccess = {

                conversationHistory.add("assistant" to it.first)

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

    fun loadDailyReflection() {
        viewModelScope.launch {
            _dailyUiState.update { it.copy(isLoading = true) }
            aiRepository.getDailyReflection(BuildConfig.GEMINI_API_KEY).fold(

                onSuccess = { reflection ->
                    _dailyUiState.update { it.copy(reflection = reflection, isLoading = false) }
                },


                onFailure = { exception ->
                    _dailyUiState.update {
                        it.copy(isLoading = false, error = exception.message)
                    }

                })
        }
    }


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



