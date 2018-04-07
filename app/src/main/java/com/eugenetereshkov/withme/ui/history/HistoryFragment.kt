package com.eugenetereshkov.withme.ui.history


import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.presentation.history.HistoryViewModel
import com.eugenetereshkov.withme.ui.global.BaseFragment
import org.koin.android.ext.android.inject


class HistoryFragment : BaseFragment() {

    override val idResLayout: Int = R.layout.fragment_history

    private val viewModel: HistoryViewModel by inject()

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }
}
