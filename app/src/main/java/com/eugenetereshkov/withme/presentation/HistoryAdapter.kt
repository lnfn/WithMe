package com.eugenetereshkov.withme.presentation

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eugenetereshkov.withme.GlideApp
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.extension.inflate
import com.eugenetereshkov.withme.presentation.history.HistoryViewModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_history.*


class HistoryAdapter : ListAdapter<HistoryViewModel.CardData, HistoryAdapter.ViewHolder>(HistoryDiffUtilsCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(parent.inflate(R.layout.item_history))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
            override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: HistoryViewModel.CardData) {
            textView.text = item.message

            GlideApp.with(itemView.context)
                    .load(item.image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(imageView)
        }
    }

    object HistoryDiffUtilsCallBack : DiffUtil.ItemCallback<HistoryViewModel.CardData>() {
        override fun areItemsTheSame(oldItem: HistoryViewModel.CardData, newItem: HistoryViewModel.CardData): Boolean =
                oldItem.createdAt.time == newItem.createdAt.time

        override fun areContentsTheSame(oldItem: HistoryViewModel.CardData, newItem: HistoryViewModel.CardData): Boolean =
                oldItem == newItem
    }
}
