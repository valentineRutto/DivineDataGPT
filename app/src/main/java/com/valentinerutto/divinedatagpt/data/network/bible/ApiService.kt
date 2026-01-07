package com.valentinerutto.divinedatagpt.data.network.bible

import android.util.Log
import com.valentinerutto.divinedatagpt.data.local.Verse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import retrofit2.http.POST

class ApiService(private val httpClient: HttpClient) {

    suspend fun getVerse(): List<Verse> {
        return try {
            httpClient.get("verse").body()
        }catch (e: Exception){
            emptyList()
        }
    }

    @POST
    suspend fun postEmotionChat(emotionRequest: EmotionRequest){
        try {
            val requestBody = EmotionRequest(emotion = emotionRequest.emotion)

            httpClient.post("chat") {
                // Configure the request
                setBody(requestBody) // Ktor automatically serializes the data class to JSON
            }
            Log.d("ApiService", "Successfully posted emotion.")
        } catch (e: Exception) {
            Log.e("ApiService", "Error posting emotion: ${e.message}")
        }
    }

}