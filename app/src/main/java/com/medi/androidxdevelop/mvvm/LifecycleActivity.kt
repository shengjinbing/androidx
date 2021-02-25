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
/**
 * 1、Activity/Fragment的生命周期如何转化为不同类型的Lifecycle.Event？
 * 2、Lifecycle.Event经过哪些处理？
 * 3、如何分发到特定的LifecycleObserver实现？
 *
 * 答案：
 * 1.静态方法injectIfNeededIn的主要作用就是为Activity提供Lifecycle能力，这里分成了两种情况，在Android 10及以上，
 * Activity的源码修改成自己可以注册进LifecycleCallbacks监听器。而为了兼容旧版本，则需要Fragment的生命周期回调中进行分发，
 * 这也就是与最初推测相比特殊的地方，可以更加留意。不过这两种情况都是根据生命周期创建了Event枚举型，并最终都经过静态方法dispatch，
 * 调用了Lifecyce的handleLifecycleEvent方法。
 *
 *
 * 1.而ComponentActivity使用Lifecycle能力在页面销毁时调用ViewModelStore实例的clear方法，清空其中的ViewModel。
 */
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