package com.valentinerutto.divinedatagpt

import android.app.Application
import com.valentinerutto.divinedatagpt.di.AppModule
import com.valentinerutto.divinedatagpt.di.databaseModule
import com.valentinerutto.divinedatagpt.di.networkModule
import com.valentinerutto.divinedatagpt.util.BibleDatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication: Application() {

    companion object {
        lateinit var INSTANCE: MyApplication
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Injected after startKoin() completes
    private val seeder: BibleDatabaseSeeder by inject()


    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        startKoin{

            androidLogger( level = Level.DEBUG)
            androidContext(this@MyApplication)
            modules(AppModule, databaseModule, networkModule)

        }
        applicationScope.launch {
            seeder.seedIfEmpty()
        }
    }
}