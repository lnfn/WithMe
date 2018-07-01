package com.eugenetereshkov.withme.model.data

import android.net.Uri
import com.eugenetereshkov.withme.presentation.addcard.AddCardViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Completable
import io.reactivex.Observable
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

            val taskSnapshotStorageTask = imageRef.putFile(file)
                    .addOnProgressListener {
                        val progress = 100.0 * it.bytesTransferred / it.totalByteCount
                        emitter.onNext(UploadImage("", progress.toInt()))
                    }
                    .addOnSuccessListener { emitter.onNext(UploadImage(it.uploadSessionUri.toString(), 100)) }
                    .addOnCompleteListener { emitter.onComplete() }
                    .addOnFailureListener { e -> if (!emitter.isDisposed) emitter.onError(e) }

            emitter.setCancellable { taskSnapshotStorageTask.cancel() }
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