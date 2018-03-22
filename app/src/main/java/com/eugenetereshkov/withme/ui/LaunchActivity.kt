package com.eugenetereshkov.withme.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.eugenetereshkov.withme.MainActivity
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.Screens
import com.eugenetereshkov.withme.presenter.LaunchViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.SupportAppNavigator

class LaunchActivity : AppCompatActivity() {

    private val viewModel: LaunchViewModel by viewModel()
    private val navigatorHolder: NavigatorHolder by inject()
    private val navigator by lazy {
        object : SupportAppNavigator(this@LaunchActivity, R.id.container) {
            override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? = when (screenKey) {
                Screens.MAIN_SCREEN -> Intent(this@LaunchActivity, MainActivity::class.java)
                else -> null
            }

            override fun createFragment(screenKey: String?, data: Any?): Fragment? = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initRemoteConf()
    }

    override fun onResume() {
        super.onResume()

        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()

        navigatorHolder.removeNavigator()
    }
}
