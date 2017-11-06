package com.eliseev.newsagent

import android.app.Activity
import android.app.Instrumentation
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasAction
import android.support.test.espresso.intent.matcher.IntentMatchers.hasData
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import com.eliseev.newsagent.di.AppComponent
import com.eliseev.newsagent.di.AppModule
import com.eliseev.newsagent.di.Injector
import com.eliseev.newsagent.model.ArticleList
import com.eliseev.newsagent.service.NewsService
import com.eliseev.newsagent.util.createArticleList
import com.nhaarman.mockito_kotlin.whenever
import it.cosenonjaviste.daggermock.DaggerMock
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
class NewsStoryActivityTest {

    @Suppress("unused")
    @get:Rule
    val daggerRule = DaggerMock.rule<AppComponent>(AppModule()) {
        set { Injector.component = it }
    }

    @Suppress("unused")
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    @Suppress("MemberVisibilityCanPrivate")
    @get:Rule
    val activityRule = IntentsTestRule(NewsStoryActivity::class.java, true, false)

    @Mock
    private lateinit var newsService: NewsService

    private val articleMap = HashMap<String, LiveData<ArticleList>>()
    private val articleData = MutableLiveData<ArticleList>()

    @Before
    fun setUp() {
        articleMap[BuildConfig.NEWS_API_SOURCE_ID] = articleData
        whenever(newsService.articles).thenReturn(articleMap)

        articleData.value = createArticleList(1)
    }

    @Test
    fun display() {
        activityRule.launchActivity(NewsStoryActivity.createIntent(InstrumentationRegistry.getTargetContext(), 0))
        onView(withText(articleData.value?.get(0)?.title)).check(matches(isDisplayed()))
        onView(withText(articleData.value?.get(0)?.description)).check(matches(isDisplayed()))
        onView(withText(R.string.article_load_error)).check(matches(not(isCompletelyDisplayed())))
    }

    @Test
    fun missingArticle() {
        activityRule.launchActivity(NewsStoryActivity.createIntent(InstrumentationRegistry.getTargetContext(), 10))
        onView(withText(R.string.article_load_error)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun openBrowser() {
        activityRule.launchActivity(NewsStoryActivity.createIntent(InstrumentationRegistry.getTargetContext(), 0))
        intending(allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(articleData.value?.get(0)?.url)))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        onView(withText(R.string.read_more)).perform(click())
        intended(allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(articleData.value?.get(0)?.url))
        )
    }
}