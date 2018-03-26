package com.eugenetereshkov.withme.presentation.global

import android.arch.lifecycle.ViewModel


abstract class BaseViewModel : ViewModel() {
    private var firstAttach = true

    init {
        if (firstAttach) {
            firstAttach = false
            this.onFirstAttach()
        }
    }

    open fun onFirstAttach() {}
}