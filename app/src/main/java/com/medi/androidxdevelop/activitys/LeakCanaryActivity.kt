package com.medi.androidxdevelop.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.utils.SingleInstance

class LeakCanaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leak_canary)
        SingleInstance.setContext(this)
    }
}

