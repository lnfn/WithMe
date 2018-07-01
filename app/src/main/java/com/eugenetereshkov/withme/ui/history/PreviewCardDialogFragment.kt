package com.eugenetereshkov.withme.ui.history

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.glide.GlideApp
import com.eugenetereshkov.withme.presentation.history.HistoryViewModel
import kotlinx.android.synthetic.main.fragment_preview_card.*


class PreviewCardDialogFragment : DialogFragment() {

    companion object {
        private const val DATA = "data"

        fun newInstance(data: HistoryViewModel.CardData) = PreviewCardDialogFragment().apply {
            arguments = bundleOf(
                    DATA to data
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_preview_card, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        context?.let {
            GlideApp.with(it)
                    .load(arguments?.getParcelable<HistoryViewModel.CardData>(DATA)?.image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(imageView)
        }
    }

}