package com.eugenetereshkov.withme.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.IUserConfig
import com.eugenetereshkov.withme.Screens
import com.eugenetereshkov.withme.entity.MenuItem
import com.eugenetereshkov.withme.presentation.global.GlobalMenuController
import ru.terrakok.cicerone.Router


class NavigationDrawerViewModel(
        private val menuController: GlobalMenuController,
        private val router: Router,
        private val userConfig: IUserConfig
) : ViewModel() {

    val logoutLiveData = MutableLiveData<Unit>()

    @MenuItem
    private var currentSelectedItem: String? = null

    fun onMenuItemClicked(@MenuItem item: String) {
        menuController.close()

        if (item != currentSelectedItem) {
            when (item) {
                MenuItem.CARD -> router.navigateTo(Screens.ADD_CARD_SCREEN)
                MenuItem.HISTORY -> router.navigateTo(Screens.HISTORY_SCREEN)
                MenuItem.LOGOUT -> logoutLiveData.value = Unit
            }
        }
    }

    fun logout() {
        userConfig.rememberMe = false
        router.newRootScreen(Screens.LAUNCH_SCREEN)
    }
}