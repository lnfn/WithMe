package com.eugenetereshkov.withme.ui.global

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder

abstract class BaseActivity : AppCompatActivity() {
    protected abstract val layoutResId: Int
    protected abstract val navigator: Navigator

    private val navigatorHolder: NavigatorHolder by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}