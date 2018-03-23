package com.eugenetereshkov.withme.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.IUserConfig
import com.eugenetereshkov.withme.Screens
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import ru.terrakok.cicerone.Router


class MainViewModel(
        private val router: Router,
        private val userConfig: IUserConfig,
        private val firebaseRemoteConfig: FirebaseRemoteConfig
) : ViewModel() {

    val remoteData = MutableLiveData<RemoteData>().apply {
        postValue(getRemoteData())
    }

    fun checkAuth() {
        if (userConfig.login) {
            userConfig.login = false
            initRemoteConf()
            router.showSystemMessage("${userConfig.name}, Привет!")
            return
        }

        if (userConfig.name.isEmpty() || userConfig.rememberMe.not()) router.newRootScreen(Screens.LAUNCH_SCREEN)
    }

    private fun initRemoteConf() {
        val cacheExpiration: Long = if (firebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) 0 else 3600
        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activateFetched()
                        remoteData.postValue(getRemoteData())
                    }
                }
    }

    private fun getRemoteData() = RemoteData(
            showPromo = firebaseRemoteConfig.getBoolean("show_back_promo"),
            backPromo = firebaseRemoteConfig.getString("back_promo"),
            message = firebaseRemoteConfig.getString("message"),
            colorDifferent = firebaseRemoteConfig.getString("colorDifferent")
    )

    data class RemoteData(
            val showPromo: Boolean,
            val backPromo: String,
            val message: String,
            val colorDifferent: String
    )
}
