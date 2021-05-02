package com.example.listpeople.adapter

import android.app.Activity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class LazyLoadScrollListener() : RecyclerView.OnScrollListener() {

    private val visibleThreshold = 5 // On last how many items we need to trigger to load more data

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
        layoutManager?.let {
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
            val itemCount: Int = layoutManager.itemCount


            if ((lastVisiblePosition + visibleThreshold) > itemCount) {
                onLoadMore(recyclerView)
            }
        }
    }

    abstract fun onLoadMore(view: RecyclerView?)
}