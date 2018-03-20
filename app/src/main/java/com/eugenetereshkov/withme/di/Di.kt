package com.eugenetereshkov.withme.di

import android.content.Context
import android.content.SharedPreferences
import com.eugenetereshkov.withme.*
import com.eugenetereshkov.withme.Constants.Companion.APP_CONTEXT
import com.eugenetereshkov.withme.Constants.Companion.AUTH_CONTEXT
import com.eugenetereshkov.withme.Constants.Companion.MAIN_CONTEXT
import com.eugenetereshkov.withme.MainActivity.Companion.ID_NEWS
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

val appModule = applicationContext {
    context(APP_CONTEXT) {
        bean {
            androidApplication().applicationContext.getSharedPreferences("APP", Context.MODE_PRIVATE)
        } bind (SharedPreferences::class)

        bean { ResourceManager(androidApplication().applicationContext) }

        context(AUTH_CONTEXT) {
            bean { UserConfig(get()) as IUserConfig }
        }

        context(MAIN_CONTEXT) {
            bean { NewsRepository() as INewsRepository }
            bean { params -> NewsPresenter(get(), get(), params[ID_NEWS], params["activity"]) }
        }
    }
}
