package com.eugenetereshkov.withme.extension

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


fun Disposable.bindTo(disposable: CompositeDisposable) {
    disposable.add(this)
}

fun ViewGroup.inflate(@LayoutRes idRes: Int): View =
        LayoutInflater.from(this.context).inflate(idRes, this, false)
