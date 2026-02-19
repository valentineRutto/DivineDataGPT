package com.valentinerutto.divinedatagpt.di

import com.valentinerutto.divinedatagpt.DivineDataViewModel
import com.valentinerutto.divinedatagpt.MyApplication
import com.valentinerutto.divinedatagpt.data.BibleRepository
import com.valentinerutto.divinedatagpt.data.local.DivineDatabase
import com.valentinerutto.divinedatagpt.data.network.RetrofitClient
import com.valentinerutto.divinedatagpt.data.network.RetrofitClient.createOkClient
import com.valentinerutto.divinedatagpt.data.network.ai.AiApi
import com.valentinerutto.divinedatagpt.data.network.ai.AiRepository
import com.valentinerutto.divinedatagpt.data.network.bible.ApiService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit

val AppModule = module {

    single { MyApplication.INSTANCE }

    single { BibleRepository(get(), get()) }

    single { AiRepository(get(), get(), get()) }

    viewModel { DivineDataViewModel(get(), get()) }
    single { DivineDatabase.getDatabase(context = androidContext()) }


}
    val databaseModule = module {
        single { get<DivineDatabase>().memorySummaryDao() }
        single { get<DivineDatabase>().messageDao() }
    }

    fun Scope.database() = get<DivineDatabase>()

    val networkModule = module {


        single(named("AI")) { RetrofitClient.provideAIOkHttpClient() }

        single(named("AI")) {
            RetrofitClient.provideRetrofit(
                RetrofitClient.GEMINI_BASE_URL,
                get(named("AI"))
            )
        }


        single(named("ESV")) { RetrofitClient.provideEsvOkHttpClient() }

        single(named("ESV")) {
            RetrofitClient.provideRetrofit(RetrofitClient.ESV_BASE_URL, get(named("ESV")))
        }


        single { createOkClient() }

        single {
            get<Retrofit>(named("ESV")).create(ApiService::class.java)
        }

        single {
            get<Retrofit>(named("AI")).create(AiApi::class.java)
        }


    }

