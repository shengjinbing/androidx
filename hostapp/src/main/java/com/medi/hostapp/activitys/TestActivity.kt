package com.medi.hostapp.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medi.hostapp.R

/**
 * Hook的地方可以分为上半场和下半场：
 * 上半场是启动activity -> AMS的过程：
 * 1.Activity的mInstrumentation
 * 2.AMN的getDefault方法获取到的对象（根据android版本不一样，AMN找不到了）
 * 下半场是启动AMS -> ActivityThread的过程：
 * 1.H的mCallback字段
 * 2.Activity的mInstrumentation对象，对应的newActivity方法和callActivityOnCreate方法。
 */
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }
}