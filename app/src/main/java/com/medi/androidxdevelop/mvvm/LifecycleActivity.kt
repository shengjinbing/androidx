package com.medi.androidxdevelop.mvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.mvvm.life.TestLife
import kotlinx.android.synthetic.main.activity_lifecycle.*

//自定义一个LifecycleOwner
class LifecycleActivity : AppCompatActivity(), LifecycleOwner {
    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle)
        lifecycleRegistry.currentState=Lifecycle.State.CREATED
        lifecycleRegistry.addObserver(TestLife(lifecycle))

        initListener()
    }

    private fun initListener() {
        tv_livedata.setOnClickListener {

        }
    }

    public override fun onStart() {
        super.onStart()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun getLifecycle(): Lifecycle {
        lifecycleRegistry = LifecycleRegistry(this)
        return lifecycleRegistry
    }
}