package com.eugenetereshkov.withme

import android.content.Context
import android.support.annotation.StringRes

class ResourceManager(private val context: Context) {
    fun getString(@StringRes id: Int): String = context.getString(id)
}