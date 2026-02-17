package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.BibleRepository
import com.valentinerutto.divinedatagpt.data.DivineDataRepository
import com.valentinerutto.divinedatagpt.data.local.Verse
import com.valentinerutto.divinedatagpt.data.network.ai.AiRepository
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
    private val repository: DivineDataRepository,
    private val aiRepository: AiRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _homeuiState = MutableStateFlow(HomeUiState())
    val homeuiState: StateFlow<HomeUiState> = _homeuiState.asStateFlow()

    init {
        updateGreeting()
        loadVerseOfDay()
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
                                reference = "PSALM 23:1-2", ""
                            )
                        )
                    }
                }
            )


        }
    }



     fun sendFeeling(emotion: String) {

         viewModelScope.launch {

             _uiState.value = UiState.Loading

             when (val result =
                 aiRepository.getReflectionForEmotion(BuildConfig.GEMINI_API_KEY, emotion)) {

                 is Resource.Success -> {
                     _uiState.value = UiState.Success(result.data.verse)

                 }

                 is Resource.Error -> {
                     _uiState.value = UiState.Error(result.message)
                 }

                 is Resource.Loading -> {
                     _uiState.value = UiState.Loading
                 }

             }

         }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }

    suspend fun explanation() = aiRepository.explainVerse(
        verseReference = "verse",
        userFeeling = "feeling"
    )


}


        data class DivineDataUiState(
            val verse: Verse? = null,
            val loading: Boolean = false,
            val errorMessage: String? = null
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



