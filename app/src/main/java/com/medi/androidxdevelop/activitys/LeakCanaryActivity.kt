package com.medi.androidxdevelop.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medi.androidxdevelop.R
import com.medi.androidxdevelop.utils.SingleInstance

/**
 * 1.0 堆文件分析器使用的库是haha
 * 2.0 堆文件分析器使用的库是shark（解决堆文件占用内存过大问题）
 *
 * 1.kotlin的匿名内部类不持有外部类的引用
 * 2.android.os.Debug.dumpHprofData(heapDumpFile.getAbsolutePath()获取堆文件
 * 3.LeakCanary自动检测以下对象的泄漏：
 *   销毁Activity实例 销毁Fragment实例 破坏片段View实例 清除ViewModel实例
 * 4.通过provider的onCreate()方法进行，初始化安装过程
 *
 *
 */
class LeakCanaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leak_canary)
        SingleInstance.setContext(this)
    }
}

