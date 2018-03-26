package com.eugenetereshkov.withme.di

import android.content.Context
import android.content.SharedPreferences
import com.eugenetereshkov.withme.*
import com.eugenetereshkov.withme.Constants.Companion.APP_CONTEXT
import com.eugenetereshkov.withme.Constants.Companion.AUTH_CONTEXT
import com.eugenetereshkov.withme.Constants.Companion.MAIN_CONTEXT
import com.eugenetereshkov.withme.presentation.CardViewModel
import com.eugenetereshkov.withme.presentation.LaunchViewModel
import com.eugenetereshkov.withme.presentation.MainViewModel
import com.eugenetereshkov.withme.presentation.NavigationDrawerViewModel
import com.eugenetereshkov.withme.presentation.global.GlobalMenuController
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
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

        bean { GlobalMenuController() }
        bean { Cicerone.create() }
        bean { get<Cicerone<Router>>().navigatorHolder }
        bean { get<Cicerone<Router>>().router }
        bean { ResourceManager(androidApplication().applicationContext) }
        bean { UserConfig(get()) as IUserConfig }
        bean {
            FirebaseRemoteConfig.getInstance().apply {
                setConfigSettings(
                        FirebaseRemoteConfigSettings.Builder()
                                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                                .build()
                )
                setDefaults(R.xml.remote_config_defaults)
            }
        }

        // Auth scope
        context(AUTH_CONTEXT) {
            viewModel { LaunchViewModel(get<Router>(), get<ResourceManager>(), get<IUserConfig>()) }
        }

        // Main scope
        context(MAIN_CONTEXT) {
            viewModel { MainViewModel(get<Router>(), get<IUserConfig>()) }
            viewModel { CardViewModel(get<FirebaseRemoteConfig>()) }
            viewModel { NavigationDrawerViewModel(get<GlobalMenuController>(), get<Router>()) }
        }
    }
}
