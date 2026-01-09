package com.valentinerutto.divinedatagpt.data.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

        fun createRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {

            val contentType = "application/json".toMediaType()

            return Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }

        fun createOkClient(): OkHttpClient {
            return OkHttpClient.Builder().addInterceptor(getLoggingInterceptor())
                .build()
        }

        private fun getLoggingInterceptor(): Interceptor {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            return httpLoggingInterceptor
        }
    }
