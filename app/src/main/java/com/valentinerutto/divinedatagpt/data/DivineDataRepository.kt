package com.valentinerutto.divinedatagpt.data

import com.valentinerutto.divinedatagpt.data.network.ApiService
import com.valentinerutto.divinedatagpt.data.network.EmotionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DivineDataRepository(val apiService: ApiService) {

    suspend fun sendEmotionToServer(emotion: String) : String{
        return withContext(Dispatchers.IO){
            apiService.postEmotionChat(EmotionRequest(emotion))
            "Feeling '$emotion' sent successfully!"

        }
    }
}