package com.eugenetereshkov.withme.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.Screens
import com.eugenetereshkov.withme.presentation.MainViewModel
import com.eugenetereshkov.withme.presentation.global.GlobalMenuController
import com.eugenetereshkov.withme.ui.addcard.AddCardFragment
import com.eugenetereshkov.withme.ui.card.CardFragment
import com.eugenetereshkov.withme.ui.drawer.NavigationDrawerFragment
import com.eugenetereshkov.withme.ui.global.BaseActivity
import com.eugenetereshkov.withme.ui.global.BaseFragment
import com.eugenetereshkov.withme.ui.history.HistoryFragment
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command


class MainActivity : BaseActivity() {

    override val layoutResId: Int = R.layout.activity_main
    override val navigator: Navigator by lazy {
        object : SupportAppNavigator(this@MainActivity, R.id.mainContainer) {
            override fun applyCommands(commands: Array<out Command>?) {
                super.applyCommands(commands)
                updateNavDrawer()
            }

            override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? = when (screenKey) {
                Screens.LAUNCH_SCREEN -> Intent(this@MainActivity, LaunchActivity::class.java)
                else -> null
            }

            override fun createFragment(screenKey: String?, data: Any?): Fragment? = when (screenKey) {
                Screens.CARD_SCREEN -> CardFragment()
                Screens.ADD_CARD_SCREEN -> AddCardFragment()
                Screens.HISTORY_SCREEN -> HistoryFragment()
                else -> null
            }
        }
    }

    private val currentFragment
        get() = supportFragmentManager.findFragmentById(R.id.mainContainer) as BaseFragment?

    private val drawerFragment
        get() = supportFragmentManager.findFragmentById(R.id.navDrawerContainer) as NavigationDrawerFragment?

    private val router: Router by inject()
    private val viewModel: MainViewModel by viewModel()
    private val menuController: GlobalMenuController by inject()
    private var menuStateDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_TransparentStatus)
        super.onCreate(savedInstanceState)

        viewModel.firstViewAttachLiveData.observe(this@MainActivity, Observer { initMainScreen() })
        viewModel.checkAuth()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        menuStateDisposable = menuController.state.subscribe { openNavDrawer(it) }
    }

    override fun onPause() {
        menuStateDisposable?.dispose()
        super.onPause()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            openNavDrawer(false)
        } else {
            currentFragment?.onBackPressed() ?: router.finishChain()
        }
    }

    private fun initMainScreen() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainContainer, CardFragment())
                .replace(R.id.navDrawerContainer, NavigationDrawerFragment())
                .commitNow()
        updateNavDrawer()
    }

    private fun openNavDrawer(open: Boolean) {
        if (open) drawerLayout.openDrawer(GravityCompat.START)
        else drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun updateNavDrawer() {
        supportFragmentManager.executePendingTransactions()

        drawerFragment?.let { drawerFragment ->
            currentFragment?.let {
                when (it) {
                    is CardFragment -> drawerFragment.onScreenChanged(NavigationDrawerFragment.MenuItem.CardItem())
                }
                enableNavDrawer(isNavDrawerAvailableForFragment(it))
            }
        }
    }

    private fun isNavDrawerAvailableForFragment(currentFragment: Fragment) = when (currentFragment) {
        is CardFragment -> true
        else -> false
    }

    private fun enableNavDrawer(enable: Boolean) {
        drawerLayout.setDrawerLockMode(
                if (enable) DrawerLayout.LOCK_MODE_UNLOCKED
                else DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                GravityCompat.START
        )
    }
}
