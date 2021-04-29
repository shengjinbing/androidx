package com.medi.androidxdevelop.activitys.ui

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.views.adapter.TestAdapter
import com.medi.androidxdevelop.views.adapter.TestData
import com.tencent.mars.xlog.Log
import kotlinx.android.synthetic.main.activity_recyc_view.*

class RecycViewActivity : AppCompatActivity() {
    private var testAdapter: TestAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyc_view)
        initView()
        initData()
    }

    private fun initView() {
        testAdapter = TestAdapter().apply {
            // 在距离列表尾部还有2个表项的时候预加载
            preloadItemCount = 2
            onPreload = {
                // 预加载业务逻辑
                loadMore()
            }
        }
        recyclerview.apply {
            layoutManager = GridLayoutManager(this@RecycViewActivity, 2)
            adapter = testAdapter
        }
    }

    private fun initData() {
        var listData: ArrayList<TestData> = ArrayList()
        for (i in 0..15) {
            listData.add(TestData("看好你$i", ""))
        }
        testAdapter?.updateData(listData)
    }

    private fun loadMore() {
        Handler().postDelayed({
            var listData: ArrayList<TestData> = ArrayList()
            val itemCount = testAdapter?.itemCount!!
            Log.d("BBBBB","请求网络了$itemCount")
            for (i in 0..15) {
                listData.add(TestData("看好你${i + itemCount}", ""))
            }
            //业务层中控制该标记位，列表内容请求成功、失败或者超时时将该标记位置为false。
            testAdapter?.isPreloading = false
            testAdapter?.updateData(listData)
        },1000)

    }
}