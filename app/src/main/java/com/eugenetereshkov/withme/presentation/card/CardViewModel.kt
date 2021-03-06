package com.eugenetereshkov.withme.presentation.card

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.presentation.addcard.AddCardViewModel
import com.eugenetereshkov.withme.presentation.global.GlobalMenuController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import ru.terrakok.cicerone.Router


class CardViewModel(
        private val firebaseRemoteConfig: FirebaseRemoteConfig,
        private val router: Router,
        private val globalMenuController: GlobalMenuController
) : ViewModel() {

    val remoteDataLiveData = MutableLiveData<RemoteData>()

    init {
        router.setResultListener(AddCardViewModel.ADD_CARD_RESULT) {
            getLastCard()
        }

        initRemoteConf()
    }

    override fun onCleared() {
        router.removeResultListener(AddCardViewModel.ADD_CARD_RESULT)
        super.onCleared()
    }

    fun openMenu() {
        globalMenuController.open()
    }

    fun onBackPressed() {
        router.exit()
    }

    private fun initRemoteConf() {
        val cacheExpiration: Long = if (firebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) 0 else 3600
        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activateFetched()
                        getLastCard()
                    }
                }
    }

    private fun getLastCard() {
        FirebaseFirestore.getInstance()
                .collection(AddCardViewModel.CARDS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result.documents.getOrNull(0)?.let {
                            val data = RemoteData(
                                    showPromo = firebaseRemoteConfig.getBoolean("show_back_promo"),
                                    backPromo = it.data?.get("image").toString(),
                                    message = it.data?.get("message").toString(),
                                    colorDifferent = firebaseRemoteConfig.getString("colorDifferent")
                            )
                            remoteDataLiveData.postValue(data)
                        }
                    } else {
                        router.showSystemMessage(it.exception?.message.orEmpty())
                    }
                }
    }

    data class RemoteData(
            val showPromo: Boolean,
            val backPromo: String,
            val message: String,
            val colorDifferent: String
    )
}
