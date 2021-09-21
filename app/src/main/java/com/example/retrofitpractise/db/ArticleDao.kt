package com.example.retrofitpractise.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.retrofitpractise.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("SELECT * FROM article")
    fun getAllArticles(): LiveData<List<Article>>
}