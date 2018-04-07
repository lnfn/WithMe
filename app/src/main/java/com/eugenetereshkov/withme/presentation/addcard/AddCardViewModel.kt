package com.eugenetereshkov.withme.presentation.addcard

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.ResourceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.io.File
import java.util.*


class AddCardViewModel(
        private val router: Router,
        private val resourceManager: ResourceManager
) : ViewModel() {

    companion object {
        const val ADD_CARD_RESULT = 1
        const val CARDS_COLLECTION = "cards_collection"
    }

    val loadingLiveData = MutableLiveData<Boolean>()
    val uploadProgressLiveData = MutableLiveData<Int>()

    private var storageTask: StorageTask<UploadTask.TaskSnapshot>? = null
    private val newCard = Card()

    override fun onCleared() {
        storageTask?.cancel()
        super.onCleared()
    }

    fun uploadImageToServer(url: String) {
        loadingLiveData.value = true
        val file = Uri.fromFile(File(url))
        Timber.d(url)
        Timber.d(file.toString())
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageRef = firebaseStorage.reference
        val imageRef = storageRef.child("images/${file.lastPathSegment}")
        val uploadTask = imageRef.putFile(file)

        storageTask = uploadTask.addOnProgressListener {
            val progress = 100.0 * it.bytesTransferred / it.totalByteCount
            uploadProgressLiveData.postValue(progress.toInt())

        }.addOnCompleteListener {
            loadingLiveData.postValue(false)

            if (it.isSuccessful) {
                val downloadUrl = it.result.downloadUrl
                newCard.image = downloadUrl?.toString().orEmpty()
                Timber.d(downloadUrl.toString())
            } else {
                router.showSystemMessage(resourceManager.getString(R.string.error))
            }
        }
    }

    fun saveCard(message: String) {
        newCard.message = message
        FirebaseFirestore.getInstance().collection(CARDS_COLLECTION)
                .add(newCard)
                .addOnSuccessListener {
                    router.showSystemMessage(resourceManager.getString(R.string.saved))
                    router.exitWithResult(ADD_CARD_RESULT, Unit)
                }
                .addOnFailureListener {
                    router.showSystemMessage(it.message)
                }
    }

    fun onBackPressed() {
        router.exit()
    }


    class Card {
        var image: String = ""
        var message: String = ""
        val createdAt: Date = Date()
    }
}