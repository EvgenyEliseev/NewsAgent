package com.eliseev.newsagent.di

import com.eliseev.newsagent.NewsListActivity
import com.eliseev.newsagent.NewsStoryActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun inject(obj: NewsListActivity)
    fun inject(obj: NewsStoryActivity)
}