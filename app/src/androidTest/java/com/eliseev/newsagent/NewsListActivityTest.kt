package com.eliseev.newsagent

import android.app.Activity
import android.app.Instrumentation
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.pm.ActivityInfo
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.eliseev.newsagent.di.AppComponent
import com.eliseev.newsagent.di.AppModule
import com.eliseev.newsagent.di.Injector
import com.eliseev.newsagent.model.ArticleList
import com.eliseev.newsagent.service.NewsService
import com.eliseev.newsagent.util.createArticleList
import com.eliseev.newsagent.util.waitForPendingBindings
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import it.cosenonjaviste.daggermock.DaggerMock
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import java.util.Date

@RunWith(AndroidJUnit4::class)
class NewsListActivityTest {

    @Rule
    @JvmField
    val daggerRule = DaggerMock.rule<AppComponent>(AppModule()) {
        set { Injector.component = it }
    }

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val activityRule = IntentsTestRule(NewsListActivity::class.java, true, false)

    @Mock
    private lateinit var newsService: NewsService

    private val articleMap = HashMap<String, LiveData<ArticleList>>()
    private val articleData = MutableLiveData<ArticleList>()
    private val errorData = MutableLiveData<Throwable>()

    @Before
    fun setUp() {
        articleMap[BuildConfig.NEWS_API_SOURCE_ID] = articleData
        whenever(newsService.articles).thenReturn(articleMap)
        whenever(newsService.error).thenReturn(errorData)
    }

    @Test
    fun loadNews() {
        articleData.value = createArticleList(3)
        activityRule.launchActivity(null)

        articleData.value!!.articles.map {
            onView(withText(it.title)).check(matches(isDisplayed()))
        }
        verify(newsService, times(1)).articles
    }

    @Test
    fun articlesChange() {
        articleData.value = createArticleList(1)
        activityRule.launchActivity(null)

        onView(withText(articleData.value?.get(0)?.title)).check(matches(isDisplayed()))

        InstrumentationRegistry.getInstrumentation().runOnMainSync({
            articleData.value = createArticleList(1, "another test")
        })
        waitForPendingBindings()
        onView(withText(articleData.value?.get(0)?.title)).check(matches(isDisplayed()))
    }

    @Test
    fun loadError() {
        activityRule.launchActivity(null)

        val errorText = Date().toString()
        errorData.value = Throwable(errorText)
        onView(withId(android.support.design.R.id.snackbar_text)).check(matches(withText(errorText)))
    }

    @Test
    fun selectArticle() {
        articleData.value = createArticleList(3)
        activityRule.launchActivity(null)

        intending(hasComponent(hasClassName(NewsStoryActivity::class.java.canonicalName)))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        onView(withId(R.id.newsList)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
        intended(allOf(
                hasComponent(hasClassName(NewsStoryActivity::class.java.canonicalName)),
                hasExtra(NewsStoryActivity.EXTRA_ARTICLE_ID, 1)
        ))
    }

    @Test
    fun landscape() {
        articleData.value = createArticleList(3)
        activityRule.launchActivity(null)

        activityRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        articleData.value!!.articles.mapIndexed { index, article ->
            onView(withId(R.id.newsList)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(index))
            waitForPendingBindings()
            onView(withText(article.title)).check(matches(isDisplayed()))
        }
    }
}
