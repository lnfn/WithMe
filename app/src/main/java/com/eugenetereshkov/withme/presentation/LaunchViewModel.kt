package com.eugenetereshkov.withme.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.Screens
import com.eugenetereshkov.withme.model.data.IUserConfig
import com.eugenetereshkov.withme.model.system.ResourceManager
import com.google.firebase.firestore.FirebaseFirestore
import ru.terrakok.cicerone.Router


class LaunchViewModel(
        private val router: Router,
        private val resourceManager: ResourceManager,
        private val userConfig: IUserConfig
) : ViewModel() {

    companion object {
        const val AUTH = "auth"

        private const val USERS = "users"
    }

    val loadingLiveData = MutableLiveData<Boolean>().apply {
        postValue(false)
    }
    val rememberMeLiveData = MutableLiveData<Boolean>().apply {
        postValue(userConfig.rememberMe)
    }

    fun onPasswordEntered(password: String) {
        loadingLiveData.postValue(true)
        FirebaseFirestore.getInstance().collection(USERS)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        loadingLiveData.postValue(false)
                        it.result.firstOrNull { it.data["password"] == password }?.let {
                            userConfig.id = it.id
                            userConfig.name = it.data["name"].toString()
                            userConfig.login = true
                            router.newRootScreen(Screens.MAIN_SCREEN, true)
                        }
                                ?: router.showSystemMessage(resourceManager.getString(R.string.password_fail))
                    } else {
                        router.showSystemMessage(it.exception?.message.orEmpty())
                    }
                }
    }

    fun onRememberMeChanged(checked: Boolean) {
        userConfig.rememberMe = checked
    }
}
