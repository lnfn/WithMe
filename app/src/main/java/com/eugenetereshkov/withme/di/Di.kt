package com.eugenetereshkov.withme.di

import android.content.Context
import android.content.SharedPreferences
import com.eugenetereshkov.withme.BuildConfig
import com.eugenetereshkov.withme.Constants.Companion.APP_CONTEXT
import com.eugenetereshkov.withme.Constants.Companion.AUTH_CONTEXT
import com.eugenetereshkov.withme.Constants.Companion.MAIN_CONTEXT
import com.eugenetereshkov.withme.IUserConfig
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.ResourceManager
import com.eugenetereshkov.withme.UserConfig
import com.eugenetereshkov.withme.presentation.LaunchViewModel
import com.eugenetereshkov.withme.presentation.MainViewModel
import com.eugenetereshkov.withme.presentation.NavigationDrawerViewModel
import com.eugenetereshkov.withme.presentation.addcard.AddCardViewModel
import com.eugenetereshkov.withme.presentation.card.CardViewModel
import com.eugenetereshkov.withme.presentation.global.GlobalMenuController
import com.eugenetereshkov.withme.presentation.history.HistoryViewModel
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

        // Auth scope
        context(AUTH_CONTEXT) {
            viewModel { LaunchViewModel(get<Router>(), get<ResourceManager>(), get<IUserConfig>()) }
        }

        // Main scope
        context(MAIN_CONTEXT) {
            viewModel { params ->
                MainViewModel(get<Router>(), get<IUserConfig>(), params[LaunchViewModel.AUTH], get<ResourceManager>())
            }
            viewModel { CardViewModel(get<FirebaseRemoteConfig>(), get<Router>(), get<GlobalMenuController>()) }
            viewModel { NavigationDrawerViewModel(get<GlobalMenuController>(), get<Router>(), get<IUserConfig>()) }
            viewModel { AddCardViewModel(get<Router>(), get<ResourceManager>()) }
            viewModel { HistoryViewModel(get<Router>()) }
        }
    }
}

val firebaseModule = applicationContext {
    val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettings(
                FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build()
        )
        setDefaults(R.xml.remote_config_defaults)
    }

    bean { firebaseRemoteConfig }
}