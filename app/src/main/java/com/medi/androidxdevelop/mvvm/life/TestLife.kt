package com.medi.androidxdevelop.mvvm.life

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class TestLife(var lifecycle: Lifecycle) : LifecycleObserver{
    private val TAG = "Lifecycle"

    /**
     * 获取当前状态
     */
    fun getCurrState(): Lifecycle.State {
        val currentState = lifecycle.currentState
        return currentState
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun testonCreate() {
        Log.d(TAG,"监听到了onCreate回调")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun testonStart() {
        Log.d(TAG,"监听到了onStart回调")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun testonResume() {
        Log.d(TAG,"监听到了onResume回调")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun testonPause() {
        Log.d(TAG,"监听到了onPause回调")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun testonStop() {
        Log.d(TAG,"监听到了onStop回调")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun testonDestory() {
        Log.d(TAG,"监听到了onDestory回调")
    }
    //任何生命周期回调都会接收到通知
    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun testonAny() {
        //Log.d(TAG,"监听到了onAny回调")
    }
}