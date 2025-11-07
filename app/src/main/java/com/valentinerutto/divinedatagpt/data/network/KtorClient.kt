package com.valentinerutto.divinedatagpt.data.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    val httpClient = HttpClient(CIO){
        install(ContentNegotiation){
            json(Json{
                prettyPrint=true
                isLenient=true
                ignoreUnknownKeys = true
            })
        }
        install(Logging){

            logger = object :Logger {
                override fun log(message: String) {
                    Log.v("Logger Ktor => $message",message)
                }
            }
            level = LogLevel.ALL
        }

        defaultRequest { url("http://0.0.0.0:8080/") }

    }
}