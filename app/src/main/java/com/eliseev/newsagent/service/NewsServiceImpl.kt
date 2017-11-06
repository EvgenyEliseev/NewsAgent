package com.eliseev.newsagent.service

import android.arch.lifecycle.MutableLiveData
import com.eliseev.newsagent.model.ArticleList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class NewsServiceImpl @Inject constructor(private val newsApi: NewsApi) : NewsService {

    override val articles = DynamicMap<String, CallbackData<ArticleList>>({ CallbackData(newsApi.getArticles(it)) })

    override val error = MutableLiveData<Throwable>()

    override fun refreshArticles(sourceId: String) {
        articles[sourceId]?.call = newsApi.getArticles(sourceId)
    }

    inner class DynamicMap<K, V>(private val defaultValue: (K) -> V): HashMap<K, V>() {

        override fun get(key: K): V? {
            val res = super.get(key)
            if (res != null) {
                return res
            }

            val value = defaultValue(key)
            put(key, value)
            return value
        }
    }

    inner class CallbackData<T>(call: Call<T>) : MutableLiveData<T>(), Callback<T> {

        var call = call
            set(value) {
                field = value
                enqueueIfRequired()
            }

        override fun onActive() {
            enqueueIfRequired()
        }

        override fun onFailure(call: Call<T>?, t: Throwable?) {
            error.value = t
        }

        override fun onResponse(call: Call<T>?, response: Response<T>?) {
            value = response?.body()
        }

        private fun enqueueIfRequired() {
            if (hasActiveObservers() && !call.isExecuted && !call.isCanceled) {
                call.enqueue(this)
            }
        }
    }
}