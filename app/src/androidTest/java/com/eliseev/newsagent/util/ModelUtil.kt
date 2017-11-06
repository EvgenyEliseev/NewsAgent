package com.eliseev.newsagent.util

import com.eliseev.newsagent.model.Article
import com.eliseev.newsagent.model.ArticleList
import java.util.Date

fun createArticleList(count: Int, nameBase: String = "test") : ArticleList {
    val articles = ArrayList<Article>()
    (0..count).mapTo(articles) { createArticle(it, nameBase) }
    return ArticleList(articles)
}

fun createArticle(index: Int, nameBase: String) : Article {
    val imageUrl: String = if (index % 2 == 0) ""
    else "https://www.google.com/favicon.ico"
    return Article("$nameBase #$index", "Description #$index", "https://www.google.com", imageUrl, Date())
}
