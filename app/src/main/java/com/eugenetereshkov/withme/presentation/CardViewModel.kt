package com.eugenetereshkov.withme.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig


class CardViewModel(
        private val firebaseRemoteConfig: FirebaseRemoteConfig
) : ViewModel() {

    val remoteDataLiveData: MutableLiveData<RemoteData> by lazy {
        MutableLiveData<RemoteData>().apply {
            postValue(getRemoteData())
            initRemoteConf()
        }
    }

    private fun initRemoteConf() {
        val cacheExpiration: Long = if (firebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) 0 else 3600
        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activateFetched()
                        remoteDataLiveData.postValue(getRemoteData())
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