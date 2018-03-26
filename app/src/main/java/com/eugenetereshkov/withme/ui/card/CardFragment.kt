package com.eugenetereshkov.withme.ui.card


import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.extension.bindTo
import com.eugenetereshkov.withme.presentation.CardViewModel
import com.eugenetereshkov.withme.ui.global.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_card.*
import org.koin.android.architecture.ext.viewModel
import java.text.SimpleDateFormat
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class CardFragment : BaseFragment() {

    private companion object {
        private const val DIFFERENT_PERIOD_UPDATE = 1000L
        private const val START_DATE = "11-11-2017 22"
    }

    override val idResLayout: Int = R.layout.fragment_card

    private val viewModel: CardViewModel by viewModel()
    private val schedulerSingleThread = Schedulers.from(Executors.newSingleThreadExecutor())
    private val disposable = CompositeDisposable()
    private val startDateFormat = SimpleDateFormat("dd-MM-yyyy HH")

    private val onClickListener by lazy {
        View.OnClickListener {
            when (it.id) {
                differentTextView.id -> {
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        differentTextView.setOnClickListener(onClickListener)
        viewModel.remoteDataLiveData.observe(this@CardFragment, Observer { data ->
            data?.let { setData(it) }
        })
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

    override fun onStop() {
        disposable.clear()
        super.onStop()
    }

    private fun setData(data: CardViewModel.RemoteData) {
        initBackPromo(data.showPromo, data.backPromo)
        initMessage(data.message)
        setColorDifferent(data.colorDifferent)
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
        val startDate = startDateFormat.parse(START_DATE)
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
