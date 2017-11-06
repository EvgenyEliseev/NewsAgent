package com.eliseev.newsagent.util

import android.databinding.BindingAdapter
import android.text.TextUtils
import android.widget.ImageView
import com.squareup.picasso.Picasso

object ImageBindingAdapter {

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageUrl(view: ImageView, url: String?) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(view.context).load(url).into(view)
        }
    }
}