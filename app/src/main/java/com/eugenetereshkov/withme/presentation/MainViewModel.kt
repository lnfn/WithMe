package com.eugenetereshkov.withme.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.IUserConfig
import com.eugenetereshkov.withme.Screens
import ru.terrakok.cicerone.Router


class MainViewModel(
        private val router: Router,
        private val userConfig: IUserConfig
) : ViewModel() {

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
