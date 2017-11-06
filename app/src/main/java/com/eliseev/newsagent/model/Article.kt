package com.eliseev.newsagent.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Article(

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("urlToImage")
    val image: String,

    @SerializedName("publishedAt")
    val date: Date
)

data class ArticleList(

    @SerializedName("articles")
    val articles: List<Article>

) {
    val size: Int
        inline get() = articles.size

    operator fun get(index: Int) = articles[index]
}