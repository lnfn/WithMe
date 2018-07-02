package com.eugenetereshkov.withme.ui.history


import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
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
    private val adapter by lazy {
        HistoryAdapter { switch, _, data ->
            context?.let {
                val background = imagePreview.rootView.getBlurredScreenDrawable(it)
                GlideApp.with(it)
                        .asBitmap()
                        .load(data.image)
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                                return false
                            }

                            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                if (resource != null) {
                                    val palette = Palette.from(resource).generate()
                                    messageLayout.setBackgroundColor(palette.getMutedColor(ContextCompat.getColor(it, R.color.white)))
                                }
                                return false
                            }

                        })
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onLoadCleared(placeholder: Drawable?) {
                                (previewImageView.drawable as BitmapDrawable).bitmap.recycle()
                            }

                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                vibrate()
                                imagePreview.background = background
                                textViewMessage.text = data.message
                                previewImageView.setImageBitmap(resource)
                                imagePreview.isVisible = switch
                            }
                        })
                return@let
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
                            if (imagePreview.isVisible) {
                                imagePreview.isGone = true
                            }
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
}
