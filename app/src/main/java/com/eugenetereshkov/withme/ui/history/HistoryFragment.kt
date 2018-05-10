package com.eugenetereshkov.withme.ui.history


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.presentation.HistoryAdapter
import com.eugenetereshkov.withme.presentation.history.HistoryViewModel
import com.eugenetereshkov.withme.ui.global.BaseFragment
import kotlinx.android.synthetic.main.fragment_history.*
import org.koin.android.ext.android.inject


class HistoryFragment : BaseFragment() {

    private companion object {
        private const val RECYCLER_VIEW_STATE = "recycler_view_state"
    }

    override val idResLayout: Int = R.layout.fragment_history

    private val viewModel: HistoryViewModel by inject()
    private val adapter by lazy { HistoryAdapter() }

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
