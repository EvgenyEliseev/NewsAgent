package com.eliseev.newsagent

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.eliseev.newsagent.adapter.ArticlesAdapter
import com.eliseev.newsagent.di.Injector
import com.eliseev.newsagent.service.NewsService
import kotlinx.android.synthetic.main.activity_main.newsList
import kotlinx.android.synthetic.main.activity_main.toolbar
import javax.inject.Inject

class NewsListActivity : AppCompatActivity() {

    @Inject
    lateinit var service: NewsService

    private val adapter = ArticlesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.component.inject(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        newsList.adapter = adapter

        service.articles[BuildConfig.NEWS_API_SOURCE_ID]?.observe(this, Observer {
            it?.let {
                adapter.setList(it.articles)
            }
        })

        service.error.observe(this, Observer { error ->
            Snackbar.make(newsList, error?.message.toString(), Snackbar.LENGTH_LONG).show()
        })

        adapter.selectedItemIndex.observe(this, Observer {
            it?.let {
                startActivity(NewsStoryActivity.createIntent(this, it))
            }
        })
    }
}
