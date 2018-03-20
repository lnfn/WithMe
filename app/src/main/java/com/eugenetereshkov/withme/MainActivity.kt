package com.eugenetereshkov.withme

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.BounceInterpolator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.releaseContext
import java.text.SimpleDateFormat
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val newsPresenter: NewsPresenter by inject {
        mapOf(
                ID_NEWS to "11",
                "activity" to this@MainActivity
        )
    }
    private val userConfig: IUserConfig by inject()

    private val schedulerSingleThread = Schedulers.from(Executors.newSingleThreadExecutor())
    private val disposable = CompositeDisposable()

    companion object {
        const val startDate = "11-11-2017 22"
        const val ID_NEWS = "id_news"
    }

    private val startDateFormat = SimpleDateFormat("dd-MM-yyyy HH")
    private val firebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("onCreate", userConfig.name)

        remoteConf()

        userConfig.name = "Eugene Tereshkov"
    }

    override fun onStart() {
        super.onStart()

        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .map { getDifferent() }
                .subscribeOn(schedulerSingleThread)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { differentTextView.text = it }
                .bindTo(disposable)
    }

    override fun onResume() {
        super.onResume()

        Log.i("onResume", newsPresenter.getNews().joinToString())
    }

    override fun onStop() {
        disposable.clear()
        releaseContext(Constants.AUTH_CONTEXT)
        Log.i("onStop", userConfig.name)

        super.onStop()
    }

    private fun remoteConf() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()

        firebaseRemoteConfig.setConfigSettings(configSettings)
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults)
        val cacheExpiration: Long = if (firebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) 0 else 3600

        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activateFetched()
                        val backPromo = firebaseRemoteConfig.getString("back_promo")
                        val showPromo = firebaseRemoteConfig.getBoolean("show_back_promo")
                        val message = firebaseRemoteConfig.getString("message")
                        val colorDifferent = firebaseRemoteConfig.getString("colorDifferent")
                        initBackPromo(showPromo, backPromo)
                        initMessage(message)
                        setColorDifferent(colorDifferent)
                    }
                })
    }

    private fun setColorDifferent(colorDifferent: String) {
        if (colorDifferent.isNotBlank()) differentTextView.setTextColor(Color.parseColor(colorDifferent))
    }

    private fun startAnimate(view: View) {
        view.alpha = 0f
        view.translationX = -100f
        view.visibility = View.VISIBLE
        view.animate().alpha(1f)
                .translationX(100f)
                .setInterpolator(BounceInterpolator())
                .setDuration(1000).start()
    }

    private fun initBackPromo(showPromo: Boolean, backPromo: String) {
        if (showPromo) {
            Glide.with(this)
                    .load(backPromo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(backImageView)
        }

//        startAnimate(animateImageView)
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
