package com.eugenetereshkov.withme

import android.app.Activity


class NewsPresenter(
        private val newsRepository: INewsRepository,
        private val resourceManager: ResourceManager,
        private val idNews: String,
        private val activity: Activity
) {
    fun getNews() = newsRepository.getNews().map {
        "${activity::class.java.simpleName} id:$idNews $it ${resourceManager.getString(R.string.app_name)}"
    }
}