package com.eliseev.newsagent.service

import android.arch.lifecycle.LiveData
import com.eliseev.newsagent.model.ArticleList

interface NewsService {

    val articles: Map<String, LiveData<ArticleList>>

    val error: LiveData<Throwable>

    fun refreshArticles(sourceId: String)
}