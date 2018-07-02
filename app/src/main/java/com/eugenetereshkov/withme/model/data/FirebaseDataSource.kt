package com.eugenetereshkov.withme.model.data

import android.net.Uri
import com.eugenetereshkov.withme.presentation.addcard.AddCardViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import java.io.File


typealias UploadImage = Pair<String, Int>

interface IFirebaseDataSource {
    fun uploadImage(url: String): Observable<UploadImage>
    fun saveCard(data: AddCardViewModel.Card): Completable
}

class FirebaseDataSource : IFirebaseDataSource {

    private val firebaseStorage = FirebaseStorage.getInstance()
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    override fun uploadImage(url: String): Observable<UploadImage> {
        return Observable.create<UploadImage> { emitter ->
            val file = Uri.fromFile(File(url))
            val storageRef = firebaseStorage.reference
            val imageRef = storageRef.child("images/${file.lastPathSegment}")

            val uploadTask = imageRef.putFile(file)
            val urlTask: Task<Uri> = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                if (!it.isSuccessful) throw it.exception!!
                return@Continuation imageRef.downloadUrl
            })
                    .addOnCompleteListener {
                        Timber.d(it.result.toString())
                        emitter.onNext(UploadImage(it.result.toString(), 100))
                        emitter.onComplete()
                    }
                    .addOnFailureListener { e -> if (!emitter.isDisposed) emitter.onError(e) }

            emitter.setCancellable { uploadTask.cancel() }
        }
    }

    override fun saveCard(data: AddCardViewModel.Card): Completable {
        return Completable.create { emitter ->
            firebaseFirestore.collection(AddCardViewModel.CARDS_COLLECTION)
                    .add(data)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { if (!emitter.isDisposed) emitter.onError(it) }
        }
    }
}