package com.eugenetereshkov.withme

import android.content.SharedPreferences
import androidx.core.content.edit

interface IUserConfig {
    var id: String
    var name: String
    var rememberMe: Boolean
    var login: Boolean
}

class UserConfig(
        private val sharedPreferences: SharedPreferences
) : IUserConfig {

    private companion object {
        private const val ID_USER = "id_user"
        private const val NAME = "name"
        private const val REMEMBER_ME = "remember_me"
        private const val LOGIN = "login"
    }

    override var id: String
        get() = sharedPreferences.getString(ID_USER, "")
        set(value) {
            sharedPreferences.edit { putString(ID_USER, value) }
        }

    override var name: String
        set(value) {
            sharedPreferences.edit { putString(NAME, value) }
        }
        get() = sharedPreferences.getString(NAME, "")

    override var rememberMe: Boolean
        get() = sharedPreferences.getBoolean(REMEMBER_ME, false)
        set(value) {
            sharedPreferences.edit { putBoolean(REMEMBER_ME, value) }
        }

    override var login: Boolean
        get() = sharedPreferences.getBoolean(LOGIN, false)
        set(value) {
            sharedPreferences.edit { putBoolean(LOGIN, value) }
        }
}