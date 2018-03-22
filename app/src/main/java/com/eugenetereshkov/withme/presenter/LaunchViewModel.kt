package com.eugenetereshkov.withme.presenter

import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.BuildConfig
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.Screens
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import ru.terrakok.cicerone.Router


class LaunchViewModel(
        private val router: Router
) : ViewModel() {

    private val firebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }

    fun initRemoteConf() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()

        firebaseRemoteConfig.setConfigSettings(configSettings)
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults)
        val cacheExpiration: Long = if (firebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) 0 else 3600

        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activateFetched()
                        router.replaceScreen(Screens.MAIN_SCREEN)
                    }
                }
    }
}
