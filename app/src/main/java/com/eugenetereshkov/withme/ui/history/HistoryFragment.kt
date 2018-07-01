package com.eugenetereshkov.withme.ui.history


import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.glide.GlideApp
import com.eugenetereshkov.withme.presentation.HistoryAdapter
import com.eugenetereshkov.withme.presentation.history.HistoryViewModel
import com.eugenetereshkov.withme.ui.global.BaseFragment
import com.eugenetereshkov.withme.ui.global.getBlurredScreenDrawable
import kotlinx.android.synthetic.main.fragment_history.*
import org.koin.android.ext.android.inject


class HistoryFragment : BaseFragment() {

    private companion object {
        private const val RECYCLER_VIEW_STATE = "recycler_view_state"
    }

    override val idResLayout: Int = R.layout.fragment_history

    private val viewModel: HistoryViewModel by inject()
    private lateinit var dialog: DialogFragment
    private val adapter by lazy {
        HistoryAdapter { switch, view, data ->
            context?.let {
                val background = imagePreview.rootView.getBlurredScreenDrawable(it)
                GlideApp.with(it)
                        .asBitmap()
                        .load(data.image)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                vibrate()
                                imagePreview.background = background
                                previewImageView.setImageBitmap(resource)
                                imagePreview.isVisible = switch
                            }
                        })
                return@let Unit
            }
        }
    }

    private fun vibrate() {
        context?.let {
            (it.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).run {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrate(30)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.apply {
            title = getString(R.string.history)
            setNavigationOnClickListener { viewModel.onBackPressed() }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = this@HistoryFragment.adapter
            addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean {
                    when (e?.action) {
                        MotionEvent.ACTION_UP -> {
                            if (imagePreview.isVisible) imagePreview.isGone = true
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }

        viewModel.historyLiveData.observe(this, Observer { history: List<HistoryViewModel.CardData>? ->
            history?.let {
                adapter.submitList(it)
                savedInstanceState?.let {
                    recyclerView.layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_VIEW_STATE))
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(RECYCLER_VIEW_STATE, recyclerView.layoutManager.onSaveInstanceState())
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }
}
