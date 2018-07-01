package com.eugenetereshkov.withme.ui.card


import android.arch.lifecycle.Observer
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eugenetereshkov.withme.Constants.Companion.START_DATE
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.extension.bindTo
import com.eugenetereshkov.withme.extension.timeDifferent
import com.eugenetereshkov.withme.glide.GlideApp
import com.eugenetereshkov.withme.presentation.card.CardViewModel
import com.eugenetereshkov.withme.ui.global.BaseFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_card.*
import org.koin.android.architecture.ext.viewModel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class CardFragment : BaseFragment() {

    private companion object {
        private const val DIFFERENT_PERIOD_UPDATE = 1000L
    }

    override val idResLayout: Int = R.layout.fragment_card

    private val differentTextViewTouchListener = object : View.OnTouchListener {
        private var downPT = PointF()
        private var startPT = PointF()

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    view.x = startPT.x + event.x - downPT.x
                    view.y = startPT.y + event.y - downPT.y
                    startPT.set(view.x, view.y)
                }
                MotionEvent.ACTION_DOWN -> {
                    downPT.set(event.x, event.y)
                    startPT.set(view.x, view.y)
                }
            }

            return true
        }
    }
    private val viewModel: CardViewModel by viewModel()
    private val schedulerSingleThread = Schedulers.from(Executors.newSingleThreadExecutor())
    private val disposable = CompositeDisposable()

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

        toolbar.setNavigationOnClickListener { viewModel.openMenu() }
        differentTextView.apply {
            setOnClickListener(onClickListener)
            setOnTouchListener(differentTextViewTouchListener)
        }
        viewModel.remoteDataLiveData.observe(this@CardFragment, Observer { data ->
            data?.let { setData(it) }
        })
    }

    override fun onStart() {
        super.onStart()

        Observable.interval(0, DIFFERENT_PERIOD_UPDATE, TimeUnit.MILLISECONDS, schedulerSingleThread)
                .map { START_DATE.timeDifferent() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { differentTextView.text = it }
                .bindTo(disposable)
    }

    override fun onStop() {
        disposable.clear()
        super.onStop()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
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
            GlideApp.with(this@CardFragment)
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
}
