package com.medi.androidxdevelop.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.leakcanary.HeapDump
import com.medi.androidxdevelop.utils.SingleInstance
import leakcanary.AppWatcher

/**
 * https://square.github.io/leakcanary/
 * https://mp.weixin.qq.com/s?__biz=MzA5ODQ1ODU5NA==&mid=2247484037&idx=1&sn=5745ee913af677fee192bac77507d418&chksm=90900c08a7e7851ec4bf6f2b3a9af3406c9ab78402722dc623409b949ad683c1d7aa084cca57&token=1449112143&lang=zh_CN#rd
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
 */
class LeakCanaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leak_canary)
        //第一个内存泄露的例子，单例持有Activity的引用
        //SingleInstance.setContext(this)
        //手动观察
        SingleInstance.helper.name = "李四"

    }


    override fun onDestroy() {
        super.onDestroy()
        AppWatcher.objectWatcher.expectWeaklyReachable(SingleInstance.helper,"helpex泄露了吗")
        HeapDump.createDumpFile(this)
    }
}

