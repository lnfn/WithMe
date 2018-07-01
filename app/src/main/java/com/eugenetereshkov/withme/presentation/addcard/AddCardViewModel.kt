package com.eugenetereshkov.withme.presentation.addcard

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.extension.bindTo
import com.eugenetereshkov.withme.model.repository.IAddCardRepository
import com.eugenetereshkov.withme.model.system.ResourceManager
import io.reactivex.disposables.CompositeDisposable
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.*


class AddCardViewModel(
        private val router: Router,
        private val resourceManager: ResourceManager,
        private val addCardRepository: IAddCardRepository
) : ViewModel() {

    companion object {
        const val ADD_CARD_RESULT = 1
        const val CARDS_COLLECTION = "cards_collection"
    }

    val loadingLiveData = MutableLiveData<Boolean>()
    val uploadProgressLiveData = MutableLiveData<Int>()

    private val disposable = CompositeDisposable()
    private val newCard = Card()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    fun uploadImageToServer(url: String) {
        addCardRepository.addImageToServer(url)
                .doOnSubscribe { loadingLiveData.value = true }
                .doOnTerminate { loadingLiveData.value = false }
                .subscribe(
                        { uploadImage ->
                            val (imageUrl, progress) = uploadImage
                            uploadProgressLiveData.value = progress

                            if (imageUrl.isNotEmpty()) {
                                Timber.d(imageUrl)
                                newCard.image = imageUrl
                            }
                        },
                        this::showError
                )
                .bindTo(disposable)
    }

    fun saveCard(message: String) {
        newCard.message = message
        addCardRepository.addCard(newCard)
                .doOnSubscribe { loadingLiveData.value = true }
                .doOnTerminate { loadingLiveData.value = false }
                .subscribe(
                        {
                            router.showSystemMessage(resourceManager.getString(R.string.saved))
                            router.exitWithResult(ADD_CARD_RESULT, Unit)
                        },
                        this::showError
                )
                .bindTo(disposable)
    }

    fun onBackPressed() {
        router.exit()
    }

    private fun showError(t: Throwable) {
        router.showSystemMessage(resourceManager.getString(R.string.error))
    }

    class Card {
        var image: String = ""
        var message: String = ""
        val createdAt: Date = Date()
    }
}