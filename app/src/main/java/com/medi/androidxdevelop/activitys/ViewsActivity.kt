package com.medi.androidxdevelop.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.activitys.ui.RecycViewActivity
import kotlinx.android.synthetic.main.activity_views.*

class ViewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_views)
        tv_recyclerview.setOnClickListener {
            startActivity(Intent(this, RecycViewActivity::class.java))
        }
    }
}