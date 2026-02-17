package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.network.ai.AiRepository
import com.valentinerutto.divinedatagpt.data.network.ai.model.Reflection
import com.valentinerutto.divinedatagpt.data.network.bible.VerseOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class UserProfile(val name: String,val photoUrl: String?,val hasNotifications: Boolean = false)
data class Emotion(val name: String, val emoji: String)
data class HomeUiState(
    val verseOfDay: Reflection? = null,
    val greeting: String = "GOOD MORNING",
    val isLoading: Boolean = false,
    val error: String? = null,
)

class HomeViewModel(private val repository: AiRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        updateGreeting()
        loadVerseOfDay()
    }

    private fun updateGreeting() {

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "GOOD MORNING"
            in 12..16 -> "GOOD AFTERNOON"
            else -> "GOOD EVENING"
        }

        _uiState.value = _uiState.value.copy(greeting = greeting)
    }

    private fun loadVerseOfDay() {
        viewModelScope.launch {
            // In production, fetch from API
            // For now, we have a default verse


                VerseOfDay(
                    verse = "The Lord is my shepherd; I shall not want. He makes me lie down in green pastures. He leads me beside still waters.",
                    reference = "PSALM 23:1-2"
                )

            _uiState.update { it.copy(isLoading = true, error = null) }


        }
    }

    fun onEmotionSelected(emotion: Emotion) {
        // Navigate to reflection screen with selected emotion
        // This could trigger navigation in your NavHost
        viewModelScope.launch {
            // You can emit an event or update state to trigger navigation
        }
    }

    fun onStartDeepReflection() {
        // Navigate to chat/reflection screen
        viewModelScope.launch {
            // Trigger navigation
        }
    }

    fun onShareVerse() {
        // This will be handled in the Activity/Fragment
    }


}