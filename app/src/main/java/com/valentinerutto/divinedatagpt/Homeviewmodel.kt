package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.network.VerseOfDay
import com.valentinerutto.divinedatagpt.ui.theme.screens.Emotion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class UserProfile(val name: String,val photoUrl: String?,val hasNotifications: Boolean = false)
data class HomeUiState(
    val userProfile: UserProfile = UserProfile(
        name = "Sarah",
        photoUrl = null,
        hasNotifications = true
    ),
    val emotions: List<Emotion> = listOf(
        Emotion("Anxious", "😰"),
        Emotion("Grateful", "🙏"),
        Emotion("Lonely", "☁️"),
        Emotion("Inspired", "✨"),
        Emotion("Stressed", "🌊")
    ),
    val verseOfDay: VerseOfDay = VerseOfDay(
        verse = "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God.",
        reference = "PHILIPPIANS 4:6",
        imageUrl = null
    ),

    val currentScreen: NavigationScreen = NavigationScreen.HOME,
    val greeting: String = "GOOD MORNING",
    val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {

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

            // You can add logic to rotate verses daily
            val verses = listOf(
                VerseOfDay(
                    verse = "Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God.",
                    reference = "PHILIPPIANS 4:6"
                ),
                VerseOfDay(
                    verse = "For I know the plans I have for you, declares the Lord, plans to prosper you and not to harm you, plans to give you hope and a future.",
                    reference = "JEREMIAH 29:11"
                ),
                VerseOfDay(
                    verse = "The Lord is my shepherd; I shall not want. He makes me lie down in green pastures. He leads me beside still waters.",
                    reference = "PSALM 23:1-2"
                ),
                VerseOfDay(
                    verse = "Peace I leave with you; my peace I give you. Let not your hearts be troubled, neither let them be afraid.",
                    reference = "JOHN 14:27"
                ),
                VerseOfDay(
                    verse = "Trust in the Lord with all your heart and lean not on your own understanding; in all your ways submit to him, and he will make your paths straight.",
                    reference = "PROVERBS 3:5-6"
                )
            )

            // Select verse based on day of year
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val verseIndex = dayOfYear % verses.size

            _uiState.value = _uiState.value.copy(
                verseOfDay = verses[verseIndex]
            )
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

    fun onNotificationClick() {
        // Open notifications
        viewModelScope.launch {
            // Clear notification badge
            _uiState.value = _uiState.value.copy(
                userProfile = _uiState.value.userProfile.copy(hasNotifications = false)
            )
        }
    }

    fun onNavigate(screen: NavigationScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun updateUserProfile(name: String, photoUrl: String? = null) {
        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(
                name = name,
                photoUrl = photoUrl
            )
        )
    }

    fun markNotificationAsRead() {
        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(hasNotifications = false)
        )
    }
}