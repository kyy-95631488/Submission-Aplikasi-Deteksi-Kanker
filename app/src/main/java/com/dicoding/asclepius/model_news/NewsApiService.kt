package com.dicoding.asclepius.model_news

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("q") query: String,
        @Query("category") category: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsResponse>
}

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)
