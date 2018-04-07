package com.eugenetereshkov.withme.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import androidx.core.view.isVisible
import com.eugenetereshkov.withme.Constants
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.Screens
import com.eugenetereshkov.withme.extension.bindTo
import com.eugenetereshkov.withme.presentation.LaunchViewModel
import com.eugenetereshkov.withme.ui.global.BaseActivity
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_splash.*
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.releaseContext
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.android.SupportAppNavigator
import timber.log.Timber

class LaunchActivity : BaseActivity() {

    override val layoutResId: Int = R.layout.activity_splash
    override val navigator: Navigator by lazy {
        object : SupportAppNavigator(this@LaunchActivity, R.id.container) {
            override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? = when (screenKey) {
                Screens.MAIN_SCREEN -> Intent(this@LaunchActivity, MainActivity::class.java)
                else -> null
            }

            override fun createFragment(screenKey: String?, data: Any?): Fragment? = null
        }
    }

    private val viewModel: LaunchViewModel by viewModel()
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.rememberMeLiveData.observe(this@LaunchActivity, Observer { checked ->
            checked?.let { rememberCheckBox.isChecked = checked }
        })

        viewModel.loadingLiveData.observe(this@LaunchActivity, Observer { loading ->
            loading?.let {
                group.post {
                    group?.isVisible = it.not()
                    rogressBar?.isVisible = it
                }
            }
        })

        rememberCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onRememberMeChanged(isChecked)
        }
    }

    override fun onResume() {
        super.onResume()

        passwordEditText.textChanges()
                .map { it.toString().trim() }
                .doOnNext { Timber.d(it) }
                .filter { it.length == 4 }
                .subscribe { viewModel.onPasswordEntered(it) }
                .bindTo(disposable)
    }

    override fun onPause() {
        super.onPause()
        disposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) releaseContext(Constants.AUTH_CONTEXT)
    }
}
