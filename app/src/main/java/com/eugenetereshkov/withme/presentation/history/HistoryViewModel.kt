package com.eugenetereshkov.withme.presentation.history

import android.arch.lifecycle.ViewModel
import ru.terrakok.cicerone.Router

class HistoryViewModel(
        private val router: Router
) : ViewModel() {

    fun onBackPressed() {
        router.exit()
    }
}