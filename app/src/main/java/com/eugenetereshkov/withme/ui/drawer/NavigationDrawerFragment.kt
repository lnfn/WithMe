package com.eugenetereshkov.withme.ui.drawer


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.entity.MenuItem
import com.eugenetereshkov.withme.presentation.NavigationDrawerViewModel
import com.eugenetereshkov.withme.ui.global.BaseFragment
import com.eugenetereshkov.withme.ui.global.ConfirmDialogFragment
import kotlinx.android.synthetic.main.fragment_navigation_drawer.*
import org.koin.android.ext.android.inject


class NavigationDrawerFragment : BaseFragment(), NavigationDrawerView,
        ConfirmDialogFragment.OnClickListener {

    private companion object {
        private const val CONFIRM_LOGOUT_TAG = "confirm_logout_tag"
    }

    override val idResLayout: Int = R.layout.fragment_navigation_drawer

    private val viewModel: NavigationDrawerViewModel by inject()
    private val itemClickListener = { view: View ->
        when (view.id) {
            textViewAddCardItem.id, textViewHistory.id, textViewLogout.id -> viewModel.onMenuItemClicked(view.tag as String)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        textViewAddCardItem.apply {
            tag = MenuItem.CARD
            setOnClickListener(itemClickListener)
        }

        textViewHistory.apply {
            tag = MenuItem.HISTORY
            setOnClickListener(itemClickListener)
        }

        textViewLogout.apply {
            tag = MenuItem.LOGOUT
            setOnClickListener(itemClickListener)
        }

        viewModel.logoutLiveData.observe(this, Observer {
            ConfirmDialogFragment.newInstants(
                    msg = getString(R.string.logout_question),
                    tag = CONFIRM_LOGOUT_TAG
            ).show(childFragmentManager, CONFIRM_LOGOUT_TAG)
        })
    }

    override fun dialogConfirm(tag: String) {
        viewModel.logout()
    }

    override fun onScreenChanged(@MenuItem item: String) {
    }
}
