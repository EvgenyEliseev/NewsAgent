package com.eliseev.newsagent.service

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.eliseev.newsagent.model.ArticleList
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Collections

class NewsServiceTest {

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    @Mock
    private lateinit var newsApi: NewsApi

    @Mock
    private lateinit var call: Call<ArticleList>

    private lateinit var service: NewsService

    @Before
    fun setUp() {
        whenever(newsApi.getArticles(anyString())).thenReturn(call)
        service = NewsServiceImpl(newsApi)
    }

    @Test
    fun observeNewSource() {
        val result = ArticleList(Collections.emptyList())
        doAnswer {
            it.getArgument<Callback<ArticleList>>(0).onResponse(call, Response.success(result))
        }.whenever(call).enqueue(ArgumentMatchers.any())

        val observer = mock<Observer<ArticleList>>()
        service.articles["test"]?.observeForever(observer)

        verify(observer, times(1)).onChanged(result)
    }

    @Test
    fun refreshSource() {
        val result = ArticleList(Collections.emptyList())
        doAnswer {
            it.getArgument<Callback<ArticleList>>(0).onResponse(call, Response.success(result))
        }.whenever(call).enqueue(ArgumentMatchers.any())

        val observer = mock<Observer<ArticleList>>()
        service.articles["test"]?.observeForever(observer)
        service.refreshArticles("test")

        verify(observer, times(2)).onChanged(result)
    }

    @Test
    fun observeError() {
        val error = Throwable()
        doAnswer {
            it.getArgument<Callback<ArticleList>>(0).onFailure(call, error)
        }.whenever(call).enqueue(ArgumentMatchers.any())

        val dataObserver = mock<Observer<ArticleList>>()
        service.articles["test"]?.observeForever(dataObserver)

        val errorObserver = mock<Observer<Throwable>>()
        service.error.observeForever(errorObserver)

        verify(dataObserver, never()).onChanged(any())
        verify(errorObserver, times(1)).onChanged(error)
    }
}