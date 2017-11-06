package com.eliseev.newsagent.di

import com.eliseev.newsagent.BuildConfig
import com.eliseev.newsagent.service.NewsApi
import com.eliseev.newsagent.service.NewsService
import com.eliseev.newsagent.service.NewsServiceImpl
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
open class AppModule(open val baseUrl: String = BuildConfig.NEWS_API_BASE_URL) {

    @Provides
    @Singleton
    fun provideStoreApi(): NewsApi {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    open fun provideNewsService(api: NewsApi): NewsService = NewsServiceImpl(api)
}