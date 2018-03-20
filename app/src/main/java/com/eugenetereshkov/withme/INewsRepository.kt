package com.eugenetereshkov.withme

interface INewsRepository {
    fun getNews(): List<Int>
}

class NewsRepository : INewsRepository {
    override fun getNews() = (0..10).toList()
}