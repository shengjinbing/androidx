package com.medi.androidxdevelop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.medi.androidxdevelop.activitys.AppViewScreenActivity
import com.medi.androidxdevelop.activitys.AsyncTaskActivity
import com.medi.androidxdevelop.activitys.LeakCanaryActivity
import com.medi.androidxdevelop.mvvm.MvvmActivity
import com.sensorsdata.analytics.android.sdk.SensorsDataTrackViewOnClick
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(),CoroutineScope {
    private val job = Job()
    private var isUI = true
    override val coroutineContext: CoroutineContext
        get() = if (isUI) Dispatchers.Main + job else Dispatchers.IO + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(MyObserver())
        Looper.getMainLooper()

        btn_mvvm.setOnClickListener {
            startActivity(Intent(this,MvvmActivity::class.java))
        }
        btn_screen.setOnClickListener {
            startActivity(Intent(this,AppViewScreenActivity::class.java))
        }

        btn_asynctask.setOnClickListener {
            startActivity(Intent(this,AsyncTaskActivity::class.java))

        }
        btn_leakCannary.setOnClickListener {
            startActivity(Intent(this,LeakCanaryActivity::class.java))
        }

    }

    @SensorsDataTrackViewOnClick
    fun testAnnotation(view: View) {
        Toast.makeText(applicationContext,"测试asm",Toast.LENGTH_LONG).show()
    }


    /**
     * 监听生命周期
     */
    class MyObserver : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun connectListener() {
            Log.d("BBBBB","ON_RESUME")
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun disconnectListener() {
            Log.d("BBBBB","ON_PAUSE")

        }
    }
}


