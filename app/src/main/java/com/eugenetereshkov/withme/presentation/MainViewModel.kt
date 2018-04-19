package com.eugenetereshkov.withme.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.IUserConfig
import com.eugenetereshkov.withme.Screens
import ru.terrakok.cicerone.Router


class MainViewModel(
        private val router: Router,
        private val userConfig: IUserConfig,
        private val isAuth: Boolean
) : ViewModel() {

    val firstViewAttachLiveData = MutableLiveData<Unit>()

    fun checkAuth() {

        if (userConfig.login) {
            if (isAuth || userConfig.rememberMe) {
                firstViewAttachLiveData.value = Unit
                router.showSystemMessage("${userConfig.name}, Привет!")
                return
            }
        }

        router.newRootScreen(Screens.LAUNCH_SCREEN)
    }
}
