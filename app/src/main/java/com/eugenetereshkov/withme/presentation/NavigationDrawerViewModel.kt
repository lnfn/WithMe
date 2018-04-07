package com.eugenetereshkov.withme.presentation

import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.Screens
import com.eugenetereshkov.withme.presentation.global.GlobalMenuController
import com.eugenetereshkov.withme.ui.drawer.NavigationDrawerFragment
import ru.terrakok.cicerone.Router


class NavigationDrawerViewModel(
        private val menuController: GlobalMenuController,
        private val router: Router
) : ViewModel() {

    private var currentSelectedItem: NavigationDrawerFragment.MenuItem? = null

    fun onMenuItemClicked(item: NavigationDrawerFragment.MenuItem) {
        menuController.close()

        if (item != currentSelectedItem) {
            when (item) {
                is NavigationDrawerFragment.MenuItem.CardItem -> router.navigateTo(Screens.ADD_CARD_SCREEN)
                is NavigationDrawerFragment.MenuItem.History -> router.navigateTo(Screens.HISTORY_SCREEN)
            }
        }
    }
}