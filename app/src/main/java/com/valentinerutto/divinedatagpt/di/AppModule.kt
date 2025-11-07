package com.valentinerutto.divinedatagpt.di

import com.valentinerutto.divinedatagpt.DivineDataViewModel
import com.valentinerutto.divinedatagpt.MyApplication
import com.valentinerutto.divinedatagpt.data.DivineDataRepository
import com.valentinerutto.divinedatagpt.data.local.DivineDatabase
import com.valentinerutto.divinedatagpt.data.network.ApiService
import com.valentinerutto.divinedatagpt.data.network.KtorClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module

val AppModule = module{

    single { MyApplication.INSTANCE }

single{ KtorClient.httpClient }
    single { ApiService(get()) }
    single { DivineDataRepository(get()) }

    viewModel { DivineDataViewModel() }

    single { DivineDatabase.getDatabase(context = androidContext()) }

}

val databaseModule = module {

}
fun Scope.database() = get<DivineDatabase>()
