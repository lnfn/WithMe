package com.eugenetereshkov.withme.ui.drawer

import com.eugenetereshkov.withme.entity.MenuItem

interface NavigationDrawerView {
    fun onScreenChanged(@MenuItem item: String)
}