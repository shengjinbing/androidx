package com.medi.androidxdevelop.views.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.medi.androidxdevelop.R
import com.tencent.mars.xlog.Log

class TestAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // 预加载回调
    var onPreload: (() -> Unit)? = null
    // 预加载偏移量
    var preloadItemCount = 0
    // 列表滚动状态
    private var scrollState = SCROLL_STATE_IDLE
    // 增加预加载状态标记位
    var isPreloading = false

    private var listData: ArrayList<TestData> = ArrayList()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // 更新滚动状态
                scrollState = newState
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }


    override fun getItemCount(): Int = listData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_test, parent, false)
        return TestViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("BBBBB","onBindViewHolder================$position")

        checkPreload(position)
        val testViewHolder = holder as? TestViewHolder
        testViewHolder?.run {
            title.text = listData[position].title
        }
    }

    fun updateData(datas: List<TestData>) {
        listData.addAll(datas)
        notifyDataSetChanged()
    }

    // 判断是否进行预加载
    private fun checkPreload(position: Int) {
        if (onPreload != null
            && position == (itemCount - 1 - preloadItemCount).coerceAtLeast(0)// 索引值等于阈值
            && scrollState != SCROLL_STATE_IDLE // 列表正在滚动
            && !isPreloading // 预加载不在进行中
        ) {
            isPreloading = true // 表示正在执行预加载
            onPreload?.invoke()
            Log.d("BBBBB","checkPreload--------$position")
        }
    }
}

class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.tv_title)
}

data class TestData(val title: String, val path: String)