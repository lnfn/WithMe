package com.eugenetereshkov.withme.presentation.history

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.presentation.addcard.AddCardViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*


class HistoryViewModel(
        private val router: Router
) : ViewModel() {

    val historyLiveData by lazy {
        MutableLiveData<List<CardData>>().also {
            getHistory()
            getUpdate()
        }
    }

    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null
    private var data = mutableListOf<CardData>()


    override fun onCleared() {
        listenerRegistration?.remove()
        super.onCleared()
    }

    private fun getUpdate() {
        listenerRegistration = firestore.collection(AddCardViewModel.CARDS_COLLECTION)
                .addSnapshotListener { snapshots, e ->

                    Timber.d("Firestore Event")

                    if (e != null) return@addSnapshotListener

                    snapshots?.documentChanges?.forEach { dc ->
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                            }
                            DocumentChange.Type.MODIFIED -> {
                            }
                            DocumentChange.Type.REMOVED -> {
                                data.removeAll { it.id == dc.document.id }
                                historyLiveData.postValue(data)
                            }
                        }
                    }
                }
    }

    private fun getHistory() {
        firestore.collection(AddCardViewModel.CARDS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val cardList = it.result.documents.flatMap {
                            listOf(
                                    CardData(
                                            id = it.id,
                                            image = it.data?.get("image").toString(),
                                            message = it.data?.get("message").toString(),
                                            createdAt = it.data?.get("createdAt") as Date
                                    )
                            )
                        }
                        data = cardList as MutableList<CardData>
                        historyLiveData.postValue(data)
                    } else {
                        router.showSystemMessage(it.exception?.message.orEmpty())
                    }
                }
    }

    fun onBackPressed() {
        router.exit()
    }

    data class CardData(
            val id: String,
            val image: String,
            val message: String,
            val createdAt: Date
    )
}