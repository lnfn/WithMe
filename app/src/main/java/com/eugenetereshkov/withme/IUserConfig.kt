package com.eugenetereshkov.withme

import android.content.SharedPreferences
import androidx.content.edit

interface IUserConfig {
    var name: String
}

class UserConfig(
        private val sharedPreferences: SharedPreferences
) : IUserConfig {
    override var name: String
        set(value) {
            sharedPreferences.edit { putString("name", value) }
        }
        get() = sharedPreferences.getString("name", "")
}