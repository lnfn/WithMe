package com.eugenetereshkov.withme.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.Screens
import com.eugenetereshkov.withme.viewmodel.MainViewModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.SupportAppNavigator
import java.text.SimpleDateFormat
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val navigatorHolder: NavigatorHolder by inject()
    private val navigator by lazy {
        object : SupportAppNavigator(this@MainActivity, R.id.container) {
            override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?): Intent? = when (screenKey) {
                Screens.LAUNCH_SCREEN -> Intent(this@MainActivity, LaunchActivity::class.java)
                else -> null
            }

            override fun createFragment(screenKey: String?, data: Any?): Fragment? = null
        }
    }

    private val viewModel: MainViewModel by viewModel { mapOf(ID_NEWS to "11") }
    private val schedulerSingleThread = Schedulers.from(Executors.newSingleThreadExecutor())
    private val disposable = CompositeDisposable()

    companion object {
        private const val DIFFERENT_PERIOD_UPDATE = 1000L
        const val startDate = "11-11-2017 22"
        const val ID_NEWS = "id_news"
    }

    private val startDateFormat = SimpleDateFormat("dd-MM-yyyy HH")
    private val onClickListener by lazy {
        View.OnClickListener {
            when (it.id) {
                differentTextView.id -> {
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_TransparentStatus)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        differentTextView.setOnClickListener(onClickListener)
        viewModel.remoteData.observe(this@MainActivity, Observer { data -> data?.let { setData(it) } })
        viewModel.checkAuth()
    }

    override fun onStart() {
        super.onStart()

        Observable.interval(DIFFERENT_PERIOD_UPDATE, TimeUnit.MILLISECONDS)
                .map { getDifferent() }
                .subscribeOn(schedulerSingleThread)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { differentTextView.text = it }
                .bindTo(disposable)
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }

    override fun onStop() {
        disposable.clear()
        super.onStop()
    }

    private fun setData(data: MainViewModel.RemoteData) {
        FirebaseRemoteConfig.getInstance().run {
            initBackPromo(data.showPromo, data.backPromo)
            initMessage(data.message)
            setColorDifferent(data.colorDifferent)
        }
    }

    private fun setColorDifferent(colorDifferent: String) {
        if (colorDifferent.isNotBlank()) differentTextView.setTextColor(Color.parseColor(colorDifferent))
    }

    private fun initBackPromo(showPromo: Boolean, backPromo: String) {
        if (showPromo) {
            Glide.with(this)
                    .load(backPromo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(backImageView)
        }
    }

    private fun initMessage(message: String) {
        if (message.isNotBlank()) {
            messageTextView.text = message
            messageTextView.visibility = View.VISIBLE
        }
    }

    private fun getDifferent(): String {
        val startDate = startDateFormat.parse(startDate)
        val startMillis = startDate.time
        val nowMillis = System.currentTimeMillis()
        val diff = nowMillis - startMillis

        val diffSeconds = diff / 1000 % 60
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000) % 24
        val diffDays = diff / (24 * 60 * 60 * 1000)

        val time = "%1\$02d:%2\$02d:%3\$02d".format(diffHours, diffMinutes, diffSeconds)

        return "$diffDays ${getDayAddition(diffDays.toInt())}\n$time"
    }

    private fun getDayAddition(num: Int): String {
        val preLastDigit = num % 100 / 10

        if (preLastDigit == 1) {
            return "дней"
        }

        return when (num % 10) {
            1 -> "день"
            2, 3, 4 -> "дня"
            else -> "дней"
        }
    }
}

fun Disposable.bindTo(disposable: CompositeDisposable) {
    disposable.add(this)
}
