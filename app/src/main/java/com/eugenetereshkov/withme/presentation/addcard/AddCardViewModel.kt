package com.eugenetereshkov.withme.presentation.addcard

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.ResourceManager
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.io.File


class AddCardViewModel(
        private val router: Router,
        private val resourceManager: ResourceManager
) : ViewModel() {

    val loadingLiveData = MutableLiveData<Boolean>()
    val uploadProgressLiveData = MutableLiveData<Int>()

    private var storageTask: StorageTask<UploadTask.TaskSnapshot>? = null

    fun uploadImageToServer(url: String) {
        loadingLiveData.value = true
        val file = Uri.fromFile(File(url))
        Timber.d(url)
        Timber.d(file.toString())
        val firebaseStorage = FirebaseStorage.getInstance()
        val storageRef = firebaseStorage.reference
        val imageRef = storageRef.child("images/${file.lastPathSegment}")
        val uploadTask = imageRef.putFile(file)

        storageTask = uploadTask.addOnProgressListener({
            val progress = 100.0 * it.bytesTransferred / it.totalByteCount
            uploadProgressLiveData.postValue(progress.toInt())

        }).addOnCompleteListener({
            loadingLiveData.postValue(false)

            if (it.isSuccessful) {
                val downloadUrl = it.result.downloadUrl
                Timber.d(downloadUrl.toString())
            } else {
                router.showSystemMessage(resourceManager.getString(R.string.error))
            }
        })
    }
}