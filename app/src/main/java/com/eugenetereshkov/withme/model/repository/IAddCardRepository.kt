package com.eugenetereshkov.withme.model.repository

import com.eugenetereshkov.withme.model.data.FirebaseDataSource
import com.eugenetereshkov.withme.model.data.UploadImage
import com.eugenetereshkov.withme.presentation.addcard.AddCardViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface IAddCardRepository {
    fun addImageToServer(url: String): Observable<UploadImage>
    fun addCard(data: AddCardViewModel.Card): Completable
}

class AddCardRepository(
        private val firebaseDataSource: FirebaseDataSource
) : IAddCardRepository {

    override fun addImageToServer(url: String): Observable<UploadImage> =
            firebaseDataSource.uploadImage(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    override fun addCard(data: AddCardViewModel.Card): Completable {
        return firebaseDataSource.saveCard(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}