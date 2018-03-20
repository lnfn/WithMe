package com.eugenetereshkov.withme

import android.app.Application
import android.util.Log
import com.eugenetereshkov.withme.di.appModule
import org.koin.ContextCallback
import org.koin.android.ext.android.startKoin
import org.koin.standalone.StandAloneContext.registerContextCallBack

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(appModule))

        registerContextCallBack(object : ContextCallback {
            override fun onContextReleased(contextName: String) {
                Log.i("onContextReleased", "Context $contextName has been dropped")
            }
        })
    }
}