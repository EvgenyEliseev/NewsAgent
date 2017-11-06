package com.eliseev.newsagent

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.eliseev.newsagent.databinding.ActivityNewsStoryBinding
import com.eliseev.newsagent.di.Injector
import com.eliseev.newsagent.service.NewsService
import kotlinx.android.synthetic.main.activity_news_story.article_details
import kotlinx.android.synthetic.main.activity_news_story.error_message
import kotlinx.android.synthetic.main.activity_news_story.readMore
import kotlinx.android.synthetic.main.activity_news_story.toolbar
import javax.inject.Inject

class NewsStoryActivity : AppCompatActivity() {

    @Inject
    lateinit var service: NewsService

    private lateinit var binding: ActivityNewsStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.component.inject(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_story)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val articleIndex = intent.getIntExtra(EXTRA_ARTICLE_ID, 0)
        service.articles[BuildConfig.NEWS_API_SOURCE_ID]?.observe(this, Observer {
            val articleList = service.articles[BuildConfig.NEWS_API_SOURCE_ID]?.value
            if (articleList != null && articleIndex < articleList.size) {
                binding.article = articleList[articleIndex]
                error_message.visibility = View.INVISIBLE
                article_details.visibility = View.VISIBLE
            } else {
                error_message.visibility = View.VISIBLE
                article_details.visibility = View.INVISIBLE
            }
        })

        readMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(binding.article?.url)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    companion object {

        @VisibleForTesting
        const val EXTRA_ARTICLE_ID = "extra_article_id"

        fun createIntent(context: Context, articleIndex: Int) : Intent {
            val intent = Intent(context, NewsStoryActivity::class.java)
            intent.putExtra(EXTRA_ARTICLE_ID, articleIndex)
            return intent
        }
    }
}
