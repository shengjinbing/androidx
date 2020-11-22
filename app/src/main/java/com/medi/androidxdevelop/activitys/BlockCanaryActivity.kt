package com.medi.androidxdevelop.activitys

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.moduth.blockcanary.internal.BlockInfo
import com.medi.androidxdevelop.R
import kotlinx.android.synthetic.main.activity_block_canary.*

/**
 * TraceView:性能开销很大，这个方法中的方法调用都会统计
 *          Debug.startMethodTracing("")
 *          Debug.stopMethodTracing("")
 *          生成的文件在外部存储的files文件下面
 *
 *
 * systemTrace：systrace命令行的用法如下:
 * 第一步：cd $ANDROID_HOME/platform-tools/systrace。
 * 第二步：python systrace.py [options] [category1] [category2] ... [categoryN]
 * options	描述：
 *
 *  -o < FILE >	输出的目标文件
 *  -t N, –time=N	执行时间，默认5s
 * -b N, –buf-size=N	buffer大小（单位kB),用于限制trace总大小，默认无上限
 * -k < KFUNCS >，–ktrace=< KFUNCS >	追踪kernel函数，用逗号分隔
 * -a < APP_NAME >,–app=< APP_NAME >	追踪应用包名，用逗号分隔
 * –from-file=< FROM_FILE >	从文件中创建互动的systrace
 * -e < DEVICE_SERIAL >,–serial=< DEVICE_SERIAL >	指定设备
 * -l, –list-categories	列举可用的tags
 *
 * category常用的：
 * sched：CPU调度的信息，非常重要；你能看到CPU在每个时间段在运行什么线程；线程调度情况，比如锁信息。
 * gfx：Graphic系统的相关信息，包括SurfaceFlinger，VSYNC消息，Texture，RenderThread等；分析卡顿非常依赖这个。
 * view：View绘制系统的相关信息，比如onMeasure，onLayout等；对分析卡顿比较有帮助。
 * am：ActivityManager调用的相关信息；用来分析Activity的启动过程比较有效。
 * dalvik： 虚拟机相关信息，比如GC停顿等。
 * binder_driver：Binder驱动的相关信息，如果你怀疑是Binder IPC的问题，不妨打开这个。
 * core_services：SystemServer中系统核心Service的相关信息，分析特定问题用。
 *
 *
 * 3.不使用python的SysTrace使用
 * 第一步：开发者选项中打开
 * 第二步：adb pull data/local/traces 然后open .
 * 第三步：在perfetto中打开
 *
 *
 * 微信的matrix通过ASM插庄记录方法和启动耗时
 *
 * @property TAG String
 */
class BlockCanaryActivity : AppCompatActivity() {
    private val TAG:String = "BlockCanary_log"
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreate(savedInstanceState: Bundle?) {

        Trace.beginSection("BlockCanary_onCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_canary)
        btn_block.setOnClickListener{
            SystemClock.sleep(2000)
            val stringBuilder = StringBuilder()
            for (stackTraceElement in Looper.getMainLooper().thread.stackTrace) {
                stringBuilder.append(savedInstanceState.toString()).append(BlockInfo.SEPARATOR)
            }
            Log.d(TAG,stringBuilder.toString())
        }

        Trace.endSection()
    }
}