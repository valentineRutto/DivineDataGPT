package com.valentinerutto.divinedatagpt.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AiRetrofit {

    val api: AiApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .build()
            .create(AiApi::class.java)
    }
}
