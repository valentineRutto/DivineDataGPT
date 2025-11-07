package com.valentinerutto.divinedatagpt.data

import com.valentinerutto.divinedatagpt.data.network.ApiService
import com.valentinerutto.divinedatagpt.data.network.EmotionRequest

class DivineDataRepository(val apiService: ApiService) {
    suspend fun sendEmotionToServer(emotion: String) {
        apiService.postEmotionChat(EmotionRequest(emotion))
    }
}