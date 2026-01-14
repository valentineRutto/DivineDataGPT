package com.valentinerutto.divinedatagpt

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinerutto.divinedatagpt.data.DivineDataRepository
import com.valentinerutto.divinedatagpt.data.local.Verse
import com.valentinerutto.divinedatagpt.data.network.ai.AiRepository
import com.valentinerutto.divinedatagpt.ui.theme.screens.sampleSpiritContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DivineDataViewModel (private val repository: DivineDataRepository, private val aiRepository: AiRepository): ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var uiModel = mutableStateOf(sampleSpiritContent())
        private set

     fun sendFeeling(emotion: String) {

         viewModelScope.launch {

             _uiState.value = UiState.Loading

             try {

                 delay(2000)

                 val result = repository.sendEmotionToServer(emotion)

                 _uiState.value = UiState.Success(result)

             } catch (e: Exception) {
                 _uiState.value = UiState.Error(e.message ?: "Unknown error")
             }
         }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
    val explanation = aiRepository.explainVerse(
        verseReference = verse,
        userFeeling = feeling
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


