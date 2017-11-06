package com.eliseev.newsagent

import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.eliseev.newsagent.di.AppComponent
import com.eliseev.newsagent.di.AppModule
import com.eliseev.newsagent.di.DaggerAppComponent
import com.eliseev.newsagent.di.Injector
import com.eliseev.newsagent.util.createArticleList
import com.eliseev.newsagent.util.waitForPendingBindings
import com.google.gson.Gson
import it.cosenonjaviste.daggermock.DaggerMockRule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MockedTest {

    @Suppress("unused")
    @get:Rule
    val daggerRule = MockWebServerRule()

    @Suppress("MemberVisibilityCanPrivate")
    @get:Rule
    val activityRule = IntentsTestRule(NewsListActivity::class.java, true, false)

    private val webServer = MockWebServer()
    private val articleList = createArticleList(10)

    @Before
    fun setUp() {
        webServer.enqueue(MockResponse().setBody(Gson().toJson(articleList)))
        activityRule.launchActivity(null)
    }

    @Test
    fun list() {
        articleList.articles.forEachIndexed { index, article ->
            onView(withId(R.id.newsList)).perform(scrollToPosition<RecyclerView.ViewHolder>(index))
            waitForPendingBindings()
            onView(withText(article.title)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun details() {
        onView(withId(R.id.newsList)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withText(articleList[0].description)).check(matches(isDisplayed()))
    }

    @Test
    fun pressHome() {
        onView(withId(R.id.newsList)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withText(articleList[0].description)).check(matches(isDisplayed()))
        onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description)).perform(click())
        onView(withText(articleList[0].description)).check(doesNotExist())
    }

    @Test
    fun pressBack() {
        onView(withId(R.id.newsList)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withText(articleList[0].description)).check(matches(isDisplayed()))
        Espresso.pressBack()
        onView(withText(articleList[0].description)).check(doesNotExist())
    }

    inner class MockWebServerRule : DaggerMockRule<AppComponent>(AppComponent::class.java) {

        init {
            customizeBuilder<DaggerAppComponent.Builder> {
                it.appModule(AppModule(webServer.url("/").toString()))
            }
            set { Injector.component = it }
        }
    }
}