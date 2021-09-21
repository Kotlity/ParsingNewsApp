package com.example.retrofitpractise.viewmodelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.retrofitpractise.repository.NewsRepository
import com.example.retrofitpractise.viewmodel.NewsViewModel

class NewsViewModelFactory(val newsRepository: NewsRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository) as T
    }

}