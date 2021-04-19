package com.medi.androidxdevelop.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.leakcanary.HeapDump
import com.medi.androidxdevelop.utils.SingleInstance
import kotlinx.android.synthetic.main.activity_leak_canary.*
import leakcanary.AppWatcher

/**
 * https://square.github.io/leakcanary/
 * https://mp.weixin.qq.com/s?__biz=MzA5ODQ1ODU5NA==&mid=2247484037&idx=1&sn=5745ee913af677fee192bac77507d418&chksm=90900c08a7e7851ec4bf6f2b3a9af3406c9ab78402722dc623409b949ad683c1d7aa084cca57&token=1449112143&lang=zh_CN#rd
 * Android 性能监控框架 Matrix（3）文件Hprof 分析
 * https://juejin.cn/post/6854573208520097799
 * 内部使用的堆文件分析器：
 * 1.堆文件分析器使用的库是haha（1.0版本）
 * 2.堆文件分析器使用的库是shark（解决堆文件占用内存过大问题2.0版本）
 *
 * 实例是否回需要回收的算法：
 * 1.引用计数法
 *   循环引用问题，图片（引用计数器.png）
 * 2.可达性分析法
 *   和引用计数法比较，可达性分析法多了一个叫做GC Root的概念，而这些GC Roots就是我们可达性分析法的起点，在周志明前辈的《深入理解Java虚拟机》中就已经提到过了这个概念，它主要分为几类：
 *   1.在方法区中类静态属性引用的对象，譬如Java类的引用类型静态变量。
 *   2.在方法区中常量引用的对象，譬如字符串常量池里的引用。
 *   3.在本地方法栈中JNI引用的对象。
 *   4.在Java虚拟机栈中引用的对象，譬如Android的主入口类ActivityThread。
 *   5.所有被同步锁持有的对象。
 *   ......
 *
 *
 * LeakCanary使用总结
 * 1.kotlin的匿名内部类不持有外部类的引用
 * 2.LeakCanary自动检测以下对象的泄漏：
 *   销毁Activity实例
 *   销毁Fragment实例
 *   破坏片段View实例
 *   清除ViewModel实例
 * 3.AppWatcherInstaller : ContentProvider()通过在AndroidManifest文件中注册，在onCreate()方法实现自动安装。
 *
 * 1.FragmentAndViewModelWatcher
 * Fragment为了兼容在Android源码中几个不同包名的实现，对它们的检测也需要分别实现，我们在FragmentAndViewModelWatcher
 * 中只关注AndroidXFragmentDestroyWatcher对AndroidX中Fragment的内存泄露检测即可，其他几个实现类似。
 * FragmentAndViewModelWatcher先同样通过注册Application.ActivityLifecycleCallbacks回调，适时获取Activity引用，
 * 并在AndroidXFragmentDestroyWatcher获取Activity的supportFragmentManager，向其注册FragmentManager.FragmentLifecycleCallbacks。
 * 在其中的onFragmentDestroyed与onFragmentViewDestroyed回调中将Fragment和Fragment的View纳入内存泄露检测。
 *
 * 2.对于ViewModel的检测，则需要关注ViewModelClearedWatcher，通过用上一步获取的Activity引用，添加名为ViewModelClearedWatcher
 * 的spy ViewModel，来获得收到onCleared回调的能力，因为对于一个ViewModelStoreOwner（Activity，Fragment）来说，自己的一个
 * ViewModel回调了onCleared，则其他ViewModel的onCleared也应该被调用。这些ViewModel是通过ViewModelStore的mMap属性反射
 * 获取的。在spy ViewModel的onCleared回调中，纳入内存泄露检测。
 *
 * 3.RootViewWatcher
 * 对于Android里Window中的RootView，即DecorView，可以通过注册addOnAttachStateChangeListener在View的onViewDetachedFromWindow
 * 时进行检测。而获取待检测对象的引用就不像Activity和Fragment一样有回调可以依赖了。LeakCanary采取了Hook的方式在install方法对RootView
 * 的容器进行替换，具体来说就是通过反射机制将WindowManagerGlobal中的mViews（包含所有Window中的DecorView）的ArrayList容器的实现修改
 * ，在其add方法中获取DecorView的引用，之后设置OnAttachStateChangeListener回调进行检测。
 *
 * 4.ServiceWatcher
 * 而Android中Service，无论是获取引用还是监测时机的确定都没有系统的回调可以依赖，LeakCanary都是采用Hook的方式达到目的。
 * 首先通过反射拿到ActivityThread中的mServices，这是包含app中全部Service的一个Map。在install方法中有两个Hook点，
 * 首先是Android 消息机制的中转中心，名为H的Handler，系统侧对应用侧的全部回调都需要经过它的周转。因为Handler中mCallback执
 * 行的优先级大于handleMessage方法，Leakcanary替换H的mCallback实现，当消息为STOP_SERVICE时，便从mServices取出该消息
 * 对应的Service作为待检测Service引用。第二个Hook点为ActivityManagerService，通过动态代理修改它的serviceDoneExecuting方法
 * ，在其真正实现前增加内存泄露检测，其余方法保持不变。
 *
 *
这些类纳入检测纳入检测的时机，可总结为如下表格：(重点)
                        如何获取引用	                                何时纳入监测
Activity	           ActivityLifecycleCallbacks回调	         onActivityDestroyed
Fragment	           FragmentLifecycleCallbacks回调	         onFragmentDestroyed
Fragment中的View        FragmentLifecycleCallbacks回调	         onFragmentViewDestroyed
ViewModel	           反射获取ViewModelStore的mMap	             spy ViewModel的onCleared
Window中的DecorView	   Hook WindowManagerGlobal中的mViews	     onViewDetachedFromWindow
Service	Hook H的mCallback实现，当消息为STOP_SERVICE时，从ActivityThread中的mServices获取	Hook ActivityManagerService，serviceDoneExecuting中检测

 核心机制就是引用队列
 */


class LeakCanaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leak_canary)
        //第一个内存泄露的例子，单例持有Activity的引用
        SingleInstance.setContext(this)
        //手动观察
        //SingleInstance.helper.name = "李四"
        val arrayList = ArrayList<String>()
        with(ArrayList<String>()){

        }
        var a:Int=1
        arrayList.let{
            a = 5
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        //AppWatcher.objectWatcher.expectWeaklyReachable(SingleInstance.helper,"helpex泄露了吗")
        //HeapDump.createDumpFile(this)
    }
}

