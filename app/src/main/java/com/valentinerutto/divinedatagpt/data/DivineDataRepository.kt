package com.valentinerutto.divinedatagpt.data

import com.valentinerutto.divinedatagpt.data.network.bible.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DivineDataRepository(val apiService: ApiService) {

    suspend fun sendEmotionToServer(emotion: String) : String{

        return withContext(Dispatchers.IO){

            "Feeling '$emotion' sent successfully!"

        }
    }
}