package com.eugenetereshkov.withme.presentation

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.Screens
import com.eugenetereshkov.withme.model.data.IUserConfig
import com.eugenetereshkov.withme.model.system.ResourceManager
import com.google.firebase.auth.FirebaseAuth
import ru.terrakok.cicerone.Router
import timber.log.Timber


class MainViewModel(
        private val router: Router,
        private val userConfig: IUserConfig,
        private val isAuth: Boolean,
        private val resourceManager: ResourceManager
) : ViewModel() {

    val firstViewAttachLiveData = MutableLiveData<Unit>()

    private val firebaseAuth = FirebaseAuth.getInstance()

    init {
        checkFirebaseAuth()
    }

    private fun checkFirebaseAuth() {
        firebaseAuth.currentUser?.let {
            firebaseAuth.signInAnonymously()
                    .addOnSuccessListener {
                        Timber.d(firebaseAuth.currentUser.toString())
                        checkAuth()
                    }
                    .addOnFailureListener { Timber.d(it.message) }
        } ?: checkAuth()
    }

    private fun checkAuth() {
        if (userConfig.login) {
            if (isAuth || userConfig.rememberMe) {
                firstViewAttachLiveData.value = Unit
                router.showSystemMessage(resourceManager.getString(R.string.hi, userConfig.name))
                return
            }
        }

        router.newRootScreen(Screens.LAUNCH_SCREEN)
    }
}
