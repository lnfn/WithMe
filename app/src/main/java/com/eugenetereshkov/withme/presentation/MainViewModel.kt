package com.eugenetereshkov.withme.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.IUserConfig
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.ResourceManager
import com.eugenetereshkov.withme.Screens
import ru.terrakok.cicerone.Router


class MainViewModel(
        private val router: Router,
        private val userConfig: IUserConfig,
        private val isAuth: Boolean,
        private val resourceManager: ResourceManager
) : ViewModel() {

    val firstViewAttachLiveData = MutableLiveData<Unit>()

    fun checkAuth() {
        if (firstViewAttachLiveData.value == null) {
            if (userConfig.login) {
                if (isAuth || userConfig.rememberMe) {
                    firstViewAttachLiveData.value = Unit
                    router.showSystemMessage(resourceManager.getString(R.string.hi, userConfig.name))
                    return
                }
            }

            router.newRootScreen(Screens.LAUNCH_SCREEN)
        }
    }
}
