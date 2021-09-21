package com.example.retrofitpractise.repository

import com.example.retrofitpractise.db.ArticleDatabase
import com.example.retrofitpractise.model.Article
import com.example.retrofitpractise.retrofit.RetrofitInstance

class NewsRepository(val articleDatabase: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) = RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun getTeslaNews(car: String, pageNumber: Int) = RetrofitInstance.api.getTeslaNews(car, pageNumber)

    suspend fun getSearchNews(searchQuery: String, pageNumber: Int) = RetrofitInstance.api.getSearhNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = articleDatabase.getArticleDao().upsert(article)


    suspend fun delete(article: Article) = articleDatabase.getArticleDao().deleteArticle(article)

    fun getAllArticles() = articleDatabase.getArticleDao().getAllArticles()
}