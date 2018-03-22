package com.eugenetereshkov.withme

import android.app.Application
import com.eugenetereshkov.withme.di.appModule
import org.koin.ContextCallback
import org.koin.android.ext.android.startKoin
import org.koin.standalone.StandAloneContext.registerContextCallBack
import timber.log.Timber
import timber.log.Timber.DebugTree


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        startKoin(this, listOf(appModule))

        registerContextCallBack(object : ContextCallback {
            override fun onContextReleased(contextName: String) {
                Timber.i("Context $contextName has been dropped")
            }
        })
    }
}