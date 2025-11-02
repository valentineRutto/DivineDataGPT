package com.valentinerutto.divinedatagpt

import androidx.lifecycle.ViewModel
import com.valentinerutto.divinedatagpt.data.local.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DivineDataViewModel : ViewModel(){
    private val _uiState = MutableStateFlow<DivineDataUiState>(DivineDataUiState())
    val uiState: StateFlow<DivineDataUiState> = _uiState


}
data class DivineDataUiState(val verse: Verse? = null ,
                             val loading: Boolean = false,
                             val errorMessage: String? = null)