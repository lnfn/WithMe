package com.eugenetereshkov.withme.presentation

import android.arch.lifecycle.MutableLiveData
import com.eugenetereshkov.withme.IUserConfig
import com.eugenetereshkov.withme.Screens
import com.eugenetereshkov.withme.presentation.global.BaseViewModel
import ru.terrakok.cicerone.Router


class MainViewModel(
        private val router: Router,
        private val userConfig: IUserConfig
) : BaseViewModel() {

    private var firstStart = true
    val firstViewAttachLiveData = MutableLiveData<Unit>()

    fun checkAuth() {
        if (firstStart.not()) return

        firstStart = false

        if (userConfig.login) {
            userConfig.login = false
            firstViewAttachLiveData.value = Unit
            router.showSystemMessage("${userConfig.name}, Привет!")
            return
        }

        if (userConfig.name.isEmpty() || userConfig.rememberMe.not()) router.newRootScreen(Screens.LAUNCH_SCREEN)
    }
}
