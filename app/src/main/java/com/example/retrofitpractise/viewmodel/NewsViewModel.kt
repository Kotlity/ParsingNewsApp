package com.example.retrofitpractise.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitpractise.additional.Resource
import com.example.retrofitpractise.model.Article
import com.example.retrofitpractise.model.ArticleResponse
import com.example.retrofitpractise.repository.NewsRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository): ViewModel() {

    val breakingNews: MutableLiveData<Resource<ArticleResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: ArticleResponse? = null

    val teslaNews: MutableLiveData<Resource<ArticleResponse>> = MutableLiveData()
    var teslaNewsPage = 1
    var teslaNewsResponse: ArticleResponse? = null

    val searchNews: MutableLiveData<Resource<ArticleResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: ArticleResponse? = null

    init {
        getBreakingNews("ua")
        getTeslaNews("tesla")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun getTeslaNews(car: String) = viewModelScope.launch {
        teslaNews.postValue(Resource.Loading())
        val response = newsRepository.getTeslaNews(car, teslaNewsPage)
        teslaNews.postValue(handleTeslaNewsResponse(response))
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.getSearchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }

    fun getAllArticles() = newsRepository.getAllArticles()

    private fun handleBreakingNewsResponse(response: Response<ArticleResponse>): Resource<ArticleResponse> {
        if (response.isSuccessful) {
            response.body()?.let {  resultResponse ->  // that is the response we got from RetrofitAPI
                breakingNewsPage++
                if (breakingNewsResponse == null) {         // this block of code will be executed
                    breakingNewsResponse = resultResponse   //  if the first page is loaded
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleTeslaNewsResponse(response: Response<ArticleResponse>): Resource<ArticleResponse> {
        if (response.isSuccessful) {
            response.body()?.let {  resultResponse ->
                teslaNewsPage++
                if (teslaNewsResponse == null) {
                    teslaNewsResponse = resultResponse
                } else {
                    val oldArticles = teslaNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(teslaNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<ArticleResponse>): Resource<ArticleResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}