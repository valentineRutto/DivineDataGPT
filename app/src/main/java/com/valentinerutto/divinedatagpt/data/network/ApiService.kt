package com.valentinerutto.divinedatagpt.data.network

import com.valentinerutto.divinedatagpt.data.local.Verse
import io.ktor.client.HttpClient
import retrofit2.http.GET
import retrofit2.http.POST

class ApiService {

    @GET("verse")
    suspend fun getVerse(): List<Verse> {
        return emptyList()
    }

    @POST
    suspend fun postEmotionChat(){

    }

}