package com.example.retrofitpractise.retrofit

import com.example.retrofitpractise.key.ApiKey.Companion.key
import com.example.retrofitpractise.model.ArticleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") countryCode: String = "ua",
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = key
    ): Response<ArticleResponse>

    @GET("v2/everything")
    suspend fun getTeslaNews(
        @Query("q") car: String = "tesla",
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = key
    ): Response<ArticleResponse>

    @GET("v2/everything")
    suspend fun getSearhNews(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = key
    ): Response<ArticleResponse>
}