package com.eugenetereshkov.withme.model.system

import android.content.Context
import android.support.annotation.StringRes

class ResourceManager(private val context: Context) {
    fun getString(@StringRes resId: Int): String = context.getString(resId)

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String =
            context.getString(resId, *formatArgs)
}