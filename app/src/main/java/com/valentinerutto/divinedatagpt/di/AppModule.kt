package com.valentinerutto.divinedatagpt.di

import com.valentinerutto.divinedatagpt.DivineDataViewModel
import com.valentinerutto.divinedatagpt.MyApplication
import com.valentinerutto.divinedatagpt.data.DivineDataRepository
import com.valentinerutto.divinedatagpt.data.local.DivineDatabase
import com.valentinerutto.divinedatagpt.data.network.bible.ApiService
import com.valentinerutto.divinedatagpt.data.network.KtorClient
import com.valentinerutto.divinedatagpt.data.network.ai.AiApi
import com.valentinerutto.divinedatagpt.data.network.bible.ESVApiService
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val AppModule = module {

    single { MyApplication.INSTANCE }

    single { KtorClient.httpClient }
    single { ApiService(get()) }
    single { DivineDataRepository(get()) }

    viewModel { DivineDataViewModel(get()) }

    single { DivineDatabase.getDatabase(context = androidContext()) }

}
    val databaseModule = module {

    }

    fun Scope.database() = get<DivineDatabase>()

    val networkModule = module {

        single {
            OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        single<AiApi> {
            Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(get())
                .build()
                .create(AiApi::class.java)
        }

        single<ESVApiService> {
            Retrofit.Builder()
                .baseUrl("https://api.esv.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    get<OkHttpClient>().newBuilder()
                        .addInterceptor { chain ->
                            chain.proceed(
                                chain.request().newBuilder()
                                    .addHeader(
                                        "Authorization",
                                        "Token ${BuildConfig.ESV_API_KEY}"
                                    )
                                    .build()
                            )
                        }
                        .build()
                )
                .build()
                .create(ESVApiService::class.java)
        }
    }

