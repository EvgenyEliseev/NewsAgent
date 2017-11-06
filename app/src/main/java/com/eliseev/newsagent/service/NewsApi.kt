package com.eliseev.newsagent.service

import com.eliseev.newsagent.BuildConfig
import com.eliseev.newsagent.model.ArticleList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApi {

    @Headers("X-Api-Key: ${BuildConfig.NEWS_API_KEY}")
    @GET("articles")
    fun getArticles(@Query("source") source: String): Call<ArticleList>
}