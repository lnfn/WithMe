package com.eugenetereshkov.withme.ui.drawer


import android.os.Bundle
import android.view.View
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.presentation.NavigationDrawerViewModel
import com.eugenetereshkov.withme.ui.global.BaseFragment
import kotlinx.android.synthetic.main.fragment_navigation_drawer.*
import org.koin.android.ext.android.inject


class NavigationDrawerFragment : BaseFragment(), NavigationDrawerView {

    override val idResLayout: Int = R.layout.fragment_navigation_drawer

    private val viewModel: NavigationDrawerViewModel by inject()
    private val itemClickListener = { view: View ->
        when (view.id) {
            textViewAddCardItem.id -> viewModel.onMenuItemClicked(view.tag as MenuItem)
            textViewHistory.id -> viewModel.onMenuItemClicked(view.tag as MenuItem)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        textViewAddCardItem.apply {
            tag = MenuItem.CardItem()
            setOnClickListener(itemClickListener)
        }

        textViewHistory.apply {
            tag = MenuItem.History()
            setOnClickListener(itemClickListener)
        }
    }

    override fun onScreenChanged(item: MenuItem) {
    }

    sealed class MenuItem {
        class CardItem : MenuItem()
        class History : MenuItem()
    }
}
