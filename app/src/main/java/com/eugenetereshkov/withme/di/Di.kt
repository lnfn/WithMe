package com.eugenetereshkov.withme.di

import android.content.Context
import android.content.SharedPreferences
import com.eugenetereshkov.withme.*
import com.eugenetereshkov.withme.Constants.Companion.APP_CONTEXT
import com.eugenetereshkov.withme.Constants.Companion.AUTH_CONTEXT
import com.eugenetereshkov.withme.Constants.Companion.MAIN_CONTEXT
import com.eugenetereshkov.withme.MainActivity.Companion.ID_NEWS
import com.eugenetereshkov.withme.presenter.LaunchViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router


val appModule = applicationContext {
    // Application scope
    context(APP_CONTEXT) {
        bean {
            androidApplication().applicationContext.getSharedPreferences("APP", Context.MODE_PRIVATE)
        } bind (SharedPreferences::class)

        bean { Cicerone.create() }
        bean { get<Cicerone<Router>>().navigatorHolder }
        bean { get<Cicerone<Router>>().router }

        bean { ResourceManager(androidApplication().applicationContext) }

        // Auth scope
        context(AUTH_CONTEXT) {
            bean { UserConfig(get()) as IUserConfig }
            viewModel { LaunchViewModel(get<Router>()) }
        }

        // Main scope
        context(MAIN_CONTEXT) {
            bean { NewsRepository() as INewsRepository }
            bean { params -> NewsPresenter(get(), get(), params[ID_NEWS], params["activity"]) }
        }
    }
}
