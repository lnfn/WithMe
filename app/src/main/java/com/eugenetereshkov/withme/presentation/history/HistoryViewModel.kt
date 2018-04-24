package com.eugenetereshkov.withme.presentation.history

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.presentation.addcard.AddCardViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ru.terrakok.cicerone.Router
import java.util.*

class HistoryViewModel(
        private val router: Router
) : ViewModel() {

    val historyLiveData = MutableLiveData<List<CardData>>()

    fun getHistory() {
        FirebaseFirestore.getInstance().collection(AddCardViewModel.CARDS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val cardList = it.result.documents.flatMap {
                            listOf(
                                    CardData(
                                            image = it.data?.get("image").toString(),
                                            message = it.data?.get("message").toString(),
                                            createdAt = it.data?.get("createdAt") as Date
                                    )
                            )
                        }

                        historyLiveData.value = cardList
                    } else {
                        router.showSystemMessage(it.exception?.message.orEmpty())
                    }
                }
    }

    fun onBackPressed() {
        router.exit()
    }

    data class CardData(
            val image: String,
            val message: String,
            val createdAt: Date
    )
}