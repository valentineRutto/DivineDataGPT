package com.valentinerutto.divinedatagpt

import android.app.Application
import com.valentinerutto.divinedatagpt.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication: Application() {

    companion object {
        lateinit var INSTANCE: MyApplication
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        startKoin{
            androidLogger( level = Level.DEBUG)
            androidContext(this@MyApplication)
           modules(AppModule    )
        }
    }
}