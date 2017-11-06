package com.eliseev.newsagent.adapter

import android.arch.lifecycle.MutableLiveData
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.eliseev.newsagent.databinding.NewsItemBinding
import com.eliseev.newsagent.model.Article

class ArticlesAdapter : ListAdapter<Article, ArticlesAdapter.ViewHolder>(ArticleDiff()) {

    val selectedItemIndex = MutableLiveData<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.article = getItem(position)
    }

    inner class ViewHolder(private val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        var article: Article? = null
            set(value) {
                field = value
                binding.article = value
            }

        init {
            itemView.setOnClickListener({
                selectedItemIndex.value = adapterPosition
            })
        }
    }
}

private class ArticleDiff : DiffCallback<Article>() {

    override fun areItemsTheSame(old: Article, new: Article): Boolean = (old.url == new.url)

    override fun areContentsTheSame(old: Article, new: Article): Boolean = (old == new)
}